package ru.ok.android.ui.groups.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.TopCategoriesSpinnerAdapter;
import ru.ok.android.fragments.TopCategoriesSpinnerAdapter.Item;
import ru.ok.android.fragments.TopCategoriesSpinnerAdapter.TopCategoriesSpinnerAdapterListener;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.CoordinatorLayoutNested;
import ru.ok.android.ui.coordinator.behaviors.AppBarGroupsOwnLayoutBehavior;
import ru.ok.android.ui.coordinator.behaviors.GroupsShadowBehavior;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.loadmore.DefaultLoadMoreViewProvider;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreRecyclerAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreView;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.groups.GroupUtils;
import ru.ok.android.ui.groups.GroupUtils.GroupCreateRequestedListener;
import ru.ok.android.ui.groups.adapters.GroupsHorizontalAdapter;
import ru.ok.android.ui.groups.adapters.GroupsRecyclerAdapter;
import ru.ok.android.ui.groups.adapters.GroupsRecyclerAdapter.Listener;
import ru.ok.android.ui.groups.adapters.GroupsVerticalAdapter;
import ru.ok.android.ui.groups.fragments.GroupsFragment.GroupsVerticalSpanSizeLookup;
import ru.ok.android.ui.groups.loaders.CategoriesGroupsLoader;
import ru.ok.android.ui.groups.loaders.GroupsLoaderResult;
import ru.ok.android.ui.groups.loaders.GroupsTopCategoriesLoader;
import ru.ok.android.ui.groups.loaders.GroupsTopCategoriesLoader.Result;
import ru.ok.android.ui.groups.loaders.UserGroupsLoader;
import ru.ok.android.ui.groups.search.GroupSearchController;
import ru.ok.android.ui.groups.search.GroupsSearchFragment;
import ru.ok.android.ui.swiperefresh.OkGroupsSwipeRefreshLayout;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.ui.utils.ItemCountChangedDataObserver;
import ru.ok.android.ui.utils.RecyclerMergeAdapter;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusGroupsHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.GroupInfo;
import ru.ok.model.groups.GroupsTopCategoryItem;
import ru.ok.onelog.groups.GroupJoinClickFactory;
import ru.ok.onelog.groups.GroupJoinClickSource;

public class GroupsComboFragment extends BaseFragment implements OnRefreshListener, TopCategoriesSpinnerAdapterListener, Listener {
    private OnScrollListener adjustScrollToPositionScrollListener;
    private int categoryGroupsPageLoadCount;
    private String categoryId;
    private CoordinatorLayoutNested coordinatorLayoutNested;
    private String currentTopCategoryId;
    private SmartEmptyViewAnimated emptyView;
    private AdapterDataObserver emptyViewItemCountObserver;
    private int gridColumnsGap;
    private boolean groupCategoryWasLoaded;
    private boolean groupCreateRequested;
    private GroupCreateRequestedListener groupCreateRequestedListener;
    private GroupSearchController groupSearchController;
    private GroupsHorizontalAdapter groupsOwnAdapter;
    private View groupsOwnAll;
    private AppBarLayout groupsOwnAppBar;
    private AppBarGroupsOwnLayoutBehavior groupsOwnAppBarBehavior;
    private View groupsOwnContent;
    private View groupsOwnShadow;
    private GroupsVerticalAdapter groupsPortalAdapter;
    private View groupsPortalContent;
    private Behavior groupsPortalContentBehavior;
    private OnScrollListener groupsPortalScrollListener;
    private GroupsShadowBehavior groupsShadowBehavior;
    private HeadersRecyclerAdapter headersRecyclerAdapter;
    private LoadMoreRecyclerAdapter loadMoreOwnAdapter;
    private LoadMoreAdapterListener loadMoreOwnListener;
    private LoadMoreRecyclerAdapter loadMorePortalAdapter;
    private LoadMoreAdapterListener loadMorePortalListener;
    private GroupsHorizontalLinearLayoutManager recyclerGroupsOwnLayoutManager;
    private RecyclerView recyclerViewGroupsOwn;
    private RecyclerView recyclerViewGroupsPortal;
    private GridLayoutManager recyclerViewGroupsPortalLayoutManager;
    private GroupsVerticalSpanSizeLookup spanSizeLookup;
    private String stateOwnGroupsAnchorForward;
    private String statePortalGroupsAnchorForward;
    private OkGroupsSwipeRefreshLayout swipeRefreshLayout;
    private TopCategoriesSpinnerAdapter topCategoriesAdapter;
    private TopCategoriesProcessor topCategoriesProcessor;
    private int userGroupsPageLoadCount;

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.15 */
    static /* synthetic */ class AnonymousClass15 {
        static final /* synthetic */ int[] f109x22ae40df;

        static {
            f109x22ae40df = new int[ErrorType.values().length];
            try {
                f109x22ae40df[ErrorType.NO_INTERNET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f109x22ae40df[ErrorType.RESTRICTED_GROUPS_ACCESS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.1 */
    class C09251 extends ItemCountChangedDataObserver {
        C09251() {
        }

        public void onItemCountMayChange() {
            int i = 0;
            if (GroupsComboFragment.this.emptyView != null) {
                boolean isEmpty;
                if (GroupsComboFragment.this.groupsOwnAdapter.getItemCount() == 0 && GroupsComboFragment.this.groupsPortalAdapter.getItemCount() == 0) {
                    isEmpty = true;
                } else {
                    isEmpty = false;
                }
                SmartEmptyViewAnimated access$000 = GroupsComboFragment.this.emptyView;
                if (!isEmpty) {
                    i = 8;
                }
                access$000.setVisibility(i);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.2 */
    class C09262 extends OnScrollListener {
        C09262() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == 0 && recyclerView.getChildCount() != 0 && recyclerView.getAdapter() != null) {
                int firstVisiblePosition = GroupsComboFragment.this.recyclerGroupsOwnLayoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = GroupsComboFragment.this.recyclerGroupsOwnLayoutManager.findLastVisibleItemPosition();
                if (firstVisiblePosition != -1 && lastVisiblePosition != recyclerView.getAdapter().getItemCount() - 1) {
                    int scrollToPosition = firstVisiblePosition;
                    View firstChild = recyclerView.getChildAt(0);
                    int left = firstChild.getLeft();
                    if (left != 0) {
                        if (Math.abs(left) > firstChild.getWidth() / 2 && recyclerView.canScrollHorizontally(1)) {
                            scrollToPosition++;
                        }
                        GroupsComboFragment.this.recyclerGroupsOwnLayoutManager.adjusterSmoothScrollToPosition(recyclerView, scrollToPosition);
                    }
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.3 */
    class C09273 extends OnScrollListener {
        C09273() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            GroupsComboFragment.this.groupsShadowBehavior.processScrollOffsetRecyclerTop(GroupsComboFragment.this.groupsOwnShadow, GroupsComboFragment.this.groupsPortalContent.getTop(), GroupsComboFragment.this.recyclerViewGroupsPortal.computeVerticalScrollOffset());
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.4 */
    class C09284 implements OnKeyListener {
        C09284() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == 4) {
                SearchView searchView = GroupsComboFragment.this.groupSearchController.getSearchView();
                GroupsSearchFragment searchFragment = GroupsComboFragment.this.groupSearchController.getGroupsSearchFragment();
                if (!(searchView == null || searchFragment == null || searchFragment.getView() == null || searchView.isShown())) {
                    searchFragment.getView().setVisibility(8);
                }
            }
            return false;
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.5 */
    class C09295 implements OnStubButtonClickListener {
        C09295() {
        }

        public void onStubButtonClick(Type type) {
            GroupsComboFragment.this.onRefresh();
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.6 */
    class C09306 extends ItemDecoration {
        C09306() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (GroupsComboFragment.this.recyclerViewGroupsPortalLayoutManager.getSpanCount() != 1) {
                outRect.right += GroupsComboFragment.this.gridColumnsGap;
            }
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.7 */
    class C09317 extends ItemDecoration {
        C09317() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.left += GroupsComboFragment.this.getResources().getDimensionPixelSize(2131231019);
            }
            if (position == parent.getAdapter().getItemCount() - 1) {
                outRect.right += GroupsComboFragment.this.getResources().getDimensionPixelSize(2131231019);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.8 */
    class C09328 extends DefaultLoadMoreViewProvider {
        C09328() {
        }

        public LoadMoreView createLoadMoreView(Context context, boolean isTopView, ViewGroup parent) {
            return (LoadMoreView) LayoutInflater.from(context).inflate(2130903278, parent, false);
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsComboFragment.9 */
    class C09339 implements OnClickListener {
        C09339() {
        }

        public void onClick(View v) {
            NavigationHelper.showCurrentUserGroups(GroupsComboFragment.this.getActivity());
        }
    }

    private class CategoriesGroupsCallback implements LoaderCallbacks<GroupsLoaderResult> {
        private CategoriesGroupsCallback() {
        }

        public Loader<GroupsLoaderResult> onCreateLoader(int id, Bundle args) {
            return new CategoriesGroupsLoader(GroupsComboFragment.this.getContext(), null, GroupsComboFragment.this.categoryGroupsPageLoadCount);
        }

        public void onLoadFinished(Loader<GroupsLoaderResult> loader, GroupsLoaderResult result) {
            GroupsComboFragment.this.processGroupLoaderResult(result, 1);
        }

        public void onLoaderReset(Loader<GroupsLoaderResult> loader) {
        }
    }

    public class GroupsTopCategoriesCallback implements LoaderCallbacks<Result> {
        public Loader<Result> onCreateLoader(int id, Bundle args) {
            return new GroupsTopCategoriesLoader(GroupsComboFragment.this.getContext(), 100);
        }

        public void onLoadFinished(Loader<Result> loader, Result data) {
            Logger.m173d("Group categories %s", data.categories);
            if (data.isSuccess && data.categories != null && data.categories.size() > 0) {
                TopCategoriesProcessor processor = new TopCategoriesProcessor(data.categories, null);
                if (GroupsComboFragment.this.groupCategoryWasLoaded) {
                    processor.process();
                } else {
                    GroupsComboFragment.this.topCategoriesProcessor = processor;
                }
            }
        }

        public void onLoaderReset(Loader<Result> loader) {
        }
    }

    public static class HeaderViewHolder extends ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class HeadersRecyclerAdapter extends Adapter implements AdapterItemViewTypeMaxValueProvider {
        private boolean enabled;
        private ViewHolder groupsSectionHeaderViewHolder;

        public HeadersRecyclerAdapter() {
            this.enabled = true;
        }

        void setGroupsPortalViewHolder(ViewHolder groupsPortalViewHolder) {
            this.groupsSectionHeaderViewHolder = groupsPortalViewHolder;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return this.groupsSectionHeaderViewHolder;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
        }

        public int getItemViewType(int position) {
            return 2131624362;
        }

        public int getItemCount() {
            return this.enabled ? 1 : 0;
        }

        public int getItemViewTypeMaxValue() {
            return 2131624362;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            this.groupsSectionHeaderViewHolder.itemView.setVisibility(enabled ? 0 : 8);
        }
    }

    private class TopCategoriesProcessor {
        public final List<GroupsTopCategoryItem> categories;

        private TopCategoriesProcessor(List<GroupsTopCategoryItem> categories) {
            this.categories = categories;
        }

        public void process() {
            GroupsComboFragment.this.initActionBarListMode();
            List<Item> items = GroupsComboFragment.this.getTopCategoriesItems(this.categories);
            GroupsComboFragment.this.topCategoriesAdapter.setData(items);
            GroupsComboFragment.this.topCategoriesAdapter.notifyDataSetChanged();
            if (GroupsComboFragment.this.categoryId != null && items.size() > 0) {
                int size = items.size();
                for (int i = 0; i < size; i++) {
                    if (GroupsComboFragment.this.categoryId.equals(((Item) items.get(i)).id)) {
                        ActionBar actionBar = GroupsComboFragment.this.getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setSelectedNavigationItem(i);
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    private class UserGroupsCallback implements LoaderCallbacks<GroupsLoaderResult> {
        private UserGroupsCallback() {
        }

        public Loader<GroupsLoaderResult> onCreateLoader(int id, Bundle args) {
            return new UserGroupsLoader(GroupsComboFragment.this.getContext(), OdnoklassnikiApplication.getCurrentUser().uid, GroupsComboFragment.this.userGroupsPageLoadCount);
        }

        public void onLoadFinished(Loader<GroupsLoaderResult> loader, GroupsLoaderResult result) {
            Logger.m173d("User groups %s", result.groupInfos);
            GroupsComboFragment.this.processGroupLoaderResult(result, 0);
        }

        public void onLoaderReset(Loader<GroupsLoaderResult> loader) {
        }
    }

    public GroupsComboFragment() {
        this.categoryGroupsPageLoadCount = 30;
        this.userGroupsPageLoadCount = 20;
        this.emptyViewItemCountObserver = new C09251();
        this.adjustScrollToPositionScrollListener = new C09262();
        this.groupsPortalScrollListener = new C09273();
        this.loadMorePortalListener = new LoadMoreAdapterListener() {
            public void onLoadMoreTopClicked() {
            }

            public void onLoadMoreBottomClicked() {
                if (!GroupsComboFragment.this.groupsPortalAdapter.isLoading()) {
                    CategoriesGroupsLoader categoriesGroupsLoader = GroupsComboFragment.this.getCategoriesGroupsLoader();
                    categoriesGroupsLoader.setAnchor(GroupsComboFragment.this.statePortalGroupsAnchorForward);
                    categoriesGroupsLoader.setDirection(PagingDirection.FORWARD);
                    categoriesGroupsLoader.forceLoad();
                    GroupsComboFragment.this.groupsPortalAdapter.setLoading(true);
                }
            }
        };
        this.loadMoreOwnListener = new LoadMoreAdapterListener() {
            public void onLoadMoreTopClicked() {
            }

            public void onLoadMoreBottomClicked() {
                if (!GroupsComboFragment.this.groupsOwnAdapter.isLoading()) {
                    UserGroupsLoader userGroupsLoader = GroupsComboFragment.this.getUserGroupsLoader();
                    userGroupsLoader.setAnchor(GroupsComboFragment.this.stateOwnGroupsAnchorForward);
                    userGroupsLoader.setDirection(PagingDirection.FORWARD);
                    userGroupsLoader.forceLoad();
                    GroupsComboFragment.this.groupsOwnAdapter.setLoading(true);
                }
            }
        };
        this.groupCreateRequestedListener = new GroupCreateRequestedListener() {
            public void onGroupCreateRequested() {
                GroupsComboFragment.this.groupCreateRequested = true;
            }
        };
    }

    protected int getLayoutId() {
        return 2130903199;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.categoryId = getArguments() != null ? getArguments().getString("categoryId") : null;
        this.topCategoriesAdapter = new TopCategoriesSpinnerAdapter(getContext());
        this.topCategoriesAdapter.setListener(this);
        setHasOptionsMenu(true);
        this.groupSearchController.onRestoreInstanceState(savedInstanceState);
    }

    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.categoryGroupsPageLoadCount = GroupUtils.groupsVerticalBigItemsPageCount(getContext());
        this.userGroupsPageLoadCount = GroupUtils.groupsHorizontalItemsPageCount(getContext());
        getLoaderManager().initLoader(2131624277, null, new GroupsTopCategoriesCallback()).forceLoad();
        getLoaderManager().initLoader(2131624279, null, new UserGroupsCallback()).forceLoad();
        getLoaderManager().initLoader(2131624275, null, new CategoriesGroupsCallback()).forceLoad();
        initUi(view, savedInstanceState);
        view.setOnKeyListener(new C09284());
    }

    private void initUi(View view, Bundle savedInstanceState) {
        this.coordinatorLayoutNested = (CoordinatorLayoutNested) view.findViewById(2131624839);
        this.coordinatorLayoutNested.setNestedScrollingEnabled(true);
        this.groupsOwnAppBar = (AppBarLayout) view.findViewById(2131624840);
        this.groupsOwnAppBarBehavior = (AppBarGroupsOwnLayoutBehavior) ((LayoutParams) this.groupsOwnAppBar.getLayoutParams()).getBehavior();
        this.groupsOwnContent = view.findViewById(2131624841);
        this.groupsPortalContent = view.findViewById(2131624845);
        this.groupsPortalContentBehavior = ((LayoutParams) this.groupsPortalContent.getLayoutParams()).getBehavior();
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setState(SmartEmptyViewAnimated.State.LOADING);
        this.emptyView.setType(getEmptyViewType());
        this.emptyView.setButtonClickListener(new C09295());
        this.swipeRefreshLayout = (OkGroupsSwipeRefreshLayout) view.findViewById(2131624611);
        this.swipeRefreshLayout.setGroupsOwnAppBarLayout(this.groupsOwnAppBar);
        this.swipeRefreshLayout.setOnRefreshListener(this);
        initOwnGroupsUi(view);
        initPortalGroupsUi(view);
    }

    private void initPortalGroupsUi(View view) {
        this.recyclerViewGroupsPortal = (RecyclerView) view.findViewById(2131624846);
        this.recyclerViewGroupsPortalLayoutManager = new GridLayoutManager(getContext(), GroupsFragment.getColumnCount(getContext()));
        this.recyclerViewGroupsPortal.setLayoutManager(this.recyclerViewGroupsPortalLayoutManager);
        this.groupsPortalAdapter = new GroupsVerticalAdapter(getContext(), true, true);
        this.groupsPortalAdapter.setListener(this);
        this.loadMorePortalAdapter = new LoadMoreRecyclerAdapter(getActivity(), this.groupsPortalAdapter, this.loadMorePortalListener, LoadMoreMode.BOTTOM);
        this.loadMorePortalAdapter.getController().setBottomPermanentState(LoadMoreState.DISABLED);
        this.loadMorePortalAdapter.getController().setBottomAutoLoad(true);
        RecyclerMergeAdapter mergeAdapter = new RecyclerMergeAdapter();
        this.headersRecyclerAdapter = createHeaderRecyclerAdapter();
        this.headersRecyclerAdapter.setGroupsPortalViewHolder(new HeaderViewHolder(LayoutInflater.from(getContext()).inflate(2130903235, this.recyclerViewGroupsPortal, false)));
        mergeAdapter.addAdapter(this.headersRecyclerAdapter);
        mergeAdapter.addAdapter(this.loadMorePortalAdapter);
        this.recyclerViewGroupsPortal.addOnScrollListener(this.groupsPortalScrollListener);
        this.spanSizeLookup = new GroupsVerticalSpanSizeLookup(this.recyclerViewGroupsPortal, this.loadMorePortalAdapter, true);
        this.gridColumnsGap = getContext().getResources().getDimensionPixelOffset(2131231021);
        this.recyclerViewGroupsPortal.addItemDecoration(new C09306());
        this.recyclerViewGroupsOwn.addItemDecoration(new C09317());
        this.recyclerViewGroupsPortalLayoutManager.setSpanSizeLookup(this.spanSizeLookup);
        this.recyclerViewGroupsPortal.setAdapter(mergeAdapter);
    }

    private HeadersRecyclerAdapter createHeaderRecyclerAdapter() {
        return new HeadersRecyclerAdapter();
    }

    public void onRefresh() {
        CategoriesGroupsLoader categoriesGroupsLoader = getCategoriesGroupsLoader();
        UserGroupsLoader userGroupsLoader = getUserGroupsLoader();
        if (this.groupsOwnAdapter.isLoading()) {
            this.swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (this.emptyView.getType() == Type.NO_INTERNET) {
            getTopCategoriesLoader().forceLoad();
        }
        this.statePortalGroupsAnchorForward = null;
        categoriesGroupsLoader.setAnchor(this.statePortalGroupsAnchorForward);
        categoriesGroupsLoader.forceLoad();
        this.stateOwnGroupsAnchorForward = null;
        userGroupsLoader.setAnchor(this.stateOwnGroupsAnchorForward);
        userGroupsLoader.forceLoad();
        this.emptyView.setState(SmartEmptyViewAnimated.State.LOADING);
    }

    public static Bundle newArguments(String categoryId) {
        Bundle args = new Bundle();
        args.putString("categoryId", categoryId);
        return args;
    }

    private void initOwnGroupsUi(View view) {
        this.recyclerViewGroupsOwn = (RecyclerView) view.findViewById(2131624844);
        this.recyclerGroupsOwnLayoutManager = new GroupsHorizontalLinearLayoutManager(getContext(), 0, false);
        this.recyclerViewGroupsOwn.setLayoutManager(this.recyclerGroupsOwnLayoutManager);
        this.groupsOwnShadow = view.findViewById(2131624847);
        this.groupsShadowBehavior = (GroupsShadowBehavior) ((LayoutParams) this.groupsOwnShadow.getLayoutParams()).getBehavior();
        this.groupsOwnAdapter = new GroupsHorizontalAdapter(getContext());
        this.groupsOwnAdapter.setListener(this);
        this.loadMoreOwnAdapter = new LoadMoreRecyclerAdapter(getActivity(), this.groupsOwnAdapter, this.loadMoreOwnListener, LoadMoreMode.BOTTOM, new C09328());
        this.loadMoreOwnAdapter.getController().setBottomPermanentState(LoadMoreState.DISABLED);
        this.loadMoreOwnAdapter.getController().setBottomAutoLoad(true);
        this.recyclerViewGroupsOwn.setAdapter(this.loadMoreOwnAdapter);
        this.recyclerViewGroupsOwn.addOnScrollListener(this.adjustScrollToPositionScrollListener);
        this.groupsOwnAdapter.registerAdapterDataObserver(this.emptyViewItemCountObserver);
        this.groupsOwnAll = view.findViewById(2131624843);
        this.groupsOwnAll.setOnClickListener(new C09339());
    }

    private Type getEmptyViewType() {
        return Type.GROUPS_LIST;
    }

    private CategoriesGroupsLoader getCategoriesGroupsLoader() {
        return (CategoriesGroupsLoader) getLoaderManager().getLoader(2131624275);
    }

    private UserGroupsLoader getUserGroupsLoader() {
        return (UserGroupsLoader) getLoaderManager().getLoader(2131624279);
    }

    private GroupsTopCategoriesLoader getTopCategoriesLoader() {
        return (GroupsTopCategoriesLoader) getLoaderManager().getLoader(2131624277);
    }

    public boolean onTopCategorySelected(int position, String id) {
        if (!TextUtils.equals(this.currentTopCategoryId, id)) {
            this.currentTopCategoryId = id;
            if (id == null) {
                showOwnGroupsAppBar();
                this.headersRecyclerAdapter.setEnabled(true);
                this.spanSizeLookup.setHasHeader(true);
            } else {
                hideOwnGroupsAppBar();
                this.headersRecyclerAdapter.setEnabled(false);
                this.spanSizeLookup.setHasHeader(false);
            }
            groupsPortalScrollToTop();
            CategoriesGroupsLoader categoriesGroupsLoader = getCategoriesGroupsLoader();
            categoriesGroupsLoader.setAnchor(null);
            this.statePortalGroupsAnchorForward = null;
            categoriesGroupsLoader.setCategoryId(id);
            categoriesGroupsLoader.forceLoad();
        }
        return true;
    }

    public void groupsPortalScrollToTop() {
        if (this.recyclerViewGroupsPortalLayoutManager.findFirstVisibleItemPosition() > 15) {
            this.recyclerViewGroupsPortal.scrollToPosition(15);
        }
        this.recyclerViewGroupsPortal.smoothScrollToPosition(0);
    }

    private void hideOwnGroupsAppBar() {
        if (this.groupsOwnAppBar.getTop() != (-this.groupsOwnAppBar.getHeight())) {
            this.groupsOwnAppBarBehavior.setPendingAction(2);
            this.groupsOwnAppBar.setExpanded(false, true);
        }
        this.groupsOwnAppBar.post(new Runnable() {
            public void run() {
                ((AppBarLayout.LayoutParams) GroupsComboFragment.this.groupsOwnContent.getLayoutParams()).setScrollFlags(0);
                ((LayoutParams) GroupsComboFragment.this.groupsPortalContent.getLayoutParams()).setBehavior(null);
                GroupsComboFragment.this.coordinatorLayoutNested.requestLayout();
            }
        });
    }

    private void showOwnGroupsAppBar() {
        ((LayoutParams) this.groupsPortalContent.getLayoutParams()).setBehavior(this.groupsPortalContentBehavior);
        if (!isGroupsOwnAppBarShown()) {
            this.groupsOwnAppBarBehavior.setPendingAction(1);
            this.groupsOwnAppBar.setExpanded(true, true);
        }
        this.groupsOwnAppBar.post(new Runnable() {
            public void run() {
                ((AppBarLayout.LayoutParams) GroupsComboFragment.this.groupsOwnContent.getLayoutParams()).setScrollFlags(5);
            }
        });
    }

    public void onGroupInfoClick(GroupInfo groupInfo, GroupsRecyclerAdapter adapter, int position) {
        NavigationHelper.showGroupInfo(getActivity(), groupInfo.getId());
    }

    private List<Item> getTopCategoriesItems(List<GroupsTopCategoryItem> categories) {
        List<Item> items = new ArrayList();
        String title = getStringLocalized(2131165961);
        items.add(new Item(title, null, getStringLocalized(2131165966), null));
        for (int i = 0; i < categories.size(); i++) {
            GroupsTopCategoryItem category = (GroupsTopCategoryItem) categories.get(i);
            items.add(new Item(title, category.name, category.name, category.id));
        }
        return items;
    }

    private void initActionBarListMode() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            actionBar.setSubtitle(null);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(1);
            actionBar.setListNavigationCallbacks(this.topCategoriesAdapter, this.topCategoriesAdapter);
        }
    }

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getContext(), 2131165961);
    }

    private void processGroupLoaderResult(GroupsLoaderResult result, int source) {
        View contentView;
        LinearLayoutManager layoutManager;
        GroupsRecyclerAdapter adapter;
        LoadMoreRecyclerAdapter loadMoreAdapter;
        boolean isOwn = source == 0;
        if (isOwn) {
            contentView = this.groupsOwnContent;
            layoutManager = this.recyclerGroupsOwnLayoutManager;
            adapter = this.groupsOwnAdapter;
            loadMoreAdapter = this.loadMoreOwnAdapter;
        } else {
            contentView = this.groupsPortalContent;
            layoutManager = this.recyclerViewGroupsPortalLayoutManager;
            adapter = this.groupsPortalAdapter;
            loadMoreAdapter = this.loadMorePortalAdapter;
        }
        loadMoreAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
        Type emptyViewType = getEmptyViewType();
        LoadMoreState loadingState;
        if (result.isSuccess) {
            Logger.m173d("Loaded groups %s", result.groupInfos);
            this.swipeRefreshLayout.setVisibility(0);
            boolean isEmpty = result.loadParams.anchor == null && (result.groupInfos == null || result.groupInfos.size() == 0);
            contentView.setVisibility(isEmpty ? 8 : 0);
            if (result.loadParams.anchor == null) {
                layoutManager.scrollToPosition(0);
                adapter.setItems(result.groupInfos, result.groupsAdditionalInfos);
                adapter.notifyDataSetChanged();
            } else if (result.groupInfos != null) {
                int oldSize = adapter.getItemCount();
                adapter.addItems(result.groupInfos, result.groupsAdditionalInfos);
                loadMoreAdapter.notifyItemRangeInserted(loadMoreAdapter.getController().getExtraTopElements() + oldSize, result.groupInfos.size());
            }
            if (result.loadParams.direction == PagingDirection.FORWARD) {
                if (isOwn) {
                    this.stateOwnGroupsAnchorForward = result.anchor;
                } else {
                    this.statePortalGroupsAnchorForward = result.anchor;
                }
                loadingState = result.hasMore ? LoadMoreState.LOAD_POSSIBLE_NO_LABEL : LoadMoreState.DISABLED;
                loadMoreAdapter.getController().setBottomAutoLoad(result.hasMore);
                loadMoreAdapter.getController().setBottomPermanentState(loadingState);
            }
            if (!(isOwn || this.groupCategoryWasLoaded)) {
                this.groupCategoryWasLoaded = true;
                if (this.topCategoriesProcessor != null) {
                    this.topCategoriesProcessor.process();
                    this.topCategoriesProcessor = null;
                }
            }
        } else {
            Logger.m176e("Failed load groups");
            emptyViewType = convertErrorType(result.errorType);
            loadingState = (result.errorType != ErrorType.NO_INTERNET || this.groupsPortalAdapter.getItemCount() <= 0) ? LoadMoreState.DISABLED : LoadMoreState.DISCONNECTED;
            loadMoreAdapter.getController().setBottomPermanentState(loadingState);
        }
        adapter.setLoading(false);
        this.swipeRefreshLayout.setRefreshing(false);
        if (!isOwn) {
            this.emptyView.setType(emptyViewType);
            this.emptyView.setState(SmartEmptyViewAnimated.State.LOADED);
        }
    }

    public static Type convertErrorType(ErrorType errorType) {
        switch (AnonymousClass15.f109x22ae40df[errorType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return Type.NO_INTERNET;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return Type.RESTRICTED;
            default:
                return Type.ERROR;
        }
    }

    public void onGroupInfoJoinClick(GroupInfo groupInfo) {
        OneLog.log(GroupJoinClickFactory.get(GroupJoinClickSource.groups_page_combo_portal));
        BusGroupsHelper.inviteToGroup(groupInfo.getId());
    }

    @Subscribe(on = 2131623946, to = 2131624173)
    public final void onInviteGroupResult(BusEvent event) {
        if (event.resultCode != -1) {
            ErrorType errorType = ErrorType.from(event.bundleOutput);
            if (errorType != null) {
                showTimedToastIfVisible(errorType.getDefaultErrorMessage(), 1);
            }
        } else if (event.bundleOutput.getBoolean("GROUP_INVITE_RESULT_VALUE")) {
            showTimedToastIfVisible(getStringLocalized(2131165943), 0);
            String groupId = event.bundleInput.getString("GROUP_ID");
            if (groupId != null) {
                this.groupsPortalAdapter.addInvitedGroupIdAndNotify(groupId);
            }
        } else {
            showTimedToastIfVisible(2131165791, 1);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        int i = 1;
        super.onConfigurationChanged(newConfig);
        if (DeviceUtils.isSmall(getContext())) {
            boolean isLandscape = getContext().getResources().getConfiguration().orientation == 2;
            GridLayoutManager gridLayoutManager = this.recyclerViewGroupsPortalLayoutManager;
            if (isLandscape) {
                i = 2;
            }
            gridLayoutManager.setSpanCount(i);
        }
        this.groupsOwnAdapter.notifyDataSetChanged();
        this.recyclerViewGroupsOwn.invalidate();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.groupSearchController = new GroupSearchController(getActivity(), this);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflateMenuLocalized(2131689492, menu);
        this.groupSearchController.onCreateOptionsMenu(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.groupSearchController.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625469:
                GroupUtils.showCreateGroupDialog(getContext(), this.groupCreateRequestedListener);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624168)
    public final void onGroupCreate(BusEvent event) {
        if (this.groupCreateRequested) {
            if (event.resultCode == -1) {
                Bundle bundle = event.bundleOutput;
                showTimedToastIfVisible(2131165940, 1);
                if (bundle != null) {
                    String groupId = bundle.getString("GROUP_ID");
                    if (getActivity() != null) {
                        onRefresh();
                        NavigationHelper.showGroupInfo(getActivity(), groupId);
                    }
                }
            } else {
                GroupUtils.processCreateGroupFail(getActivity(), event);
            }
            this.groupCreateRequested = false;
        }
    }

    public boolean handleBack() {
        if (this.groupSearchController.handleBack()) {
            return true;
        }
        if (isGroupsOwnAppBarShown() || this.currentTopCategoryId == null || getSupportActionBar() == null || getSupportActionBar().getSelectedNavigationIndex() == 0) {
            return super.handleBack();
        }
        getSupportActionBar().setSelectedNavigationItem(0);
        return true;
    }

    private boolean isGroupsOwnAppBarShown() {
        return this.groupsOwnAppBar.getTop() == 0;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.groupSearchController.onSaveInstanceState(outState);
    }

    protected void onInternetAvailable() {
        processInternetAvailable(this.loadMorePortalAdapter, this.recyclerViewGroupsPortalLayoutManager, this.loadMorePortalListener);
        processInternetAvailable(this.loadMoreOwnAdapter, this.recyclerGroupsOwnLayoutManager, this.loadMoreOwnListener);
    }

    private void processInternetAvailable(LoadMoreRecyclerAdapter loadMoreAdapter, LinearLayoutManager layoutManager, LoadMoreAdapterListener loadMoreListener) {
        if (loadMoreAdapter != null && layoutManager != null && loadMoreAdapter.getController().getBottomPermanentState() == LoadMoreState.DISCONNECTED) {
            loadMoreAdapter.getController().setBottomAutoLoad(true);
            loadMoreAdapter.getController().setBottomPermanentState(LoadMoreState.LOAD_POSSIBLE_NO_LABEL);
            if (layoutManager.findLastVisibleItemPosition() > loadMoreAdapter.getItemCount() - 3) {
                loadMoreListener.onLoadMoreBottomClicked();
            }
        }
    }
}
