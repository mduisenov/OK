package ru.ok.android.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.adapters.ImageBlockerRecyclerProvider;
import ru.ok.android.ui.stream.BaseRefreshRecyclerFragment;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.refresh.SwipeRefreshProvider;

public abstract class RefreshableContentRecyclerFragment<TAdapter extends Adapter & ImageBlockerRecyclerProvider, C> extends BaseRefreshRecyclerFragment<TAdapter> implements LoaderCallbacks<C> {
    private final boolean autoRefreshOnCreate;
    protected RefreshableRecyclerFragmentHelper refreshHelper;

    protected abstract RefreshableRecyclerFragmentHelper createRefreshHelper();

    protected abstract void onContentChanged();

    protected RefreshableContentRecyclerFragment() {
        this(true);
    }

    protected RefreshableContentRecyclerFragment(boolean autoRefreshOnCreate) {
        this.autoRefreshOnCreate = autoRefreshOnCreate;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentMainView = super.onCreateView(inflater, container, savedInstanceState);
        this.refreshHelper = createRefreshHelper();
        if (this.refreshHelper != null) {
            onPreSetAdapter(fragmentMainView);
            this.refreshHelper.onFragmentCreateView(fragmentMainView, null);
            initRefresh(fragmentMainView);
        }
        return fragmentMainView;
    }

    protected void initRefresh(View fragmentMainView) {
        this.refreshHelper.setRefreshProvider(new SwipeRefreshProvider((SwipeRefreshLayout) fragmentMainView.findViewById(2131624611)));
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.refreshHelper != null) {
            this.refreshHelper.onFragmentDestroyView();
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);
        if (this.autoRefreshOnCreate) {
            startRefresh(false);
        }
    }

    public void startRefresh(boolean manual) {
        Logger.m173d("[%s] manual=%s", getClass().getSimpleName(), Boolean.valueOf(manual));
        if (this.refreshHelper != null) {
            this.refreshHelper.startRefresh(manual);
        }
    }

    public void onRefresh() {
        if (this.refreshHelper != null) {
            this.refreshHelper.startRefresh(true);
        }
    }

    protected void onPreSetAdapter(View mainView) {
    }
}
