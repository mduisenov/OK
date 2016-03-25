package ru.ok.android.ui.fragments.handlers;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout;
import ru.ok.android.utils.refresh.RefreshProvider;
import ru.ok.android.utils.refresh.RefreshProviderOnRefreshListener;
import ru.ok.android.utils.refresh.SwipeRefreshProvider;
import ru.ok.android.utils.refresh.SwipeUpRefreshProvider;

public abstract class RefreshViewHandler extends BaseViewHandler implements RefreshProviderOnRefreshListener {
    protected RefreshProvider refreshProvider;

    protected void onViewCreated(LayoutInflater inflater, View view) {
        this.refreshProvider = createRefreshProvider(view);
        if (this.refreshProvider != null) {
            this.refreshProvider.setOnRefreshListener(this);
        }
    }

    private RefreshProvider createRefreshProvider(View view) {
        View swipeRefresh = view.findViewById(getSwipeRefreshId() == -1 ? 2131624611 : getSwipeRefreshId());
        if (swipeRefresh == null) {
            return null;
        }
        if (swipeRefresh instanceof SwipeRefreshLayout) {
            return new SwipeRefreshProvider((SwipeRefreshLayout) swipeRefresh);
        }
        if (swipeRefresh instanceof SwipeUpRefreshLayout) {
            return new SwipeUpRefreshProvider((SwipeUpRefreshLayout) swipeRefresh);
        }
        return null;
    }

    protected int getSwipeRefreshId() {
        return -1;
    }
}
