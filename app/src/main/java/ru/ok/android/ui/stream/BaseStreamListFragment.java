package ru.ok.android.ui.stream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.benchmark.StreamBenchmark;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.feeds.subscribe.StreamSubscriptionManager;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.StreamRecyclerView;
import ru.ok.android.ui.activity.StartVideoUploadActivity;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.loadmore.DefaultLoadMoreViewProvider;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreConditionCallbackImpl;
import ru.ok.android.ui.custom.loadmore.LoadMoreController;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreRecyclerAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreView;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.dialogs.ConfirmationDialog;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.ui.groups.list.StreamItemRecyclerAdapter;
import ru.ok.android.ui.mediatopic.view.MediaComposerPanel;
import ru.ok.android.ui.mediatopic.view.MediaComposerPanel.MediaComposerPanelListener;
import ru.ok.android.ui.stream.FeedHeaderActionsDialog.FeedHeaderActionsDialogListener;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.android.ui.stream.data.StreamData;
import ru.ok.android.ui.stream.data.StreamDataFragment;
import ru.ok.android.ui.stream.data.StreamDataFragment.StreamDataCallback;
import ru.ok.android.ui.stream.data.StreamListPosition;
import ru.ok.android.ui.stream.list.PromoLinkViewHolder;
import ru.ok.android.ui.stream.list.ShownOnScrollListener;
import ru.ok.android.ui.stream.list.StreamItem;
import ru.ok.android.ui.stream.list.StreamItemAdapter;
import ru.ok.android.ui.stream.list.StreamLayoutConfig;
import ru.ok.android.ui.stream.list.controller.RecyclerViewCallback;
import ru.ok.android.ui.stream.view.PromoLinkAndHolidayView;
import ru.ok.android.ui.stream.view.StreamScrollTopView;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.android.ui.tabbar.manager.BaseTabbarManager;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.ui.utils.FabHelper;
import ru.ok.android.ui.utils.RecyclerMergeAdapter;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusStreamHelper;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.StreamPage;
import ru.ok.model.stream.StreamPageKey;

public abstract class BaseStreamListFragment extends BaseStreamRefreshRecyclerFragment implements OnRefreshListener, OnLayoutChangeListener, LoadMoreAdapterListener, MediaComposerPanelListener, FeedHeaderActionsDialogListener, StreamDataCallback, ShownOnScrollListener {
    protected StreamDataFragment dataFragment;
    private boolean hasRestoredPosition;
    protected Adapter headersRecyclerAdapter;
    private StreamHolidayControl holidayControl;
    private int loadBottomLimitPosition;
    private int loadTopLimitPosition;
    protected MediaComposerPanel mediaComposerHidePanel;
    private StreamPromoLinkControl promoLinkControl;
    private boolean restoreSwipeRefreshingOnLayout;
    protected StreamListStatistics stats;
    protected Storages storages;
    protected StreamContext streamContext;
    protected StreamRecyclerView streamRecyclerView;
    protected SwipeRefreshLayout swipeRefresh;
    private UIHandler uiHandler;
    private boolean userHasTouchedListView;
    private final int[] xy;

    /* renamed from: ru.ok.android.ui.stream.BaseStreamListFragment.1 */
    class C12151 implements Runnable {
        C12151() {
        }

        public void run() {
            if (BaseStreamListFragment.this.dataFragment.isLoading()) {
                BaseStreamListFragment.this.swipeRefresh.setRefreshing(true);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.stream.BaseStreamListFragment.2 */
    class C12162 implements OnTouchListener {
        C12162() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            BaseStreamListFragment.this.userHasTouchedListView = true;
            return false;
        }
    }

    /* renamed from: ru.ok.android.ui.stream.BaseStreamListFragment.3 */
    class C12173 implements OnStubButtonClickListener {
        C12173() {
        }

        public void onStubButtonClick(Type type) {
            BaseStreamListFragment.this.onRetryClick();
        }
    }

    /* renamed from: ru.ok.android.ui.stream.BaseStreamListFragment.4 */
    class C12184 extends LoadMoreConditionCallbackImpl {
        C12184() {
        }

        public boolean isTimeToLoadBottom(int position, int count) {
            return BaseStreamListFragment.this.isTimeToLoadBottom();
        }

        public boolean isTimeToLoadTop(int position, int count) {
            return BaseStreamListFragment.this.isTimeToLoadTop();
        }
    }

    /* renamed from: ru.ok.android.ui.stream.BaseStreamListFragment.5 */
    static /* synthetic */ class C12195 {
        static final /* synthetic */ int[] f118x22ae40df;

        static {
            f118x22ae40df = new int[ErrorType.values().length];
            try {
                f118x22ae40df[ErrorType.NO_INTERNET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f118x22ae40df[ErrorType.RESTRICTED_ACCESS_SECTION_FOR_FRIENDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f118x22ae40df[ErrorType.USER_BLOCKED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f118x22ae40df[ErrorType.YOU_ARE_IN_BLACK_LIST.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public interface AdapterWithPromoLinks {
        void setPromoLinksHolder(PromoLinkViewHolder promoLinkViewHolder);
    }

    private class HeadersRecyclerAdapter extends Adapter implements AdapterWithPromoLinks, AdapterItemViewTypeMaxValueProvider {
        private PromoLinkViewHolder promoLinkViewHolder;

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return this.promoLinkViewHolder;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
        }

        public int getItemViewType(int position) {
            return 1;
        }

        public int getItemCount() {
            return 1;
        }

        public void setPromoLinksHolder(PromoLinkViewHolder holder) {
            this.promoLinkViewHolder = holder;
        }

        public int getItemViewTypeMaxValue() {
            return 1;
        }
    }

    class StreamLoadMoreRecyclerAdapter<TAdapter extends Adapter & AdapterItemViewTypeMaxValueProvider> extends LoadMoreRecyclerAdapter {
        private final TAdapter streamRecyclerAdapter;

        /* renamed from: ru.ok.android.ui.stream.BaseStreamListFragment.StreamLoadMoreRecyclerAdapter.1 */
        class C12201 extends DefaultLoadMoreViewProvider {
            final /* synthetic */ BaseStreamListFragment val$this$0;

            C12201(BaseStreamListFragment baseStreamListFragment) {
                this.val$this$0 = baseStreamListFragment;
            }

            public LoadMoreView createLoadMoreView(Context context, boolean isTopView, ViewGroup parent) {
                LoadMoreView view = super.createLoadMoreView(context, isTopView, parent);
                if (!isTopView) {
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), this.val$this$0.getResources().getDimensionPixelOffset(2131230999));
                }
                return view;
            }
        }

        public StreamLoadMoreRecyclerAdapter(TAdapter streamRecyclerAdapter) {
            super(BaseStreamListFragment.this.getContext(), streamRecyclerAdapter, BaseStreamListFragment.this, LoadMoreMode.BOTH, new C12201(BaseStreamListFragment.this));
            this.streamRecyclerAdapter = streamRecyclerAdapter;
        }
    }

    private class StreamWithHeadersLoadMoreRecyclerViewCallback implements RecyclerViewCallback {
        private StreamWithHeadersLoadMoreRecyclerViewCallback() {
        }

        public int findFirstVisibleStreamItemPosition() {
            return BaseStreamListFragment.this.recyclerLayoutManager.findFirstVisibleItemPosition() - BaseStreamListFragment.this.getRecyclerAdapterStreamItemsTopOffset();
        }

        public int findLastVisibleStreamItemPosition() {
            return BaseStreamListFragment.this.recyclerLayoutManager.findLastVisibleItemPosition() - BaseStreamListFragment.this.getRecyclerAdapterStreamItemsTopOffset();
        }
    }

    private class UIHandler extends Handler {
        private UIHandler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    BaseStreamListFragment.this.handleInternetAvailable();
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    BaseStreamListFragment.this.showMarkAsSpamSuccesful();
                default:
            }
        }

        void postOnConnectionAvailable() {
            removeMessages(1);
            sendEmptyMessageDelayed(1, 3000);
        }

        void postMarkAsSpamSuccessful() {
            removeMessages(2);
            sendEmptyMessageDelayed(2, 1000);
        }
    }

    protected abstract StreamContext createStreamContext();

    protected abstract Collection<? extends GeneralUserInfo> getFilteredUsers();

    protected abstract boolean isMediaPostPanelRequired();

    public BaseStreamListFragment() {
        this.uiHandler = new UIHandler();
        this.userHasTouchedListView = false;
        this.hasRestoredPosition = false;
        this.loadBottomLimitPosition = -1;
        this.loadTopLimitPosition = -1;
        this.xy = new int[2];
    }

    public boolean isAdapterManualProcessing() {
        return true;
    }

    public void onCreate(Bundle savedInstanceState) {
        Logger.m173d("savedInstanceState=%s", savedInstanceState);
        super.onCreate(savedInstanceState);
        Context context = getContext();
        this.stats = new StreamListStatistics();
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            this.userHasTouchedListView = savedInstanceState.getBoolean("user_touched_list_view", false);
        }
        this.streamContext = createStreamContext();
        this.storages = Storages.getInstance(context, OdnoklassnikiApplication.getCurrentUser().getId());
        initDataFragment(savedInstanceState);
        initRecyclerAdapter();
    }

    protected void initDataFragment(Bundle savedInstanceState) {
        if (Logger.isLoggingEnable()) {
            Logger.m173d(">>> savedInstanceState=%s", savedInstanceState);
            if (savedInstanceState != null) {
                for (String key : savedInstanceState.keySet()) {
                    Logger.m173d("initDataFragment: %s=%s", key, savedInstanceState.get(key));
                }
            }
        }
        FragmentManager fragmentManager = getFragmentManager();
        StreamDataFragment dataFragment = (StreamDataFragment) fragmentManager.findFragmentByTag("stream_data");
        StreamListPosition savedPosition = null;
        if (savedInstanceState != null) {
            savedPosition = (StreamListPosition) savedInstanceState.getParcelable("position");
        }
        if (dataFragment == null) {
            Logger.m172d("creating new data fragment");
            dataFragment = new StreamDataFragment();
            fragmentManager.beginTransaction().add((Fragment) dataFragment, "stream_data").commit();
        }
        Logger.m173d("initDataFragment: setArguments: savedPosition=%s", savedPosition);
        dataFragment.setArguments(this.streamContext, savedPosition);
        dataFragment.setCallback(this);
        this.dataFragment = dataFragment;
        Logger.m172d("initDataFragment <<<");
    }

    @Nullable
    protected StreamListPosition getCurrentPosition() {
        if (this.recyclerView == null || this.recyclerView.getChildCount() == 0) {
            return null;
        }
        int firstVisiblePosition = this.recyclerLayoutManager.findFirstVisibleItemPosition() - getRecyclerAdapterStreamItemsTopOffset();
        if (firstVisiblePosition < 0 || firstVisiblePosition >= this.streamItemRecyclerAdapter.getItemCount()) {
            return null;
        }
        StreamItem firstItem = this.streamItemRecyclerAdapter.getItem(firstVisiblePosition);
        StreamPageKey pageKey = firstItem.feedWithState == null ? null : firstItem.feedWithState.feed.getPageKey();
        Logger.m173d("pageKey=%s itemId=%d itemViewTop=%d position=%d", pageKey, Long.valueOf(firstItem.getId()), Integer.valueOf(this.recyclerView.getChildAt(0).getTop()), Integer.valueOf(firstVisiblePosition));
        if (pageKey != null) {
            return new StreamListPosition(pageKey, firstItem.getId(), itemViewTop, firstVisiblePosition);
        }
        return null;
    }

    public void onSaveInstanceState(Bundle outState) {
        Logger.m173d("userHasTouchedListView=%s", Boolean.valueOf(this.userHasTouchedListView));
        super.onSaveInstanceState(outState);
        outState.putBoolean("user_touched_list_view", this.userHasTouchedListView);
        outState.putParcelable("position", getCurrentPosition());
        if (this.swipeRefresh != null) {
            outState.putBoolean("swipe_refreshing", this.swipeRefresh.isRefreshing());
        }
    }

    public void onDestroy() {
        Logger.m172d("");
        super.onDestroy();
        if (this.adapter != null) {
            this.streamItemRecyclerAdapter.close();
        }
        if (this.statHandler != null) {
            this.statHandler.dispose();
        }
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (this.swipeRefresh != null) {
            if (this.dataFragment.isLoading() && this.restoreSwipeRefreshingOnLayout) {
                this.swipeRefresh.post(new C12151());
            }
            this.restoreSwipeRefreshingOnLayout = false;
        }
    }

    public void onRefresh() {
        Logger.m172d("onRefresh >>>");
        if (getActivity() == null) {
            Logger.m184w("onRefresh: activity is null");
        } else if (this.dataFragment.isLoading()) {
            Logger.m184w("onRefresh: data fragment is loading");
            if (this.swipeRefresh != null) {
                this.swipeRefresh.setRefreshing(false);
            }
            if (this.holidayControl != null) {
                this.holidayControl.nextHoliday();
            }
        } else {
            if (this.dataFragment.refresh()) {
                if (this.swipeRefresh != null) {
                    this.swipeRefresh.setRefreshing(true);
                }
                if (this.streamItemRecyclerAdapter == null || this.streamItemRecyclerAdapter.getItemCount() == 0) {
                    this.emptyView.setState(State.LOADING);
                }
            }
            Logger.m173d("onRefresh <<< startedRefresh=%s", Boolean.valueOf(startedRefresh));
        }
    }

    private void initBannerHeaderView(RecyclerView recyclerView) {
        if (this.headersRecyclerAdapter instanceof AdapterWithPromoLinks) {
            PromoLinkAndHolidayView promoLinkAndHolidayView = new PromoLinkAndHolidayView(getActivity());
            PromoLinkViewHolder promoLinkViewHolder = new PromoLinkViewHolder(promoLinkAndHolidayView);
            ((AdapterWithPromoLinks) this.headersRecyclerAdapter).setPromoLinksHolder(promoLinkViewHolder);
            this.promoLinkControl = new StreamPromoLinkControl(promoLinkViewHolder, this.streamItemRecyclerAdapter);
            this.promoLinkControl.onAttach(getActivity());
            if (this.streamContext.type == 1) {
                this.holidayControl = new StreamHolidayControl(promoLinkAndHolidayView, getActivity());
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.m172d("");
        View mainView = super.onCreateView(inflater, container, savedInstanceState);
        this.streamRecyclerView = (StreamRecyclerView) mainView.findViewById(2131624731);
        onPreSetHeaderBannerView(this.recyclerView);
        mainView.setBackgroundColor(getResources().getColor(2131493183));
        this.recyclerViewScrollListeners.addListener(this.streamItemRecyclerAdapter.getScrollListener());
        this.recyclerViewScrollListeners.addListener(this.stats.getScrollListener());
        this.recyclerView.setOnTouchListener(new C12162());
        this.recyclerView.setClipToPadding(false);
        this.recyclerView.setVisibility(8);
        this.emptyView.setState(State.LOADING);
        this.emptyView.setVisibility(0);
        this.emptyView.setButtonClickListener(new C12173());
        this.swipeRefresh = (SwipeRefreshLayout) mainView.findViewById(2131624611);
        if (this.swipeRefresh != null) {
            this.swipeRefresh.setOnRefreshListener(this);
        }
        updateMediaPostPanel(mainView);
        return mainView;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (this.promoLinkControl != null) {
            this.promoLinkControl.onAttach(activity);
        }
    }

    public void onDetach() {
        super.onDetach();
        if (this.promoLinkControl != null) {
            this.promoLinkControl.onDetach();
        }
    }

    protected int getLayoutId() {
        return 2130903367;
    }

    private boolean mediaPostPanelEnabled() {
        return isMediaPostPanelRequired();
    }

    protected void onPreSetHeaderBannerView(RecyclerView recyclerView) {
    }

    protected void updateMediaPostPanel(View view) {
        updateMediaPostPanel(view, false);
    }

    protected void updateMediaPostPanel(View view, boolean orientationChanged) {
        if (mediaPostPanelEnabled()) {
            onCreateMediaPostPanel(view);
            if (this.mediaComposerHidePanel != null) {
                if (orientationChanged) {
                    this.mediaComposerHidePanel.updateLayoutOnOrientationChange();
                }
                this.mediaComposerHidePanel.setVisibility(0);
            }
        } else if (this.mediaComposerHidePanel != null) {
            this.mediaComposerHidePanel.setVisibility(8);
        }
    }

    public void onViewCreated(View view1, Bundle savedInstanceState) {
        boolean z = false;
        Logger.m172d("");
        super.onViewCreated(view1, savedInstanceState);
        initStreamHeaderViews();
        this.emptyView.setState(State.LOADING);
        if (savedInstanceState != null && savedInstanceState.getBoolean("swipe_refreshing", false) && this.dataFragment.isInitialized() && this.dataFragment.isLoading()) {
            z = true;
        }
        this.restoreSwipeRefreshingOnLayout = z;
    }

    protected void initStreamHeaderViews() {
        initBannerHeaderView(this.recyclerView);
        initAppPollHeaderView(this.recyclerView);
    }

    protected void updateStreamLayout() {
        super.updateStreamLayout();
        if (getActivity() != null) {
            updateStreamHeaderViewsForStreamLayout();
        }
    }

    protected void updateStreamHeaderViewsForStreamLayout() {
        updatePromoLinkHeaderViewForLayout();
        updateAppPollHeaderViewForLayout(this.layoutConfig);
    }

    private void updatePromoLinkHeaderViewForLayout() {
        if (this.promoLinkControl != null) {
            this.promoLinkControl.updateLayout(this.layoutConfig);
        }
    }

    private void onCreateMediaPostPanel(View fragmentView) {
        if (this.mediaComposerHidePanel == null) {
            this.mediaComposerHidePanel = FabHelper.createMediaComposerPanel(getContext(), getCoordinatorManager().coordinatorLayout, fragmentView);
            this.mediaComposerHidePanel.setListener(this);
        }
    }

    protected void showHasNewFeeds(int newFeedsCount) {
        Logger.m173d("newFeedsCount=%d", Integer.valueOf(newFeedsCount));
        StreamScrollTopView scrollTopView = this.scrollTopView;
        if (scrollTopView != null) {
            scrollTopView.setNewEventCount(newFeedsCount);
        }
    }

    protected void hideHasNewFeeds() {
        Logger.m172d("");
        if (this.scrollTopView != null) {
            this.scrollTopView.setNewEventCount(0);
        }
    }

    protected String getTitle() {
        return getStringLocalized(2131165852);
    }

    protected void onHideFragment() {
        Logger.m172d("");
        super.onHideFragment();
    }

    protected void onShowFragment() {
        Logger.m172d("");
        super.onShowFragment();
        if (this.mediaComposerHidePanel.isExpanded()) {
            this.mediaComposerHidePanel.applyStateCollapsed();
        }
    }

    protected void ensureFab() {
        super.ensureFab();
        if (mediaPostPanelEnabled()) {
            getCoordinatorManager().ensureFab(this.mediaComposerHidePanel, "fab_stream");
        }
    }

    protected void removeFab() {
        super.removeFab();
        if (mediaPostPanelEnabled()) {
            getCoordinatorManager().remove(this.mediaComposerHidePanel);
        }
    }

    protected void onConfigurationOrientationChanged() {
        super.onConfigurationOrientationChanged();
        updateMediaPostPanel(getView(), true);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.supportInvalidateOptionsMenu();
        }
    }

    public void refresh() {
        Logger.m172d("");
        if (this.dataFragment != null) {
            this.dataFragment.refresh();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625510:
                Logger.m172d("selected Refresh action");
                StatisticManager.getInstance().addStatisticEvent("refresh_menu", new Pair[0]);
                if (this.dataFragment != null) {
                    this.dataFragment.refresh();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onInternetAvailable() {
        Logger.m172d("");
        this.uiHandler.postOnConnectionAvailable();
    }

    private void handleInternetAvailable() {
        Logger.m172d("");
        if (getActivity() != null && this.loadMoreAdapter != null && this.dataFragment != null) {
            LoadMoreController controller = this.loadMoreAdapter.getController();
            controller.setAutoLoadSuppressed(false);
            StreamData data = this.dataFragment.getData();
            if (!(controller.getBottomCurrentState() == LoadMoreState.LOADING || controller.getBottomPermanentState() == LoadMoreState.LOAD_IMPOSSIBLE)) {
                if (data.canLoadBottom()) {
                    controller.setBottomPermanentState(LoadMoreState.LOAD_POSSIBLE);
                    controller.setBottomAutoLoad(true);
                    if (isTimeToLoadBottom()) {
                        controller.startBottomLoading();
                    }
                } else {
                    controller.setBottomPermanentState(LoadMoreState.LOAD_IMPOSSIBLE);
                    controller.setBottomAutoLoad(false);
                }
            }
            if (controller.getTopCurrentState() != LoadMoreState.LOADING && controller.getTopPermanentState() != LoadMoreState.LOAD_IMPOSSIBLE) {
                if (data.canLoadTop()) {
                    controller.setTopPermanentState(LoadMoreState.LOAD_POSSIBLE);
                    controller.setTopAutoLoad(true);
                    if (isTimeToLoadTop()) {
                        controller.startTopLoading();
                        return;
                    }
                    return;
                }
                controller.setTopPermanentState(LoadMoreState.LOAD_IMPOSSIBLE);
                controller.setTopAutoLoad(false);
            }
        }
    }

    protected void reCalculateLimitPositions(StreamData data) {
        int totalItems = data.getItems().size();
        int totalFeeds = data.feeds.size();
        if (totalFeeds != 0 && totalItems != 0) {
            float avgItemsPerFeed = ((float) totalItems) / ((float) totalFeeds);
            this.loadBottomLimitPosition = Math.max(0, totalItems - ((int) ((((float) ((StreamPage) data.pages.getLast()).feeds.size()) * avgItemsPerFeed) * 0.5f)));
            this.loadTopLimitPosition = Math.min(totalItems - 1, (int) ((((float) ((StreamPage) data.pages.getFirst()).feeds.size()) * avgItemsPerFeed) * 0.5f));
        } else if (data.canHaveData()) {
            this.loadBottomLimitPosition = 0;
            this.loadTopLimitPosition = totalItems - 1;
        } else {
            this.loadBottomLimitPosition = -1;
            this.loadTopLimitPosition = -1;
        }
    }

    public void onLoadMoreTopClicked() {
        Logger.m172d("");
        if (this.dataFragment != null) {
            this.dataFragment.loadTop();
        }
    }

    public void onLoadMoreBottomClicked() {
        Logger.m172d("");
        if (this.dataFragment != null) {
            this.dataFragment.loadBottom();
        }
    }

    public void onScrollTopClick(int count) {
        Logger.m173d("count=%d", Integer.valueOf(count));
        super.onScrollTopClick(count);
    }

    protected Adapter createRecyclerAdapter() {
        this.streamItemRecyclerAdapter = new StreamItemRecyclerAdapter(getActivity(), obtainStreamItemViewController(getActivity(), this, getLogContext()), this, this.statHandler, getLogContext());
        this.streamItemRecyclerAdapter.setRecyclerViewCallback(new StreamWithHeadersLoadMoreRecyclerViewCallback());
        this.streamItemRecyclerAdapter.getStreamItemViewController().setFeedHeaderViewListener(this);
        this.streamItemRecyclerAdapter.getStreamItemViewController().setFeedReshareHeaderViewListener(this);
        this.streamItemRecyclerAdapter.setHasStableIds(true);
        this.streamItemRecyclerAdapter.getStreamItemViewController().setShownOnScrollListener(this);
        this.loadMoreAdapter = new StreamLoadMoreRecyclerAdapter(this.streamItemRecyclerAdapter);
        initLoadMoreAdapter();
        RecyclerMergeAdapter mergeAdapter = new RecyclerMergeAdapter();
        this.headersRecyclerAdapter = createHeaderRecyclerAdapter();
        mergeAdapter.addAdapter(this.headersRecyclerAdapter);
        mergeAdapter.addAdapter(this.loadMoreAdapter);
        return mergeAdapter;
    }

    @NonNull
    protected Adapter createHeaderRecyclerAdapter() {
        return new HeadersRecyclerAdapter();
    }

    protected void initLoadMoreAdapter() {
        LoadMoreController loadMoreController = this.loadMoreAdapter.getController();
        loadMoreController.setBottomPermanentState(LoadMoreState.DISABLED);
        loadMoreController.setBottomAutoLoad(true);
        loadMoreController.setTopPermanentState(LoadMoreState.DISABLED);
        loadMoreController.setTopAutoLoad(true);
        loadMoreController.setConditionCallback(new C12184());
    }

    protected int getHeadersCount() {
        return this.headersRecyclerAdapter != null ? this.headersRecyclerAdapter.getItemCount() : 0;
    }

    protected String getLogContext() {
        return this.streamContext.logContext;
    }

    private boolean isTimeToLoadTop() {
        if (this.loadTopLimitPosition < 0 || this.loadMoreAdapter == null) {
            return false;
        }
        if (this.dataFragment.getData().getItems().isEmpty()) {
            return true;
        }
        int firstVisiblePosition = this.recyclerLayoutManager.findFirstVisibleItemPosition();
        int itemsTopOffset = getRecyclerAdapterStreamItemsTopOffset();
        if (firstVisiblePosition == -1 || firstVisiblePosition < itemsTopOffset) {
            Logger.m172d("empty or hit top load more view");
            return true;
        }
        Logger.m173d("topPosition=%d loadTopLimitPosition=%d", Integer.valueOf(firstVisiblePosition - itemsTopOffset), Integer.valueOf(this.loadTopLimitPosition));
        if (firstVisiblePosition - itemsTopOffset > this.loadTopLimitPosition) {
            return false;
        }
        return true;
    }

    private boolean isTimeToLoadBottom() {
        if (this.loadBottomLimitPosition < 0 || this.loadMoreAdapter == null) {
            return false;
        }
        if (this.dataFragment.getData().getItems().isEmpty()) {
            return true;
        }
        int lastVisiblePosition = this.recyclerLayoutManager.findLastVisibleItemPosition();
        int itemsTopOffset = getRecyclerAdapterStreamItemsTopOffset();
        if (lastVisiblePosition == -1 || lastVisiblePosition < itemsTopOffset) {
            return false;
        }
        Logger.m173d("bottomPosition=%d loadBottomLimitPosition=%d", Integer.valueOf(lastVisiblePosition - itemsTopOffset), Integer.valueOf(this.loadBottomLimitPosition));
        if (lastVisiblePosition - itemsTopOffset < this.loadBottomLimitPosition) {
            return false;
        }
        return true;
    }

    private void setDataToAdapter(StreamData data, boolean isRefresh) {
        setDataToAdapter(data, isRefresh, 0, -1);
    }

    protected void setDataToAdapter(StreamData data, boolean isRefresh, int updateType, int newItemsCount) {
        boolean scrollToTopForPromoLink;
        boolean isEmpty;
        int i;
        boolean scrollToTop = isRefresh;
        if (this.userHasTouchedListView || this.hasRestoredPosition || data.headerBanners == null || data.headerBanners.isEmpty()) {
            scrollToTopForPromoLink = false;
        } else {
            scrollToTopForPromoLink = true;
        }
        if (data.canHaveData()) {
            isEmpty = false;
        } else {
            isEmpty = true;
        }
        RecyclerView recyclerView = this.recyclerView;
        if (isEmpty) {
            i = 8;
        } else {
            i = 0;
        }
        recyclerView.setVisibility(i);
        reCalculateLimitPositions(data);
        this.streamItemRecyclerAdapter.setItems(data.items);
        setLoadMoreStateFromData(data, null);
        if (this.recyclerView.getAdapter() == null) {
            this.recyclerView.setAdapter(this.adapter);
            this.streamItemRecyclerAdapter.updateForLayoutSize(this.recyclerView, this.layoutConfig, false);
            this.adapter.notifyDataSetChanged();
        } else if (updateType == 2) {
            this.adapter.notifyItemRangeInserted((getRecyclerAdapterStreamItemsTopOffset() + this.streamItemRecyclerAdapter.getItemCount()) - newItemsCount, newItemsCount);
        } else if (updateType == 1) {
            this.adapter.notifyItemRangeInserted(0, newItemsCount);
        } else {
            this.adapter.notifyDataSetChanged();
        }
        updateStreamHeaderViewsDataIfNecessary(data);
        if (scrollToTop) {
            this.recyclerView.scrollToPosition(0);
        } else if (scrollToTopForPromoLink) {
            this.recyclerView.smoothScrollToPosition(0);
        }
    }

    private void updateStreamHeaderViewsDataIfNecessary(@NonNull StreamData data) {
        if (this.promoLinkControl != null) {
            this.promoLinkControl.updatePromoLinks(data.headerBanners);
        }
        if (this.holidayControl != null) {
            this.holidayControl.updateHolidays(data.holidays);
        }
        updateAppPoll(data.getItems().isEmpty());
    }

    private void setLoadMoreStateFromData(StreamData data, @Nullable ErrorType error) {
        LoadMoreState loadMoreState;
        LoadMoreController controller = this.loadMoreAdapter.getController();
        controller.setTopCurrentState(LoadMoreState.IDLE);
        controller.setBottomCurrentState(LoadMoreState.IDLE);
        boolean canLoadBottom = data.canLoadBottom();
        boolean canLoadTop = data.canLoadTop();
        controller.setTopAutoLoad(canLoadTop);
        controller.setBottomAutoLoad(canLoadBottom);
        LoadMoreState loadPossibleState = LoadMoreState.LOAD_POSSIBLE;
        LoadMoreState loadImpossibleState = LoadMoreState.LOAD_IMPOSSIBLE;
        if (error != null) {
            loadPossibleState = LoadMoreState.DISCONNECTED;
            controller.setAutoLoadSuppressed(true);
            int errorMsgId = error == ErrorType.NO_INTERNET ? 0 : getShortTextForLoadMoreView(error);
            controller.setBottomMessageForState(LoadMoreState.DISCONNECTED, errorMsgId);
            controller.setTopMessageForState(LoadMoreState.DISCONNECTED, errorMsgId);
        }
        if (canLoadBottom) {
            loadMoreState = loadPossibleState;
        } else {
            loadMoreState = loadImpossibleState;
        }
        controller.setBottomPermanentState(loadMoreState);
        if (!canLoadTop) {
            loadPossibleState = loadImpossibleState;
        }
        controller.setTopPermanentState(loadPossibleState);
    }

    protected void resetRefreshAndEmptyView(boolean isDataEmpty) {
        int i = 0;
        if (this.swipeRefresh != null) {
            this.swipeRefresh.setRefreshing(false);
        }
        if (this.emptyView != null) {
            if (isDataEmpty) {
                this.emptyView.setType(getEmptyViewType());
            }
            this.emptyView.setState(State.LOADED);
            SmartEmptyViewAnimated smartEmptyViewAnimated = this.emptyView;
            if (!isDataEmpty) {
                i = 8;
            }
            smartEmptyViewAnimated.setVisibility(i);
        }
    }

    @NonNull
    protected Type getEmptyViewType() {
        return Type.STREAM;
    }

    public void onInitialDataLoaded(StreamData data, StreamListPosition restoredPosition, int benchmarkSeqId) {
        int i = 1;
        Logger.m173d(">>> data=%s restoredPosition=%s benchmarkSeqId=%d", data, restoredPosition, Integer.valueOf(benchmarkSeqId));
        if (getActivity() != null) {
            resetRefreshAndEmptyView(!data.canHaveData());
            boolean z = this.hasRestoredPosition;
            if (restoredPosition == null) {
                i = 0;
            }
            this.hasRestoredPosition = z | i;
            setDataToAdapter(data, false);
            if (restoredPosition != null) {
                this.recyclerLayoutManager.scrollToPositionWithOffset(this.loadMoreAdapter.getController().getExtraTopElements() + restoredPosition.adapterPosition, restoredPosition.viewTop);
            }
            StreamBenchmark.display(benchmarkSeqId);
            Logger.m172d("<<<");
        }
    }

    public void onStreamRefreshed(StreamData data, int benchmarkSeqId) {
        boolean z = false;
        Logger.m173d(">>> data=%s benchmarkSeqId=%d", data, Integer.valueOf(benchmarkSeqId));
        if (getActivity() != null) {
            if (!data.canHaveData()) {
                z = true;
            }
            resetRefreshAndEmptyView(z);
            setDataToAdapter(data, true);
            StreamBenchmark.display(benchmarkSeqId);
            if (this.holidayControl != null) {
                this.holidayControl.nextHoliday();
            }
            scrollToTop();
            appBarExpand();
            Logger.m172d("<<<");
        }
    }

    public void onDeletedFeeds(StreamData data) {
        boolean z = true;
        Logger.m173d(">>> data=%s", data);
        if (getActivity() != null) {
            if (data.canHaveData()) {
                z = false;
            }
            resetRefreshAndEmptyView(z);
            setDataToAdapter(data, false);
            Logger.m172d("<<<");
        }
    }

    public void onAddedBottomChunk(StreamData data, int newItemsCount, int benchmarkSeqId) {
        boolean z = true;
        Logger.m173d(">>> data=%s newItemsCount=%d benchmarkSeqId=%d", data, Integer.valueOf(newItemsCount), Integer.valueOf(benchmarkSeqId));
        if (getActivity() != null) {
            if (data.canHaveData()) {
                z = false;
            }
            resetRefreshAndEmptyView(z);
            setDataToAdapter(data, false, 2, newItemsCount);
            StreamBenchmark.display(benchmarkSeqId);
            if (newItemsCount == 0 && data.canLoadBottom() && isTimeToLoadBottom()) {
                this.loadMoreAdapter.getController().startBottomLoading();
            }
            Logger.m172d("<<<");
        }
    }

    protected void initAppPollHeaderView(RecyclerView recyclerView) {
    }

    protected void updateAppPoll(boolean isStreamEmpty) {
    }

    protected void updateAppPollHeaderViewForLayout(StreamLayoutConfig layoutConfig) {
    }

    public void onAddedTopChunk(StreamData data, int newItemsCount, int benchmarkSeqId) {
        Logger.m173d(">>> data=%s newItemsCount=%d benchmarkSeqId=%d", data, Integer.valueOf(newItemsCount), Integer.valueOf(benchmarkSeqId));
        if (getActivity() != null) {
            boolean z;
            if (data.canHaveData()) {
                z = false;
            } else {
                z = true;
            }
            resetRefreshAndEmptyView(z);
            int savedTop = Integer.MAX_VALUE;
            int savedStreamPosition = -1;
            for (int i = 0; i < this.recyclerView.getChildCount(); i++) {
                View view = this.recyclerView.getChildAt(i);
                if (view.getTag() instanceof StreamItemAdapter.ViewHolder) {
                    savedTop = view.getTop();
                    if (i > 0) {
                        savedStreamPosition = 0;
                    } else {
                        savedStreamPosition = this.recyclerLayoutManager.findFirstVisibleItemPosition() - getRecyclerAdapterStreamItemsTopOffset();
                    }
                    setDataToAdapter(data, false, 1, newItemsCount);
                    if (savedTop != Integer.MAX_VALUE) {
                        this.recyclerLayoutManager.scrollToPositionWithOffset((savedStreamPosition + newItemsCount) + getRecyclerAdapterStreamItemsTopOffset(), savedTop);
                    }
                    StreamBenchmark.display(benchmarkSeqId);
                    if (newItemsCount == 0 && data.canLoadTop() && isTimeToLoadTop()) {
                        this.loadMoreAdapter.getController().startTopLoading();
                    }
                    Logger.m172d("<<<");
                }
            }
            setDataToAdapter(data, false, 1, newItemsCount);
            if (savedTop != Integer.MAX_VALUE) {
                this.recyclerLayoutManager.scrollToPositionWithOffset((savedStreamPosition + newItemsCount) + getRecyclerAdapterStreamItemsTopOffset(), savedTop);
            }
            StreamBenchmark.display(benchmarkSeqId);
            this.loadMoreAdapter.getController().startTopLoading();
            Logger.m172d("<<<");
        }
    }

    public void onInitialDataLoadingError(ErrorType errorType) {
        Logger.m185w("%s", errorType);
        if (this.swipeRefresh != null) {
            this.swipeRefresh.setRefreshing(false);
        }
        showEmptyViewError(errorType);
    }

    public void onStreamRefreshError(ErrorType errorType) {
        boolean hasData = true;
        Logger.m185w("%s", errorType);
        if (this.swipeRefresh != null) {
            this.swipeRefresh.setRefreshing(false);
        }
        StreamData data = this.dataFragment.getData();
        if (this.dataFragment.getData().items.isEmpty()) {
            hasData = false;
        }
        if (hasData) {
            resetRefreshAndEmptyView(false);
            setLoadMoreStateFromData(data, errorType);
        } else {
            showEmptyViewError(errorType);
        }
        if (this.holidayControl != null) {
            this.holidayControl.nextHoliday();
        }
    }

    private void showEmptyViewError(ErrorType errorType) {
        this.recyclerView.setVisibility(8);
        if (this.emptyView != null) {
            this.emptyView.setVisibility(0);
            this.emptyView.setState(State.LOADED);
            this.emptyView.setType(streamErrorToEmptyViewError(errorType));
        }
    }

    private Type streamErrorToEmptyViewError(ErrorType errorType) {
        switch (C12195.f118x22ae40df[errorType.ordinal()]) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                return Type.NO_INTERNET;
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                return Type.RESTRICTED_ACCESS_FOR_FRIENDS;
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                int type = this.streamContext == null ? 0 : this.streamContext.type;
                if (type == 2) {
                    return Type.USER_BLOCKED;
                }
                if (type == 3) {
                    return Type.GROUP_BLOCKED;
                }
                return Type.ERROR;
            case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return Type.RESTRICTED_YOU_ARE_IN_BLACK_LIST;
            default:
                return Type.ERROR;
        }
    }

    public void onAddBottomChunkError(ErrorType errorType) {
        Logger.m185w("%s", errorType);
        resetRefreshAndEmptyView(this.dataFragment.getData().items.isEmpty());
        LoadMoreController controller = this.loadMoreAdapter.getController();
        controller.setBottomCurrentState(LoadMoreState.IDLE);
        controller.setBottomAutoLoad(true);
        controller.setAutoLoadSuppressed(true);
        if (errorType == ErrorType.NO_INTERNET) {
            controller.setBottomMessageForState(LoadMoreState.DISCONNECTED, 0);
        } else {
            controller.setBottomMessageForState(LoadMoreState.DISCONNECTED, getShortTextForLoadMoreView(errorType));
        }
        controller.setBottomPermanentState(LoadMoreState.DISCONNECTED);
    }

    public void onAddTopChunkError(ErrorType errorType) {
        Logger.m185w("%s", errorType);
        resetRefreshAndEmptyView(this.dataFragment.getData().items.isEmpty());
        LoadMoreController controller = this.loadMoreAdapter.getController();
        controller.setTopCurrentState(LoadMoreState.IDLE);
        controller.setTopAutoLoad(true);
        controller.setAutoLoadSuppressed(true);
        if (errorType == ErrorType.NO_INTERNET) {
            controller.setTopMessageForState(LoadMoreState.DISCONNECTED, 0);
        } else {
            controller.setTopMessageForState(LoadMoreState.DISCONNECTED, getShortTextForLoadMoreView(errorType));
        }
        controller.setTopPermanentState(LoadMoreState.DISCONNECTED);
    }

    private int getShortTextForLoadMoreView(ErrorType errorType) {
        return 2131165791;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.m173d("requestCode=%d, result=%d, data=%s", Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
        switch (requestCode) {
            case RECEIVED_VALUE:
                if (resultCode == -1) {
                    deleteFeed(data);
                }
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                if (resultCode == -1) {
                    markAsSpam(data);
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void markAsSpam(Intent data) {
        Context context = getActivity();
        if (context == null) {
            Logger.m184w("context is null");
            return;
        }
        long feedId = data.getLongExtra("FEED_ID", 0);
        String spamId = data.getStringExtra("FEED_SPAM_ID");
        String deleteId = data.getStringExtra("FEED_DELETE_ID");
        Logger.m173d("feedId=%d spamId=%s deleteId=%s itemPostionHint=%d", Long.valueOf(feedId), spamId, deleteId, Integer.valueOf(data.getIntExtra("ITEM_POSITION", -1)));
        if (TextUtils.isEmpty(deleteId)) {
            BusStreamHelper.feedMarkAsSpam(feedId, spamId, this.streamContext.logContext);
            return;
        }
        doDeleteFeed(context, deleteId, spamId, feedId, itemPositiomHint);
        this.uiHandler.postMarkAsSpamSuccessful();
    }

    protected void showMarkAsSpamSuccesful() {
        TimeToast.show(getContext(), 2131166067, 0);
    }

    protected void deleteFeed(Intent data) {
        Context context = getActivity();
        if (context == null) {
            Logger.m184w("context is null");
            return;
        }
        long feedId = data.getLongExtra("FEED_ID", 0);
        String deleteId = data.getStringExtra("DELETE_ID");
        boolean unsubscribe = data.getBooleanExtra("IS_UNSUBSCRIBE", false);
        int itemAdapterPositionHint = data.getIntExtra("ITEM_ADAPTER_POSITION", -1);
        Logger.m173d("feedId=%d deleteId=%s unsubscribe=%s", Long.valueOf(feedId), deleteId, Boolean.valueOf(unsubscribe));
        if (unsubscribe) {
            int i;
            ArrayList<String> friendIds = data.getStringArrayListExtra("FRIEND_IDS");
            ArrayList<String> groupIds = data.getStringArrayListExtra("GROUP_IDS");
            Logger.m173d("friendsIds=%s groupIds=%s", friendIds, groupIds);
            StreamSubscriptionManager subscriptionManager = this.storages.getStreamSubscriptionManager();
            if (friendIds != null) {
                for (i = friendIds.size() - 1; i >= 0; i--) {
                    subscriptionManager.unsubscribeUser((String) friendIds.get(i), this.streamContext.logContext);
                }
            }
            if (groupIds != null) {
                for (i = groupIds.size() - 1; i >= 0; i--) {
                    subscriptionManager.unsubscribeGroup((String) groupIds.get(i), this.streamContext.logContext);
                }
            }
            this.streamItemRecyclerAdapter.deleteByOwner(friendIds, groupIds);
            reCalculateLimitPositions(this.dataFragment.getData());
        } else {
            doDeleteFeed(context, deleteId, null, feedId, itemAdapterPositionHint);
        }
        logDeleteFeed(data, unsubscribe);
    }

    private void doDeleteFeed(Context context, String deleteId, String spamId, long feedId, int itemAdapterPositionHint) {
        this.storages.getDeletedFeedsManager().deleteFeed(deleteId, spamId, this.streamContext.logContext);
        this.streamItemRecyclerAdapter.deleteFeed(feedId, itemAdapterPositionHint);
        reCalculateLimitPositions(this.dataFragment.getData());
    }

    private void logDeleteFeed(Intent data, boolean unsubscribe) {
        int position = data.getIntExtra("FEED_POSITION", -1);
        String feedStatInfo = data.getStringExtra("FEED_STAT_INFO");
        StreamStats.clickHideConfirm(position, feedStatInfo);
        if (unsubscribe) {
            StreamStats.clickHideConfirmUnsubscribe(position, feedStatInfo);
        }
    }

    public void onWriteNoteClicked() {
        Logger.m172d("");
        Intent createTopic = new Intent();
        createTopic.setClassName(getContext(), "ru.ok.android.ui.activity.MediaComposerUserActivity");
        startActivity(createTopic);
        MediaComposerStats.open("hidepanel/" + getClass().getSimpleName(), MediaTopicType.USER);
    }

    public void onUploadPhotoClicked() {
        Logger.m172d("");
        NavigationHelper.startPhotoUploadSequence(getActivity(), null, 0, 0);
    }

    public void onUploadVideoClicked() {
        Logger.m172d("");
        StartVideoUploadActivity.startVideoUpload(getActivity(), null);
    }

    public void onLikeClicked(int position, Feed feed, LikeInfoContext likeInfo) {
        Logger.m173d("feedId=%d, likeInfo=%s", Long.valueOf(feed.getId()), likeInfo);
        StreamStats.clickLike(position, feed, likeInfo);
        this.storages.getLikeManager().toggle(likeInfo);
    }

    public void onMarkAsSpamClicked(int position, Feed feed, int itemAdapterPosition) {
        Logger.m173d("feedId=%d spamId=%s", Long.valueOf(feed.getId()), feed.getSpamId());
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(2131165870, 2131165869, 2131165623, 2131165476, 1);
        dialog.setTargetFragment(this, 1);
        dialog.getArguments().putLong("FEED_ID", feed.getId());
        dialog.getArguments().putInt("ITEM_POSITION", itemAdapterPosition);
        dialog.getArguments().putInt("FEED_POSITION", position);
        dialog.getArguments().putString("FEED_STAT_INFO", feed.getFeedStatInfo());
        dialog.getArguments().putString("FEED_SPAM_ID", feed.getSpamId());
        dialog.getArguments().putString("FEED_DELETE_ID", feed.getDeleteId());
        dialog.show(getFragmentManager(), "feed-spam");
        StreamStats.clickComplain(position, feed);
    }

    public void onMediaTopicClicked(int position, Feed feed, DiscussionSummary discussionSummary) {
        Logger.m173d("discussionSummary=%s", discussionSummary);
        openDiscussion(discussionSummary, Page.INFO);
        String discType = discussionSummary.discussion.type;
        if (DiscussionGeneralInfo.Type.GROUP_TOPIC.name().equals(discType) || DiscussionGeneralInfo.Type.USER_STATUS.name().equals(discType) || DiscussionGeneralInfo.Type.HAPPENING_TOPIC.name().equals(discType)) {
            StreamStats.clickMediaTopic();
        }
    }

    public void onCommentClicked(int position, Feed feed, DiscussionSummary discussionSummary) {
        Logger.m173d("discussionSummary=%s", discussionSummary);
        openDiscussion(discussionSummary, Page.MESSAGES);
        StreamStats.clickComment(position, feed, discussionSummary);
    }

    public void openDiscussion(DiscussionSummary discussionSummary, Page page) {
        NavigationHelper.showDiscussionCommentsFragment(getActivity(), discussionSummary.discussion, page, null);
    }

    public LikeInfoContext onLikePhotoClicked(int position, Feed feed, LikeInfoContext likeInfo) {
        Logger.m173d("feedId=%d, likeInfo=%s", Long.valueOf(feed.getId()), likeInfo);
        StreamStats.clickLikePhoto(position, feed, likeInfo);
        return this.storages.getLikeManager().toggle(likeInfo);
    }

    public void onMediaTopicTextEditClick(String topicId, int blockIndex, String text) {
    }

    public void onRetryClick() {
        Logger.m172d("");
        if (this.dataFragment != null) {
            this.emptyView.setState(State.LOADING);
            this.emptyView.setVisibility(0);
            this.dataFragment.refresh();
        }
    }

    public boolean onShownOnScroll(Feed feed, Rect visibleRect, int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }
        int visibleTop = visibleRect.top;
        int visibleBottom = visibleRect.bottom;
        if (BaseCompatToolbarActivity.isUseTabbar(activity)) {
            OdklTabbar tabbar = ((BaseTabbarManager) activity).getTabbarView();
            if (tabbar != null) {
                tabbar.getLocationOnScreen(this.xy);
                if (this.xy[1] < visibleBottom) {
                    visibleBottom = this.xy[1];
                }
            }
        }
        if (visibleTop > visibleBottom) {
            visibleTop = visibleBottom;
        }
        if ((visibleBottom - visibleTop) * 10 < viewHeight * 3) {
            return false;
        }
        Logger.m173d("Send shownOnScroll stats: feedId=%d uuid=%s", Long.valueOf(feed.getId()), feed.getUuid());
        this.statHandler.onShownOnScroll(feed);
        return true;
    }

    protected boolean isStreamStatsEnabled() {
        return true;
    }
}
