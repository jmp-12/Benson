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


package com.github.johnpersano.benson.lexicon;

import android.content.Context;

import java.util.List;

import me.palazzetti.adktoolkit.AdkManager;

/* All module classes should extend this class and override its methods. */
public class Query {

    /**
     * Get input String List for the module.
     *
     * @return {@link java.util.List}
     */
    public List<String> getInputs() {

        return null;

    }

    /**
     * Get response from module.
     *
     * @param context The current Context.
     * @param hypothesis The full string containing the user's speech.
     * @param adkManager The ADK Manager which communicates with the Arduino DUE.
     * @return {@link Response}
     */
    public Response getResponse(Context context,  String hypothesis, AdkManager adkManager) {

        return null;

    }

}
