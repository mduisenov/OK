package ru.ok.android.ui.custom.mediacomposer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

public class SimpleLayoutTransitionAnimator extends LayoutTransitionAnimator {
    private int[][] preLayoutXY;
    private int preLayoutXYOffset;

    public SimpleLayoutTransitionAnimator(ViewGroup parent, long animationDurationMs) {
        super(parent, animationDurationMs);
    }

    public SimpleLayoutTransitionAnimator(ViewGroup parent, long animationDurationMs, int[][] initialScreenPositions) {
        this(parent, animationDurationMs, initialScreenPositions, 0);
    }

    public SimpleLayoutTransitionAnimator(ViewGroup parent, long animationDurationMs, int[][] initialScreenPositions, int initialScreenPositionsOffset) {
        this(parent, animationDurationMs);
        this.preLayoutXY = initialScreenPositions;
        this.preLayoutXYOffset = initialScreenPositionsOffset;
    }

    protected boolean shouldAnimateChild(int position) {
        return true;
    }

    protected void onStartAnimationPreLayout() {
        if (this.preLayoutXY == null) {
            int childCount = this.parent.getChildCount();
            this.preLayoutXY = new int[childCount][];
            this.preLayoutXYOffset = 0;
            for (int i = 0; i < childCount; i++) {
                View childView = this.parent.getChildAt(i);
                this.preLayoutXY[i] = new int[2];
                childView.getLocationOnScreen(this.preLayoutXY[i]);
            }
        }
    }

    protected void onStartAnimationPostLayout() {
        AnimatorSet animatorSet = new AnimatorSet();
        createAnimatorsPostLayout(animatorSet);
        startAnimators(animatorSet);
    }

    protected void createAnimatorsPostLayout(AnimatorSet animatorSet) {
        int childCount = this.parent.getChildCount();
        int childCountPreLayout = this.preLayoutXY.length - this.preLayoutXYOffset;
        long delay = 0;
        int i = 0;
        while (i < childCount && i < childCountPreLayout) {
            if (shouldAnimateChild(i)) {
                String str;
                Animator firstAnimator;
                Animator secondAnimator;
                View childView = this.parent.getChildAt(i);
                childView.getLocationOnScreen(xy);
                int postLayoutX = xy[0];
                int postLayoutY = xy[1];
                int preLayoutX = this.preLayoutXY[this.preLayoutXYOffset + i][0];
                int preLayoutY = this.preLayoutXY[this.preLayoutXYOffset + i][1];
                Animator animatorX = null;
                Animator animatorY = null;
                if (preLayoutX != postLayoutX) {
                    childView.setTranslationX((float) (preLayoutX - postLayoutX));
                    str = "translationX";
                    animatorX = ObjectAnimator.ofFloat(childView, r17, new float[]{0.0f});
                    animatorX.setDuration(this.animationDurationMs);
                }
                if (preLayoutY != postLayoutY) {
                    childView.setTranslationY((float) (preLayoutY - postLayoutY));
                    str = "translationY";
                    animatorY = ObjectAnimator.ofFloat(childView, r17, new float[]{0.0f});
                    animatorY.setDuration(this.animationDurationMs);
                }
                if (animatorX == null) {
                    firstAnimator = animatorY;
                } else {
                    firstAnimator = animatorX;
                }
                if (animatorX == null) {
                    secondAnimator = null;
                } else {
                    secondAnimator = animatorY;
                }
                if (firstAnimator != null) {
                    animatorSet.play(firstAnimator).after(delay);
                    if (secondAnimator != null) {
                        animatorSet.play(firstAnimator).with(secondAnimator);
                    }
                    delay += 50;
                }
            }
            i++;
        }
    }
}
