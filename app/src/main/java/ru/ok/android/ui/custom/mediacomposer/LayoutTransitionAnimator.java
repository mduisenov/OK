package ru.ok.android.ui.custom.mediacomposer;

import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import ru.ok.android.utils.UIUtils;

public abstract class LayoutTransitionAnimator implements OnGlobalLayoutListener {
    protected static final int[] xy;
    protected final long animationDurationMs;
    protected AnimatorListener listener;
    protected final ViewGroup parent;

    protected abstract void onStartAnimationPostLayout();

    protected abstract void onStartAnimationPreLayout();

    public LayoutTransitionAnimator(ViewGroup parent, long animationDurationMs) {
        this.parent = parent;
        this.animationDurationMs = animationDurationMs;
    }

    public void setListener(AnimatorListener listener) {
        this.listener = listener;
    }

    public final void startAnimation(AnimatorListener listener) {
        setListener(listener);
        startAnimation();
    }

    public final void startAnimation() {
        this.parent.getViewTreeObserver().addOnGlobalLayoutListener(this);
        onStartAnimationPreLayout();
    }

    public void onGlobalLayout() {
        UIUtils.removeOnGlobalLayoutListener(this.parent, this);
        onStartAnimationPostLayout();
    }

    protected void startAnimators(AnimatorSet animatorSet) {
        if (this.listener != null) {
            animatorSet.addListener(this.listener);
        }
        animatorSet.start();
    }

    static {
        xy = new int[2];
    }
}
