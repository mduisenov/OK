package ru.ok.android.ui.search.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Toast;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.SearchListView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreConditionCallback;
import ru.ok.android.ui.custom.loadmore.LoadMoreController;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreRecyclerAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.search.fragment.SearchResultsAdapter.OnAdapterItemClickListener;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.search.SearchResults;
import ru.ok.model.search.SearchResults.SearchContext;
import ru.ok.model.search.SearchType;

public class SearchFragment extends BaseFragment implements OnItemClickListener, OnStubButtonClickListener, LoadMoreAdapterListener, LoadMoreConditionCallback, OnAdapterItemClickListener {
    private static final LoadMoreState LOAD_MORE_POSSIBLE_STATE;
    private boolean combineResults;
    private SmartEmptyViewAnimated emptyView;
    private SearchListView listView;
    private LoadMoreRecyclerAdapter loadMoreRecyclerAdapter;
    private String pendingAnchor;
    private String query;
    private View rootView;
    private OnScrollListener scrollListener;
    private SearchResultsAdapter searchAdapter;
    private SearchContext searchContext;
    private SearchResults searchResults;
    private SearchType[] searchTypes;
    private int state;

    public interface OnUserActionListener {
        void onCommunityRequested(GroupInfo groupInfo);

        void onGroupRequested(GroupInfo groupInfo);

        void onShowContexted(SearchType searchType);

        void onUserRequested(UserInfo userInfo);
    }

    /* renamed from: ru.ok.android.ui.search.fragment.SearchFragment.1 */
    class C11951 extends OnScrollListener {
        C11951() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
            if (scrollState == 1) {
                KeyBoardUtils.hideKeyBoard(SearchFragment.this.getActivity());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }
    }

    /* renamed from: ru.ok.android.ui.search.fragment.SearchFragment.2 */
    class C11962 implements OnPreDrawListener {
        C11962() {
        }

        public boolean onPreDraw() {
            SearchFragment.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            SearchFragment.this.listView.animate(true, null);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.search.fragment.SearchFragment.3 */
    class C11973 implements Runnable {
        C11973() {
        }

        public void run() {
            SearchFragment.this.clearCurrentSearchResults();
            SearchFragment.this.doSearchQuery();
        }
    }

    public SearchFragment() {
        this.state = -1;
        this.scrollListener = new C11951();
    }

    static {
        LOAD_MORE_POSSIBLE_STATE = LoadMoreState.LOAD_POSSIBLE_NO_LABEL;
    }

    public static SearchFragment newInstance(boolean combineResults, SearchContext searchContext, SearchType... searchTypes) {
        String str = "New instance created for search context %s";
        Object[] objArr = new Object[1];
        objArr[0] = searchContext == null ? SearchContext.ALL.name() : searchContext.name();
        Logger.m173d(str, objArr);
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("sfrgmcntxt", searchContext == null ? SearchContext.ALL.ordinal() : searchContext.ordinal());
        if (searchTypes == null) {
            args.putIntArray("sfrgmtps", new int[]{SearchType.ALL.ordinal()});
        } else {
            args.putParcelableArray("sfrgmtps", searchTypes);
        }
        args.putBoolean("sfrgmcmbn", combineResults);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.m172d("onCreate called");
        this.searchContext = SearchContext.values()[getArguments().getInt("sfrgmcntxt")];
        this.searchTypes = castTypesArray(getArguments().getParcelableArray("sfrgmtps"));
        this.combineResults = getArguments().getBoolean("sfrgmcmbn");
        if (savedInstanceState != null) {
            Logger.m172d("Restoring instance");
            this.query = savedInstanceState.getString("sfrgmquery");
            this.searchContext = SearchContext.values()[savedInstanceState.getInt("sfrgmcntxt")];
            this.searchTypes = castTypesArray(savedInstanceState.getParcelableArray("sfrgmtps"));
            this.combineResults = savedInstanceState.getBoolean("sfrgmcmbn");
            this.searchResults = (SearchResults) savedInstanceState.getParcelable("sfrgmrslts");
            this.pendingAnchor = savedInstanceState.getString("sfrgmpndnch");
        }
    }

    private SearchType[] castTypesArray(Parcelable[] parcelables) {
        if (parcelables == null) {
            return null;
        }
        int length = parcelables.length;
        SearchType[] result = new SearchType[length];
        for (int i = 0; i < length; i++) {
            result[i] = (SearchType) parcelables[i];
        }
        return result;
    }

    protected int getLayoutId() {
        return 2130903202;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = LocalizationManager.inflate(getActivity(), getLayoutId(), container, false);
        this.listView = (SearchListView) this.rootView.findViewById(2131624731);
        this.listView.addOnScrollListener(this.scrollListener);
        this.emptyView = (SmartEmptyViewAnimated) this.rootView.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
        if (savedInstanceState != null) {
            setState(savedInstanceState.getInt("sfrgmstt"));
            this.listView.layoutManager.scrollToPosition(savedInstanceState.getInt("sfrgmlstpos"));
            boolean z = this.searchResults != null && this.searchResults.isHasMore();
            processHasMore(z);
        } else {
            setState(0);
        }
        return this.rootView;
    }

    private void processHasMore(boolean hasMore) {
        if (this.loadMoreRecyclerAdapter != null) {
            LoadMoreController controller = this.loadMoreRecyclerAdapter.getController();
            boolean loadPossible = !this.combineResults && hasMore;
            controller.setBottomPermanentState(loadPossible ? LOAD_MORE_POSSIBLE_STATE : LoadMoreState.LOAD_IMPOSSIBLE);
            controller.setBottomAutoLoad(loadPossible);
        }
    }

    public void onStubButtonClick(Type type) {
        doSearchQuery();
    }

    public void onItemClick(View view, int position) {
        this.searchAdapter.triggerAdapterItemClick(position);
    }

    public void onUserClicked(UserInfo userInfo) {
        if (getActivity() instanceof OnUserActionListener) {
            ((OnUserActionListener) getActivity()).onUserRequested(userInfo);
        }
    }

    public void onGroupClicked(GroupInfo groupInfo) {
        if (getActivity() instanceof OnUserActionListener) {
            ((OnUserActionListener) getActivity()).onGroupRequested(groupInfo);
        }
    }

    public void onCommunityClicked(GroupInfo groupInfo) {
        if (getActivity() instanceof OnUserActionListener) {
            ((OnUserActionListener) getActivity()).onCommunityRequested(groupInfo);
        }
    }

    public void onExpandClicked(SearchType type) {
        if (getActivity() instanceof OnUserActionListener) {
            ((OnUserActionListener) getActivity()).onShowContexted(type);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("sfrgmstt", this.state);
        outState.putCharSequence("sfrgmquery", this.query);
        outState.putInt("sfrgmcntxt", this.searchContext.ordinal());
        outState.putParcelableArray("sfrgmtps", this.searchTypes);
        outState.putBoolean("sfrgmcmbn", this.combineResults);
        outState.putParcelable("sfrgmrslts", this.searchResults);
        outState.putString("sfrgmpndnch", this.pendingAnchor);
        outState.putInt("sfrgmlstpos", this.listView.layoutManager.findFirstCompletelyVisibleItemPosition());
    }

    public void setState(int state) {
        boolean animateShow = true;
        if (this.state != state) {
            Logger.m173d("State will be changed from %s to %s", Integer.valueOf(this.state), Integer.valueOf(state));
            if (!(state == 2 && this.state == 1)) {
                animateShow = false;
            }
            this.state = state;
            updateViews();
            if (animateShow) {
                this.listView.getViewTreeObserver().addOnPreDrawListener(new C11962());
            }
        }
    }

    private LoadMoreRecyclerAdapter obtainLoadMoreAdapter(SearchResultsAdapter searchResultsAdapter) {
        this.loadMoreRecyclerAdapter = new LoadMoreRecyclerAdapter(getContext(), searchResultsAdapter, this, LoadMoreMode.BOTTOM);
        this.loadMoreRecyclerAdapter.getController().setConditionCallback(this);
        return this.loadMoreRecyclerAdapter;
    }

    protected void updateViews() {
        Logger.m173d("Views update requested with state %s", Integer.valueOf(this.state));
        Context context = this.listView.getContext();
        if (this.state == 2) {
            if (this.searchAdapter == null) {
                Logger.m172d("Creating new search adapter");
                if (this.combineResults) {
                    this.searchAdapter = new SearchResultsCombinedAdapter(context, this.searchResults.getFound());
                } else {
                    this.searchAdapter = new SearchResultsContextedAdapter(context, this.searchResults.getFound());
                }
                this.searchAdapter.setOnAdapterItemClickListener(this);
                this.searchAdapter.getItemClickListenerController().addItemClickListener(this);
                this.listView.setAdapter(obtainLoadMoreAdapter(this.searchAdapter));
            }
            this.emptyView.setVisibility(8);
            this.listView.setVisibility(0);
            return;
        }
        switch (this.state) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.emptyView.setState(State.LOADING);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.emptyView.setType(Type.SEARCH);
                this.emptyView.setState(State.LOADED);
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.emptyView.setType(Type.ERROR);
                this.emptyView.setState(State.LOADED);
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                this.emptyView.setType(Type.NO_INTERNET);
                this.emptyView.setState(State.LOADED);
                break;
        }
        this.listView.setVisibility(8);
        this.emptyView.setVisibility(0);
    }

    public void search(String query, boolean forceRequery, boolean hideResutsWithAnimation) {
        Logger.m173d("Search for \"%s\" requested", query);
        String trimmed = query != null ? query.trim() : "";
        if (forceRequery || !TextUtils.equals(trimmed, this.query)) {
            Logger.m172d("Query is changed");
            this.query = trimmed;
            this.searchResults = null;
            this.pendingAnchor = null;
            if (TextUtils.isEmpty(trimmed)) {
                Logger.m172d("Switching to idle state");
                clearCurrentSearchResults();
                setState(0);
                return;
            }
            if (this.loadMoreRecyclerAdapter != null) {
                this.loadMoreRecyclerAdapter.getController().setBottomPermanentState(LoadMoreState.LOADING);
            }
            if (this.state == 2 && hideResutsWithAnimation) {
                this.listView.animate(false, new C11973());
                return;
            }
            clearCurrentSearchResults();
            doSearchQuery();
        }
    }

    protected void clearCurrentSearchResults() {
        if (this.searchAdapter != null) {
            this.searchAdapter.clear();
        }
        if (this.loadMoreRecyclerAdapter != null) {
            this.loadMoreRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void doSearchQuery() {
        Logger.m172d("Search action requested");
        if (!TextUtils.isEmpty(this.query)) {
            Logger.m173d("Will search for query \"%s\"", this.query);
            requestSearch();
            setState(1);
            safeSetLoadMoreBottomState(LoadMoreState.LOADING);
        }
    }

    private void safeSetLoadMoreBottomState(LoadMoreState state) {
        if (this.loadMoreRecyclerAdapter != null) {
            this.loadMoreRecyclerAdapter.getController().setBottomPermanentState(state);
        }
    }

    protected final void loadMore() {
        if (this.pendingAnchor == null && this.searchResults != null && this.searchResults.isHasMore()) {
            this.pendingAnchor = this.searchResults.getAnchor();
            requestSearch();
            safeSetLoadMoreBottomState(LoadMoreState.LOADING);
        }
    }

    private void requestSearch() {
        Bundle bundleInput = new Bundle();
        bundleInput.putString("sqquery", this.query);
        bundleInput.putString("sqanchor", this.pendingAnchor);
        bundleInput.putSerializable("sqcontext", this.searchContext);
        bundleInput.putParcelableArray("sqtypes", this.searchTypes);
        bundleInput.putInt("sqcount", 40);
        GlobalBus.send(2131624113, new BusEvent(bundleInput));
    }

    public void onLoadMoreTopClicked() {
    }

    public void onLoadMoreBottomClicked() {
        loadMore();
    }

    @Subscribe(on = 2131623946, to = 2131624257)
    public final void onSearchResult(BusEvent event) {
        Logger.m172d("Recieved search results");
        String query = event.bundleInput.getString("sqquery");
        SearchContext searchContext = (SearchContext) event.bundleInput.getSerializable("sqcontext");
        SearchType[] searchTypes = (SearchType[]) event.bundleInput.getParcelableArray("sqtypes");
        String anchor = event.bundleInput.getString("sqanchor");
        if (this.loadMoreRecyclerAdapter != null) {
            this.loadMoreRecyclerAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
        }
        if (TextUtils.equals(query, this.query) && searchContext == this.searchContext && SearchUtils.typesMatch(searchTypes, this.searchTypes) && TextUtils.equals(anchor, this.pendingAnchor)) {
            Logger.m173d("Search results are relevant for current query \"%s\"", this.query);
            this.pendingAnchor = null;
            SearchResults results = (SearchResults) event.bundleOutput.getParcelable("sqresult");
            if (event.resultCode == -2 || event.resultCode == -3 || results == null) {
                Logger.m172d("Search returned with an error");
                if (this.searchResults == null || this.searchResults.getFound().isEmpty()) {
                    setState(event.resultCode == -3 ? 5 : 4);
                    safeSetLoadMoreBottomState(LoadMoreState.LOAD_IMPOSSIBLE);
                    return;
                }
                safeSetLoadMoreBottomState(LOAD_MORE_POSSIBLE_STATE);
                int toastResId = 2131165834;
                if (event.resultCode == -3) {
                    toastResId = 2131165984;
                }
                Toast.makeText(getActivity(), getStringLocalized(toastResId), 1).show();
            } else if (results.getFound().isEmpty()) {
                Logger.m172d("Nothing was found");
                setState(3);
                safeSetLoadMoreBottomState(LoadMoreState.LOAD_IMPOSSIBLE);
            } else {
                Logger.m172d("Non empty results returned");
                if (this.searchResults == null) {
                    this.searchResults = new SearchResults();
                    Logger.m172d("New search results holder created");
                }
                setState(2);
                this.searchResults.setHasMore(results.isHasMore());
                this.searchResults.setAnchor(results.getAnchor());
                this.searchResults.setSearchContext(results.getSearchContext());
                this.searchResults.getFound().addAll(results.getFound());
                this.searchAdapter.addResults(results.getFound());
                processHasMore(this.searchResults.isHasMore());
            }
        }
    }

    public boolean isTimeToLoadTop(int position, int count) {
        return false;
    }

    public boolean isTimeToLoadBottom(int position, int count) {
        return this.searchAdapter.shouldLoadMore(position);
    }
}
