package ru.ok.android.utils.refresh;

import ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout;
import ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout.OnRefreshListener;

public class SwipeUpRefreshProvider extends AbstractRefreshProvider implements OnRefreshListener {
    private SwipeUpRefreshLayout swipeRefreshLayout;

    public SwipeUpRefreshProvider(SwipeUpRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(this);
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
