package ru.ok.android.ui.groups.fragments;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.ViewConfiguration;

public class GroupsHorizontalLinearLayoutManager extends LinearLayoutManager {
    private int minFlingVelocity;

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsHorizontalLinearLayoutManager.1 */
    class C09381 extends LinearSmoothScroller {
        C09381(Context x0) {
            super(x0);
        }

        public PointF computeScrollVectorForPosition(int targetPosition) {
            return GroupsHorizontalLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        protected int calculateTimeForScrolling(int dx) {
            return super.calculateTimeForScrolling(dx) * 10;
        }

        protected int getHorizontalSnapPreference() {
            return -1;
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsHorizontalLinearLayoutManager.2 */
    class C09392 extends LinearSmoothScroller {
        C09392(Context x0) {
            super(x0);
        }

        public PointF computeScrollVectorForPosition(int targetPosition) {
            return GroupsHorizontalLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        protected int calculateTimeForScrolling(int dx) {
            return super.calculateTimeForScrolling(dx) / 3;
        }

        protected int getHorizontalSnapPreference() {
            return -1;
        }
    }

    public GroupsHorizontalLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.minFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    public void adjusterSmoothScrollToPosition(RecyclerView recyclerView, int position) {
        LinearSmoothScroller linearSmoothScroller = new C09381(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public boolean canScrollVertically() {
        return true;
    }

    private int flingDeltaPosition(int velocityX, int pageCount) {
        int velocityDeltaMin = this.minFlingVelocity + 1;
        int deltaFlingPosition = (int) Math.floor(1.0d + Math.log(1.0d + ((((((double) velocityDeltaMin) + (Math.pow((double) (((float) (Math.min(16000, Math.abs(velocityX)) - velocityDeltaMin)) / ((float) (16000 - velocityDeltaMin))), 1.399999976158142d) * ((double) (16000 - velocityDeltaMin)))) - ((double) velocityDeltaMin)) * Math.exp((double) (pageCount - 1))) / ((double) (16000 - velocityDeltaMin)))));
        if (velocityX < 0) {
            return deltaFlingPosition * -1;
        }
        return deltaFlingPosition;
    }

    public boolean fling(RecyclerView recyclerView, int velocityX, int velocityY) {
        if (Math.abs(velocityX) < this.minFlingVelocity) {
            return false;
        }
        int lastVisiblePosition = findLastVisibleItemPosition();
        int firstVisiblePosition = findFirstVisibleItemPosition();
        int deltaFlingPosition = flingDeltaPosition(velocityX, (lastVisiblePosition - firstVisiblePosition) - 1);
        int position = velocityX > 0 ? Math.min(getItemCount() - 1, firstVisiblePosition + deltaFlingPosition) : Math.max(0, firstVisiblePosition + deltaFlingPosition);
        LinearSmoothScroller linearSmoothScroller = new C09392(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
        return true;
    }
}
