package ru.ok.android.ui.stream.data;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.flurry.android.FlurryAgent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.benchmark.StreamBenchmark;
import ru.ok.android.flurry.StreamErrors;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.feeds.DeletedFeedsManager;
import ru.ok.android.services.feeds.DeletedFeedsManager.DeleteFeedListener;
import ru.ok.android.services.feeds.subscribe.StreamSubscriptionManager;
import ru.ok.android.services.feeds.subscribe.StreamSubscriptionManager.StreamSubscriptionListener;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.stream.GetStreamProcessor;
import ru.ok.android.services.processors.stream.StreamLoadException;
import ru.ok.android.services.processors.stream.UnreadStream;
import ru.ok.android.statistics.stream.StreamBannerStatisticsHandler;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.stream.list.Feed2StreamItemBinder;
import ru.ok.android.ui.stream.list.FeedDisplayParams;
import ru.ok.android.ui.stream.list.StreamItem;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NamedThreadFactory;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.StreamPage;
import ru.ok.model.stream.StreamPageKey;
import ru.ok.model.stream.banner.PromoLinkBuilder;

public class StreamDataFragment extends Fragment implements DeleteFeedListener, StreamSubscriptionListener {
    private static final AtomicReference<ArrayList<FeedWithState>> feedsRef;
    private static int instanceCount;
    private static final AtomicReference<ArrayList<StreamItem>> itemsRef;
    private ExecutorService bgExecutor;
    private WeakReference<StreamDataCallback> callbackRef;
    private Context context;
    private Feed2StreamItemBinder converter;
    private Future currentTask;
    @NonNull
    private StreamData data;
    private volatile int deliverIdDeletedFeed;
    private volatile int deliverResultCount;
    private final Object deliverResultLock;
    private final int instanceId;
    private boolean isInitialized;
    private boolean isLoading;
    private Storages storages;
    private StreamContext streamContext;
    private UIHandler uiHandler;

    public interface StreamDataCallback {
        void onAddBottomChunkError(ErrorType errorType);

        void onAddTopChunkError(ErrorType errorType);

        void onAddedBottomChunk(StreamData streamData, int i, int i2);

        void onAddedTopChunk(StreamData streamData, int i, int i2);

        void onDeletedFeeds(StreamData streamData);

        void onInitialDataLoaded(@NonNull StreamData streamData, @Nullable StreamListPosition streamListPosition, int i);

        void onInitialDataLoadingError(ErrorType errorType);

        void onStreamRefreshError(ErrorType errorType);

        void onStreamRefreshed(StreamData streamData, int i);
    }

    abstract class BaseStreamTask implements Runnable {
        final int deliverResultId;

        BaseStreamTask(int deliverResultCount) {
            this.deliverResultId = deliverResultCount;
        }
    }

    class CheckDeletedFeedsTask extends BaseStreamTask {
        final Message origMsg;

        CheckDeletedFeedsTask(int deliverResultId, Message origMsg) {
            super(deliverResultId);
            this.origMsg = Message.obtain(origMsg);
        }

        public void run() {
            StreamData data = this.origMsg.obj;
            StreamDataFragment.this.checkForDeletedFeeds(data);
            data.deliverResultId = this.deliverResultId;
            StreamDataFragment.this.uiHandler.sendMessage(this.origMsg);
        }
    }

    class DeleteFeedsTask extends BaseStreamTask {
        final StreamData data;

        DeleteFeedsTask(int deliverResultCount) {
            super(deliverResultCount);
            this.data = new StreamData(StreamDataFragment.this.data);
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            Logger.m173d("[%d] checkDeletedFeeds.run >>>", Integer.valueOf(StreamDataFragment.this.instanceId));
            boolean hasChanges = StreamDataFragment.this.checkForDeletedFeeds(this.data);
            this.data.deliverResultId = this.deliverResultId;
            StreamDataFragment.this.postDeliverDeletedFeeds(this.data, hasChanges);
            Logger.m173d("[%d] checkDeletedFeeds.run <<< %d ms", Integer.valueOf(StreamDataFragment.this.instanceId), Long.valueOf(System.currentTimeMillis() - startTime));
        }
    }

    class InitUnreadStreamRunnable implements Runnable {
        InitUnreadStreamRunnable() {
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            Logger.m172d("initUnreadStream >>>");
            UnreadStream.getInstance(StreamDataFragment.this.context, OdnoklassnikiApplication.getCurrentUser().getId()).waitForInitialization();
            Logger.m173d("initUnreadStream <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
        }
    }

    abstract class ReloadStreamRunnable extends BaseStreamTask {
        int benchmarkSeqId;
        StreamData data;
        protected boolean doGetPromoLinks;
        ErrorType errorType;
        protected boolean firstLoadFromWeb;
        protected boolean forceLoadFromWeb;
        boolean fromApi;
        @NonNull
        protected StreamPageKey pageKey;

        ReloadStreamRunnable(int deliverResultCount, StreamPageKey pageKey, @NonNull boolean doGetPromoLinks, boolean forceLoadFromWeb, boolean firstLoadFromWeb) {
            super(deliverResultCount);
            this.data = new StreamData();
            this.pageKey = pageKey;
            this.doGetPromoLinks = doGetPromoLinks;
            this.forceLoadFromWeb = forceLoadFromWeb;
            this.firstLoadFromWeb = firstLoadFromWeb;
        }

        public void run() {
            Logger.m173d("[%d] reloadStream.run >>> pageKey=%s doGetPromoLinks=%s forceLoadFromWeb=%s firstLoadFromWeb=%s", Integer.valueOf(StreamDataFragment.this.instanceId), this.pageKey, Boolean.valueOf(this.doGetPromoLinks), Boolean.valueOf(this.forceLoadFromWeb), Boolean.valueOf(this.firstLoadFromWeb));
            try {
                StreamWithPromoLinks response = GetStreamProcessor.getStream(StreamDataFragment.this.context, this.pageKey, StreamDataFragment.this.streamContext, this.doGetPromoLinks, this.forceLoadFromWeb, this.firstLoadFromWeb, null, 0);
                Logger.m173d("[%d] reloadStream.run: response=%s", Integer.valueOf(StreamDataFragment.this.instanceId), response);
                this.benchmarkSeqId = response.benchmarkSeqId;
                StreamPage page = response.streamPage;
                this.fromApi = response.fromAPI;
                StreamDataFragment.this.addPage(this.data, page, 2, StreamDataFragment.this.converter, this.benchmarkSeqId);
                if (response.promoLinks != null) {
                    int size = response.promoLinks.size();
                    for (int i = 0; i < size; i++) {
                        try {
                            this.data.headerBanners.add(((PromoLinkBuilder) response.promoLinks.get(i)).build());
                        } catch (FeedObjectException e) {
                            Logger.m180e(e, "Invalid promo link: %s", promoLinkBuilder);
                        }
                    }
                }
                this.data.holidays = response.holidays;
            } catch (StreamLoadException e2) {
                Logger.m180e(e2, "[%d] reloadStream.run: %s", Integer.valueOf(StreamDataFragment.this.instanceId), e2);
                this.errorType = ErrorType.from(e2.getErrorBundle());
                StreamErrors.logAndFilterError("reload_stream_runnable", e2.getMessage(), e2);
            } catch (Exception e3) {
                Logger.m180e(e3, "[%d] reloadStream.run: %s", Integer.valueOf(StreamDataFragment.this.instanceId), e3);
                this.errorType = ErrorType.GENERAL;
                StreamErrors.logAndFilterError("reload_stream_runnable_general", e3.getMessage(), e3);
            }
            Logger.m173d("[%d] reloadStream.run <<< data=%s errorType=%s", Integer.valueOf(StreamDataFragment.this.instanceId), this.data, this.errorType);
        }
    }

    class InitialLoadRunnable extends ReloadStreamRunnable {
        private final boolean restorePositionFromPrefs;
        @Nullable
        private StreamListPosition savedPosition;

        InitialLoadRunnable(int deliverResultId, StreamPageKey pageKey, boolean restorePositionFromPrefs, boolean forceLoadFromWeb, StreamListPosition savedPosition) {
            super(deliverResultId, pageKey, pageKey.isFirstPage(), forceLoadFromWeb, false);
            this.savedPosition = savedPosition;
            this.restorePositionFromPrefs = restorePositionFromPrefs;
        }

        public void run() {
            boolean forceRefresh;
            Logger.m173d("[%d] provided saved position: %s", Integer.valueOf(StreamDataFragment.this.instanceId), this.savedPosition);
            StreamSettingsHelper settingsHelper = new StreamSettingsHelper(StreamDataFragment.this.context, StreamDataFragment.this.streamContext);
            if (this.savedPosition == null && StreamDataFragment.this.isTimeForForceRefresh(settingsHelper)) {
                forceRefresh = true;
            } else {
                forceRefresh = false;
            }
            if (forceRefresh) {
                this.firstLoadFromWeb = true;
            }
            if (!forceRefresh && this.restorePositionFromPrefs && this.savedPosition == null) {
                this.savedPosition = settingsHelper.getStreamPosition();
                Logger.m173d("[%d] saved position from prefs: %s", Integer.valueOf(StreamDataFragment.this.instanceId), this.savedPosition);
            }
            if (this.savedPosition != null) {
                this.pageKey = this.savedPosition.pageKey;
                this.doGetPromoLinks = this.pageKey.isFirstPage();
                this.forceLoadFromWeb &= this.pageKey.isFirstPage();
            }
            super.run();
            if (this.errorType != null) {
                StreamDataFragment.this.postDeliverInitialDataLoadingError(this.deliverResultId, this.errorType);
                return;
            }
            StreamListPosition restoredPosition = restorePosition();
            if (forceRefresh && this.fromApi) {
                this.data.hasRefreshedFromWeb = true;
            }
            this.data.deliverResultId = this.deliverResultId;
            StreamDataFragment.this.postDeliverInitialResult(this.data, restoredPosition, this.benchmarkSeqId);
        }

        private StreamListPosition restorePosition() {
            StreamListPosition restoredPosition = null;
            if (this.savedPosition != null) {
                int restoredItemPosition = StreamDataFragment.this.findPosition(this.data, this.savedPosition);
                if (restoredItemPosition != -1) {
                    restoredPosition = new StreamListPosition(this.savedPosition.pageKey, this.savedPosition.itemId, this.savedPosition.viewTop, restoredItemPosition);
                }
            }
            Logger.m173d("[%d] restored position: %s", Integer.valueOf(StreamDataFragment.this.instanceId), restoredPosition);
            return restoredPosition;
        }
    }

    class LoadChunkRunnable extends BaseStreamTask {
        private final StreamData data;
        private final int side;

        LoadChunkRunnable(int deliverResultId, StreamData data, int side) {
            super(deliverResultId);
            this.data = data;
            this.side = side;
        }

        public void run() {
            Logger.m173d("[%d] loadChunk.run >>> data=%s side=%d", Integer.valueOf(StreamDataFragment.this.instanceId), this.data, Integer.valueOf(this.side));
            ErrorType errorType = null;
            int newItemsCount = 0;
            StreamPageKey pageKey = this.side == 1 ? this.data.topPageKey : this.data.bottomPageKey;
            if (pageKey == null) {
                Logger.m185w("[%d] loadChunk.run <<< pageKey is null", Integer.valueOf(StreamDataFragment.this.instanceId));
                errorType = ErrorType.GENERAL;
                FlurryAgent.onError("load_chunk_runnable_page_key", "pageKey is null", "null");
            } else {
                Logger.m185w("[%d] loadChunk.run: loading page by pageKey=%s", Integer.valueOf(StreamDataFragment.this.instanceId), pageKey);
                StreamPage topPage = null;
                try {
                    if (this.side == 2) {
                        topPage = this.data.pages.isEmpty() ? null : (StreamPage) this.data.pages.getLast();
                    }
                    StreamPageKey topPageKey = topPage == null ? null : topPage.getKey();
                    long streamTs = topPage == null ? 0 : topPage.getStreamTs();
                    boolean firstLoadFromWeb = this.side == 2 && this.data.hasRefreshedFromWeb;
                    boolean doLoadPromoLinks = pageKey.isFirstPage();
                    StreamWithPromoLinks streamWithPromoLinks = GetStreamProcessor.getStream(StreamDataFragment.this.context, pageKey, StreamDataFragment.this.streamContext, doLoadPromoLinks, false, firstLoadFromWeb, topPageKey, streamTs);
                    int benchmarkSeqId = streamWithPromoLinks.benchmarkSeqId;
                    StreamPage page = streamWithPromoLinks.streamPage;
                    Logger.m173d("[%d] loadChunk.run: page=%s", Integer.valueOf(StreamDataFragment.this.instanceId), page);
                    if (page != null) {
                        int startItemCount = this.data.items.size();
                        StreamDataFragment.this.addPage(this.data, page, this.side, StreamDataFragment.this.converter, benchmarkSeqId);
                        newItemsCount = this.data.items.size() - startItemCount;
                    }
                    if (doLoadPromoLinks) {
                        this.data.headerBanners.clear();
                        ArrayList<PromoLinkBuilder> promoLinks = streamWithPromoLinks.promoLinks;
                        if (promoLinks != null) {
                            int size = promoLinks.size();
                            for (int i = 0; i < size; i++) {
                                this.data.headerBanners.add(((PromoLinkBuilder) promoLinks.get(i)).build());
                            }
                        }
                        this.data.holidays = streamWithPromoLinks.holidays;
                    }
                    this.data.deliverResultId = this.deliverResultId;
                    if (this.side == 1) {
                        StreamDataFragment.this.postDeliverAddTopChunk(this.data, newItemsCount, benchmarkSeqId);
                    } else {
                        StreamDataFragment.this.postDeliverAddBottomChunk(this.data, newItemsCount, benchmarkSeqId);
                    }
                } catch (StreamLoadException e) {
                    Logger.m180e(e, "[%d] loadChunk.run: %s", Integer.valueOf(StreamDataFragment.this.instanceId), e);
                    errorType = ErrorType.from(e.getErrorBundle());
                    StreamErrors.logAndFilterError("load_chunk_runnable", e.getMessage(), e);
                } catch (Exception e2) {
                    Logger.m180e(e2, "[%d] loadChunk.run: %s", Integer.valueOf(StreamDataFragment.this.instanceId), e2);
                    errorType = ErrorType.GENERAL;
                    StreamErrors.logAndFilterError("load_chunk_runnable_general", e2.getMessage(), e2);
                }
            }
            if (errorType != null) {
                StreamDataFragment.this.postDeliverAddChunkError(this.deliverResultId, errorType, this.side);
            }
            Logger.m173d("[%d] loadChunk.run <<< data=%s newItemsCount=%d errorType=%s", Integer.valueOf(StreamDataFragment.this.instanceId), this.data, Integer.valueOf(newItemsCount), errorType);
        }
    }

    class RefreshStreamRunnable extends ReloadStreamRunnable {
        RefreshStreamRunnable(int deliverResultId) {
            super(deliverResultId, StreamPageKey.firstPageKey(20), true, true, true);
        }

        public void run() {
            super.run();
            if (this.errorType != null) {
                StreamDataFragment.this.postDeliverStreamRefreshError(this.deliverResultId, this.errorType);
                return;
            }
            this.data.hasRefreshedFromWeb = true;
            this.data.deliverResultId = this.deliverResultId;
            StreamDataFragment.this.postDeliverRefreshResult(this.data, this.benchmarkSeqId);
        }
    }

    class UIHandler extends Handler {
        UIHandler() {
        }

        private boolean isDataDeliveryMessage(Message msg) {
            return msg.what == 1 || msg.what == 4 || msg.what == 3 || msg.what == 2 || msg.what == 8;
        }

        public void handleMessage(Message msg) {
            boolean z = true;
            if ((msg.obj instanceof StreamData ? ((StreamData) msg.obj).deliverResultId : msg.arg2) != StreamDataFragment.this.deliverResultCount) {
                Logger.m185w("[%d] message not delivered, because of outdated deliverResultId=%d", Integer.valueOf(StreamDataFragment.instanceCount), Integer.valueOf(msg.obj instanceof StreamData ? ((StreamData) msg.obj).deliverResultId : msg.arg2));
            } else if (!isDataDeliveryMessage(msg) || msg.obj.deliverResultId > StreamDataFragment.this.deliverIdDeletedFeed) {
                switch (msg.what) {
                    case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                        Bundle extra = msg.peekData();
                        StreamDataFragment.this.deliverInitialResult((StreamData) msg.obj, extra == null ? null : (StreamListPosition) extra.getParcelable("pos"), msg.arg2);
                    case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                        StreamDataFragment.this.deliverRefreshResult((StreamData) msg.obj, msg.arg2);
                    case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                        StreamDataFragment.this.deliverAddChunk((StreamData) msg.obj, msg.arg1, 1, msg.arg2);
                    case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                        StreamDataFragment.this.deliverAddChunk((StreamData) msg.obj, msg.arg1, 2, msg.arg2);
                    case MessagesProto.Message.UUID_FIELD_NUMBER /*5*/:
                        StreamDataFragment.this.deliverInitialDataLoadingError((ErrorType) msg.obj);
                    case MessagesProto.Message.REPLYTO_FIELD_NUMBER /*6*/:
                        StreamDataFragment.this.deliverStreamRefreshError((ErrorType) msg.obj);
                    case MessagesProto.Message.ATTACHES_FIELD_NUMBER /*7*/:
                        StreamDataFragment.this.deliverAddChunkError((ErrorType) msg.obj, msg.arg1);
                    case MessagesProto.Message.TASKID_FIELD_NUMBER /*8*/:
                        StreamDataFragment streamDataFragment = StreamDataFragment.this;
                        StreamData streamData = (StreamData) msg.obj;
                        if (msg.arg1 == 0) {
                            z = false;
                        }
                        streamDataFragment.deliverDeletedFeeds(streamData, z);
                    default:
                }
            } else {
                Logger.m173d("[%d] starting check task", Integer.valueOf(StreamDataFragment.instanceCount));
                StreamDataFragment.this.currentTask = StreamDataFragment.this.submitToBgThread(new CheckDeletedFeedsTask(StreamDataFragment.access$004(StreamDataFragment.this), msg));
            }
        }
    }

    static /* synthetic */ int access$004(StreamDataFragment x0) {
        int i = x0.deliverResultCount + 1;
        x0.deliverResultCount = i;
        return i;
    }

    static {
        instanceCount = 0;
        itemsRef = new AtomicReference();
        feedsRef = new AtomicReference();
    }

    public void setArguments(StreamContext streamContext, StreamListPosition savedPosition) {
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
            setArguments(args);
        }
        args.putParcelable("stream_context", streamContext);
        args.putParcelable("saved_position", savedPosition);
    }

    public void setInitOnCreate(boolean initOnCreate) {
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
            setArguments(args);
        }
        args.putBoolean("init_on_create", initOnCreate);
    }

    public StreamDataFragment() {
        this.data = new StreamData();
        this.uiHandler = new UIHandler();
        this.isInitialized = false;
        this.isLoading = false;
        int i = instanceCount + 1;
        instanceCount = i;
        this.instanceId = i;
        this.deliverResultCount = 0;
        this.deliverResultLock = new Object();
        this.deliverIdDeletedFeed = 0;
        Logger.m173d("[%d] Ctor", Integer.valueOf(this.instanceId));
        setRetainInstance(true);
    }

    public void setCallback(StreamDataCallback callback) {
        Logger.m173d("[%d] setCallback: callback=%s", Integer.valueOf(this.instanceId), callback);
        this.callbackRef = new WeakReference(callback);
    }

    public StreamData getData() {
        return this.data;
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public void onCreate(Bundle savedInstanceState) {
        Logger.m173d("[%d] >>> savedInstanceState=%s", Integer.valueOf(this.instanceId), savedInstanceState);
        super.onCreate(savedInstanceState);
        this.context = getActivity().getApplicationContext();
        this.streamContext = getStreamContext();
        this.converter = new Feed2StreamItemBinder(this.context, FeedDisplayParams.fromStreamContext(this.streamContext));
        this.bgExecutor = createBgExecutor();
        this.storages = Storages.getInstance(this.context, OdnoklassnikiApplication.getCurrentUser().getId());
        this.storages.getDeletedFeedsManager().registerListener(this);
        this.storages.getStreamSubscriptionManager().registerListener(this);
        if (getInitOnCreate()) {
            init();
        }
        Logger.m173d("[%d] <<<", Integer.valueOf(this.instanceId));
    }

    public void onDestroy() {
        Logger.m173d("[%d]", Integer.valueOf(this.instanceId));
        super.onDestroy();
        this.bgExecutor.shutdownNow();
    }

    private Future<?> submitToBgThread(Runnable task) {
        if (!this.bgExecutor.isShutdown()) {
            try {
                return this.bgExecutor.submit(task);
            } catch (Throwable e) {
                Logger.m186w(e, "Failed to submit task");
            }
        }
        return null;
    }

    public void init() {
        Logger.m173d("[%d] init >>>", Integer.valueOf(this.instanceId));
        if (this.isInitialized) {
            Logger.m185w("[%d] init <<< already initialized", Integer.valueOf(this.instanceId));
            StreamListPosition restoredPosition = null;
            StreamListPosition savedPosition = getSavedPosition();
            if (savedPosition != null) {
                restoredPosition = new StreamListPosition(savedPosition.pageKey, savedPosition.itemId, savedPosition.viewTop, findPosition(this.data, getSavedPosition()));
            }
            deliverInitialResult(this.data, restoredPosition, 0);
        } else if (this.isLoading) {
            Logger.m185w("[%d] init <<< loading in progress, do nothing", Integer.valueOf(this.instanceId));
        } else {
            if (this.context == null) {
                getArguments().putBoolean("init_on_create", true);
            }
            startInitialLoading(getSavedPosition());
            Logger.m173d("[%d] init <<<", Integer.valueOf(this.instanceId));
        }
    }

    public boolean refresh() {
        Logger.m173d("[%d] refresh >>>", Integer.valueOf(this.instanceId));
        if (this.isLoading) {
            Logger.m185w("[%d] refresh <<< loading in progress, do nothing", Integer.valueOf(this.instanceId));
            return false;
        }
        this.isLoading = true;
        int i = this.deliverResultCount + 1;
        this.deliverResultCount = i;
        this.currentTask = submitToBgThread(new RefreshStreamRunnable(i));
        Logger.m173d("[%d] refresh <<< ", Integer.valueOf(this.instanceId));
        return true;
    }

    public boolean loadBottom() {
        Logger.m173d("[%d] loadBottom >>>", Integer.valueOf(this.instanceId));
        if (this.isLoading) {
            Logger.m185w("[%d] loadBottom <<< loading in progress, do nothing", Integer.valueOf(this.instanceId));
            return false;
        } else if (this.data.canLoadBottom()) {
            this.isLoading = true;
            int i = this.deliverResultCount + 1;
            this.deliverResultCount = i;
            this.currentTask = submitToBgThread(new LoadChunkRunnable(i, new StreamData(this.data), 2));
            Logger.m173d("[%d] loadBottom <<<", Integer.valueOf(this.instanceId));
            return true;
        } else {
            Logger.m185w("[%d] loadBottom <<< cannot load bottom", Integer.valueOf(this.instanceId));
            return false;
        }
    }

    public boolean loadTop() {
        Logger.m173d("[%d] loadTop >>>", Integer.valueOf(this.instanceId));
        if (this.isLoading) {
            Logger.m185w("[%d] loadTop <<< loading in progress, do nothing", Integer.valueOf(this.instanceId));
            return false;
        } else if (this.data.canLoadTop()) {
            this.isLoading = true;
            int i = this.deliverResultCount + 1;
            this.deliverResultCount = i;
            this.currentTask = submitToBgThread(new LoadChunkRunnable(i, new StreamData(this.data), 1));
            Logger.m173d("[%d] loadTop <<<", Integer.valueOf(this.instanceId));
            return true;
        } else {
            Logger.m185w("[%d] loadTop <<< cannot load top", Integer.valueOf(this.instanceId));
            return false;
        }
    }

    public void reset() {
        Logger.m173d("[%d] reset", Integer.valueOf(this.instanceId));
        this.data = new StreamData();
        this.isInitialized = false;
        cancelCurrentTask();
    }

    public void reset(StreamPage firstPage, boolean keepPromoLinks) {
        Logger.m173d("[%d] reset: firstPage=%s keepPromoLinks=%s", Integer.valueOf(this.instanceId), firstPage, Boolean.valueOf(keepPromoLinks));
        cancelCurrentTask();
        StreamData newData = new StreamData();
        if (keepPromoLinks) {
            newData.headerBanners.addAll(this.data.headerBanners);
        }
        newData.holidays = this.data.holidays;
        addPage(newData, firstPage, 2, this.converter, 0);
        this.data = newData;
        deliverRefreshResult(newData, 0);
    }

    protected StreamPageKey defaultInitialPage() {
        return StreamPageKey.firstPageKey(20);
    }

    private void cancelCurrentTask() {
        if (this.currentTask != null) {
            this.currentTask.cancel(false);
        }
        this.currentTask = null;
        this.isLoading = false;
        synchronized (this.deliverResultLock) {
            this.deliverResultCount++;
            this.uiHandler.removeCallbacksAndMessages(null);
            Logger.m173d("[%d] cancelCurrentTask: %s, deliverResultCount=%d", Integer.valueOf(this.instanceId), this.currentTask, Integer.valueOf(this.deliverResultCount));
        }
    }

    @NonNull
    private StreamContext getStreamContext() {
        StreamContext streamContext = (StreamContext) getArguments().getParcelable("stream_context");
        if (streamContext == null) {
            return StreamContext.stream();
        }
        return streamContext;
    }

    @Nullable
    private StreamListPosition getSavedPosition() {
        return (StreamListPosition) getArguments().getParcelable("saved_position");
    }

    private boolean getInitOnCreate() {
        return getArguments().getBoolean("init_on_create", true);
    }

    private int findPosition(StreamData data, @Nullable StreamListPosition savedPosition) {
        if (savedPosition == null) {
            return -1;
        }
        int dataSize = data.items.size();
        int adapterPosition = savedPosition.adapterPosition;
        if (adapterPosition >= 0 && adapterPosition < dataSize && ((StreamItem) data.items.get(adapterPosition)).getId() == savedPosition.itemId) {
            return adapterPosition;
        }
        for (int i = 0; i < dataSize; i++) {
            if (((StreamItem) data.items.get(i)).getId() == savedPosition.itemId) {
                return i;
            }
        }
        return -1;
    }

    private void startInitialLoading(@Nullable StreamListPosition savedPosition) {
        boolean restorePositionFromPrefs;
        Logger.m173d("[%d] startInitialLoading: savedPosition=%d", Integer.valueOf(this.instanceId), savedPosition);
        this.isLoading = true;
        boolean forceLoadFromWeb = false;
        if (this.streamContext.type == 1) {
            submitToBgThread(new InitUnreadStreamRunnable());
        } else {
            forceLoadFromWeb = savedPosition == null;
        }
        StreamPageKey pageKey = savedPosition == null ? defaultInitialPage() : savedPosition.pageKey;
        if (this.streamContext.type == 1) {
            restorePositionFromPrefs = true;
        } else {
            restorePositionFromPrefs = false;
        }
        int i = this.deliverResultCount + 1;
        this.deliverResultCount = i;
        this.currentTask = submitToBgThread(new InitialLoadRunnable(i, pageKey, restorePositionFromPrefs, forceLoadFromWeb, savedPosition));
    }

    private void startDeleteFeeds() {
        Logger.m172d("[%d] startDeleteFeeds");
        int i = this.deliverResultCount + 1;
        this.deliverResultCount = i;
        this.currentTask = submitToBgThread(new DeleteFeedsTask(i));
    }

    void deliverInitialResult(StreamData data, StreamListPosition restoredPosition, int benchmarkSeqId) {
        Logger.m173d("[%d] deliverInitialResult: data=%s restoredPosition=%s benchmarkSeqId=%d", Integer.valueOf(this.instanceId), data, restoredPosition, Integer.valueOf(benchmarkSeqId));
        this.data = data;
        this.isLoading = false;
        this.isInitialized = true;
        this.currentTask = null;
        StreamDataCallback callback = (StreamDataCallback) this.callbackRef.get();
        Logger.m173d("[%d] deliverInitialResult: callback=%s", Integer.valueOf(this.instanceId), callback);
        if (callback != null) {
            callback.onInitialDataLoaded(data, restoredPosition, benchmarkSeqId);
        }
    }

    void postDeliverInitialResult(StreamData data, StreamListPosition restoredPosition, int benchmarkSeqId) {
        Logger.m173d("[%d] postDeliverInitialResult: deliverResultId=%d data=%s restoredPosition=%s benchmarkSeqId=%d", Integer.valueOf(this.instanceId), Integer.valueOf(data.deliverResultId), data, restoredPosition, Integer.valueOf(benchmarkSeqId));
        Message msg = Message.obtain(this.uiHandler, 1, 0, benchmarkSeqId, data);
        if (restoredPosition != null) {
            msg.getData().putParcelable("pos", restoredPosition);
        }
        this.uiHandler.sendMessage(msg);
    }

    void deliverRefreshResult(StreamData data, int benchmarkSeqId) {
        Logger.m173d("[%d] deliverRefreshResult: data=%s benchmarkSeqId=%d", Integer.valueOf(this.instanceId), data, Integer.valueOf(benchmarkSeqId));
        this.data = data;
        this.isLoading = false;
        this.currentTask = null;
        StreamDataCallback callback = (StreamDataCallback) this.callbackRef.get();
        Logger.m173d("[%d] deliverRefreshResult: callback=%s", Integer.valueOf(this.instanceId), callback);
        if (callback != null) {
            callback.onStreamRefreshed(data, benchmarkSeqId);
        }
    }

    void postDeliverRefreshResult(StreamData data, int benchmarkSeqId) {
        Logger.m173d("[%d] postDeliverRefreshResult: deliverResultId=%d data=%s benchmarkSeqId=%d", Integer.valueOf(this.instanceId), Integer.valueOf(data.deliverResultId), data, Integer.valueOf(benchmarkSeqId));
        this.uiHandler.sendMessage(Message.obtain(this.uiHandler, 2, 0, benchmarkSeqId, data));
    }

    void deliverAddChunk(StreamData data, int newItemsCount, int side, int benchmarkSeqId) {
        Logger.m173d("[%d] deliverAddChunk: data=%s newItemsCount=%d side=%d benchmarkSeqId=%d", Integer.valueOf(this.instanceId), data, Integer.valueOf(newItemsCount), Integer.valueOf(side), Integer.valueOf(benchmarkSeqId));
        this.data = data;
        this.isLoading = false;
        this.currentTask = null;
        StreamDataCallback callback = (StreamDataCallback) this.callbackRef.get();
        Logger.m173d("[%d] deliverAddChunk: callback=%s", Integer.valueOf(this.instanceId), callback);
        if (callback == null) {
            return;
        }
        if (side == 1) {
            callback.onAddedTopChunk(data, newItemsCount, benchmarkSeqId);
        } else {
            callback.onAddedBottomChunk(data, newItemsCount, benchmarkSeqId);
        }
    }

    void postDeliverAddBottomChunk(StreamData data, int newItemsCount, int benchmarkSeqId) {
        Logger.m173d("[%d] postDeliverAddBottomChunk: deliverResultId=%d data=%s newItemsCount=%d benchmarkSeqId=%d", Integer.valueOf(this.instanceId), Integer.valueOf(data.deliverResultId), data, Integer.valueOf(newItemsCount), Integer.valueOf(benchmarkSeqId));
        this.uiHandler.sendMessage(Message.obtain(this.uiHandler, 4, newItemsCount, benchmarkSeqId, data));
    }

    void postDeliverAddTopChunk(StreamData data, int newItemsCount, int benchmarkSeqId) {
        Logger.m173d("[%d] postDeliverAddTopChunk: deliverResultId=%d data=%s newItemsCount=%d benchmarkSeqId=%d", Integer.valueOf(this.instanceId), Integer.valueOf(data.deliverResultId), data, Integer.valueOf(newItemsCount), Integer.valueOf(benchmarkSeqId));
        this.uiHandler.sendMessage(Message.obtain(this.uiHandler, 3, newItemsCount, benchmarkSeqId, data));
    }

    void deliverDeletedFeeds(StreamData data, boolean hasChanged) {
        this.data = data;
        this.isLoading = false;
        this.currentTask = null;
        StreamDataCallback callback = (StreamDataCallback) this.callbackRef.get();
        Logger.m173d("[%d] deliverDeletedFeeds: data=%s hasChanged=%s callback=%s", Integer.valueOf(this.instanceId), data, Boolean.valueOf(hasChanged), callback);
        if (hasChanged && callback != null) {
            callback.onDeletedFeeds(data);
        }
    }

    void postDeliverDeletedFeeds(StreamData data, boolean hasChanged) {
        int i = 1;
        Logger.m173d("[%d] postDeliverDeletedFeeds: deliverResultId=%d data=%s hasChanged=%s", Integer.valueOf(this.instanceId), Integer.valueOf(data.deliverResultId), data, Boolean.valueOf(hasChanged));
        UIHandler uIHandler = this.uiHandler;
        Handler handler = this.uiHandler;
        if (!hasChanged) {
            i = 0;
        }
        uIHandler.sendMessage(Message.obtain(handler, 8, i, 0, data));
    }

    void deliverInitialDataLoadingError(ErrorType errorType) {
        Logger.m173d("[%d] deliverInitialDataLoadingError: %s", Integer.valueOf(this.instanceId), errorType);
        this.isLoading = false;
        this.currentTask = null;
        StreamDataCallback callback = (StreamDataCallback) this.callbackRef.get();
        Logger.m173d("[%d] deliverInitialDataLoadingError: callback=%s", Integer.valueOf(this.instanceId), callback);
        if (callback != null) {
            callback.onInitialDataLoadingError(errorType);
        }
    }

    void deliverStreamRefreshError(ErrorType errorType) {
        Logger.m173d("[%d] deliverStreamRefreshError: %s", Integer.valueOf(this.instanceId), errorType);
        this.isLoading = false;
        this.currentTask = null;
        StreamDataCallback callback = (StreamDataCallback) this.callbackRef.get();
        Logger.m173d("[%d] deliverStreamRefreshError: callback=%s", Integer.valueOf(this.instanceId), callback);
        if (callback != null) {
            callback.onStreamRefreshError(errorType);
        }
    }

    void deliverAddChunkError(ErrorType errorType, int side) {
        String str = "[%d] deliverAddChunkError: %s %s";
        Object[] objArr = new Object[3];
        objArr[0] = Integer.valueOf(this.instanceId);
        objArr[1] = errorType;
        objArr[2] = side == 1 ? "TOP" : "BOTTOM";
        Logger.m173d(str, objArr);
        this.isLoading = false;
        this.currentTask = null;
        StreamDataCallback callback = (StreamDataCallback) this.callbackRef.get();
        Logger.m173d("[%d] deliverAddChunkError: callback=%s", Integer.valueOf(this.instanceId), callback);
        if (callback == null) {
            return;
        }
        if (side == 1) {
            callback.onAddTopChunkError(errorType);
        } else {
            callback.onAddBottomChunkError(errorType);
        }
    }

    void postDeliverInitialDataLoadingError(int deliverResultId, ErrorType errorType) {
        Logger.m173d("[%d] postDeliverInitialDataLoadingError: deliverResultId=%d error=%s", Integer.valueOf(this.instanceId), Integer.valueOf(deliverResultId), errorType);
        this.uiHandler.sendMessage(Message.obtain(this.uiHandler, 5, 0, deliverResultId, errorType));
    }

    void postDeliverStreamRefreshError(int deliverResultId, ErrorType errorType) {
        Logger.m173d("[%d] postDeliverStreamRefreshError: deliverResultId=%d error=%s", Integer.valueOf(this.instanceId), Integer.valueOf(deliverResultId), errorType);
        this.uiHandler.sendMessage(Message.obtain(this.uiHandler, 6, 0, deliverResultId, errorType));
    }

    void postDeliverAddChunkError(int deliverResultId, ErrorType errorType, int side) {
        String str = "[%d] postDeliverAddChunkError: deliverResultId=%d error=%s side=%s";
        Object[] objArr = new Object[4];
        objArr[0] = Integer.valueOf(this.instanceId);
        objArr[1] = Integer.valueOf(deliverResultId);
        objArr[2] = errorType;
        objArr[3] = side == 1 ? "TOP" : "BOTTOM";
        Logger.m173d(str, objArr);
        this.uiHandler.sendMessage(Message.obtain(this.uiHandler, 7, side, deliverResultId, errorType));
    }

    private ExecutorService createBgExecutor() {
        return new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), new NamedThreadFactory("StreamDataFragment.Bg"));
    }

    private void addPage(StreamData data, StreamPage page, int side, Feed2StreamItemBinder converter, int benchmarkSeqId) {
        int i;
        boolean wasEmpty = data.pages.isEmpty();
        DeletedFeedsManager deletedFeedsManager = this.storages.getDeletedFeedsManager();
        StreamSubscriptionManager subscriptionManager = this.storages.getStreamSubscriptionManager();
        if (side == 1) {
            data.pages.addFirst(page);
            data.topPageKey = page.getTopKey();
            if (wasEmpty) {
                data.bottomPageKey = page.getBottomKey();
            }
        } else {
            data.pages.addLast(page);
            data.bottomPageKey = page.getBottomKey();
            if (wasEmpty) {
                data.topPageKey = page.getTopKey();
            }
        }
        ArrayList<StreamItem> pageItems = (ArrayList) itemsRef.getAndSet(null);
        if (pageItems == null) {
            pageItems = new ArrayList();
        }
        ArrayList<FeedWithState> pageFeeds = (ArrayList) feedsRef.getAndSet(null);
        if (pageFeeds == null) {
            pageFeeds = new ArrayList();
        }
        int size = page.feeds.size();
        for (i = 0; i < size; i++) {
            Feed feed = (Feed) page.feeds.get(i);
            boolean isDeleted = feed.getDeleteId() != null && deletedFeedsManager.isFeedDeleted(feed.getDeleteId());
            if (!isDeleted) {
                boolean isUnsubscribed = this.streamContext.type == 1 && subscriptionManager.isUnsubscribedFeedOwner(feed);
                if (!isUnsubscribed) {
                    pageFeeds.add(new FeedWithState(feed));
                    StreamBannerStatisticsHandler.handleShown(this.context, feed);
                }
            }
        }
        converter.feeds2items(pageFeeds, pageItems);
        if (side == 1) {
            data.feeds.addAll(0, pageFeeds);
            size = data.feeds.size();
            for (i = 0; i < size; i++) {
                ((FeedWithState) data.feeds.get(i)).position = i;
            }
            data.items.addAll(0, pageItems);
        } else {
            int initialSize = data.feeds.size();
            data.feeds.addAll(pageFeeds);
            size = pageFeeds.size();
            for (i = 0; i < size; i++) {
                ((FeedWithState) pageFeeds.get(i)).position = initialSize + i;
            }
            data.items.addAll(pageItems);
        }
        pageItems.clear();
        pageFeeds.clear();
        itemsRef.set(pageItems);
        feedsRef.set(pageFeeds);
        StreamBenchmark.generateCards(benchmarkSeqId);
    }

    public void onStreamSubscription(int ownerType, String ownerId, boolean isSubscribed) {
        Logger.m173d("ownerType=%d ownerId=%s isSubscribed=%s", Integer.valueOf(ownerType), ownerId, Boolean.valueOf(isSubscribed));
        if (!isSubscribed) {
            if (ThreadUtil.isMainThread()) {
                this.deliverIdDeletedFeed = this.deliverResultCount;
                checkForDeletedFeeds(this.data);
                return;
            }
            startDeleteFeeds();
        }
    }

    public void onFeedDeleted(String deleteId) {
        if (ThreadUtil.isMainThread()) {
            this.deliverIdDeletedFeed = this.deliverResultCount;
            checkForDeletedFeeds(this.data);
        } else {
            startDeleteFeeds();
        }
        Logger.m173d("deleteId=%s deliverIdDeletedFeed=%d", deleteId, Integer.valueOf(this.deliverIdDeletedFeed));
    }

    private boolean checkForDeletedFeeds(StreamData data) {
        long startTime = System.currentTimeMillis();
        int deletedFeedCount = 0;
        int deletedItemCount = 0;
        DeletedFeedsManager dfm = this.storages.getDeletedFeedsManager();
        StreamSubscriptionManager ssm = this.storages.getStreamSubscriptionManager();
        int feedPos = 0;
        while (feedPos < data.feeds.size()) {
            FeedWithState feedWs = (FeedWithState) data.feeds.get(feedPos);
            String deleteId = feedWs.feed.getDeleteId();
            if ((deleteId == null || !dfm.isFeedDeleted(deleteId)) && !ssm.isUnsubscribedFeedOwner(feedWs.feed)) {
                int feedPos2 = feedPos + 1;
                feedWs.position = feedPos;
                feedPos = feedPos2;
            } else {
                data.feeds.remove(feedPos);
                deletedFeedCount++;
                r16 = new Object[2];
                r16[0] = Long.valueOf(feedWs.feed.getId());
                r16[1] = deleteId;
                Logger.m173d("deleted feedId=%d deleteId=%s", r16);
            }
        }
        if (deletedFeedCount > 0) {
            for (int i = data.items.size() - 1; i >= 0; i--) {
                Feed feed = ((StreamItem) data.items.get(i)).feedWithState.feed;
                if (feed == null) {
                    data.items.remove(i);
                    deletedItemCount++;
                } else {
                    deleteId = feed.getDeleteId();
                    if ((deleteId != null && dfm.isFeedDeleted(deleteId)) || ssm.isUnsubscribedFeedOwner(feed)) {
                        data.items.remove(i);
                        deletedItemCount++;
                    }
                }
            }
        }
        Logger.m173d("deleted %d feeds and %d items in %d ms", Integer.valueOf(deletedFeedCount), Integer.valueOf(deletedItemCount), Long.valueOf(System.currentTimeMillis() - startTime));
        return deletedFeedCount > 0 || deletedItemCount > 0;
    }

    private boolean isTimeForForceRefresh(StreamSettingsHelper settingsHelper) {
        if (this.streamContext.type != 1) {
            return false;
        }
        boolean forceRefresh = false;
        long lastActivityTs = settingsHelper.getLastActivityTs();
        long elapsedTime = 0;
        long refreshInterval = 0;
        if (lastActivityTs > 0) {
            elapsedTime = System.currentTimeMillis() - lastActivityTs;
            refreshInterval = settingsHelper.getForceRefreshInterval() * 1000;
            if (elapsedTime >= refreshInterval) {
                forceRefresh = true;
            }
        }
        Logger.m173d("[%d] lastActivityTs=%d, elapsedTime=%d, refreshInterval=%d -> forceRefresh=%s", Integer.valueOf(this.instanceId), Long.valueOf(lastActivityTs), Long.valueOf(elapsedTime), Long.valueOf(refreshInterval), Boolean.valueOf(forceRefresh));
        return forceRefresh;
    }
}
