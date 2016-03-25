package ru.ok.android.utils.pagetransformer;

import android.view.View;
import ru.ok.android.utils.pagetransformer.ControllablePageTransformer.PageTransformerCallback;

public class ZoomOutPageTransformer extends ControllablePageTransformer {
    private static float MIN_ALPHA;
    private static float MIN_SCALE;

    static {
        MIN_SCALE = 0.85f;
        MIN_ALPHA = 0.75f;
    }

    public ZoomOutPageTransformer(PageTransformerCallback callback) {
        super(callback);
    }

    public void applyTransformation(View page, float position) {
        if (position == 0.0f) {
            clearTransformation(page);
            return;
        }
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();
        if (position <= -1.0f) {
            clearTransformation(page);
        } else if (position < 1.0f) {
            float scaleFactor = Math.max(MIN_SCALE, 1.0f - Math.abs(position));
            float vertMargin = (((float) pageHeight) * (1.0f - scaleFactor)) / 2.0f;
            float horzMargin = (((float) pageWidth) * (1.0f - scaleFactor)) / 2.0f;
            if (position < 0.0f) {
                page.setTranslationX(horzMargin - (vertMargin / 2.0f));
            } else {
                page.setTranslationX((-horzMargin) + (vertMargin / 2.0f));
            }
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setAlpha(MIN_ALPHA + (((scaleFactor - MIN_SCALE) / (1.0f - MIN_SCALE)) * (1.0f - MIN_ALPHA)));
        } else {
            clearTransformation(page);
        }
    }

    private void clearTransformation(View view) {
        view.setAlpha(1.0f);
        view.setTranslationX(0.0f);
    }
}
