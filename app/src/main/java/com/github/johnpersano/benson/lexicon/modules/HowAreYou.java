package com.github.johnpersano.benson.lexicon.modules;


import android.content.Context;

import com.github.johnpersano.benson.R;
import com.github.johnpersano.benson.lexicon.Query;
import com.github.johnpersano.benson.lexicon.Response;

import java.util.Arrays;
import java.util.List;

import me.palazzetti.adktoolkit.AdkManager;


public class HowAreYou extends Query {

    @Override
    public List<String> getInputs() {

        return Arrays.asList("how are you");

    }

    @Override
    public Response getResponse(Context context, String hypothesis, AdkManager adkManager) {

        return new Response()
                .setReply(Response.getRandomReply(context.getResources().getStringArray(R.array.how_are_you_default)))
                .setNestedLexicon(Arrays.asList(new PositiveStatus(), new NegativeStatus()));

    }

    private class PositiveStatus extends Query {

        @Override
        public List<String> getInputs() {

            return Arrays.asList("good", "great", "excellent", "wonderful", "dandy", "super", "terrific");

        }

        @Override
        public Response getResponse(Context context, String hypothesis, AdkManager adkManager) {

            return new Response()
                    .setReply(Response.getRandomReply(context.getResources().getStringArray(R.array.how_are_you_positive_status)));

        }

    }

    private class NegativeStatus extends Query {

        @Override
        public List<String> getInputs() {

            return Arrays.asList("not good", "bad", "terrible", "horrible");

        }

        @Override
        public Response getResponse(Context context, String hypothesis, AdkManager adkManager) {

            return new Response()
                    .setReply(Response.getRandomReply(context.getResources().getStringArray(R.array.how_are_you_negative_status)))
                    .setNestedLexicon(Arrays.asList(new YesJoke(), new NoJoke()));

        }

    }

    private class YesJoke extends Joke {

        @Override
        public List<String> getInputs() {

            return Arrays.asList("yes", "maybe", "okay", "sure", "certainly");

        }

        @Override
        public Response getResponse(Context context, String hypothesis, AdkManager adkManager) {

            return super.getResponse(context, hypothesis, adkManager);

        }

    }

    private class NoJoke extends Query {

        @Override
        public List<String> getInputs() {

            return Arrays.asList("no", "not now", "not at all", "absolutely not");

        }

        @Override
        public Response getResponse(Context context, String hypothesis, AdkManager adkManager) {

            return new Response()
                    .setReply(Response.getRandomReply(context.getResources().getStringArray(R.array.how_are_you_no_joke)));

        }

    }

}
