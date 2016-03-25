package ru.ok.android.ui.presents.interpolators;

import android.view.animation.Interpolator;

public class CardFlipOutInterpolator implements Interpolator {
    public float getInterpolation(float x) {
        float y;
        x *= 20.0f;
        if (x <= 7.0f) {
            y = ((2.04f * x) * x) + 2.0f;
        } else if (x <= 11.0f) {
            y = (-3.125f * x) + 121.875f;
        } else if (x <= 14.0f) {
            y = (4.166f * x) + 42.666f;
        } else if (x <= 17.0f) {
            y = (-x) + 114.0f;
        } else {
            y = x + 80.0f;
        }
        return y / 100.0f;
    }
}
