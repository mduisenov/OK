package ru.ok.android.ui;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import android.view.View;

public class CoordinatorLayoutNested extends CoordinatorLayout implements NestedScrollingChild {
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;

    public CoordinatorLayoutNested(Context context) {
        this(context, null);
    }

    public CoordinatorLayoutNested(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoordinatorLayoutNested(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        if (VERSION.SDK_INT >= 21) {
            super.setNestedScrollingEnabled(enabled);
        }
        if (this.mNestedScrollingChildHelper != null) {
            this.mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
        }
    }

    public boolean isNestedScrollingEnabled() {
        return this.mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int axes) {
        return this.mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    public void stopNestedScroll() {
        if (VERSION.SDK_INT >= 21) {
            super.stopNestedScroll();
        }
        this.mNestedScrollingChildHelper.stopNestedScroll();
    }

    public boolean hasNestedScrollingParent() {
        return this.mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return this.mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return this.mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(child, target, nestedScrollAxes) || this.mNestedScrollingChildHelper.startNestedScroll(nestedScrollAxes);
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    public void onStopNestedScroll(View target) {
        super.onStopNestedScroll(target);
        this.mNestedScrollingChildHelper.onStopNestedScroll(target);
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        this.mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (target.getId() == 2131624844 && Math.abs(dx) > Math.abs(dy) && Math.abs(dy) < getResources().getDimensionPixelSize(2131231201)) {
            dy = 0;
        }
        this.mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, null);
        int xConsumed = consumed[0];
        int yConsumed = consumed[1];
        super.onNestedPreScroll(target, dx - xConsumed, dy - yConsumed, consumed);
        consumed[0] = consumed[0] + xConsumed;
        consumed[1] = consumed[1] + yConsumed;
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return this.mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed) || super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY) || super.onNestedPreFling(target, velocityX, velocityY);
    }
}
