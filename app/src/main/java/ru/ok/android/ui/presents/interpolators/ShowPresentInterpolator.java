package ru.ok.android.ui.presents.interpolators;

import android.view.animation.Interpolator;

public class ShowPresentInterpolator implements Interpolator {
    public float getInterpolation(float x) {
        float y;
        x *= 14.0f;
        if (x <= 8.0f) {
            y = ((-5.1f * (x - 6.0f)) * (x - 6.0f)) + 110.0f;
        } else {
            y = ((-1.15f * (x - 11.0f)) * (x - 11.0f)) + 102.0f;
        }
        return y / 100.0f;
    }
}
