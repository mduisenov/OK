package ru.ok.android.services.processors.stream;

import android.content.Context;
import android.os.ConditionVariable;
import android.text.TextUtils;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.flurry.StreamErrors;
import ru.ok.android.storage.StorageException;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.android.utils.LogUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.java.api.json.stream.JsonGetStreamParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.stream.GetStreamRequest;
import ru.ok.model.events.OdnkEvent.EventType;
import ru.ok.model.stream.StreamPage;
import ru.ok.model.stream.StreamPageKey;
import ru.ok.model.stream.UnreadStreamPage;

public class UnreadStream {
    private static UnreadStream instance;
    private static boolean streamDisplayedAfterLogin;
    private final Context context;
    private final String currentUserId;
    private UnreadStreamPage firstUnreadPage;
    private final ConditionVariable initCondition;
    private final Storages storages;
    private final StreamFileCache unreadFileCache;
    private final StreamPageKey unreadPageKey;
    private final StreamContext unreadStreamContext;

    /* renamed from: ru.ok.android.services.processors.stream.UnreadStream.1 */
    class C04941 implements Runnable {
        C04941() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r5 = this;
            r2 = ru.ok.android.services.processors.stream.UnreadStream.this;	 Catch:{ StorageException -> 0x0036 }
            r1 = ru.ok.android.services.processors.stream.UnreadStream.this;	 Catch:{ StorageException -> 0x0036 }
            r1 = r1.unreadFileCache;	 Catch:{ StorageException -> 0x0036 }
            r3 = ru.ok.android.ui.stream.data.StreamContext.stream();	 Catch:{ StorageException -> 0x0036 }
            r4 = 20;
            r4 = ru.ok.model.stream.StreamPageKey.firstPageKey(r4);	 Catch:{ StorageException -> 0x0036 }
            r1 = r1.get(r3, r4);	 Catch:{ StorageException -> 0x0036 }
            r1 = (ru.ok.model.stream.UnreadStreamPage) r1;	 Catch:{ StorageException -> 0x0036 }
            r2.firstUnreadPage = r1;	 Catch:{ StorageException -> 0x0036 }
            r1 = ru.ok.android.services.processors.stream.UnreadStream.this;	 Catch:{ StorageException -> 0x0036 }
            r1 = r1.firstUnreadPage;	 Catch:{ StorageException -> 0x0036 }
            if (r1 == 0) goto L_0x002c;
        L_0x0023:
            r1 = ru.ok.android.services.processors.stream.UnreadStream.this;	 Catch:{ StorageException -> 0x0036 }
            r1 = r1.firstUnreadPage;	 Catch:{ StorageException -> 0x0036 }
            r1.resolveRefs();	 Catch:{ StorageException -> 0x0036 }
        L_0x002c:
            r1 = ru.ok.android.services.processors.stream.UnreadStream.this;
            r1 = r1.initCondition;
            r1.open();
        L_0x0035:
            return;
        L_0x0036:
            r0 = move-exception;
            r1 = "Failed to load unread stream from cache: %s";
            r2 = 1;
            r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x0053 }
            r3 = 0;
            r2[r3] = r0;	 Catch:{ all -> 0x0053 }
            ru.ok.android.utils.Logger.m180e(r0, r1, r2);	 Catch:{ all -> 0x0053 }
            r1 = ru.ok.android.services.processors.stream.UnreadStream.this;	 Catch:{ all -> 0x0053 }
            r2 = 0;
            r1.firstUnreadPage = r2;	 Catch:{ all -> 0x0053 }
            r1 = ru.ok.android.services.processors.stream.UnreadStream.this;
            r1 = r1.initCondition;
            r1.open();
            goto L_0x0035;
        L_0x0053:
            r1 = move-exception;
            r2 = ru.ok.android.services.processors.stream.UnreadStream.this;
            r2 = r2.initCondition;
            r2.open();
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.processors.stream.UnreadStream.1.run():void");
        }
    }

    /* renamed from: ru.ok.android.services.processors.stream.UnreadStream.2 */
    class C04952 implements Runnable {
        C04952() {
        }

        public void run() {
            synchronized (UnreadStream.this) {
                UnreadStream.this.initCondition.block();
                UnreadStream.this.copyUnreadToRegular();
                UnreadStream.this.clearUnreadStream();
                GlobalBus.send(2131624017, new BusEvent());
            }
        }
    }

    public static synchronized UnreadStream getInstance(Context context, String currentUserId) {
        UnreadStream unreadStream;
        synchronized (UnreadStream.class) {
            if (instance == null || !TextUtils.equals(instance.currentUserId, currentUserId)) {
                instance = new UnreadStream(context, currentUserId);
            }
            unreadStream = instance;
        }
        return unreadStream;
    }

    static {
        streamDisplayedAfterLogin = true;
    }

    public UnreadStream(Context context, String currentUserId) {
        this.initCondition = new ConditionVariable();
        this.unreadStreamContext = StreamContext.stream();
        this.unreadPageKey = StreamPageKey.firstPageKey(20);
        this.context = context.getApplicationContext();
        this.storages = Storages.getInstance(context, currentUserId);
        this.unreadFileCache = this.storages.getUnreadStreamCache();
        this.currentUserId = currentUserId;
        init();
    }

    private void init() {
        ThreadUtil.execute(new C04941());
    }

    public void waitForInitialization() {
        this.initCondition.block();
    }

    public UnreadStreamPage getFirstUnreadPage() {
        this.initCondition.block();
        return this.firstUnreadPage;
    }

    public static void onLoggedIn() {
        streamDisplayedAfterLogin = false;
    }

    public synchronized void onDisplayedUnreadStream() {
        this.firstUnreadPage = null;
        ThreadUtil.execute(new C04952());
        resetEventManager();
    }

    public synchronized void onLoadedFirstStreamPage() {
        this.initCondition.block();
        clearUnreadStream();
        resetEventManager();
    }

    public static void onDisplayedFirstStreamPage() {
        streamDisplayedAfterLogin = true;
    }

    private void resetEventManager() {
        EventsManager eventsManager = EventsManager.getInstance();
        eventsManager.setEmptyValue(EventType.ACTIVITIES);
        eventsManager.sendActualValue();
        eventsManager.clearActivityCounter();
    }

    private void clearUnreadStream() {
        Logger.m172d("");
        this.firstUnreadPage = null;
        try {
            this.unreadFileCache.remove(StreamContext.stream(), StreamPageKey.firstPageKey(20));
        } catch (StorageException e) {
            Logger.m180e(e, "Failed to clear unread stream cache: %s", e);
        }
    }

    public BaseRequest getUpdateRequest() {
        if (streamDisplayedAfterLogin) {
            return GetStreamProcessor.createRequest(StreamPageKey.firstPageKey(1), StreamContext.stream(), false, GetStreamRequest.FIELDS_ONLY_FEEDS, null, "FRIENDSHIP,JOIN,MESSAGE,PRESENT,PIN,CONTENT,GIFTS_CAMPAIGN", null);
        }
        return null;
    }

    public String getBatchResponseField() {
        return "stream_get_response";
    }

    public synchronized UnreadStreamPage handleUpdateResponse(JSONObject response) {
        UnreadStreamPage unreadStreamPage;
        long startTime = System.currentTimeMillis();
        Logger.m172d(">>>");
        this.initCondition.block();
        int unreadCountApi = getUnreadCountFromApi(response);
        Logger.m173d("unreadCountApi=%d", Integer.valueOf(unreadCountApi));
        if (unreadCountApi <= 0) {
            clearUnreadStream();
            Logger.m173d("<<< no unread feeds, %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
            unreadStreamPage = null;
        } else if (this.firstUnreadPage == null || this.firstUnreadPage.getTotalUnreadFeedsCount() < unreadCountApi) {
            this.firstUnreadPage = refreshUnreadStream(unreadCountApi);
            Logger.m173d("<<< refreshed in %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
            unreadStreamPage = this.firstUnreadPage;
        } else {
            Logger.m173d("<<< nothing new, %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
            unreadStreamPage = this.firstUnreadPage;
        }
        return unreadStreamPage;
    }

    private UnreadStreamPage refreshUnreadStream(int unreadCount) {
        UnreadStreamPage unreadPage;
        StreamPageKey pageKey = StreamPageKey.firstPageKey(20);
        long requestTs = System.currentTimeMillis();
        try {
            StreamPage page = GetStreamProcessor.getStreamFromAPI(this.context, this.storages, pageKey, StreamContext.stream(), false, false, null, 0).streamPage;
            Logger.m173d("got first page of stream from API: %s", page);
            LogUtils.logFeeds(page.feeds, "GetEventsProcessor.getStreamNewEvents:");
            unreadPage = new UnreadStreamPage(page, unreadCount);
        } catch (StreamLoadException e) {
            Logger.m180e(e, "Failed to load stream for new events: %s", e);
            StreamErrors.logAndFilterError("refresh_unread_stream", e.getMessage(), e);
            unreadPage = null;
        }
        if (unreadPage != null) {
            try {
                this.unreadFileCache.put(StreamContext.stream(), pageKey, unreadPage, requestTs);
            } catch (StorageException e2) {
                Logger.m180e(e2, "Failed to save unread stream page to cache: %s", e2);
            }
        }
        return unreadPage;
    }

    private static int getUnreadCountFromApi(JSONObject jsonGetStream) {
        try {
            return JsonGetStreamParser.parseStreamUnreadCount(jsonGetStream);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to parse response from stream.get");
            return 0;
        }
    }

    private void copyUnreadToRegular() {
        try {
            StreamPage unreadPage = this.unreadFileCache.get(this.unreadStreamContext, this.unreadPageKey);
            if (unreadPage != null) {
                Storages.getInstance(this.context, this.currentUserId).getStreamCache().put(StreamContext.stream(), this.unreadPageKey, new StreamPage(unreadPage), unreadPage.getStreamTs());
                return;
            }
            Logger.m185w("Unread stream cache is empty: context=%s key=%s", this.unreadStreamContext, this.unreadPageKey);
        } catch (StorageException e) {
            Logger.m180e(e, "Failed to copy unread stream page to regular cache: %s", e);
        }
    }
}
