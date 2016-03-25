package ru.ok.android.ui.custom.scroll;

import android.database.DataSetObserver;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class BaseScrollListener extends DataSetObserver implements OnScrollListener {
    private final DeltaListScrollListener listener;
    private int prevFirstVisibleItem;
    private int prevFirstVisibleItemTop;
    private int prevLastVisibleItem;
    private int prevLastVisibleItemTop;
    private long time;

    public BaseScrollListener(DeltaListScrollListener listener) {
        this.time = -1;
        this.listener = listener;
    }

    public void onChanged() {
        super.onChanged();
        this.time = -1;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (visibleItemCount > 0) {
            long now = AnimationUtils.currentAnimationTimeMillis();
            int lastVisibleItem = (firstVisibleItem + visibleItemCount) - 1;
            if (this.time != -1) {
                int distance;
                if (this.prevFirstVisibleItem >= firstVisibleItem && this.prevFirstVisibleItem <= lastVisibleItem) {
                    distance = view.getChildAt(this.prevFirstVisibleItem - firstVisibleItem).getTop() - this.prevFirstVisibleItemTop;
                } else if (this.prevLastVisibleItem < firstVisibleItem || this.prevLastVisibleItem > lastVisibleItem) {
                    int heightSum = 0;
                    for (int i = 0; i < visibleItemCount; i++) {
                        heightSum += view.getChildAt(i).getHeight();
                    }
                    distance = (heightSum / visibleItemCount) * (this.prevFirstVisibleItem - firstVisibleItem);
                } else {
                    distance = view.getChildAt(this.prevLastVisibleItem - firstVisibleItem).getTop() - this.prevLastVisibleItemTop;
                }
                this.listener.onListScroll(distance, firstVisibleItem, totalItemCount - visibleItemCount, visibleItemCount);
            }
            this.prevFirstVisibleItem = firstVisibleItem;
            this.prevFirstVisibleItemTop = view.getChildAt(0).getTop();
            this.prevLastVisibleItem = lastVisibleItem;
            this.prevLastVisibleItemTop = view.getChildAt(visibleItemCount - 1).getTop();
            this.time = now;
        }
    }
}
