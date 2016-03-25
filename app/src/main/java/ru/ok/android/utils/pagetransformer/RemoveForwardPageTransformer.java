package ru.ok.android.utils.pagetransformer;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class RemoveForwardPageTransformer implements PageTransformer {
    private final float scalingStart;

    public RemoveForwardPageTransformer() {
        this.scalingStart = 0.3f;
    }

    public void transformPage(View page, float position) {
        if (position > 0.0f) {
            int w = page.getWidth();
            float translationX = (((float) w) * (1.0f - position)) - ((float) w);
            float translationY = 0.0f;
            float alpha = Math.max(0.0f, 1.0f - Math.abs(position));
            float scaleFactor = 1.0f - (0.3f * position);
            if (position < 0.0f) {
                int h = page.getHeight();
                translationY = -((((float) h) * (1.0f - position)) - ((float) h));
            }
            page.setAlpha(alpha);
            page.setTranslationX(translationX);
            page.setTranslationY(translationY);
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            return;
        }
        page.setAlpha(1.0f);
        page.setTranslationX(0.0f);
        page.setTranslationY(0.0f);
        page.setScaleX(1.0f);
        page.setScaleY(1.0f);
    }
}
