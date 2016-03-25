package ru.ok.android.ui.users.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.TwoSourcesDataLoader.Result;
import android.support.v4.content.TwoSourcesDataLoaderHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
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
import ru.ok.android.ui.users.fragments.data.FriendsLoader;
import ru.ok.android.ui.users.fragments.data.FriendsLoaderBundle;
import ru.ok.android.ui.users.fragments.data.FriendsRelationsAdapter.RelationItem;
import ru.ok.android.ui.users.fragments.data.FriendsSearchBundle;
import ru.ok.android.ui.users.fragments.data.FriendsSearchLoader;
import ru.ok.android.ui.users.fragments.data.FriendsSuggestionsAdapter;
import ru.ok.android.ui.users.fragments.data.FriendsSuggestionsLoader;
import ru.ok.android.ui.users.fragments.data.adapter.FriendsGridAdapter;
import ru.ok.android.ui.users.fragments.data.adapter.FriendsListAdapter;
import ru.ok.android.ui.users.fragments.data.adapter.FriendsListWithHeadersAdapter;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsArrayBaseStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsArrayGridStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsArrayListStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsFilterBaseStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsFilterGridStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsFilterListStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsSearchBaseStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsSearchGridStrategy;
import ru.ok.android.ui.users.fragments.data.strategy.FriendsSearchListStrategy;
import ru.ok.android.ui.users.fragments.utils.UpdateAdapterHandler;
import ru.ok.android.ui.utils.HideTabbarItemDecorator;
import ru.ok.android.ui.utils.LoadItemAdapter;
import ru.ok.android.ui.utils.RecyclerMergeHeaderAdapter;
import ru.ok.android.ui.utils.StickyHeaderItemDecorator;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.android.widget.GridView;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo;

public final class FriendsFragmentNew extends BaseRefreshFragment implements OnStubButtonClickListener {
    private Activity activity;
    private int alphabetAdditionalOffset;
    private StickyHeaderItemDecorator alphabetDecorator;
    private final OnScrollListener autoLoadingListener;
    private FriendsArrayBaseStrategy bestFriendsStrategy;
    SmartEmptyViewAnimated emptyView;
    private final LoaderCallbacks<Result<FriendsLoaderBundle>> friendsCallback;
    private final LoaderCallbacks<FriendsSearchBundle> friendsSearchCallback;
    private FriendsFilterBaseStrategy friendsStrategy;
    private final LoaderCallbacks<List<UserInfo>> friendsSuggestionsCallback;
    private UpdateAdapterHandler handler;
    private RecyclerView list;
    private ScrollListenerRecyclerSet listeners;
    private LoadItemAdapter loadItemAdapter;
    private TwoSourcesDataLoaderHelper loaderHelper;
    private String query;
    private RelativesType relationType;
    private RelationsListener relationsListener;
    private final Handler searchHandler;
    private FriendsSearchBaseStrategy searchStrategy;
    private int sectionHeaderHeight;
    private StickyHeaderItemDecorator sectionHeaderItemDecorator;
    private FriendsSuggestionsAdapter suggestionsAdapter;
    private int toolbarHeight;

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsFragmentNew.1 */
    class C12981 implements LoaderCallbacks<List<UserInfo>> {
        C12981() {
        }

        public Loader<List<UserInfo>> onCreateLoader(int id, Bundle args) {
            return new FriendsSuggestionsLoader(FriendsFragmentNew.this.getContext(), FriendsFragmentNew.this.getParticipantsAvatarsCount());
        }

        public void onLoadFinished(Loader<List<UserInfo>> loader, List<UserInfo> data) {
            if (data != null && !data.isEmpty()) {
                int navBarHeight;
                FriendsFragmentNew.this.suggestionsAdapter.updateUsers(data);
                SmartEmptyViewAnimated smartEmptyViewAnimated = FriendsFragmentNew.this.emptyView;
                if (FriendsFragmentNew.this.suggestionsAdapter.getItemCount() > 0) {
                    navBarHeight = DimenUtils.getNavBarHeight(FriendsFragmentNew.this.getContext());
                } else {
                    navBarHeight = 0;
                }
                smartEmptyViewAnimated.setPadding(0, navBarHeight, 0, 0);
            }
        }

        public void onLoaderReset(Loader<List<UserInfo>> loader) {
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsFragmentNew.2 */
    class C12992 implements LoaderCallbacks<Result<FriendsLoaderBundle>> {
        C12992() {
        }

        public Loader<Result<FriendsLoaderBundle>> onCreateLoader(int id, Bundle args) {
            return new FriendsLoader(FriendsFragmentNew.this.getActivity(), FriendsFragmentNew.this.loaderHelper.isPerformWebLoading(args));
        }

        public void onLoadFinished(Loader<Result<FriendsLoaderBundle>> loader, Result<FriendsLoaderBundle> result) {
            FriendsFragmentNew.this.bestFriendsStrategy.updateUsers(((FriendsLoaderBundle) result.loadedData).bestFriends);
            FriendsFragmentNew.this.friendsStrategy.updateSubRelations(((FriendsLoaderBundle) result.loadedData).subRelations);
            FriendsFragmentNew.this.friendsStrategy.updateUsers(((FriendsLoaderBundle) result.loadedData).adapterBundle);
            FriendsSearchLoader searchLoader = (FriendsSearchLoader) FriendsFragmentNew.this.getLoaderManager().getLoader(2);
            if (searchLoader != null) {
                if (FriendsFragmentNew.this.friendsStrategy.isThatQuery(searchLoader.query)) {
                    FriendsFragmentNew.this.friendsStrategy.injectFilteredFriends(searchLoader.getFriends());
                }
            }
            if (FriendsFragmentNew.this.relationsListener != null) {
                FriendsFragmentNew.this.relationsListener.updateRelations(((FriendsLoaderBundle) result.loadedData).relationCounts);
            }
            FriendsFragmentNew.this.loaderHelper.onLoadFinished(loader, result);
            FriendsFragmentNew.this.handler.onDataReceived();
            FriendsFragmentNew.this.updateDecorationsVisibility();
        }

        public void onLoaderReset(Loader<Result<FriendsLoaderBundle>> loader) {
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsFragmentNew.3 */
    class C13003 implements LoaderCallbacks<FriendsSearchBundle> {
        C13003() {
        }

        public Loader<FriendsSearchBundle> onCreateLoader(int id, Bundle args) {
            return new FriendsSearchLoader(FriendsFragmentNew.this.getActivity(), args.getString("search_query"));
        }

        public void onLoadFinished(Loader<FriendsSearchBundle> loader, @Nullable FriendsSearchBundle data) {
            List list;
            List list2 = null;
            FriendsSearchBaseStrategy access$800 = FriendsFragmentNew.this.searchStrategy;
            if (data != null) {
                list = data.users;
            } else {
                list = null;
            }
            access$800.updateUsers(list);
            FriendsFilterBaseStrategy access$400 = FriendsFragmentNew.this.friendsStrategy;
            if (data != null) {
                list2 = data.friends;
            }
            access$400.injectFilteredFriends(list2);
            FriendsFragmentNew.this.loadItemAdapter.setLoading(false);
            FriendsFragmentNew.this.emptyView.setState(State.LOADED);
        }

        public void onLoaderReset(Loader<FriendsSearchBundle> loader) {
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsFragmentNew.4 */
    class C13014 extends Handler {
        C13014() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVED_VALUE:
                    String query = msg.obj;
                    Bundle bundle = new Bundle();
                    bundle.putString("search_query", query);
                    FriendsFragmentNew.this.loadItemAdapter.setLoading(true);
                    FriendsFragmentNew.this.getLoaderManager().restartLoader(2, bundle, FriendsFragmentNew.this.friendsSearchCallback);
                    FriendsFragmentNew.this.listeners.addListener(FriendsFragmentNew.this.autoLoadingListener);
                    FriendsFragmentNew.this.emptyView.setState(State.LOADING);
                default:
                    throw new IllegalArgumentException("Unknown msg.what = " + msg.what);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsFragmentNew.5 */
    class C13025 extends OnScrollListener {
        C13025() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!FriendsFragmentNew.this.loadItemAdapter.isLoading() && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - 3) {
                FriendsSearchLoader loader = (FriendsSearchLoader) FriendsFragmentNew.this.getLoaderManager().getLoader(2);
                if (loader != null && loader.isHasMore()) {
                    loader.forceLoad();
                    FriendsFragmentNew.this.loadItemAdapter.setLoading(true);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FriendsFragmentNew.6 */
    class C13036 extends AdapterDataObserver {
        final /* synthetic */ RecyclerMergeHeaderAdapter val$mergeAdapter;

        C13036(RecyclerMergeHeaderAdapter recyclerMergeHeaderAdapter) {
            this.val$mergeAdapter = recyclerMergeHeaderAdapter;
        }

        public void onChanged() {
            boolean isEmpty;
            int i = 0;
            if (this.val$mergeAdapter.getItemCount() == 0 || this.val$mergeAdapter.getItemCount() == FriendsFragmentNew.this.suggestionsAdapter.getItemCount()) {
                isEmpty = true;
            } else {
                isEmpty = false;
            }
            boolean hasSearchRequest = FriendsFragmentNew.this.searchHandler.hasMessages(0);
            SmartEmptyViewAnimated smartEmptyViewAnimated = FriendsFragmentNew.this.emptyView;
            if (!isEmpty || hasSearchRequest) {
                i = 8;
            }
            smartEmptyViewAnimated.setVisibility(i);
            if (FriendsFragmentNew.this.isVisible() && FriendsFragmentNew.this.emptyView.getVisibility() == 0) {
                FriendsFragmentNew.this.appBarExpand();
            }
        }
    }

    public interface RelationsListener {
        void updateRelations(List<RelationItem> list);
    }

    public FriendsFragmentNew() {
        this.friendsSuggestionsCallback = new C12981();
        this.friendsCallback = new C12992();
        this.friendsSearchCallback = new C13003();
        this.searchHandler = new C13014();
        this.autoLoadingListener = new C13025();
    }

    private int getParticipantsAvatarsCount() {
        return DeviceUtils.isSmall(getActivity()) ? 3 : 7;
    }

    private void updateDecorationsVisibility() {
        boolean queryEmpty = TextUtils.isEmpty(this.query);
        boolean hasBestFriends = this.bestFriendsStrategy.getItemsCount() > 0;
        this.list.removeItemDecoration(this.sectionHeaderItemDecorator);
        if (!queryEmpty || hasBestFriends) {
            this.list.addItemDecoration(this.sectionHeaderItemDecorator);
            this.alphabetDecorator.setStickyPermanentOffset(this.sectionHeaderHeight);
            return;
        }
        this.alphabetDecorator.setStickyPermanentOffset(this.alphabetAdditionalOffset);
    }

    private void updateBestFriendsVisibility() {
        boolean z = true;
        boolean queryEmpty = TextUtils.isEmpty(this.query);
        boolean noRelationType;
        if (this.relationType == null || this.relationType == RelativesType.ALL) {
            noRelationType = true;
        } else {
            noRelationType = false;
        }
        FriendsArrayBaseStrategy friendsArrayBaseStrategy = this.bestFriendsStrategy;
        if (!(queryEmpty && noRelationType)) {
            z = false;
        }
        friendsArrayBaseStrategy.setEnabled(z);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Adapter friendsAdapter;
        Adapter bestFriendsAdapter;
        Adapter searchAdapter;
        View result = inflateViewLocalized(getLayoutId(), container, false);
        this.emptyView = (SmartEmptyViewAnimated) result.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
        this.list = (RecyclerView) result.findViewById(2131624731);
        this.listeners = new ScrollListenerRecyclerSet();
        this.list.setOnScrollListener(this.listeners);
        ScrollLoadRecyclerViewBlocker handleBlocker = ScrollLoadRecyclerViewBlocker.forIdleAndTouchIdle();
        this.listeners.addListener(handleBlocker);
        this.activity = getActivity();
        boolean isSmall = DeviceUtils.isSmall(this.activity);
        if (isSmall) {
            this.friendsStrategy = new FriendsFilterListStrategy(this.activity);
            friendsAdapter = new FriendsListWithHeadersAdapter(this.activity, 2130903209, this.friendsStrategy, 2131166244, true, true);
            this.bestFriendsStrategy = new FriendsArrayListStrategy(this.activity);
            bestFriendsAdapter = new FriendsListAdapter(this.activity, 2130903209, this.bestFriendsStrategy, 2131165431, true, true);
            this.searchStrategy = new FriendsSearchListStrategy();
            searchAdapter = new FriendsListAdapter(this.activity, 2130903209, this.searchStrategy, 2131166480, false, false);
        } else {
            int columns = GridView.getCountColumns(this.activity);
            int paddingLeft = (int) Utils.dipToPixels(this.activity, 40.0f);
            this.friendsStrategy = new FriendsFilterGridStrategy(this.activity, columns, true);
            Adapter friendsGridAdapter = new FriendsGridAdapter(this.activity, columns, this.friendsStrategy, 2131166244, paddingLeft, true);
            this.bestFriendsStrategy = new FriendsArrayGridStrategy(columns);
            friendsGridAdapter = new FriendsGridAdapter(this.activity, columns, this.bestFriendsStrategy, 2131165431, paddingLeft, true);
            this.searchStrategy = new FriendsSearchGridStrategy(columns);
            friendsGridAdapter = new FriendsGridAdapter(this.activity, columns, this.searchStrategy, 2131166480, paddingLeft, false);
            this.list.setBackgroundColor(getResources().getColor(2131493131));
        }
        this.friendsStrategy.setAdapter(friendsAdapter);
        this.bestFriendsStrategy.setAdapter(bestFriendsAdapter);
        this.searchStrategy.setAdapter(searchAdapter);
        RecyclerMergeHeaderAdapter recyclerMergeHeaderAdapter = new RecyclerMergeHeaderAdapter(true, isSmall);
        Adapter friendsSuggestionsAdapter = new FriendsSuggestionsAdapter(this.activity, handleBlocker, getParticipantsAvatarsCount(), isSmall);
        this.suggestionsAdapter = friendsSuggestionsAdapter;
        recyclerMergeHeaderAdapter = recyclerMergeHeaderAdapter.addAdapter(friendsSuggestionsAdapter).addAdapter(bestFriendsAdapter).addAdapter(friendsAdapter).addAdapter(searchAdapter);
        friendsSuggestionsAdapter = new LoadItemAdapter(this.activity);
        this.loadItemAdapter = friendsSuggestionsAdapter;
        RecyclerMergeHeaderAdapter mergeAdapter = recyclerMergeHeaderAdapter.addAdapter(friendsSuggestionsAdapter);
        mergeAdapter.registerAdapterDataObserver(new C13036(mergeAdapter));
        this.friendsStrategy.setRelationType(this.relationType);
        if (!TextUtils.isEmpty(this.query)) {
            setQuery(this.query, true);
        }
        this.list.setAdapter(mergeAdapter);
        this.handler = new UpdateAdapterHandler(mergeAdapter);
        this.list.setLayoutManager(new LinearLayoutManager(this.activity, 1, false));
        this.sectionHeaderItemDecorator = new StickyHeaderItemDecorator(this.list, mergeAdapter, mergeAdapter.getAdapterSectionHeaderPrivider());
        this.alphabetDecorator = new StickyHeaderItemDecorator(this.list, mergeAdapter, mergeAdapter);
        this.list.addItemDecoration(this.alphabetDecorator);
        if (!isSmall) {
            this.alphabetDecorator.setSectionsGap((int) Utils.dipToPixels(this.activity, 24.0f));
        }
        if (isSmall) {
            this.list.addItemDecoration(new HideTabbarItemDecorator(this.activity));
        }
        this.toolbarHeight = DimenUtils.getToolbarHeight(this.activity);
        this.alphabetAdditionalOffset = (int) Utils.dipToPixels(this.activity, 16.0f);
        this.alphabetDecorator.setStickyPermanentOffset(this.alphabetAdditionalOffset);
        this.sectionHeaderHeight = this.activity.getResources().getDimensionPixelOffset(2131231008);
        return result;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.loaderHelper = new TwoSourcesDataLoaderHelper(this.emptyView, getLoaderManager(), 0, this.friendsCallback, this.refreshProvider, true);
        this.loaderHelper.setEmptyViewType(Type.FRIENDS_LIST);
        this.loaderHelper.startLoader(false, true);
        getLoaderManager().initLoader(1, null, this.friendsSuggestionsCallback);
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
        getLoaderManager().restartLoader(1, null, this.friendsSuggestionsCallback);
        this.loaderHelper.startLoader(true, false);
    }

    public void onStubButtonClick(Type type) {
        if (type == Type.NO_INTERNET) {
            onRefresh();
            return;
        }
        Logger.m172d("Find friends button clicked");
        Fragment parentFragment = getParentFragment();
        if (parentFragment == null) {
            Logger.m184w("Parent fragment is null");
        } else {
            ((FriendsTabFragment) parentFragment).selectSearch();
        }
    }

    public void setQuery(String query, boolean runImmediately) {
        this.query = query;
        this.searchHandler.removeMessages(0);
        if (this.friendsStrategy != null) {
            boolean queryEmpty = TextUtils.isEmpty(query);
            this.loaderHelper.setEmptyViewType(queryEmpty ? Type.FRIENDS_LIST : Type.SEARCH);
            if (queryEmpty) {
                this.friendsStrategy.setQuery(query);
                getLoaderManager().destroyLoader(2);
                this.searchStrategy.updateUsers(null);
                this.loadItemAdapter.setLoading(false);
                this.listeners.removeListener(this.autoLoadingListener);
            } else {
                Message msg = Message.obtain();
                msg.obj = query;
                this.searchHandler.sendMessageDelayed(msg, runImmediately ? 0 : 1000);
                this.friendsStrategy.setQuery(query);
            }
            updateBestFriendsVisibility();
            updateDecorationsVisibility();
        }
    }

    public void setRelationsListener(RelationsListener relationsListener) {
        this.relationsListener = relationsListener;
    }

    public void setRelationType(RelativesType type) {
        this.relationType = type;
        updateBestFriendsVisibility();
        if (this.friendsStrategy != null) {
            this.friendsStrategy.setRelationType(type);
        }
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

    @Subscribe(on = 2131623946, to = 2131624162)
    public void onFriendsUpdated(BusEvent event) {
        if (getActivity() != null) {
            this.loaderHelper.startLoader(false, false);
        }
    }
}
