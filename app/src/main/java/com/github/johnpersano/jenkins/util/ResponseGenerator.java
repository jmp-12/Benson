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
import android.content.res.XmlResourceParser;

import com.github.johnpersano.jenkins.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ResponseGenerator {

    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = "ResponseGenerator";

    private static final String QUERY = "Query";
    private static final String ATTRIBUTE_QUERY = "query";

    private static final String RESPONSE = "Response";
    private static final String ATTRIBUTE_REPLY = "reply";
    private static final String ATTRIBUTE_MOOD = "mood";
    private static final String ATTRIBUTE_SERIAL = "serial";

    private Context mContext;

    public ResponseGenerator(Context context) {

        this.mContext = context;

    }

    /* Convert responses defined in XML to HashMap. */
    @SuppressWarnings("ConstantConditions")
    public HashMap<String, ArrayList<JenkinsResponse>> getResponseMap() throws XmlPullParserException, IOException {

        /* HashMap will store a key (user speech) and an array of potential replies. */
        final HashMap<String, ArrayList<JenkinsResponse>> responseHashMap =
                new HashMap<String, ArrayList<JenkinsResponse>>();

        final XmlResourceParser xmlResourceParser = mContext.getResources().getXml(R.xml.generic_responses);

        int eventType = xmlResourceParser.getEventType();

        /* Reuse objects for efficiency. */
        String key = "";
        ArrayList<JenkinsResponse> jenkinsResponseArrayList = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if(eventType == XmlPullParser.START_TAG) {

                if(xmlResourceParser.getName().equalsIgnoreCase(QUERY)) {

                    key = xmlResourceParser.getAttributeValue(null, ATTRIBUTE_QUERY);

                    jenkinsResponseArrayList = new ArrayList<JenkinsResponse>();

                }

                if(xmlResourceParser.getName().equalsIgnoreCase(RESPONSE)) {

                    final JenkinsResponse jenkinsResponse = new JenkinsResponse();
                    jenkinsResponse.setReply(xmlResourceParser.getAttributeValue(null, ATTRIBUTE_REPLY));
                    jenkinsResponse.setMood(jenkinsResponse.convertStringToMood(xmlResourceParser.getAttributeValue(null, ATTRIBUTE_MOOD)));
                    jenkinsResponse.setSerial(xmlResourceParser.getAttributeValue(null, ATTRIBUTE_SERIAL));

                    jenkinsResponseArrayList.add(jenkinsResponse);

                }

            } else if (eventType == XmlPullParser.END_TAG) {

                if(xmlResourceParser.getName().equalsIgnoreCase(QUERY)) {

                    responseHashMap.put(key, jenkinsResponseArrayList);

                }

            }

            eventType = xmlResourceParser.next();

        }

        xmlResourceParser.close();

        return responseHashMap;

    }

}
