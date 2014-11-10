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


package com.github.johnpersano.benson.lexicon.modules;

import android.content.Context;

import com.github.johnpersano.benson.R;
import com.github.johnpersano.benson.lexicon.Query;
import com.github.johnpersano.benson.lexicon.Response;

import java.util.Arrays;
import java.util.List;

import me.palazzetti.adktoolkit.AdkManager;


public class Component extends Query {

    /* Search the hypothesis for these words. The space is necessary. */
    private static final String ON = " on";
    private static final String OFF = " off";

    /* These values will be sent to the Arduino via serial. */
    private static final String SERIAL_ON = "1";
    private static final String SERIAL_OFF = "0";

    @Override
    public List<String> getInputs() {

        return Arrays.asList("turn", "switch", "component", "exponent");

    }

    @Override
    public Response getResponse(Context context, String hypothesis, AdkManager adkManager) {

        if (hypothesis.contains(ON)) {

            adkManager.writeSerial(SERIAL_ON);

            return new Response()
                    .setReply(Response.getRandomReply(context.getResources().getStringArray(R.array.component_on_default)));

        } else if (hypothesis.contains(OFF)) {

            adkManager.writeSerial(SERIAL_OFF);

            return new Response()
                    .setReply(Response.getRandomReply(context.getResources().getStringArray(R.array.component_off_default)));

        } else {

            return new Response()
                    .setReply(Response.getRandomReply(context.getResources().getStringArray(R.array.component_default)))
                    .setNestedLexicon(Arrays.asList(new ComponentOn(), new ComponentOff()));

        }

    }

    private class ComponentOn extends Query {

        @Override
        public List<String> getInputs() {

            return Arrays.asList("on", "ron", "an", "bon");

        }

        @Override
        public Response getResponse(Context context, String hypothesis, AdkManager adkManager) {

            adkManager.writeSerial(SERIAL_ON);

            return new Response()
                    .setReply(Response.getRandomReply(context.getResources().getStringArray(R.array.component_on_default)));

        }

    }

    private class ComponentOff extends Query {

        @Override
        public List<String> getInputs() {

            return Arrays.asList("off", "of");

        }

        @Override
        public Response getResponse(Context context, String hypothesis, AdkManager adkManager) {

            adkManager.writeSerial(SERIAL_OFF);

            return new Response()
                    .setReply(Response.getRandomReply(context.getResources().getStringArray(R.array.component_off_default)));

        }

    }

}