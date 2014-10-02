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

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/* Holds a response to a given query. */
public class Response {

    /**
     * Each response is paired with an appropriate mood. If Benson is joyous, he will
     * respond with the joyous response to a given query. Each mood represents a color
     * for Benson's visualiser.
     */
    public enum Mood {

        JOYOUS(Color.GREEN),
        HAPPY(Color.CYAN),
        NORMAL(Color.WHITE),
        ANNOYED(Color.YELLOW),
        AGGRAVATED(Color.MAGENTA),
        ANGRY(Color.RED);

        private int color;

        Mood(int color) {

            this.color = color;

        }

        public int getColor() {

            return color;

        }

    }

    public Response(String reply) {

        this.reply = new ArrayList<String>();
        this.reply.add(reply);

    }

    /* The preset mood for the response. */
    public Mood mood;

    /* The reply that Benson will speak. */
    public List<String> reply;

    /* The mood adjustment for a particular response. */
    public int adjustment;

    /* String to be sent to the DUE via serial. */
    public String serial;

    /* A nested set of queries for a response. Used for contextual conversations. */
    @SerializedName("lexicon")
    public List<Query> queries;

}
