package ru.ok.android.ui.groups.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.model.cache.ram.UsersCache;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.loadmore.DefaultLoadMoreViewProvider;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreRecyclerAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreView;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.groups.GroupUtils;
import ru.ok.android.ui.groups.GroupUtils.GroupCreateRequestedListener;
import ru.ok.android.ui.groups.adapters.GroupsRecyclerAdapter;
import ru.ok.android.ui.groups.adapters.GroupsRecyclerAdapter.Listener;
import ru.ok.android.ui.groups.adapters.GroupsVerticalAdapter;
import ru.ok.android.ui.groups.loaders.BaseGroupsPageLoader;
import ru.ok.android.ui.groups.loaders.GroupsLoaderResult;
import ru.ok.android.ui.groups.loaders.UserGroupsLoader;
import ru.ok.android.ui.groups.search.GroupSearchController;
import ru.ok.android.ui.stream.BaseRefreshRecyclerFragment;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;

public class GroupsFragment extends BaseRefreshRecyclerFragment<Adapter> implements LoadMoreAdapterListener, Listener {
    private GridLayoutManager gridLayoutManager;
    private boolean groupCreateRequested;
    private GroupCreateRequestedListener groupCreateRequestedListener;
    private GroupSearchController groupSearchController;
    protected GroupsVerticalAdapter groupsAdapter;
    protected LoadMoreRecyclerAdapter loadMoreAdapter;
    private String stateGroupsAnchorForward;
    protected String subtitle;
    protected String title;
    private int userGroupsLoadCount;
    private String userId;

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsFragment.1 */
    class C09341 extends DefaultLoadMoreViewProvider {
        C09341() {
        }

        public LoadMoreView createLoadMoreView(Context context, boolean isTopView, ViewGroup parent) {
            return (LoadMoreView) LayoutInflater.from(context).inflate(2130903278, parent, false);
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsFragment.2 */
    class C09352 extends AsyncTask<String, Void, UserInfo> {
        C09352() {
        }

        protected UserInfo doInBackground(String... params) {
            return UsersStorageFacade.queryUser(params[0]);
        }

        protected void onPostExecute(UserInfo userInfo) {
            if (userInfo != null) {
                GroupsFragment.this.processUserInfo(userInfo);
            } else {
                BusUsersHelper.getUserInfos(Collections.singletonList(GroupsFragment.this.userId), false, false);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsFragment.3 */
    class C09363 implements OnStubButtonClickListener {
        C09363() {
        }

        public void onStubButtonClick(Type type) {
            GroupsFragment.this.onRefresh();
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupsFragment.4 */
    class C09374 implements GroupCreateRequestedListener {
        C09374() {
        }

        public void onGroupCreateRequested() {
            GroupsFragment.this.groupCreateRequested = true;
        }
    }

    private class GroupsLoaderCallback implements LoaderCallbacks<GroupsLoaderResult> {
        private GroupsLoaderCallback() {
        }

        public Loader<GroupsLoaderResult> onCreateLoader(int id, Bundle args) {
            return new UserGroupsLoader(GroupsFragment.this.getContext(), GroupsFragment.this.userId == null ? OdnoklassnikiApplication.getCurrentUser().uid : GroupsFragment.this.userId, GroupsFragment.this.userGroupsLoadCount);
        }

        public void onLoadFinished(Loader<GroupsLoaderResult> loader, GroupsLoaderResult data) {
            GroupsFragment.this.processGroupLoaderResult(data);
        }

        public void onLoaderReset(Loader<GroupsLoaderResult> loader) {
        }
    }

    public static class GroupsVerticalSpanSizeLookup extends SpanSizeLookup {
        protected boolean hasHeader;
        protected final LoadMoreRecyclerAdapter loadMoreRecyclerAdapter;
        protected final RecyclerView recyclerView;

        public GroupsVerticalSpanSizeLookup(RecyclerView recyclerView, LoadMoreRecyclerAdapter loadMoreRecyclerAdapter, boolean hasHeader) {
            this.recyclerView = recyclerView;
            this.loadMoreRecyclerAdapter = loadMoreRecyclerAdapter;
            this.hasHeader = hasHeader;
        }

        public int getSpanSize(int position) {
            if (getGridLayoutManager().getSpanCount() == 2) {
                if (this.hasHeader && position == 0) {
                    return 2;
                }
                Adapter adapter = this.recyclerView.getAdapter();
                if (adapter != null && position == adapter.getItemCount() - 1 && this.loadMoreRecyclerAdapter.getController().isBottomViewAdded()) {
                    return 2;
                }
            }
            return 1;
        }

        protected GridLayoutManager getGridLayoutManager() {
            return (GridLayoutManager) this.recyclerView.getLayoutManager();
        }

        public void setHasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
        }
    }

    public GroupsFragment() {
        this.userGroupsLoadCount = 20;
        this.groupCreateRequestedListener = new C09374();
    }

    protected CharSequence getTitle() {
        return this.title;
    }

    protected CharSequence getSubtitle() {
        return this.subtitle;
    }

    public void onLoadMoreTopClicked() {
    }

    public void onLoadMoreBottomClicked() {
        if (!this.groupsAdapter.isLoading()) {
            BaseGroupsPageLoader userGroupsLoader = getGroupsLoader();
            userGroupsLoader.setAnchor(this.stateGroupsAnchorForward);
            userGroupsLoader.setDirection(PagingDirection.FORWARD);
            userGroupsLoader.forceLoad();
            this.groupsAdapter.setLoading(true);
        }
    }

    protected BaseGroupsPageLoader getGroupsLoader() {
        return (UserGroupsLoader) getLoaderManager().getLoader(2131624276);
    }

    protected GroupsVerticalAdapter getGroupsAdapter() {
        return new GroupsVerticalAdapter(getContext(), false, false);
    }

    protected Adapter createRecyclerAdapter() {
        this.groupsAdapter = getGroupsAdapter();
        this.groupsAdapter.setListener(this);
        this.loadMoreAdapter = new LoadMoreRecyclerAdapter(getActivity(), this.groupsAdapter, this, LoadMoreMode.BOTTOM, new C09341());
        this.loadMoreAdapter.getController().setBottomPermanentState(LoadMoreState.DISABLED);
        this.loadMoreAdapter.getController().setBottomAutoLoad(true);
        return this.loadMoreAdapter;
    }

    protected int getLayoutId() {
        return 2130903200;
    }

    public void onRefresh() {
        if (this.groupsAdapter.isLoading()) {
            this.swipeRefreshLayout.setRefreshing(false);
            return;
        }
        this.emptyView.setState(State.LOADING);
        BaseGroupsPageLoader userGroupsLoader = getGroupsLoader();
        this.stateGroupsAnchorForward = null;
        userGroupsLoader.setAnchor(this.stateGroupsAnchorForward);
        userGroupsLoader.forceLoad();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userGroupsLoadCount = GroupUtils.groupsVerticalItemsPageCount(getContext());
        this.userId = getArguments() != null ? getArguments().getString("uid") : null;
        init();
    }

    private boolean isCurrentUserGroups() {
        return this.userId == null || this.userId.equals(OdnoklassnikiApplication.getCurrentUser().getId());
    }

    protected void init() {
        if (isCurrentUserGroups()) {
            processUserInfo(null);
        } else {
            UserInfo userInfo = UsersCache.getInstance().getUser(this.userId);
            if (userInfo == null) {
                new C09352().execute(new String[]{this.userId});
            } else {
                processUserInfo(userInfo);
            }
        }
        setHasOptionsMenu(true);
    }

    @Subscribe(on = 2131623946, to = 2131624221)
    public void onUserInfo(BusEvent e) {
        if (this.userId != null) {
            List<String> userIds = e.bundleInput.getStringArrayList("USER_IDS");
            if (userIds != null && userIds.contains(this.userId) && e.resultCode == -1) {
                ArrayList<UserInfo> userInfos = e.bundleOutput.getParcelableArrayList("USERS");
                if (userInfos != null && userInfos.size() > 0) {
                    Iterator i$ = userInfos.iterator();
                    while (i$.hasNext()) {
                        UserInfo userInfo = (UserInfo) i$.next();
                        if (this.userId.equals(userInfo.getId())) {
                            processUserInfo(userInfo);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void processUserInfo(UserInfo userInfo) {
        int i = 2131165961;
        if (userInfo == null) {
            Context context = getContext();
            if (isCurrentUserGroups()) {
                i = 2131166245;
            }
            this.title = LocalizationManager.getString(context, i);
            this.subtitle = null;
        } else {
            this.title = LocalizationManager.getString(getContext(), 2131165961);
            this.subtitle = userInfo.getAnyName();
        }
        setTitleIfVisible(this.title);
        setSubTitleIfVisible(this.subtitle);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.gridLayoutManager.setSpanSizeLookup(getSpanSizeLookup());
        return view;
    }

    protected GroupsVerticalSpanSizeLookup getSpanSizeLookup() {
        return new GroupsVerticalSpanSizeLookup(this.recyclerView, this.loadMoreAdapter, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.emptyView.setType(getEmptyViewType());
        this.emptyView.setButtonClickListener(new C09363());
        initLoaders();
    }

    protected void initLoaders() {
        this.emptyView.setState(State.LOADING);
        getLoaderManager().initLoader(2131624276, null, new GroupsLoaderCallback()).forceLoad();
    }

    public void onGroupInfoClick(GroupInfo groupInfo, GroupsRecyclerAdapter adapter, int position) {
        if (groupInfo != null && groupInfo.getId() != null) {
            NavigationHelper.showGroupInfo(getActivity(), groupInfo.getId());
        }
    }

    public void onGroupInfoJoinClick(GroupInfo groupInfo) {
    }

    public static Bundle newArguments(String userId) {
        Bundle args = new Bundle();
        args.putString("uid", userId);
        return args;
    }

    protected void processGroupLoaderResult(GroupsLoaderResult result) {
        int i = 0;
        LinearLayoutManager layoutManager = this.gridLayoutManager;
        GroupsRecyclerAdapter adapter = this.groupsAdapter;
        this.loadMoreAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
        Type emptyViewType = getEmptyViewType();
        LoadMoreState loadingState;
        if (result.isSuccess) {
            Logger.m173d("Loaded groups %s", result.groupInfos);
            if (result.loadParams.anchor == null) {
                layoutManager.scrollToPosition(0);
                adapter.setItems(result.groupInfos);
                adapter.notifyDataSetChanged();
            } else if (result.groupInfos != null && result.groupInfos.size() > 0) {
                int oldSize = adapter.getItemCount();
                adapter.addItems(result.groupInfos);
                this.loadMoreAdapter.notifyItemRangeInserted(this.loadMoreAdapter.getController().getExtraTopElements() + oldSize, result.groupInfos.size());
            }
            if (result.loadParams.direction == PagingDirection.FORWARD) {
                this.stateGroupsAnchorForward = result.anchor;
                loadingState = result.hasMore ? LoadMoreState.LOAD_POSSIBLE_NO_LABEL : LoadMoreState.DISABLED;
                this.loadMoreAdapter.getController().setBottomAutoLoad(result.hasMore);
                this.loadMoreAdapter.getController().setBottomPermanentState(loadingState);
            }
        } else {
            Logger.m176e("Failed load groups");
            emptyViewType = GroupsComboFragment.convertErrorType(result.errorType);
            loadingState = (result.errorType != ErrorType.NO_INTERNET || this.groupsAdapter.getItemCount() <= 0) ? LoadMoreState.DISABLED : LoadMoreState.DISCONNECTED;
            this.loadMoreAdapter.getController().setBottomPermanentState(loadingState);
        }
        adapter.setLoading(false);
        this.swipeRefreshLayout.setRefreshing(false);
        this.emptyView.setType(emptyViewType);
        this.emptyView.setState(State.LOADED);
        SwipeRefreshLayout swipeRefreshLayout = this.swipeRefreshLayout;
        if (adapter.getItemCount() == 0) {
            i = 8;
        }
        swipeRefreshLayout.setVisibility(i);
    }

    private Type getEmptyViewType() {
        return Type.GROUPS_LIST;
    }

    protected LinearLayoutManager getRecyclerViewLayoutManager() {
        this.gridLayoutManager = new GridLayoutManager(getContext(), getColumnCount(getContext()));
        return this.gridLayoutManager;
    }

    public static int getColumnCount(Context context) {
        int i = 1;
        if (context.getResources().getConfiguration().orientation == 2) {
            return 2;
        }
        if (!DeviceUtils.isSmall(context)) {
            i = 2;
        }
        return i;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        int i = 1;
        super.onConfigurationChanged(newConfig);
        if (DeviceUtils.isSmall(getContext())) {
            boolean isLandscape = getContext().getResources().getConfiguration().orientation == 2;
            GridLayoutManager gridLayoutManager = this.gridLayoutManager;
            if (isLandscape) {
                i = 2;
            }
            gridLayoutManager.setSpanCount(i);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (inflateMenuLocalized(2131689492, menu)) {
            this.groupSearchController.onCreateOptionsMenu(menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.groupSearchController = new GroupSearchController(getActivity(), this);
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
        return super.handleBack();
    }
}
