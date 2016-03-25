package ru.ok.android.ui.coordinator.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class GroupsShadowBehavior extends Behavior<View> {
    private View groupsPortalContent;
    private RecyclerView groupsPortalRecyclerView;
    private int shadowAppearOffset;
    private final int shadowHeight;

    public GroupsShadowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.shadowAppearOffset = context.getResources().getDimensionPixelSize(2131231016);
        this.shadowHeight = context.getResources().getDimensionPixelSize(2131231164);
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if (dependency.getId() == 2131624845) {
            this.groupsPortalContent = dependency;
        }
        return false;
    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & 2) != 0;
    }

    public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, View child, View dependency) {
        ensureViews(coordinatorLayout);
        processScrollOffsetRecyclerTop(child, this.groupsPortalContent.getTop(), this.groupsPortalRecyclerView.computeVerticalScrollOffset());
        return true;
    }

    private void ensureViews(CoordinatorLayout coordinatorLayout) {
        if (this.groupsPortalRecyclerView == null) {
            this.groupsPortalRecyclerView = (RecyclerView) coordinatorLayout.findViewById(2131624846);
        }
        if (this.groupsPortalContent == null) {
            this.groupsPortalContent = coordinatorLayout.findViewById(2131624845);
        }
    }

    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        ensureViews(coordinatorLayout);
        processScrollOffsetRecyclerTop(child, this.groupsPortalContent.getTop(), this.groupsPortalRecyclerView.computeVerticalScrollOffset());
    }

    public void processScrollOffsetRecyclerTop(View view, int top, int scrollOffset) {
        if (top <= this.shadowHeight / 2) {
            view.setAlpha((2.0f * ((float) top)) / ((float) this.shadowHeight));
            return;
        }
        float alpha = Math.min(((float) scrollOffset) / ((float) this.shadowAppearOffset), 1.0f);
        if (alpha > 0.0f) {
            view.setVisibility(0);
        }
        view.setAlpha(alpha);
    }

    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if (this.groupsPortalRecyclerView != null && this.groupsPortalContent != null) {
            processScrollOffsetRecyclerTop(child, this.groupsPortalContent.getTop(), this.groupsPortalRecyclerView.computeVerticalScrollOffset());
        }
    }
}
