/**
 * Copyright 2014 John Persano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.github.johnpersano.benson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;

import com.github.johnpersano.benson.util.CMUSphinxRecognizer;
import com.github.johnpersano.benson.views.AnimatedTextView;
import com.github.johnpersano.benson.util.Response;
import com.github.johnpersano.benson.util.ResponseGenerator;
import com.github.johnpersano.benson.util.SpeechListener;
import com.github.johnpersano.benson.util.SpeechListenerWrapper;
import com.github.johnpersano.benson.visualizer.CircleRenderer;
import com.github.johnpersano.benson.visualizer.VisualizerView;
import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import me.palazzetti.adktoolkit.AdkManager;


public class ActivityMain extends Activity implements SpeechListener {

    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG= "ActivityMain";

    /* CMUSphinx keyword requires a string key. For simplicity, the value is 'key'. */
    private static final String RECOGNITION_KEY= "key";

    /* CMUSphinx keyword. This is the only word CMUSphinx will listen for. */
    private static final String RECOGNITION_KEYWORD= "benson";

    /* Check the CMUSphinx recognition result for two possibilities. Sometimes the recognized word is repeated twice. */
    private static final List<String> mKeywords = Arrays.asList("benson", "benson benson");

    /* Generic responses for speech recognition. */
    private static final String RESPONSE_SIR = "Sir?";
    private static final String RESPONSE_ONLINE = "I am online. If you require my services, I'll be here.";
    private static final String RESPONSE_HOLD = "Let me look that up.";

    /* Custom TextView that will animate text. */
    private AnimatedTextView mAnimatedTextView;

    /* CMUSphinx speech recognizer. */
    private SpeechRecognizer mCMUSphinxRecognizer;

    /* Android text to speech. */
    private TextToSpeech mTTS;

    /* Android speech recognizer. */
    private android.speech.SpeechRecognizer mAndroidRecognizer;

    /* View for main visualizer. */
    private VisualizerView mVisualizerView;

    /* Circle renderer for main visualizer. */
    private CircleRenderer mCircleRenderer;

    /* Intent for the Android speech recognizer. */
    private Intent mAndroidRecognizerIntent;

    /* Android Adk manager to communicate with the Arduino Due. */
    private AdkManager mAdkManager;

    /* This handler will clear on screen text and reset mood ten seconds after Benson speaks. */
    private Handler mTextViewHandler;

    /* HashMap of generic responses. */
    private HashMap<String, ArrayList<Response>> mGenericResponseHashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Hide navigation UI for fullscreen */
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        /* Open connection to the Arduino Due */
        mAdkManager = new AdkManager((UsbManager) getSystemService(Context.USB_SERVICE));
        mAdkManager.open();

        /* This is the main textview for the application. */
        mAnimatedTextView = (AnimatedTextView)
                findViewById(R.id.animated_textview);

        /* Initialize the CMUSphinx speech recognizer in an AsyncTask. */
        initializeCMUSphinxRecognizer();

        /* Initialize the Android speech recognizer and recognizer intent. */
        initializeAndroidRecognizer();

        /* Initialize the visualizer, circle renderer and start listening for speech */
        initializeVisualizer();

        /* Initialize the Android text to speech service. */
        initializeSpeech();

        /* Get the generic responses from XML. */
        try {

            mGenericResponseHashMap = new ResponseGenerator(ActivityMain.this).getResponseMap();

        } catch (XmlPullParserException exception) {

            Log.e(TAG, exception.toString());

        } catch (IOException exception) {

            Log.e(TAG, exception.toString());

        }

        /* Create a handler to clear text and reset mood. */
        mTextViewHandler = new Handler();

    }

    /* Initialize CMUSphinx voice recognition. This will listen for the Benson keyword. */
    private void initializeCMUSphinxRecognizer() {

        /* Do the initialization in an AsyncTask since it takes a while. */
        new AsyncTask<Void, Void, Exception>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mAnimatedTextView.setText("Initializing...");

            }

            @Override
            protected Exception doInBackground(Void... params) {

                try {

                    mCMUSphinxRecognizer = new CMUSphinxRecognizer(ActivityMain.this).getRecognizer();

                } catch (IOException exception) {

                    return exception;

                }

                return null;

            }

            @Override
            protected void onPostExecute(Exception exception) {

                /* If no exception was thrown during initialization add recognition listener. */
                if (exception == null) {

                    mCMUSphinxRecognizer.addListener(new RecognitionListener() {

                        @Override
                        public void onBeginningOfSpeech() {

                            /* Do nothing */

                        }

                        @Override
                        public void onEndOfSpeech() {

                            /* Do nothing */

                        }

                        @Override
                        public void onPartialResult(Hypothesis hypothesis) {

                            if (hypothesis != null) {

                                /* Check if response contains the Benson keywords. */
                                if (mKeywords.contains(hypothesis.getHypstr())) {

                                    say(new Response(RESPONSE_SIR, Response.MOOD.NORMAL, null));

                                }

                            }
                        }

                        @Override
                        public void onResult(Hypothesis hypothesis) {

                            if (hypothesis != null) {

                                /* Check if response contains the Benson keywords. */
                                if (mKeywords.contains(hypothesis.getHypstr())) {

                                    say(new Response(RESPONSE_SIR, Response.MOOD.NORMAL, null));

                                }

                            }

                        }

                    });

                    /* Set keywords for the CMUSphinx speech recognizer to listen for. */
                    mCMUSphinxRecognizer.addKeyphraseSearch(RECOGNITION_KEY, RECOGNITION_KEYWORD);

                    /* Do not start recognition immediately. Recognition start/stop is handled by text to speech listener. */
                    mCMUSphinxRecognizer.stop();

                } else {

                    mAnimatedTextView.setText("Recognition error");

                }

            }

        }.execute();

    }

    /* Initialize the Android speech recognition service. No need for AsyncTask. */
    private void initializeAndroidRecognizer() {

        /* Create recognizer and set recognition listener. Used custom listener for simplicity. */
        mAndroidRecognizer = android.speech.SpeechRecognizer.createSpeechRecognizer(ActivityMain.this);
        mAndroidRecognizer.setRecognitionListener(new SpeechListenerWrapper(ActivityMain.this));

        /* Create intent for Android speech recognizer. */
        mAndroidRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mAndroidRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        mAndroidRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, ActivityMain.this.getPackageName());
        mAndroidRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        mAndroidRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

    }

    /* Initialize the visualizer. */
    private void initializeVisualizer() {

        /* Get a reference to the VisualiserView in our layout and start listening for speech. */
        mVisualizerView = (VisualizerView) findViewById(R.id.visualizer_view);
        mVisualizerView.create();

        /* Add a circle renderer to the visualizer view. Other renderers can be used from the visualizer library. */
        mCircleRenderer = new CircleRenderer(false);
        mVisualizerView.addRenderer(mCircleRenderer);

    }

    /* Initialize the Android text to speech service. This will be Benson's voice. */
    private void initializeSpeech() {

        /* Initialize the Android text to speech service and set initialization listener. */
        mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {

                /* Set utterance listener if the text to speech service was initialized correctly. */
                if (status != TextToSpeech.ERROR) {

                    /* Give Benson an English accent. Just because. */
                    mTTS.setLanguage(Locale.UK);

                    /* On first initialization tell user Benson is online. */
                    say(new Response(RESPONSE_ONLINE, Response.MOOD.NORMAL, null));

                    /* Set listener for Benson's speech. Both recognition services will start/stop based on speech progress. */
                    mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(final String utteranceId) {

                            /* If Benson is speaking, do not listen for speech recognition. */
                            mCMUSphinxRecognizer.stop();

                            /* Show what Benson is saying in the textview. Apparently this listener does not run on UI thread. */
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    mAnimatedTextView.animateText(utteranceId);

                                }
                            });

                            /* Clear pending runnable that removes text and resets mood. */
                            mTextViewHandler.removeCallbacks(mTextViewRunnable);

                        }

                        @Override
                        public void onDone(String utteranceId) {

                            /* If Benson has responded to his name, use Android speech recognizer. */
                            if (utteranceId.equals(RESPONSE_SIR)) {

                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        /* Start listening to user speech via Android speech recognition. */
                                        mAndroidRecognizer.startListening(mAndroidRecognizerIntent);

                                    }
                                });

                            /* If Benson has told the user to hold (Wolfram query) do not start listening. */
                            } else if (utteranceId.equals(RESPONSE_HOLD)) {

                                /* Prevent speech recognition while Wolfram query is running. Only used to avoid the else call. */
                                mCMUSphinxRecognizer.stop();

                            } else {

                                /* Benson has responded to the user and should start listening again. */
                                mCMUSphinxRecognizer.startListening(RECOGNITION_KEY);

                                mTextViewHandler.postDelayed(mTextViewRunnable, (10 * 1000));

                            }

                        }

                        @Override
                        public void onError(String utteranceId) {

                            /* This has never been called during testing. Start listening just in case. */
                            mCMUSphinxRecognizer.startListening(RECOGNITION_KEY);

                        }

                    });

                }

            }

        });

    }


    @Override
    public void onDestroy() {

        if(mCMUSphinxRecognizer != null) {

            mCMUSphinxRecognizer.stop();
            mCMUSphinxRecognizer = null;

        }

        if (mTTS != null) {

            mTTS.stop();
            mTTS.shutdown();

        }

        if(mAndroidRecognizer != null) {

            mAndroidRecognizer.stopListening();
            mAndroidRecognizer.destroy();

        }

        if(mVisualizerView != null) {

            mVisualizerView.release();

        }

        super.onDestroy();

    }

    /* Used for spoken response via text to speech. */
    private void say(Response response) {

        /* Create parameter with spoken text. This is used to display spoken text in animated text view. */
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, response.getReply());

        mTTS.speak(response.getReply(), TextToSpeech.QUEUE_FLUSH, params);

        /* Sets the color of circle renderer to indicate mood. */
        mCircleRenderer.changeColor(response.getMood());

    }

    @Override
    public void onSpeechResult(boolean resultOK, String speechResult, int mood) {

        if (resultOK) {

            /* User speech has programmed response in XML file */
            if (mGenericResponseHashMap.containsKey(speechResult)) {

                final ArrayList<Response> responseArrayList = mGenericResponseHashMap.get(speechResult);

                /* Multiple responses are available for each inquiry, select one at random. */
                final int randomInteger = new Random().nextInt(responseArrayList.size());

                final Response response = responseArrayList.get(randomInteger);

                say(response);

                /* Write to serial any commands found in XML. Must upload simple_sketch to the Udoo to work. */
                if (response.getSerial() != null) {

                    mAdkManager.writeSerial(response.getSerial());

                }

            /* No programmed response was found, let's call Wolfram API to response to the user's inquiry. */
            } else {

                say(new Response(RESPONSE_HOLD, Response.MOOD.NORMAL, null));

                new WolframQuery().execute(speechResult);

            }

        /* User speech triggered an error, either timeout or garbled speech. Say error response. */
        } else {

            say(new Response(speechResult, mood, null));

        }

    }

    /* This Runnable will clear any text on the screen and reset Benson's mood. */
    private Runnable mTextViewRunnable = new Runnable() {
        @Override
        public void run() {

            if(mAnimatedTextView != null) {

                mAnimatedTextView.setText(" ");

                mCircleRenderer.changeColor(Response.MOOD.NORMAL);

            }

        }

    };


    /* AsyncTask to get response from Wolfram API. */
    private class WolframQuery extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            /* Go online to > http://products.wolframalpha.com/developers/ and sign up for an app key.
             * Once you have an app key, create a string resource with the id wolfram_key and the key for text. */
            final WAEngine engine = new WAEngine();
            engine.setAppID(getResources().getString(R.string.wolfram_key));
            engine.addFormat("plaintext");

            final WAQuery query = engine.createQuery();
            query.setInput(params[0]);

            try {

                final WAQueryResult queryResult = engine.performQuery(query);

                if (queryResult.isError()) {

                    return "Error:" + queryResult.getErrorMessage();

                } else if (!queryResult.isSuccess()) {

                    return "What are you talking about?";

                } else {

                    /* Very hacky way to get Wolfram API response. Needs optimization */
                    for (int i = 0; i < queryResult.getNumPods(); i++) {

                        if (i == 2) {

                            final Object element = queryResult.getPods()[1].getSubpods()[0].getContents()[0];

                            return (((WAPlainText) element).getText());

                        }

                    }

                }

            } catch (WAException exception) {

                Log.e(TAG, exception.toString());

            }

            return "I couldn't find an answer to that.";

        }

        @Override
        protected void onPostExecute(String result) {

            say(new Response(result, Response.MOOD.NORMAL, null));

        }

    }

}


