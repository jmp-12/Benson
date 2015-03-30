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
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;

import com.github.johnpersano.benson.lexicon.Query;
import com.github.johnpersano.benson.lexicon.Response;
import com.github.johnpersano.benson.lexicon.Lexicon;
import com.github.johnpersano.benson.recognition.AndroidRecognition;
import com.github.johnpersano.benson.recognition.CMUSphinxRecognition;
import com.github.johnpersano.benson.views.AnimatedTextView;
import com.github.johnpersano.benson.views.visualizer.CircleRenderer;
import com.github.johnpersano.benson.views.visualizer.VisualizerView;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import me.palazzetti.adktoolkit.AdkManager;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;


public class ActivityBenson extends Activity implements AndroidRecognition.OnResultListener {

    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = "ActivityBenson";

    /* CMUSphinx keyword requires a string key. For simplicity, the value is set as 'key'. */
    private static final String RECOGNITION_KEY = "key";

    /* CMUSphinx keyword. This is the only word CMUSphinx will listen for. */
    private static final String RECOGNITION_KEYWORD = "benson";

    /* Generic responses for speech recognition. */
    private static final String RESPONSE_SIR = "Sir?";
    private static final String RESPONSE_ONLINE = "I am online. If you require my services, I'll be here.";
    private static final String RESPONSE_HOLD = "Let me look that up.";

    /* Custom TextView that will animate text. */
    private AnimatedTextView mAnimatedTextView;

    /* Various recognizers Benson needs to operate. */
    private SpeechRecognizer mCMUSphinxRecognizer;
    private android.speech.SpeechRecognizer mAndroidRecognizer;
    private Intent mAndroidRecognizerIntent;

    /* Benson's voice. Try experimenting with text to speech settings to change voice. */
    private TextToSpeech mTTS;

    /* The pulsating circles used to represent voice and status. */
    private VisualizerView mVisualizerView;

    /* Android Adk manager to communicate with the Arduino Due. */
    private AdkManager mAdkManager;

    /* This handler will clear on screen text and reset mood ten seconds after Benson speaks. */
    private Handler mTextViewHandler;

    /* Default conversational vocabulary. This list is used to reset Benson's lexicon and does NOT change. */
    private final List<? extends Query> mDefaultList = new Lexicon().lexicon;

    /* Conversational vocabulary. This list will change depending on response clarification. */
    private List<? extends Query> mDynamicList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benson);

        /* Hide navigation UI for fullscreen. */
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        /* Open connection to the Arduino Due. */
        mAdkManager = new AdkManager((UsbManager) getSystemService(Context.USB_SERVICE));
        mAdkManager.open();

        /* This textview serves as Benson's subtitle. */
        mAnimatedTextView = (AnimatedTextView)
                findViewById(R.id.animated_textview);
        mAnimatedTextView.setText(getResources().getString(R.string.initialization));

        /* The CMUSphinxRecognizer will continuously listen for the word 'benson'. */
        initializeCMUSphinxRecognizer();

        /* The AndroidRecognizer will be called after Benson hears his name to listen for the users speech. */
        /* Create an Android speechrecognizer and set recognition listener. Used custom listener for simplicity. */
        mAndroidRecognizer = android.speech.SpeechRecognizer.createSpeechRecognizer(ActivityBenson.this);
        mAndroidRecognizer.setRecognitionListener(new AndroidRecognition(ActivityBenson.this));

        /* Intent for Android speech recognizer. Set as class member variable to avoid unnecessary object recreation. */
        mAndroidRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mAndroidRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        mAndroidRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, ActivityBenson.this.getPackageName());
        mAndroidRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        mAndroidRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        /* The visualizer is Benson's pulsating circle. */
        mVisualizerView = (VisualizerView) findViewById(R.id.visualizer_view);
        mVisualizerView.create();

        /* Add a circle renderer to the visualizer view. Other renderers can be used from the visualizer library. */
        CircleRenderer circleRenderer = new CircleRenderer();
        mVisualizerView.addRenderer(circleRenderer);
        circleRenderer.changeColor(Color.CYAN);

        /* The Android TTS service will serve as Benson's voice. */
        initializeSpeech();

        /* Create a handler to clear the subtitle text off of the screen. */
        mTextViewHandler = new Handler();

    }

    private void initializeCMUSphinxRecognizer() {

        /* Do the initialization in an AsyncTask since it takes a while. */
        new AsyncTask<Void, Void, Exception>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mAnimatedTextView.setText(getResources().getString(R.string.initialization));

            }

            @Override
            protected Exception doInBackground(Void... params) {

                try {

                    final File assetDirectory = new Assets(ActivityBenson.this).syncAssets();
                    final File modelsDirectory = new File(assetDirectory, "models");

                    mCMUSphinxRecognizer = defaultSetup()
                            .setAcousticModel(new File(modelsDirectory, "hmm/en-us-semi"))
                            .setDictionary(new File(modelsDirectory, "dict/cmu07a.dic"))
                            .setRawLogDir(assetDirectory).setKeywordThreshold(1e-20f)
                            .getRecognizer();

                } catch (IOException exception) {

                    return exception;

                }

                return null;

            }

            @Override
            protected void onPostExecute(Exception exception) {

                /* If no exception was thrown during initialization, add recognition listener. */
                if (exception == null) {

                    mCMUSphinxRecognizer.addListener(new CMUSphinxRecognition(new CMUSphinxRecognition.OnResultListener() {

                        @Override
                        public void onSpeechResult(String hypothesis) {

                            if (hypothesis != null) {

                                /* Check if response contains the Benson keywords. */
                                if (Arrays.asList(getResources().getStringArray(R.array.cmusphinx_keywords)).contains(hypothesis)) {

                                    say(new Response().setReply(RESPONSE_SIR));

                                }

                            }

                        }

                    }));

                    /* Set the CMUSphinx speech recognizer to listen for the keyword 'benson'. */
                    mCMUSphinxRecognizer.addKeyphraseSearch(RECOGNITION_KEY, RECOGNITION_KEYWORD);

                    /* Do not start recognition immediately. Recognition start/stop is handled by a TTS listener. */
                    mCMUSphinxRecognizer.stop();

                } else {

                    mAnimatedTextView.setText(getResources().getString(R.string.error_recognition));

                }

            }

        }.execute();

    }

    private void initializeSpeech() {

        /* Initialize the Android text to speech service and set oninitialization listener. */
        mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {

                /* Set utterance listener if the text to speech service was initialized correctly. */
                if (status != TextToSpeech.ERROR) {

                    /* Give Benson an English accent. Just because. */
                    mTTS.setLanguage(Locale.UK);

                    /* On first initialization tell user Benson is online. */
                    say(new Response().setReply(RESPONSE_ONLINE));

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

                            /* Clear pending runnable that removes subtitle text. */
                            mTextViewHandler.removeCallbacks(mTextViewRunnable);

                        }

                        @Override
                        public void onDone(String utteranceId) {

                            /* If Benson has responded to his name or is asking a question, start Android SpeechRecognizer. */
                            if (utteranceId.equals(RESPONSE_SIR) || utteranceId.endsWith("?")) {

                                runOnUiThread(new Runnable() {
                                    public void run() {

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

                                /* Clear the subtitle after twelve seconds. */
                                mTextViewHandler.postDelayed(mTextViewRunnable, (12 * 1000));

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

        if (mCMUSphinxRecognizer != null) {

            mCMUSphinxRecognizer.stop();
            mCMUSphinxRecognizer = null;

        }

        if (mTTS != null) {

            mTTS.stop();
            mTTS.shutdown();

        }

        if (mAndroidRecognizer != null) {

            mAndroidRecognizer.stopListening();
            mAndroidRecognizer.destroy();

        }

        if (mVisualizerView != null) {

            mVisualizerView.release();

        }

        super.onDestroy();

    }


    @Override
    public void onSpeechResult(boolean resultOK, String hypothesis) {

        if (resultOK) {

            for (Query query : (mDynamicList != null) ? this.mDynamicList : mDefaultList) {

                for (String input : query.getInputs()) {

                    if (hypothesis.contains(input)) {

                        final Response response = query.getResponse(ActivityBenson.this, hypothesis, mAdkManager);
                        this.mDynamicList = response.getNestedLexicon();

                        say(response);

                        return;

                    }

                }

            }

            if (mDynamicList != null) {

                say(new Response().setReply(getResources().getString(R.string.response_misunderstood_nested_lexicon)));

                this.mDynamicList = null;

            } else {

                /* Benson was not able to find the user's query in his vocabulary, maybe it's a query for Wolfram Alpha. */
                say(new Response().setReply(RESPONSE_HOLD));

                new WolframQuery().execute(hypothesis);

            }

        } else {

            /* Conversation context no longer applies. Reset vocabulary. */
            this.mDynamicList = null;

            /* Error, say error response. See AndroidRecognition class. */
            say(new Response().setReply(hypothesis));

        }

    }

    private void say(Response response) {

        /* Create parameter with the text to be spoken. This is used to display subtitle. */
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, response.getReply());

        mTTS.speak(response.getReply(), TextToSpeech.QUEUE_FLUSH, params);

    }

    /* This Runnable will clear any existing text off of the screen. */
    private Runnable mTextViewRunnable = new Runnable() {
        @Override
        public void run() {

            if (mAnimatedTextView != null) {

                mAnimatedTextView.setText(" ");

            }

        }

    };

    /* AsyncTask to get response from Wolfram API. */
    private class WolframQuery extends AsyncTask<String, Void, String> {

        private static final String FORMAT = "plaintext";

        @Override
        protected String doInBackground(String... params) {

            /* Go online to > http://products.wolframalpha.com/developers/ and sign up for an app key.
             * Once you have an app key, create a string resource with the id wolfram_key and the key for text. */
            final WAEngine engine = new WAEngine();
            engine.setAppID(getResources().getString(R.string.wolfram_key));
            engine.addFormat(FORMAT);

            final WAQuery query = engine.createQuery();
            query.setInput(params[0]);

            try {

                final WAQueryResult queryResult = engine.performQuery(query);

                if (queryResult.isError()) {

                    return queryResult.getErrorMessage();

                } else if (!queryResult.isSuccess()) {

                    return Arrays.asList(getResources().getStringArray(R.array.wolfram_bad_query)).get(new Random()
                            .nextInt(getResources().getStringArray(R.array.wolfram_bad_query).length));

                } else {

                    /* Very hacky way to get Wolfram API response. Needs optimization. */
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

            return Arrays.asList(getResources().getStringArray(R.array.wolfram_no_find)).get(new Random()
                    .nextInt(getResources().getStringArray(R.array.wolfram_no_find).length));

        }

        @Override
        protected void onPostExecute(String result) {

            say(new Response().setReply(result));

        }

    }

}


