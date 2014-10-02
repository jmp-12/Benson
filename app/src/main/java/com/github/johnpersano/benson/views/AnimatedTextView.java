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

package com.github.johnpersano.benson.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Class was modified from Stack Overflow post:
 * http://stackoverflow.com/questions/16895520/moving-textview-pixel-by-pixel-every-one-second-in-android
 */
public class AnimatedTextView extends TextView {

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 35;

    @SuppressWarnings("UnusedDeclaration")
    public AnimatedTextView(Context context) {
        super(context);

        /* Empty constructor. */

    }

    @SuppressWarnings("UnusedDeclaration")
    public AnimatedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /* Empty constructor. */

    }

    private Handler mHandler = new Handler();

    private Runnable characterAdder = new Runnable() {

        @Override
        public void run() {

            setText(mText.subSequence(0, mIndex++));

            if(mIndex <= mText.length()) {

                mHandler.postDelayed(characterAdder, mDelay);

            }

        }

    };

    /* This method will animate the text similar to a typewriter. */
    public void animateText(CharSequence text) {

        mText = text;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);

    }

}