package ru.ok.android.ui.coordinator.behaviors;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import ru.ok.android.utils.Utils;

public class TabbarBehavior extends Behavior<View> {
    public TabbarBehavior(Context context) {
        this(context, null);
    }

    public TabbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        child.setTranslationY(((float) child.getMeasuredHeight()) * Math.min(1.0f, ((float) Math.abs(dependency.getTop())) / ((float) dependency.getMeasuredHeight())));
        return true;
    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & 2) == 2;
    }

    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        onScrollStop(coordinatorLayout, child);
    }

    public boolean onTouchEvent(CoordinatorLayout coordinatorLayout, View child, MotionEvent ev) {
        if (ev.getAction() == 1) {
            onScrollStop(coordinatorLayout, child);
        }
        return true;
    }

    private void onScrollStop(CoordinatorLayout coordinatorLayout, View child) {
        int tabbarHeight = child.getMeasuredHeight();
        if (tabbarHeight != 0) {
            View appBar = Utils.findDirectChildById(coordinatorLayout, 2131624640);
            float toolbarHidedRatio = ((float) Math.abs(appBar.getTop())) / ((float) appBar.getMeasuredHeight());
            if (toolbarHidedRatio != 0.0f || child.getTranslationY() != 0.0f) {
                if (toolbarHidedRatio != 1.0f || child.getTranslationY() - ((float) tabbarHeight) != 0.0f) {
                    float to = 0.0f;
                    if (toolbarHidedRatio == 1.0f || child.getTranslationY() > ((float) (tabbarHeight / 2))) {
                        to = (float) tabbarHeight;
                    }
                    float from = child.getTranslationY();
                    ObjectAnimator animator = ObjectAnimator.ofFloat(child, "translationY", new float[]{to});
                    animator.setDuration((long) (((int) (200.0f * Math.abs(to - from))) / tabbarHeight));
                    animator.start();
                }
            }
        }
    }
}
