package ru.ok.android.ui.custom.mediacomposer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import ru.ok.android.utils.Logger;

public class LinearLayoutTransitionAnimator extends LayoutTransitionAnimator {
    private final int position1;
    private final int position2;
    private int preLayoutX1;
    private int preLayoutX2;
    private int preLayoutY1;
    private int preLayoutY2;

    public LinearLayoutTransitionAnimator(ViewGroup parent, int position1, int position2, long animationDurationMs) {
        super(parent, animationDurationMs);
        this.position1 = position1;
        this.position2 = position2;
    }

    protected void onStartAnimationPreLayout() {
        int childCount = this.parent.getChildCount();
        if (this.position2 < childCount) {
            if (this.position1 >= 0 && this.position1 < childCount) {
                this.parent.getChildAt(this.position1).getLocationOnScreen(xy);
                this.preLayoutX1 = xy[0];
                this.preLayoutY1 = xy[1];
            }
            if (this.position2 >= 0 && this.position2 < childCount) {
                this.parent.getChildAt(this.position2).getLocationOnScreen(xy);
                this.preLayoutX2 = xy[0];
                this.preLayoutY2 = xy[1];
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
        ArrayList<Animator> animatorsOut = new ArrayList(childCount);
        if (this.position1 >= 0 && this.position1 < childCount) {
            this.parent.getChildAt(this.position1).getLocationOnScreen(xy);
            int postLayoutX1 = xy[0];
            int postLayoutY1 = xy[1];
            Logger.m173d("preLayoutX1=%d preLayoutY1=%d postLayoutX1=%d postLayoutY1=%d", Integer.valueOf(this.preLayoutX1), Integer.valueOf(this.preLayoutY1), Integer.valueOf(postLayoutX1), Integer.valueOf(postLayoutY1));
            if (this.preLayoutX1 != postLayoutX1) {
                animateChildrenTranslation("translationX", 0, this.position1, this.preLayoutX1 - postLayoutX1, animatorsOut);
            }
            if (this.preLayoutY1 != postLayoutY1) {
                animateChildrenTranslation("translationY", 0, this.position1, this.preLayoutY1 - postLayoutY1, animatorsOut);
            }
        }
        if (this.position2 >= 0 && this.position2 < childCount) {
            this.parent.getChildAt(this.position2).getLocationOnScreen(xy);
            int postLayoutX2 = xy[0];
            int postLayoutY2 = xy[1];
            Logger.m173d("preLayoutX2=%d preLayoutY2=%d postLayoutX2=%d postLayoutY2=%d", Integer.valueOf(this.preLayoutX2), Integer.valueOf(this.preLayoutY2), Integer.valueOf(postLayoutX2), Integer.valueOf(postLayoutY2));
            if (this.preLayoutX2 != postLayoutX2) {
                animateChildrenTranslation("translationX", this.position2, childCount - 1, this.preLayoutX2 - postLayoutX2, animatorsOut);
            }
            if (this.preLayoutY2 != postLayoutY2) {
                animateChildrenTranslation("translationY", this.position2, childCount - 1, this.preLayoutY2 - postLayoutY2, animatorsOut);
            }
        }
        animatorSet.playTogether(animatorsOut);
        animatorSet.setDuration(this.animationDurationMs);
    }

    protected void animateChildrenTranslation(String translation, int fromPosition, int toPosition, int initialTranslation, ArrayList<Animator> animatorsOut) {
        for (int i = fromPosition; i <= toPosition; i++) {
            View childView = this.parent.getChildAt(i);
            if (translation == "translationX") {
                childView.setTranslationX((float) initialTranslation);
            } else if (translation == "translationY") {
                childView.setTranslationY((float) initialTranslation);
            }
            animatorsOut.add(ObjectAnimator.ofFloat(childView, translation, new float[]{0.0f}));
        }
    }
}
