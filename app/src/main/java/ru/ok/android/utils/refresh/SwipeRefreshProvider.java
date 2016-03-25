package ru.ok.android.utils.refresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

public class SwipeRefreshProvider extends AbstractRefreshProvider implements OnRefreshListener {
    private boolean dummy;
    private SwipeRefreshLayout swipeRefreshLayout;

    public SwipeRefreshProvider(SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null) {
            this.swipeRefreshLayout = swipeRefreshLayout;
            swipeRefreshLayout.setOnRefreshListener(this);
            return;
        }
        this.dummy = true;
    }

    public boolean isRefreshing() {
        return this.swipeRefreshLayout != null ? this.swipeRefreshLayout.isRefreshing() : false;
    }

    public void refreshCompleted() {
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setRefreshEnabled(boolean enabled) {
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setEnabled(enabled);
        }
    }

    public void refreshStart() {
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(true);
        }
    }

    public void onRefresh() {
        if (this.refreshListener != null) {
            this.refreshListener.onRefresh();
        }
    }

    public void setRefreshing(boolean refreshing) {
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(refreshing);
        }
    }
}
