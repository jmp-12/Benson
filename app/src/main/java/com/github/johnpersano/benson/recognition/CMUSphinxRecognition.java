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

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;


public class CMUSphinxRecognition implements RecognitionListener {

    @SuppressWarnings("unused")
    private static final String TAG = "CMUSphinxRecognition";

    /* Custom listener for speech recognition. */
    public interface OnResultListener {

        public void onSpeechResult(String hypothesis);

    }

    private OnResultListener mOnResultListener;

    public CMUSphinxRecognition(OnResultListener onResultListener) {

        this.mOnResultListener = onResultListener;

    }

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

        mOnResultListener.onSpeechResult(hypothesis.getHypstr());

    }

    @Override
    public void onResult(Hypothesis hypothesis) {

        mOnResultListener.onSpeechResult(hypothesis.getHypstr());

    }

}
