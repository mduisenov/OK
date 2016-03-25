package ru.ok.android.ui.presents.interpolators;

import android.view.animation.Interpolator;

public class CardBounceInterpolator implements Interpolator {
    public float getInterpolation(float x) {
        float y;
        x *= 23.8f;
        if (x <= 12.7f) {
            y = ((-((x / 2.0f) - 3.2f)) * ((x / 2.0f) - 3.2f)) + 110.0f;
        } else if (x <= 19.0f) {
            y = (((x / 1.5f) - 10.7f) * ((x / 1.5f) - 10.7f)) + 95.0f;
        } else {
            y = ((-((x / 1.5f) - 14.45f)) * ((x / 1.5f) - 14.45f)) + 102.0f;
        }
        return y / 100.0f;
    }
}
