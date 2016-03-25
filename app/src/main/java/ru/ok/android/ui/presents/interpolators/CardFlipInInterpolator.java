package ru.ok.android.ui.presents.interpolators;

import android.view.animation.Interpolator;

public class CardFlipInInterpolator implements Interpolator {
    public float getInterpolation(float x) {
        x *= 8.0f;
        return ((((0.024f * x) * x) * x) * x) / 100.0f;
    }
}
