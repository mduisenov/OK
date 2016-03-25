package ru.ok.android.ui.toolbar;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ViewMarginTranslationListener {
    private final Animation hideAnimation;
    private MarginLayoutParams params;
    private Animation showAnimation;
    private float translationSize;
    private View view;

    /* renamed from: ru.ok.android.ui.toolbar.ViewMarginTranslationListener.1 */
    static class C12861 extends Animation {
        final /* synthetic */ int val$toMargin;
        final /* synthetic */ View val$view;

        C12861(View view, int i) {
            this.val$view = view;
            this.val$toMargin = i;
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            MarginLayoutParams params = (MarginLayoutParams) this.val$view.getLayoutParams();
            params.topMargin = (int) (((float) this.val$toMargin) * interpolatedTime);
            this.val$view.setLayoutParams(params);
        }
    }

    public ViewMarginTranslationListener(View v) {
        this.view = v;
        this.hideAnimation = createMarginAnimation(this.view, 0);
        this.showAnimation = createMarginAnimation(this.view, 0);
    }

    public void setTranslation(float translation) {
        stopAnimation();
        this.params.topMargin = (int) translation;
        this.view.setLayoutParams(this.params);
    }

    public void setTranslationSize(float max) {
        LayoutParams params = this.view.getLayoutParams();
        if (params instanceof MarginLayoutParams) {
            this.params = (MarginLayoutParams) params;
        } else if (params != null) {
            this.params = new MarginLayoutParams(params.width, params.height);
        } else {
            this.params = new MarginLayoutParams(-2, 0);
        }
        this.params.topMargin = (int) max;
        this.view.setLayoutParams(this.params);
        this.translationSize = max;
        this.showAnimation = createMarginAnimation(this.view, (int) this.translationSize);
    }

    public void animateHide(int fullDuration) {
        stopAnimation();
        this.hideAnimation.setDuration((long) ((int) ((((float) this.params.topMargin) / this.translationSize) * ((float) fullDuration))));
        this.view.setAnimation(this.hideAnimation);
    }

    public void animateShow(int fullDuration) {
        stopAnimation();
        this.showAnimation.setDuration((long) ((int) (((this.translationSize - ((float) this.params.topMargin)) / this.translationSize) * ((float) fullDuration))));
        this.view.setAnimation(this.showAnimation);
    }

    public void stopAnimation() {
        this.view.clearAnimation();
    }

    public static Animation createMarginAnimation(View view, int toMargin) {
        return new C12861(view, toMargin);
    }
}
