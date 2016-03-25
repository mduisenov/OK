package ru.ok.android.utils.pagetransformer;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public abstract class ControllablePageTransformer implements PageTransformer {
    private PageTransformerCallback pageTransformerCallback;

    public interface PageTransformerCallback {
        boolean shouldApplyTransformation(View view, float f);
    }

    public abstract void applyTransformation(View view, float f);

    public ControllablePageTransformer(PageTransformerCallback callback) {
        this.pageTransformerCallback = callback;
    }

    public final void transformPage(View page, float position) {
        if (this.pageTransformerCallback == null || this.pageTransformerCallback.shouldApplyTransformation(page, position)) {
            applyTransformation(page, position);
        }
    }
}
