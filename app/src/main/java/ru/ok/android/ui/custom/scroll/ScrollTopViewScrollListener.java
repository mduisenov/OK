package ru.ok.android.ui.custom.scroll;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public final class ScrollTopViewScrollListener implements OnScrollListener {
    private final ScrollTopViewScrollListenerCallback callback;
    private final ScrollTopView scrollTopView;

    public interface ScrollTopViewScrollListenerCallback {
        boolean isAllEventRead(AbsListView absListView);
    }

    public ScrollTopViewScrollListener(ScrollTopView scrollTopView, ScrollTopViewScrollListenerCallback callback) {
        this.scrollTopView = scrollTopView;
        this.callback = callback;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean z = true;
        if (view.getCount() > 0) {
            int visibleCount = visibleItemCount;
            if (visibleCount > 0) {
                boolean z2;
                int invisibleBottomViews = (totalItemCount - firstVisibleItem) - visibleCount;
                ScrollTopView scrollTopView = this.scrollTopView;
                if (invisibleBottomViews >= 10) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (invisibleBottomViews > 5) {
                    z = false;
                }
                scrollTopView.onScroll(z2, z);
                if (this.callback != null && this.callback.isAllEventRead(view)) {
                    this.scrollTopView.clearEvents(false);
                }
            }
        }
    }
}
