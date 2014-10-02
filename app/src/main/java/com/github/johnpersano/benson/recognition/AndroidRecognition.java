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

package com.github.johnpersano.benson.recognition;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;


public class AndroidRecognition implements RecognitionListener {

    @SuppressWarnings("unused")
    private static final String TAG = "SpeechListenerWrapper";

    private static final String RESPONSE_TIMEOUT = "How rude.";
    private static final String RESPONSE_BAD_SPEECH = "I have no idea what you're saying.";
    private static final String RESPONSE_PARTIAL_RESULTS =  "I'm sorry, could you repeat that?";

    /* Custom listener for speech result */
    public interface OnResultListener {

        public void onSpeechResult(boolean resultOK, String hypothesis);

    }

    private OnResultListener mOnResultListener;

    public AndroidRecognition(OnResultListener onResultListener) {

        this.mOnResultListener = onResultListener;

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

                mOnResultListener.onSpeechResult(false, RESPONSE_TIMEOUT);

                break;

            case SpeechRecognizer.ERROR_NO_MATCH:

                mOnResultListener.onSpeechResult(false, RESPONSE_BAD_SPEECH);

                break;

        }

    }

    @Override
    public void onResults(Bundle results) {

        final ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        mOnResultListener.onSpeechResult(true, matches.get(0));

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

        mOnResultListener.onSpeechResult(false, RESPONSE_PARTIAL_RESULTS);

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

        /* Do nothing. */

    }

}

