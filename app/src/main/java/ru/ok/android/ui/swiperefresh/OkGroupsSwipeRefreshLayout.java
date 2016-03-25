package ru.ok.android.ui.swiperefresh;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.coordinator.behaviors.AppBarGroupsOwnLayoutBehavior;

public class OkGroupsSwipeRefreshLayout extends OkSwipeRefreshLayout {
    private AppBarGroupsOwnLayoutBehavior groupsOwnAppBarBehavior;
    private AppBarLayout groupsOwnAppBarLayout;
    private int mActivePointerId;
    private float mInitialDownX;
    private float mInitialDownY;

    public OkGroupsSwipeRefreshLayout(Context context) {
        super(context);
        this.mActivePointerId = -1;
    }

    public OkGroupsSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mActivePointerId = -1;
    }

    public void setGroupsOwnAppBarLayout(AppBarLayout appBarLayout) {
        this.groupsOwnAppBarLayout = appBarLayout;
        this.groupsOwnAppBarBehavior = (AppBarGroupsOwnLayoutBehavior) ((LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
    }

    public boolean canChildScrollUp() {
        int scrollDx = this.groupsOwnAppBarBehavior.getTotalScrollDx();
        int scrollDy = this.groupsOwnAppBarBehavior.getTotalScrollDy();
        int prevScrollDx = this.groupsOwnAppBarBehavior.getPrevTotalScrollDx();
        int prevScrollDy = this.groupsOwnAppBarBehavior.getPrevTotalScrollDy();
        if (scrollDx == 0 && scrollDy == 0 && Math.abs(prevScrollDx) > Math.abs(prevScrollDy)) {
            return true;
        }
        if ((this.groupsOwnAppBarLayout != null && this.groupsOwnAppBarLayout.getTop() != 0) || super.canChildScrollUp()) {
            return true;
        }
        View view = findViewById(2131624846);
        if (view != null) {
            return ViewCompat.canScrollVertically(view, -1);
        }
        return false;
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0 && Math.abs(dxConsumed + dxUnconsumed) > Math.abs(dyConsumed + dyUnconsumed)) {
            dyConsumed += dyUnconsumed;
            dyUnconsumed = 0;
        }
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (MotionEventCompat.getActionMasked(ev)) {
            case RECEIVED_VALUE:
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                float initialDownY = getMotionEventY(ev, this.mActivePointerId);
                float initialDownX = getMotionEventX(ev, this.mActivePointerId);
                this.mInitialDownY = initialDownY;
                this.mInitialDownX = initialDownX;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                float y = getMotionEventY(ev, this.mActivePointerId);
                if (Math.abs(getMotionEventX(ev, this.mActivePointerId) - this.mInitialDownX) > Math.abs(y - this.mInitialDownY)) {
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float getMotionEventX(MotionEvent ev, int activePointerId) {
        int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1.0f;
        }
        return MotionEventCompat.getX(ev, index);
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1.0f;
        }
        return MotionEventCompat.getY(ev, index);
    }
}
