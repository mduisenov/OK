package ru.ok.android.ui.stream;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.scroll.ScrollListenerRecyclerSet;
import ru.ok.android.ui.fragments.base.BaseRefreshFragment;
import ru.ok.android.ui.utils.EmptyViewRecyclerDataObserver;

public abstract class BaseRefreshRecyclerFragment<TAdapter extends Adapter> extends BaseRefreshFragment {
    protected TAdapter adapter;
    protected SmartEmptyViewAnimated emptyView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected RecyclerView recyclerView;
    protected ScrollListenerRecyclerSet recyclerViewScrollListeners;
    protected SwipeRefreshLayout swipeRefreshLayout;

    /* renamed from: ru.ok.android.ui.stream.BaseRefreshRecyclerFragment.1 */
    class C12141 implements OnStubButtonClickListener {
        C12141() {
        }

        public void onStubButtonClick(Type type) {
            BaseRefreshRecyclerFragment.this.onRefresh();
        }
    }

    protected abstract TAdapter createRecyclerAdapter();

    protected int getLayoutId() {
        return 2130903363;
    }

    public boolean isAdapterManualProcessing() {
        return false;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentMainView = inflater.inflate(getLayoutId(), container, false);
        this.emptyView = (SmartEmptyViewAnimated) fragmentMainView.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(new C12141());
        this.recyclerView = (RecyclerView) fragmentMainView.findViewById(2131624731);
        this.swipeRefreshLayout = (SwipeRefreshLayout) fragmentMainView.findViewById(2131624611);
        this.recyclerViewScrollListeners = new ScrollListenerRecyclerSet();
        initRecyclerView();
        if (!isAdapterManualProcessing()) {
            initRecyclerAdapter();
            this.recyclerView.setAdapter(getRecyclerAdapter());
        }
        return fragmentMainView;
    }

    protected void initRecyclerAdapter() {
        this.adapter = createRecyclerAdapter();
        registerEmptyViewVisibilityAdapterObserver();
    }

    protected void registerEmptyViewVisibilityAdapterObserver() {
        this.adapter.registerAdapterDataObserver(new EmptyViewRecyclerDataObserver(this.emptyView, this.adapter));
    }

    protected Adapter getRecyclerAdapter() {
        return this.adapter;
    }

    protected void initRecyclerView() {
        this.recyclerView.setOnScrollListener(this.recyclerViewScrollListeners);
        this.recyclerView.setLayoutManager(getRecyclerViewLayoutManager());
    }

    protected LinearLayoutManager getRecyclerViewLayoutManager() {
        if (this.recyclerLayoutManager == null) {
            this.recyclerLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
        }
        return this.recyclerLayoutManager;
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.recyclerView != null) {
            outState.putParcelable("state_scroll_position", this.recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }
}
