package ru.ok.android.ui.coordinator.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.Behavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AppBarGroupsOwnLayoutBehavior extends Behavior {
    private int pendingAction;
    private int prevTotalScrollDx;
    private int prevTotalScrollDy;
    private int totalScrollDx;
    private int totalScrollDy;

    /* renamed from: ru.ok.android.ui.coordinator.behaviors.AppBarGroupsOwnLayoutBehavior.1 */
    class C06001 implements Runnable {
        final /* synthetic */ AppBarLayout val$child;

        C06001(AppBarLayout appBarLayout) {
            this.val$child = appBarLayout;
        }

        public void run() {
            AppBarGroupsOwnLayoutBehavior.this.adjustAppBarPosition(this.val$child);
        }
    }

    public AppBarGroupsOwnLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        this.prevTotalScrollDx = this.totalScrollDx;
        this.prevTotalScrollDy = this.totalScrollDy;
        this.totalScrollDy = 0;
        this.totalScrollDx = 0;
        super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
        return true;
    }

    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        this.totalScrollDy += dy;
        this.totalScrollDx += dx;
    }

    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, View target) {
        super.onStopNestedScroll(coordinatorLayout, appBarLayout, target);
        if (this.pendingAction != 0) {
            this.pendingAction = 0;
        } else if (Math.abs(this.totalScrollDy) >= Math.abs(this.totalScrollDx)) {
            adjustAppBarPosition(appBarLayout);
        }
    }

    private void adjustAppBarPosition(AppBarLayout appBarLayout) {
        boolean z = false;
        float hidedHeight = (float) Math.abs(appBarLayout.getTop());
        float fullHeight = (float) appBarLayout.getMeasuredHeight();
        if (hidedHeight != 0.0f && hidedHeight != fullHeight) {
            float hidedRatio = hidedHeight / fullHeight;
            float visibleRatio = 1.0f - hidedRatio;
            if (this.totalScrollDy >= 0) {
                boolean wantToHide;
                if (((double) hidedRatio) > 0.25d) {
                    wantToHide = true;
                } else {
                    wantToHide = false;
                }
                if (!wantToHide) {
                    z = true;
                }
                appBarLayout.setExpanded(z, true);
            } else if (this.totalScrollDy < 0) {
                boolean wantToExpand;
                if (((double) visibleRatio) > 0.15d) {
                    wantToExpand = true;
                } else {
                    wantToExpand = false;
                }
                appBarLayout.setExpanded(wantToExpand, true);
            }
        }
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return false;
    }

    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        if (!consumed) {
            return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
        }
        child.post(new C06001(child));
        return false;
    }

    public int getTotalScrollDy() {
        return this.totalScrollDy;
    }

    public int getTotalScrollDx() {
        return this.totalScrollDx;
    }

    public int getPrevTotalScrollDy() {
        return this.prevTotalScrollDy;
    }

    public int getPrevTotalScrollDx() {
        return this.prevTotalScrollDx;
    }

    public void setPendingAction(int pendingAction) {
        this.pendingAction = pendingAction;
    }
}
