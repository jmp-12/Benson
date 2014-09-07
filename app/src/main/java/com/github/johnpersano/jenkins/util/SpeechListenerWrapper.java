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

package com.github.johnpersano.jenkins.util;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;


public class SpeechListenerWrapper implements RecognitionListener {

    @SuppressWarnings("unused")
    private static final String TAG = "SpeechListenerWrapper";

    SpeechListener mSpeechListener;

    public SpeechListenerWrapper(SpeechListener speechListener) {

        this.mSpeechListener = speechListener;

    }

    @Override
    public void onReadyForSpeech(Bundle params) {

        /* Do nothing. */

    }


    @Override
    public void onBeginningOfSpeech() {

        /* Do nothing. */

    }

    @Override
    public void onRmsChanged(float rmsdB) {

        /* Do nothing. */

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

        /* Do nothing. */

    }

    @Override
    public void onEndOfSpeech() {

        /* Do nothing. */

    }

    @Override
    public void onError(int error) {

        switch (error) {

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:

                mSpeechListener.onSpeechResult(false, "How rude.", JenkinsResponse.MOOD.ANGRY);

                break;

            case SpeechRecognizer.ERROR_NO_MATCH:

                mSpeechListener.onSpeechResult(false, "I have no idea what you're saying.", JenkinsResponse.MOOD.AGGRAVATED);

                break;

        }

    }

    @Override
    public void onResults(Bundle results) {

        final ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        mSpeechListener.onSpeechResult(true, matches.get(0), 0);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

        mSpeechListener.onSpeechResult(false, "I'm sorry, could you repeat that?", JenkinsResponse.MOOD.INDIFFERENT);

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

        /* Do nothing. */

    }

}

