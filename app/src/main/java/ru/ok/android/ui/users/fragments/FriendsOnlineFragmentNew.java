package ru.ok.android.ui.users.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.TwoSourcesDataLoader.Result;
import android.support.v4.content.TwoSourcesDataLoaderHelper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.adapters.ScrollLoadRecyclerViewBlocker;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.scroll.ScrollListenerRecyclerSet;
import ru.ok.android.ui.fragments.base.BaseRefreshFragment;
import ru.ok.android.ui.users.fragments.data.FriendsAdapterBundle;
import ru.ok.android.ui.users.fragments.data.FriendsOnlineLoader;
import ru.ok.android.ui.users.fragments.data.adapter.FriendsGridAdapter;
import ru.ok.android.ui.users.fragments.data.adapter.FriendsListAdapter;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsFilterBaseStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsFilterGridStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsOnlineStrategy;
import ru.ok.android.ui.users.fragments.utils.UpdateAdapterHandler;
import ru.ok.android.ui.utils.DividerItemDecorator;
import ru.ok.android.ui.utils.ItemCountChangedDataObserver;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.widget.GridView;
import ru.ok.java.api.request.relatives.RelativesType;

public final class FriendsOnlineFragmentNew extends BaseRefreshFragment implements LoaderCallbacks<Result<FriendsAdapterBundle>> {
    private SmartEmptyViewAnimated emptyView;
    private UpdateAdapterHandler handler;
    private RecyclerView list;
    private ScrollListenerRecyclerSet listeners;
    private TwoSourcesDataLoaderHelper loaderHelper;
    private String query;
    private RelativesType relationType;
    private FriendsFilterBaseStrategy strategy;

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsOnlineFragmentNew.1 */
    class C13061 implements OnStubButtonClickListener {
        C13061() {
        }

        public void onStubButtonClick(Type type) {
            FriendsOnlineFragmentNew.this.onRefresh();
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsOnlineFragmentNew.2 */
    class C13072 extends ItemCountChangedDataObserver {
        C13072() {
        }

        public void onItemCountMayChange() {
            FriendsOnlineFragmentNew.this.updateEmptyViewVisibility();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Adapter adapter;
        View result = inflateViewLocalized(getLayoutId(), container, false);
        this.emptyView = (SmartEmptyViewAnimated) result.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(new C13061());
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        this.list = (RecyclerView) result.findViewById(2131624731);
        this.listeners = new ScrollListenerRecyclerSet();
        this.list.setOnScrollListener(this.listeners);
        this.list.setLayoutManager(new LinearLayoutManager(activity, 1, false));
        this.listeners.addListener(ScrollLoadRecyclerViewBlocker.forIdleAndTouchIdle());
        if (DeviceUtils.isSmall(activity)) {
            this.strategy = new FriendsOnlineStrategy(activity);
            adapter = new FriendsListAdapter(activity, 2130903210, this.strategy, 0, true, true);
        } else {
            int columns = GridView.getCountColumns(activity);
            this.strategy = new FriendsFilterGridStrategy(activity, columns, false);
            adapter = new FriendsGridAdapter(activity, columns, this.strategy, 0, 0, true);
            this.list.setBackgroundColor(getResources().getColor(2131493131));
        }
        this.strategy.setAdapter(adapter);
        if (this.relationType != null) {
            this.strategy.setRelationType(this.relationType);
        }
        if (!TextUtils.isEmpty(this.query)) {
            this.strategy.setQuery(this.query);
        }
        adapter.registerAdapterDataObserver(new C13072());
        this.list.setAdapter(adapter);
        this.list.addItemDecoration(new DividerItemDecorator(activity));
        this.handler = new UpdateAdapterHandler(adapter);
        return result;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.loaderHelper = new TwoSourcesDataLoaderHelper(this.emptyView, getLoaderManager(), 0, this, this.refreshProvider, true);
        this.loaderHelper.setEmptyViewType(Type.FRIENDS_ONLINE);
        this.loaderHelper.startLoader(false, true);
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.loaderHelper.reset();
    }

    protected int getLayoutId() {
        return 2130903221;
    }

    public void onRefresh() {
        this.emptyView.setState(State.LOADING);
        this.loaderHelper.startLoader(true, false);
    }

    public void onResume() {
        super.onResume();
        if (this.handler != null) {
            this.handler.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.handler != null) {
            this.handler.onPause();
        }
    }

    public Loader<Result<FriendsAdapterBundle>> onCreateLoader(int id, Bundle args) {
        return new FriendsOnlineLoader(getActivity(), this.loaderHelper.isPerformWebLoading(args));
    }

    public void onLoadFinished(Loader<Result<FriendsAdapterBundle>> loader, Result<FriendsAdapterBundle> result) {
        this.strategy.updateUsers((FriendsAdapterBundle) result.loadedData);
        this.loaderHelper.onLoadFinished(loader, result);
        updateEmptyViewVisibility();
        this.handler.onDataReceived();
    }

    public boolean isEmpty() {
        return this.strategy.getItemsCount() <= 0;
    }

    private void updateEmptyViewVisibility() {
        boolean isEmpty = isEmpty();
        this.emptyView.setVisibility(isEmpty ? 0 : 8);
        if (isVisible() && isEmpty) {
            appBarExpand();
        }
    }

    public void onLoaderReset(Loader<Result<FriendsAdapterBundle>> loader) {
    }

    public void setRelationType(RelativesType type) {
        this.relationType = type;
        if (this.strategy != null) {
            this.strategy.setRelationType(type);
            updateEmptyViewVisibility();
        }
    }

    public void setQuery(String query) {
        this.query = query;
        if (this.strategy != null) {
            this.loaderHelper.setEmptyViewType(TextUtils.isEmpty(query) ? Type.FRIENDS_ONLINE : Type.SEARCH);
            this.strategy.setQuery(query);
            updateEmptyViewVisibility();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624163)
    public void onFriendsUpdated(BusEvent event) {
        if (getActivity() != null) {
            this.loaderHelper.startLoader(false, false);
        }
    }
}
