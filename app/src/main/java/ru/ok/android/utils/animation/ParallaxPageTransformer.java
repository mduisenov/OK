package ru.ok.android.utils.animation;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class ParallaxPageTransformer implements PageTransformer {
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        if (position <= -1.0f) {
            view.setAlpha(0.0f);
            view.setTranslationX(0.0f);
        } else if (position <= 0.0f) {
            view.setTranslationX(0.0f);
            view.setAlpha(1.0f);
        } else if (position < 1.0f) {
            view.setAlpha(1.0f);
            view.setTranslationX((float) (((double) (((float) pageWidth) * position)) * -0.25d));
        } else {
            view.setAlpha(0.0f);
            view.setTranslationX(0.0f);
        }
    }
}
