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

import java.util.List;
import java.util.Random;

public class Response {

    /* The reply that Benson will speak. */
    private String mReply;

    /* A nested set of queries for a response. Used for contextual conversations. */
    private List<? extends Query> mNestedLexicon;

    public Response() {

        /* Do nothing */

    }

    /**
     * Set the reply Benson will speak.
     *
     * @param reply The reply Benson will speak.
     * @return An instance of the {@link Response}
     */
    public Response setReply(String reply) {

        this.mReply = reply;

        return this;

    }

    /**
     * Returns the reply Benson will speak.
     *
     * @return The {@link String} Benson will speak.
     */
    public String getReply() {

        return this.mReply;

    }

    /**
     * Set a {@link Query} {@link java.util.List} which
     * will serve as Benson's lexicon. This is useful for clarification or continuous conversation.
     *
     * @param lexicon The {@link Query} items Benson will recognize.
     * @return An instance of the {@link Response}
     */
    public Response setNestedLexicon(List<? extends Query> lexicon) {

        this.mNestedLexicon = lexicon;

        return this;

    }

    /**
     * Returns the {@link Query} {@link java.util.List} that
     * will serve as Benson's lexicon.
     *
     * @return The lexicon that Benson will use for known speech.
     */
    public List<? extends Query> getNestedLexicon() {

        return this.mNestedLexicon;

    }

    /**
     * Returns a random reply from a list of replies.
     *
     * @param replies Array resource.
     * @return Random {@link String} in the array.
     */
    public static String getRandomReply(String[] replies) {

        return replies[new Random().nextInt(replies.length)];

    }

    /**
     * Returns a random reply formatted with the current time from a list of replies.
     *
     * @param replies Array resource.
     * @return Random {@link String} in the array.
     */
    public static String getRandomTimeReply(String[] replies) {

        final android.text.format.Time time = new android.text.format.Time(android.text.format.Time.getCurrentTimezone());
        time.setToNow();

        /* Format is (12h:minute)AM/PM. See <http://linux.die.net/man/3/strftime> for other options. */
        return String.format(replies[new Random().nextInt(replies.length)], time.format("%l:%M%p"));

    }

}
