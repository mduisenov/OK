package ru.ok.android.ui.users.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.adapters.friends.FriendsContainer;
import ru.ok.android.ui.custom.cards.listcard.CardItem;
import ru.ok.android.ui.custom.cards.listcard.CardItem.Type;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem.ItemRelationType;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.indicator.PagerSlidingTabStrip;
import ru.ok.android.ui.fragments.base.BaseRefreshFragment;
import ru.ok.android.ui.relations.RelationsAdapter;
import ru.ok.android.ui.relations.RelationsLoaderMy;
import ru.ok.android.ui.relations.RelationsLoaderUser;
import ru.ok.android.ui.relations.RelationsSpinnerAdapter;
import ru.ok.android.ui.relations.RelationsSpinnerAdapter.RelationsSpinnerContainer;
import ru.ok.android.ui.utils.EmptyViewRecyclerDataObserver;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.android.widget.ViewPagerDisable;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo;
import ru.ok.model.search.SearchResult;
import ru.ok.model.search.SearchResult.SearchScope;
import ru.ok.model.search.SearchResultUser;
import ru.ok.model.search.SearchResults;
import ru.ok.model.search.SearchResults.SearchContext;
import ru.ok.model.search.SearchType;

public final class FragmentFriends extends BaseRefreshFragment implements LoaderCallbacks<FriendsContainer>, OnStubButtonClickListener {
    private SmartEmptyViewAnimated emptyView;
    private Bundle errorBundle;
    private RelationsAdapter friendsAdapter;
    private CardItem friendsCard;
    private PagerSlidingTabStrip indicator;
    private FriendsContainer lastFriendsContainer;
    private RecyclerView listView;
    private Map<RelativesType, Set<String>> relativesSetMap;
    private Map<String, Set<RelativesType>> relativesSubtypeMap;
    private final RunSearch runSearch;
    private CardItem searchCard;
    private RelationsSpinnerAdapter spinnerAdapter;
    private CardItem suggestionsCard;
    private ViewPagerDisable viewPager;

    /* renamed from: ru.ok.android.ui.users.fragments.FragmentFriends.1 */
    class C12921 extends OnScrollListener {
        final /* synthetic */ LinearLayoutManager val$recyclerLayoutManager;

        C12921(LinearLayoutManager linearLayoutManager) {
            this.val$recyclerLayoutManager = linearLayoutManager;
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1) {
                KeyBoardUtils.hideKeyBoard(FragmentFriends.this.getActivity());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (this.val$recyclerLayoutManager.findLastVisibleItemPosition() > FragmentFriends.this.friendsAdapter.getItemCount() - 3) {
                FragmentFriends.this.runSearch.loadNext();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.FragmentFriends.2 */
    static /* synthetic */ class C12932 {
        static final /* synthetic */ int[] f120x22ae40df;

        static {
            f120x22ae40df = new int[ErrorType.values().length];
            try {
                f120x22ae40df[ErrorType.NO_INTERNET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f120x22ae40df[ErrorType.YOU_ARE_IN_BLACK_LIST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f120x22ae40df[ErrorType.USER_BLOCKED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f120x22ae40df[ErrorType.RESTRICTED_ACCESS_SECTION_FOR_FRIENDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    class RunSearch implements Runnable {
        final List<Bundle> bundles;
        volatile boolean isHasMore;
        volatile boolean isLoading;
        public final CardItem progressItem;
        private Handler searchHandler;
        String searchQuery;
        SearchUsers searchUsers;

        RunSearch() {
            this.progressItem = new CardItem().setType(Type.progressBar);
            this.searchHandler = new Handler(Looper.getMainLooper());
            this.isLoading = false;
            this.isHasMore = true;
            this.searchUsers = new SearchUsers();
            this.bundles = new ArrayList();
        }

        private void setHasMore(boolean isHasMore) {
            this.isHasMore = isHasMore;
            this.progressItem.setEnable(isHasMore);
        }

        public SearchUsers onSearchResult(BusEvent busEvent) {
            SearchUsers searchUsers;
            synchronized (this.bundles) {
                if (!this.bundles.isEmpty() && Utils.equalBundles(busEvent.bundleInput, (Bundle) this.bundles.get(this.bundles.size() - 1))) {
                    boolean z;
                    SearchResults searchResults = (SearchResults) busEvent.bundleOutput.getParcelable("sqresult");
                    List<SearchResult> found = searchResults == null ? null : searchResults.getFound();
                    if (busEvent.resultCode != -1 || searchResults == null || !searchResults.isHasMore() || found == null || found.isEmpty()) {
                        z = false;
                    } else {
                        z = true;
                    }
                    setHasMore(z);
                    if (!(found == null || found.isEmpty())) {
                        for (SearchResult searchResult : found) {
                            if (searchResult.getType() == SearchType.USER) {
                                UserInfo userInfo = ((SearchResultUser) searchResult).getUserInfo();
                                if (searchResult.getScope() == SearchScope.OWN) {
                                    this.searchUsers.friends.add(userInfo);
                                } else {
                                    this.searchUsers.portal.add(userInfo);
                                }
                            }
                        }
                    }
                    if (this.isHasMore) {
                        Bundle bundle = new Bundle();
                        bundle.putString("sqquery", this.searchQuery);
                        bundle.putSerializable("sqcontext", SearchContext.USER);
                        bundle.putString("sqanchor", searchResults == null ? "" : searchResults.getAnchor());
                        bundle.putParcelableArray("sqtypes", new SearchType[]{SearchType.USER});
                        this.bundles.add(bundle);
                    }
                }
                this.isLoading = false;
                searchUsers = this.searchUsers;
            }
            return searchUsers;
        }

        public boolean isSearchEmpty() {
            return !this.isHasMore && this.searchUsers.isEmpty();
        }

        public void loadNext() {
            synchronized (this.bundles) {
                if (FragmentFriends.this.isForMe() && !this.bundles.isEmpty() && !this.isLoading && this.isHasMore) {
                    this.isLoading = true;
                    GlobalBus.send(2131624113, new BusEvent((Bundle) this.bundles.get(this.bundles.size() - 1)));
                }
            }
        }

        public void run() {
            synchronized (this.bundles) {
                this.bundles.clear();
                this.searchUsers.clear();
                this.isLoading = false;
                setHasMore(true);
                if (!TextUtils.isEmpty(this.searchQuery)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("sqquery", this.searchQuery);
                    bundle.putSerializable("sqcontext", SearchContext.USER);
                    bundle.putParcelableArray("sqtypes", new SearchType[]{SearchType.USER});
                    this.bundles.add(bundle);
                }
                loadNext();
            }
        }
    }

    private static class SearchUsers {
        public List<UserInfo> friends;
        public List<UserInfo> portal;

        private SearchUsers() {
            this.portal = new ArrayList();
            this.friends = new ArrayList();
        }

        public void clear() {
            this.portal.clear();
            this.friends.clear();
        }

        public boolean isEmpty() {
            return this.portal.isEmpty() && this.friends.isEmpty();
        }
    }

    public FragmentFriends() {
        this.relativesSetMap = null;
        this.relativesSubtypeMap = null;
        this.suggestionsCard = null;
        this.friendsCard = null;
        this.searchCard = null;
        this.emptyView = null;
        this.lastFriendsContainer = null;
        this.runSearch = new RunSearch();
    }

    protected int getLayoutId() {
        return 2130903564;
    }

    public static Bundle newArguments(String userId, String relation) {
        Bundle bundle = new Bundle();
        bundle.putString("extra_user_id", userId);
        bundle.putString("relation", relation);
        return bundle;
    }

    protected CharSequence getTitle() {
        return null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalizedActivity activity = (LocalizedActivity) getActivity();
        this.friendsAdapter = new RelationsAdapter(activity);
        this.spinnerAdapter = new RelationsSpinnerAdapter(activity, this.friendsAdapter);
        this.spinnerAdapter.setPageViews(this.indicator, this.viewPager);
        if (isForMe()) {
            GlobalBus.send(2131624119, new BusEvent());
        }
        ActionBar actionBar = activity == null ? null : activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(1);
            actionBar.setListNavigationCallbacks(this.spinnerAdapter, this.spinnerAdapter);
        }
    }

    public void onRefresh() {
        if (isForMe()) {
            GlobalBus.send(2131624119, new BusEvent());
            return;
        }
        Loader<Object> loader = getLoaderManager().getLoader(666);
        loader.reset();
        loader.forceLoad();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(getLayoutId(), null);
        this.listView = (RecyclerView) view.findViewById(2131624731);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.listView.setLayoutManager(recyclerLayoutManager);
        if (!DeviceUtils.isSmall(getContext())) {
            this.listView.setBackgroundResource(2131493183);
        }
        this.listView.setAdapter(this.friendsAdapter);
        this.friendsAdapter.getItemClickListenerController().addItemClickListener(this.friendsAdapter);
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
        this.friendsAdapter.registerAdapterDataObserver(new EmptyViewRecyclerDataObserver(this.emptyView, this.friendsAdapter));
        this.spinnerAdapter.setFriendsList(this.listView);
        Set<RelationsSpinnerContainer> containers = new HashSet();
        containers.add(new RelationsSpinnerContainer(RelativesType.ALL, 0));
        this.spinnerAdapter.setData(containers);
        this.spinnerAdapter.notifyDataSetChanged();
        this.listView.addOnScrollListener(new C12921(recyclerLayoutManager));
        updateList();
        getLoaderManager().restartLoader(666, null, this).forceLoad();
        return view;
    }

    public void onStubButtonClick(SmartEmptyViewAnimated.Type type) {
        onRefresh();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateList();
    }

    public Loader<FriendsContainer> onCreateLoader(int i, Bundle bundle) {
        if (isForMe()) {
            return new RelationsLoaderMy(getActivity());
        }
        return new RelationsLoaderUser(getActivity(), getUserId());
    }

    private boolean isForMe() {
        String userId = getUserId();
        return "my()".equals(userId) || TextUtils.equals(userId, OdnoklassnikiApplication.getCurrentUser().getId());
    }

    private String getUserId() {
        return getArguments().getString("extra_user_id");
    }

    private String getRelation() {
        return getArguments().getString("relation");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLoadFinished(android.support.v4.content.Loader<ru.ok.android.ui.adapters.friends.FriendsContainer> r24, ru.ok.android.ui.adapters.friends.FriendsContainer r25) {
        /*
        r23 = this;
        r0 = r25;
        r1 = r23;
        r1.lastFriendsContainer = r0;
        r3 = r23.getActivity();
        if (r3 == 0) goto L_0x000e;
    L_0x000c:
        if (r25 != 0) goto L_0x0012;
    L_0x000e:
        r23.updateEmptyView();
    L_0x0011:
        return;
    L_0x0012:
        r19 = r23.isForMe();
        if (r19 != 0) goto L_0x002a;
    L_0x0018:
        r0 = r23;
        r0 = r0.lastFriendsContainer;
        r19 = r0;
        r0 = r19;
        r0 = r0.errorBundle;
        r19 = r0;
        r0 = r19;
        r1 = r23;
        r1.errorBundle = r0;
    L_0x002a:
        r19 = 2131166669; // 0x7f0705cd float:1.794759E38 double:1.0529362367E-314;
        r0 = r23;
        r1 = r19;
        r16 = r0.getStringLocalized(r1);
        r19 = r23.getResources();
        r20 = 2131493134; // 0x7f0c010e float:1.860974E38 double:1.053097532E-314;
        r13 = r19.getColor(r20);
        r19 = new ru.ok.android.utils.FriendlySpannableStringBuilder;
        r19.<init>();
        r20 = 1;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r22 = new android.text.style.ForegroundColorSpan;
        r0 = r22;
        r0.<init>(r13);
        r20[r21] = r22;
        r0 = r19;
        r1 = r16;
        r2 = r20;
        r15 = r0.append(r1, r2);
        r19 = new ru.ok.android.ui.custom.cards.listcard.CardItem;
        r19.<init>();
        r20 = ru.ok.android.ui.custom.cards.listcard.CardItem.Type.block;
        r19 = r19.setType(r20);
        r0 = r25;
        r0 = r0.suggestionsFriend;
        r20 = r0;
        r19 = r19.setInfoList(r20);
        r20 = r15.build();
        r19 = r19.setTitle(r20);
        r0 = r19;
        r1 = r23;
        r1.suggestionsCard = r0;
        r19 = 2131166244; // 0x7f070424 float:1.7946728E38 double:1.0529360267E-314;
        r0 = r19;
        r10 = r3.getString(r0);
        r19 = new ru.ok.android.ui.custom.cards.listcard.CardItem;
        r19.<init>();
        r0 = r25;
        r0 = r0.userInfoList;
        r20 = r0;
        r21 = ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem.ItemRelationType.friend;
        r19 = r19.setInfoList(r20, r21);
        r0 = r19;
        r19 = r0.setTitle(r10);
        r0 = r19;
        r1 = r23;
        r1.friendsCard = r0;
        r4 = new java.util.HashMap;
        r4.<init>();
        r0 = r25;
        r0 = r0.relativesSetMap;
        r19 = r0;
        r19 = r19.entrySet();
        r12 = r19.iterator();
    L_0x00be:
        r19 = r12.hasNext();
        if (r19 == 0) goto L_0x00e6;
    L_0x00c4:
        r9 = r12.next();
        r9 = (java.util.Map.Entry) r9;
        r19 = r9.getValue();
        r19 = (java.util.Set) r19;
        r19 = r19.size();
        if (r19 == 0) goto L_0x00be;
    L_0x00d6:
        r19 = r9.getKey();
        r20 = r9.getValue();
        r0 = r19;
        r1 = r20;
        r4.put(r0, r1);
        goto L_0x00be;
    L_0x00e6:
        r0 = r23;
        r0.relativesSetMap = r4;
        r7 = new java.util.HashSet;
        r7.<init>();
        r0 = r23;
        r0 = r0.relativesSetMap;
        r19 = r0;
        r19 = r19.keySet();
        r12 = r19.iterator();
    L_0x00fd:
        r19 = r12.hasNext();
        if (r19 == 0) goto L_0x0133;
    L_0x0103:
        r17 = r12.next();
        r17 = (ru.ok.java.api.request.relatives.RelativesType) r17;
        r0 = r23;
        r0 = r0.relativesSetMap;
        r19 = r0;
        r0 = r19;
        r1 = r17;
        r5 = r0.get(r1);
        r5 = (java.util.Collection) r5;
        r20 = new ru.ok.android.ui.relations.RelationsSpinnerAdapter$RelationsSpinnerContainer;
        if (r5 != 0) goto L_0x012e;
    L_0x011d:
        r19 = 0;
    L_0x011f:
        r0 = r20;
        r1 = r17;
        r2 = r19;
        r0.<init>(r1, r2);
        r0 = r20;
        r7.add(r0);
        goto L_0x00fd;
    L_0x012e:
        r19 = r5.size();
        goto L_0x011f;
    L_0x0133:
        r19 = new ru.ok.android.ui.relations.RelationsSpinnerAdapter$RelationsSpinnerContainer;
        r20 = ru.ok.java.api.request.relatives.RelativesType.ALL;
        r0 = r25;
        r0 = r0.userInfoList;
        r21 = r0;
        r21 = r21.size();
        r19.<init>(r20, r21);
        r0 = r19;
        r7.add(r0);
        r0 = r25;
        r0 = r0.relativesSubtypeMap;
        r19 = r0;
        r0 = r19;
        r1 = r23;
        r1.relativesSubtypeMap = r0;
        r8 = 0;
        r0 = r25;
        r0 = r0.userInfoList;
        r19 = r0;
        r12 = r19.iterator();
    L_0x0160:
        r19 = r12.hasNext();
        if (r19 == 0) goto L_0x017b;
    L_0x0166:
        r18 = r12.next();
        r18 = (ru.ok.model.UserInfo) r18;
        r19 = ru.ok.android.utils.Utils.onlineStatus(r18);
        r20 = ru.ok.model.UserInfo.UserOnlineType.OFFLINE;
        r0 = r19;
        r1 = r20;
        if (r0 == r1) goto L_0x0160;
    L_0x0178:
        r8 = r8 + 1;
        goto L_0x0160;
    L_0x017b:
        r19 = new ru.ok.android.ui.relations.RelationsSpinnerAdapter$RelationsSpinnerContainer;
        r20 = ru.ok.java.api.request.relatives.RelativesType.ONLINE;
        r0 = r19;
        r1 = r20;
        r0.<init>(r1, r8);
        r0 = r19;
        r7.add(r0);
        r0 = r23;
        r0 = r0.spinnerAdapter;
        r19 = r0;
        r0 = r19;
        r0.setData(r7);
        r14 = r23.getRelation();
        r19 = android.text.TextUtils.isEmpty(r14);
        if (r19 != 0) goto L_0x01e0;
    L_0x01a0:
        r17 = ru.ok.java.api.request.relatives.RelativesType.safeValueOf(r14);
        if (r17 == 0) goto L_0x01e0;
    L_0x01a6:
        r11 = 0;
    L_0x01a7:
        r0 = r23;
        r0 = r0.spinnerAdapter;
        r19 = r0;
        r19 = r19.getData();
        r19 = r19.size();
        r0 = r19;
        if (r11 >= r0) goto L_0x01e0;
    L_0x01b9:
        r0 = r23;
        r0 = r0.spinnerAdapter;
        r19 = r0;
        r19 = r19.getData();
        r0 = r19;
        r6 = r0.get(r11);
        r6 = (ru.ok.android.ui.relations.RelationsSpinnerAdapter.RelationsSpinnerContainer) r6;
        r0 = r6.relativesType;
        r19 = r0;
        r0 = r19;
        r1 = r17;
        if (r0 != r1) goto L_0x01f7;
    L_0x01d5:
        r3 = (ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity) r3;
        r19 = r3.getSupportActionBar();
        r0 = r19;
        r0.setSelectedNavigationItem(r11);
    L_0x01e0:
        r0 = r23;
        r0 = r0.spinnerAdapter;
        r19 = r0;
        r19.notifyDataSetChanged();
        r0 = r23;
        r0 = r0.refreshProvider;
        r19 = r0;
        r19.refreshCompleted();
        r23.updateList();
        goto L_0x0011;
    L_0x01f7:
        r11 = r11 + 1;
        goto L_0x01a7;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.users.fragments.FragmentFriends.onLoadFinished(android.support.v4.content.Loader, ru.ok.android.ui.adapters.friends.FriendsContainer):void");
    }

    public void onLoaderReset(Loader<FriendsContainer> loader) {
    }

    @Subscribe(on = 2131623946, to = 2131624257)
    public final void onSearchResultBySite(BusEvent busEvent) {
        SearchUsers searchUsersBySite = this.runSearch.onSearchResult(busEvent);
        if (searchUsersBySite != null) {
            this.searchCard = new CardItem().setInfoList(searchUsersBySite.portal, ItemRelationType.portal).setTitle(getStringSafe(2131166480)).setType(Type.list_search);
            this.friendsCard = new CardItem().setInfoList(searchUsersBySite.friends, ItemRelationType.friend).setTitle(getStringSafe(2131166244));
        } else {
            this.searchCard = null;
            this.friendsCard = null;
        }
        this.friendsAdapter.setFilterEnabled(false);
        updateList();
    }

    private String getStringSafe(int strResId) {
        Activity activity = getActivity();
        return activity == null ? null : activity.getString(strResId);
    }

    @Subscribe(on = 2131623946, to = 2131624263)
    public final void onFriendsFetched(BusEvent e) {
        this.errorBundle = e.resultCode == -1 ? null : e.bundleOutput;
        this.refreshProvider.refreshCompleted();
        updateEmptyView();
    }

    private void updateList() {
        List<CardItem> cardItems = new ArrayList(3);
        if (this.friendsCard != null) {
            if (this.suggestionsCard != null && this.friendsCard.size() > 0 && isForMe()) {
                cardItems.add(this.suggestionsCard);
            }
            cardItems.add(this.friendsCard);
        }
        if (this.searchCard != null) {
            cardItems.add(this.searchCard);
            cardItems.add(this.runSearch.progressItem);
        }
        this.friendsAdapter.setData(cardItems, this.relativesSetMap, this.relativesSubtypeMap);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (this.lastFriendsContainer == null) {
            this.emptyView.setState(State.LOADING);
            return;
        }
        this.emptyView.setState(State.LOADED);
        if (this.errorBundle != null) {
            SmartEmptyViewAnimated.Type emptyViewType;
            switch (C12932.f120x22ae40df[ErrorType.from(this.errorBundle).ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    emptyViewType = SmartEmptyViewAnimated.Type.NO_INTERNET;
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    emptyViewType = SmartEmptyViewAnimated.Type.RESTRICTED_YOU_ARE_IN_BLACK_LIST;
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    emptyViewType = SmartEmptyViewAnimated.Type.USER_BLOCKED;
                    break;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    emptyViewType = SmartEmptyViewAnimated.Type.RESTRICTED_ACCESS_FOR_FRIENDS;
                    break;
                default:
                    emptyViewType = SmartEmptyViewAnimated.Type.ERROR;
                    break;
            }
            this.emptyView.setType(emptyViewType);
        } else if (!this.runSearch.isSearchEmpty() || this.runSearch.isLoading) {
            this.emptyView.setType(isForMe() ? SmartEmptyViewAnimated.Type.FRIENDS_LIST_NO_BUTTON : SmartEmptyViewAnimated.Type.FRIENDS_LIST_USER);
        } else {
            this.emptyView.setType(SmartEmptyViewAnimated.Type.SEARCH);
        }
    }
}
