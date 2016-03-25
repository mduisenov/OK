package ru.ok.android.ui.fragments.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import ru.ok.android.utils.refresh.RefreshProvider;
import ru.ok.android.utils.refresh.RefreshProviderOnRefreshListener;
import ru.ok.android.utils.refresh.SwipeRefreshProvider;

public abstract class BaseRefreshFragment extends BaseFragment implements RefreshProviderOnRefreshListener {
    protected RefreshProvider refreshProvider;

    protected abstract int getLayoutId();

    public abstract void onRefresh();

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.refreshProvider = createRefreshProvider(view);
        if (this.refreshProvider != null) {
            this.refreshProvider.setOnRefreshListener(this);
        }
    }

    protected RefreshProvider createRefreshProvider(View view) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(2131624611);
        if (swipeRefreshLayout != null) {
            return new SwipeRefreshProvider(swipeRefreshLayout);
        }
        return null;
    }
}
