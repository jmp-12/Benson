/**
 * Copyright 2011, Felix Palmer
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */

package com.github.johnpersano.benson.views.visualizer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


public class CircleRenderer extends Renderer {

    private Paint mPaint;
    private float modulation = 0;

    /**
     * Renders the audio data onto a pulsing circle
     */
    public CircleRenderer() {
        super();

        this.mPaint = new Paint();
        this.mPaint.setStrokeWidth(3f);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(Color.argb(255, 222, 92, 143));

    }

    @Override
    public void onRender(Canvas canvas, AudioData data, Rect rect) {

        for (int i = 0; i < data.bytes.length - 1; i++) {

            float[] cartPoint = {(float) i / (data.bytes.length - 1), rect.height() / 2 +
                    ((byte) (data.bytes[i] + 128)) * (rect.height() / 2) / 128};

            float[] polarPoint = toPolar(cartPoint, rect);

            mPoints[i * 4] = polarPoint[0];
            mPoints[i * 4 + 1] = polarPoint[1];

            float[] cartPoint2 = {(float) (i + 1) / (data.bytes.length - 1), rect.height() / 2 +
                    ((byte) (data.bytes[i + 1] + 128)) * (rect.height() / 2) / 128};

            float[] polarPoint2 = toPolar(cartPoint2, rect);

            mPoints[i * 4 + 2] = polarPoint2[0];
            mPoints[i * 4 + 3] = polarPoint2[1];

        }

        canvas.drawLines(mPoints, mPaint);

        // Controls the pulsing rate
        modulation += 0.045;

    }

    @Override
    public void onRender(Canvas canvas, FFTData data, Rect rect) {

        /* Do nothing, we only display audio data */

    }

    public void changeColor(int color) {

        mPaint.setColor(color);

    }

    private float[] toPolar(float[] cartesian, Rect rect) {

        double cX = rect.width() / 2;
        double cY = rect.height() / 2;
        double angle = (cartesian[0]) * 2 * Math.PI;
        float aggressive = 0.30f;

        double radius = ((rect.width() / 2) * (1 - aggressive) + aggressive * cartesian[1] / 2) * (1.2 + Math.sin(modulation)) / 2.2;
        radius += 25;
        return new float[]{ (float) (cX + radius * Math.sin(angle)),
                (float) (cY + radius * Math.cos(angle)) };

    }

}
