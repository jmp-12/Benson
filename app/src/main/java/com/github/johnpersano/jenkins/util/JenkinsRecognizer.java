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


import android.content.Context;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class JenkinsRecognizer {

    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = "JenkinsRecognizer";

    private Context mContext;

    public JenkinsRecognizer(Context context) {

        this.mContext = context;

    }

    /* Create speech recognizer. This should be done in an AsyncTask */
    public SpeechRecognizer getRecognizer() throws IOException {

            final Assets assets = new Assets(mContext);

            final File assetDirectory = assets.syncAssets();
            final File modelsDirectory = new File(assetDirectory, "models");

            return defaultSetup()
                    .setAcousticModel(new File(modelsDirectory, "hmm/en-us-semi"))
                    .setDictionary(new File(modelsDirectory, "dict/cmu07a.dic"))
                    .setRawLogDir(assetDirectory).setKeywordThreshold(1e-20f)
                    .getRecognizer();

    }

}
