package ru.ok.android.ui.stream;

import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.processors.settings.EmptyStreamSettingsGetProcessor;
import ru.ok.android.services.processors.stream.UnreadStream;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.image.GalleryScanner;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.android.ui.stream.data.StreamData;
import ru.ok.android.ui.stream.data.StreamHasNewInfo;
import ru.ok.android.ui.stream.data.StreamListPosition;
import ru.ok.android.ui.stream.data.StreamSettingsHelper;
import ru.ok.android.ui.stream.list.AppPollHolder;
import ru.ok.android.ui.stream.list.PhotoRollViewHolder;
import ru.ok.android.ui.stream.list.StreamHeaderRecyclerAdapter;
import ru.ok.android.ui.stream.list.StreamLayoutConfig;
import ru.ok.android.ui.stream.view.PhotoRollView;
import ru.ok.android.ui.utils.ItemCountChangedDataObserver;
import ru.ok.android.utils.LogUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.controls.events.EventsManager.OnEvents;
import ru.ok.android.widget.menuitems.BellActionMenuItem;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.events.OdnkEvent;
import ru.ok.model.events.OdnkEvent.EventType;
import ru.ok.model.stream.UnreadStreamPage;
import ru.ok.onelog.search.FeedSuggestionType;
import ru.ok.onelog.search.FeedSuggestionsDisplayFactory;

public final class StreamListFragment extends BaseStreamListFragment implements OnEvents {
    private AppPollHolder appPollHolder;
    private final BellActionMenuItem bellActionMenuItem;
    private StreamHasNewInfo hasNewInfo;
    private boolean isHeaderForEmptyStreamEnabled;
    private PhotoRollController photoRollControl;
    private StreamSettingsHelper settingsHelper;
    private final NewInfoHandler showNewInfoHandler;
    private StreamHeaderRecyclerAdapter streamHeaderRecyclerAdapter;

    /* renamed from: ru.ok.android.ui.stream.StreamListFragment.1 */
    class C12261 extends ItemCountChangedDataObserver {
        C12261() {
        }

        public void onItemCountMayChange() {
            boolean isEmpty;
            int i = 0;
            if (StreamListFragment.this.streamItemRecyclerAdapter.getItemCount() == 0) {
                isEmpty = true;
            } else {
                isEmpty = false;
            }
            SmartEmptyViewAnimated smartEmptyViewAnimated = StreamListFragment.this.emptyView;
            if (!isEmpty || StreamListFragment.this.isHeaderForEmptyStreamEnabled) {
                i = 8;
            }
            smartEmptyViewAnimated.setVisibility(i);
        }
    }

    /* renamed from: ru.ok.android.ui.stream.StreamListFragment.2 */
    static /* synthetic */ class C12272 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$events$OdnkEvent$EventType;

        static {
            $SwitchMap$ru$ok$model$events$OdnkEvent$EventType = new int[EventType.values().length];
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.EVENTS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public interface AdapterWithAppPolls {
        void setAppPollHolder(AppPollHolder appPollHolder);
    }

    public interface AdapterWithPhotoRoll {
        void setPhotoRollViewHolder(@NonNull PhotoRollViewHolder photoRollViewHolder);
    }

    private class NewInfoHandler extends Handler {
        private long lastClickTime;

        private NewInfoHandler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVED_VALUE:
                    Logger.m172d("Displaying news button...");
                    StreamListFragment.this.showHasNewFeeds(msg.arg1);
                default:
                    super.handleMessage(msg);
            }
        }

        public void onShowNewInfoClicked() {
            this.lastClickTime = System.currentTimeMillis();
        }

        public void wantToShowNewInfo(int valueInt) {
            long showIn = 0;
            if (this.lastClickTime > 0) {
                long delta = System.currentTimeMillis() - this.lastClickTime;
                if (delta < 30000) {
                    showIn = 30000 - delta;
                }
            }
            removeMessages(0);
            if (showIn > 0) {
                Logger.m173d("Scheduling to show in %d", Long.valueOf(showIn));
                Message msg = Message.obtain();
                msg.arg1 = valueInt;
                msg.what = 0;
                sendMessageDelayed(msg, showIn);
                return;
            }
            Logger.m172d("Displaying news button right now...");
            StreamListFragment.this.showHasNewFeeds(valueInt);
        }

        public void noNewInfo() {
            removeMessages(0);
        }
    }

    public StreamListFragment() {
        this.showNewInfoHandler = new NewInfoHandler();
        this.bellActionMenuItem = new BellActionMenuItem(this);
        this.isHeaderForEmptyStreamEnabled = false;
    }

    protected int getLayoutId() {
        return 2130903368;
    }

    protected void registerEmptyViewVisibilityAdapterObserver() {
        this.streamItemRecyclerAdapter.registerAdapterDataObserver(new C12261());
    }

    public void onCreate(Bundle savedInstanceState) {
        Logger.m172d("");
        super.onCreate(savedInstanceState);
        EventsManager.getInstance().sendActualValue();
        this.settingsHelper = new StreamSettingsHelper(getContext(), this.streamContext);
        this.isHeaderForEmptyStreamEnabled = EmptyStreamSettingsGetProcessor.isShowPymk(getContext());
    }

    public void onDestroy() {
        super.onDestroy();
        this.showNewInfoHandler.removeCallbacksAndMessages(null);
        if (this.photoRollControl != null) {
            this.photoRollControl.onDestroy();
        }
    }

    public void onResume() {
        super.onResume();
        EventsManager.getInstance().subscribe(this);
        updateAppPoll(this.streamItemRecyclerAdapter.getItemCount() <= 0);
    }

    public void onStart() {
        super.onStart();
        if (this.photoRollControl != null) {
            this.photoRollControl.onStart();
        }
    }

    public void onStop() {
        super.onStop();
        if (this.photoRollControl != null) {
            this.photoRollControl.onStop();
        }
    }

    protected void onPreSetHeaderBannerView(RecyclerView recyclerView) {
        super.onPreSetHeaderBannerView(recyclerView);
    }

    protected int getHeadersCount() {
        return 0;
    }

    public void onPause() {
        super.onPause();
        EventsManager.getInstance().unSubscribe(this);
        if (!TextUtils.isEmpty(OdnoklassnikiApplication.getCurrentUser().uid)) {
            Logger.m173d("Saving position: %s", getCurrentPosition());
            this.settingsHelper.setStreamPosition(position);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (inflateMenuLocalized(2131689495, menu)) {
            this.bellActionMenuItem.setItemMenu(menu.findItem(2131625471));
            this.bellActionMenuItem.refreshCount();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624234)
    public final void onNewEvents(BusEvent busEvent) {
        Activity activity = getActivity();
        if (activity != null) {
            Logger.m173d(">>> events=%s", EventsManager.getEventsFromBusEvent(busEvent));
            Iterator i$ = events.iterator();
            while (i$.hasNext()) {
                if (((OdnkEvent) i$.next()).type == EventType.ACTIVITIES) {
                    UnreadStreamPage unreadStreamPage = UnreadStream.getInstance(activity, OdnoklassnikiApplication.getCurrentUser().getId()).getFirstUnreadPage();
                    Logger.m173d("unreadStreamPage=%s", unreadStreamPage);
                    LogUtils.logFeeds(unreadStreamPage == null ? null : unreadStreamPage.feeds, "StreamListFragment.onNewEvents - feed cache:");
                    Logger.m173d("hasNewInfo=%s", this.hasNewInfo);
                    LogUtils.logFeeds(this.hasNewInfo == null ? null : this.hasNewInfo.page.feeds, "StreamListFragment.onNewEvents - has new info:");
                    int unreadCount = unreadStreamPage == null ? 0 : unreadStreamPage.getTotalUnreadFeedsCount();
                    if (unreadCount > 0) {
                        this.hasNewInfo = new StreamHasNewInfo(unreadCount, unreadStreamPage);
                    } else {
                        this.hasNewInfo = null;
                    }
                    Logger.m173d("set new has new info: %s", this.hasNewInfo);
                    if (this.hasNewInfo == null || this.hasNewInfo.newEventsCount <= 0) {
                        Logger.m172d("Hiding news button...");
                        this.showNewInfoHandler.noNewInfo();
                        hideHasNewFeeds();
                    } else {
                        Logger.m172d("Displaying news button...");
                        this.showNewInfoHandler.wantToShowNewInfo(this.hasNewInfo.newEventsCount);
                    }
                    Logger.m172d("<<<");
                }
            }
            Logger.m172d("<<<");
        }
    }

    public void onScrollTopClick(int count) {
        Activity activity = getActivity();
        if (activity != null) {
            Logger.m173d("count=%d, hasNewInfo=%s", Integer.valueOf(count), this.hasNewInfo);
            if (this.scrollTopView.getNewEventsCount() > 0 && this.hasNewInfo != null && this.hasNewInfo.newEventsCount > 0 && this.hasNewInfo.page != null) {
                LogUtils.logFeeds(this.hasNewInfo.page.feeds, "StreamListFragment.onScrollTopClick:");
                this.showNewInfoHandler.onShowNewInfoClicked();
                if (this.dataFragment != null) {
                    this.dataFragment.reset(this.hasNewInfo.page, true);
                    UnreadStream.getInstance(activity, OdnoklassnikiApplication.getCurrentUser().getId()).onDisplayedUnreadStream();
                }
                this.hasNewInfo = null;
            }
            super.onScrollTopClick(count);
        }
    }

    @NonNull
    protected Adapter createHeaderRecyclerAdapter() {
        this.streamHeaderRecyclerAdapter = new StreamHeaderRecyclerAdapter(getActivity(), this, LayoutInflater.from(getContext()), this.recyclerView);
        return this.streamHeaderRecyclerAdapter;
    }

    protected void initStreamHeaderViews() {
        super.initStreamHeaderViews();
        initPhotoRollHeaderView(this.recyclerView);
    }

    protected void updateStreamHeaderViewsForStreamLayout() {
        super.updateStreamHeaderViewsForStreamLayout();
        updatePhotoRollHeaderViewForLayout();
    }

    private void updatePhotoRollHeaderViewForLayout() {
        if (this.photoRollControl != null) {
            this.photoRollControl.updateLayout(this.layoutConfig);
        }
    }

    protected void initAppPollHeaderView(RecyclerView recyclerView) {
        this.appPollHolder = new AppPollHolder(View.inflate(recyclerView.getContext(), 2130903452, null), getActivity());
        if (this.headersRecyclerAdapter instanceof AdapterWithAppPolls) {
            ((AdapterWithAppPolls) this.headersRecyclerAdapter).setAppPollHolder(this.appPollHolder);
        }
    }

    protected void updateAppPoll(boolean isStreamEmpty) {
        if (this.appPollHolder != null) {
            this.appPollHolder.update(isStreamEmpty);
        }
    }

    protected void updateStreamLayout() {
        super.updateStreamLayout();
        if (this.streamHeaderRecyclerAdapter != null) {
            this.streamHeaderRecyclerAdapter.updateLandscapeMargins(this.layoutConfig);
        }
    }

    protected void updateAppPollHeaderViewForLayout(StreamLayoutConfig layoutConfig) {
        if (this.appPollHolder != null) {
            this.appPollHolder.updateForLayoutSize(layoutConfig);
        }
    }

    protected void initPhotoRollHeaderView(RecyclerView recyclerView) {
        if (VERSION.SDK_INT >= 16) {
            if (PermissionUtils.checkSelfPermission(getContext(), "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                Logger.m184w("No READ_EXTERNAL_STORAGE permissions asked for PHOTO ROLL");
                return;
            }
        }
        doInitPhotoRollHeaderView(recyclerView);
    }

    private void doInitPhotoRollHeaderView(RecyclerView recyclerView) {
        if (this.headersRecyclerAdapter instanceof AdapterWithPhotoRoll) {
            this.photoRollControl = new PhotoRollController(getContext(), (PhotoRollView) inflateViewLocalized(2130903459, recyclerView, false), new GalleryScanner(getContext(), Media.EXTERNAL_CONTENT_URI));
            ((AdapterWithPhotoRoll) this.headersRecyclerAdapter).setPhotoRollViewHolder(this.photoRollControl.getViewHolder());
        }
    }

    protected StreamContext createStreamContext() {
        return StreamContext.stream();
    }

    protected boolean isMediaPostPanelRequired() {
        return true;
    }

    protected Collection<? extends GeneralUserInfo> getFilteredUsers() {
        return null;
    }

    public void onGetNewEvents(ArrayList<OdnkEvent> events) {
        Iterator i$ = events.iterator();
        while (i$.hasNext()) {
            OdnkEvent event = (OdnkEvent) i$.next();
            switch (C12272.$SwitchMap$ru$ok$model$events$OdnkEvent$EventType[event.type.ordinal()]) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    if (this.bellActionMenuItem != null && this.bellActionMenuItem.setCount(event.getValueInt())) {
                        FragmentActivity activity = getActivity();
                        if (activity != null) {
                            activity.supportInvalidateOptionsMenu();
                            return;
                        }
                        return;
                    }
                    return;
                default:
            }
        }
    }

    protected void resetRefreshAndEmptyView(boolean isDataEmpty) {
        if (this.isHeaderForEmptyStreamEnabled && isDataEmpty) {
            isDataEmpty = false;
            updateHeaderAdapter(true);
        }
        super.resetRefreshAndEmptyView(isDataEmpty);
    }

    public void onInitialDataLoaded(StreamData data, StreamListPosition restoredPosition, int benchmarkSeqId) {
        UnreadStream.onDisplayedFirstStreamPage();
        super.onInitialDataLoaded(data, restoredPosition, benchmarkSeqId);
    }

    public void onStreamRefreshed(StreamData data, int benchmarkSeqId) {
        super.onStreamRefreshed(data, benchmarkSeqId);
        updateHeaderAdapter(data);
    }

    protected void deleteFeed(Intent data) {
        super.deleteFeed(data);
        updateHeaderAdapter(this.streamItemRecyclerAdapter.getItems().size() == 0);
    }

    public void onDeletedFeeds(StreamData data) {
        super.onDeletedFeeds(data);
        updateHeaderAdapter(data);
    }

    private void updateHeaderAdapter(StreamData data) {
        updateHeaderAdapter(!data.canHaveData());
    }

    private void initHeader() {
        if (!this.streamHeaderRecyclerAdapter.isInitialized()) {
            this.streamHeaderRecyclerAdapter.addPymkPreview();
            this.streamHeaderRecyclerAdapter.addItem(2131624298);
            OneLog.log(FeedSuggestionsDisplayFactory.get(FeedSuggestionType.search_buttons));
            this.streamHeaderRecyclerAdapter.setIsInitialized(true);
        }
    }

    protected void showHasNewFeeds(int newFeedsCount) {
        if (!this.streamHeaderRecyclerAdapter.isInitialized()) {
            super.showHasNewFeeds(newFeedsCount);
        }
    }

    protected void setDataToAdapter(StreamData data, boolean isRefresh, int updateType, int newItemsCount) {
        super.setDataToAdapter(data, isRefresh, updateType, newItemsCount);
        updateHeaderAdapter(data);
    }

    private void updateHeaderAdapter(boolean isEmpty) {
        if (this.streamHeaderRecyclerAdapter != null && this.isHeaderForEmptyStreamEnabled) {
            if (isEmpty) {
                this.emptyView.setVisibility(8);
                this.recyclerView.setVisibility(0);
                initHeader();
                this.streamHeaderRecyclerAdapter.notifyDataSetChanged();
                this.refreshProvider.setRefreshEnabled(false);
                hideHasNewFeeds();
                return;
            }
            this.streamHeaderRecyclerAdapter.removeItemsForEmptyFeed();
            this.streamHeaderRecyclerAdapter.setIsInitialized(false);
            this.refreshProvider.setRefreshEnabled(true);
        }
    }
}
