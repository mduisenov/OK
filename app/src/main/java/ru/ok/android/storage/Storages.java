package ru.ok.android.storage;

import android.content.Context;
import android.text.TextUtils;
import ru.ok.android.services.feeds.DeletedFeedsManager;
import ru.ok.android.services.feeds.subscribe.StreamSubscriptionManager;
import ru.ok.android.services.like.LikeManager;
import ru.ok.android.services.marks.MarksManager;
import ru.ok.android.services.mediatopic_polls.MtPollsManager;
import ru.ok.android.services.presents.DeletedPresentsManager;
import ru.ok.android.services.processors.stream.HolidaysFileCache;
import ru.ok.android.services.processors.stream.StreamFileCache;
import ru.ok.android.storage.sqlite.SqliteFeedBannerStatsStorage;
import ru.ok.android.storage.sqlite.SqliteFeedIdStorage;

public final class Storages {
    private static volatile Storages instance;
    private final Context context;
    private final String currentUserId;
    private volatile DeletedFeedsManager deletedFeedsManager;
    private volatile DeletedPresentsManager deletedPresentsManager;
    private volatile IFeedBannerStatsStorage feedBannerStatsStorage;
    private volatile IFeedIdStorage feedIdStorage;
    private volatile HolidaysFileCache holidaysCache;
    private StreamCacheInitCondition initCondition;
    private volatile LikeManager likeManager;
    private final Object lock;
    private volatile MarksManager marksManager;
    private volatile MtPollsManager mtPollsManager;
    private volatile StreamFileCache streamCache;
    private volatile StreamSubscriptionManager streamSubscriptionManager;
    private StreamCacheInitListener streamTrimListener;
    private volatile StreamFileCache streamUnreadCache;

    public static final Storages getInstance(Context context, String currentUserId) {
        Storages instance = instance;
        if (instance == null || !TextUtils.equals(instance.currentUserId, currentUserId)) {
            synchronized (Storages.class) {
                try {
                    instance = instance;
                    if (instance == null || !TextUtils.equals(instance.currentUserId, currentUserId)) {
                        Storages instance2 = new Storages(context, currentUserId);
                        try {
                            instance = instance2;
                            instance = instance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            instance = instance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return instance;
    }

    public void waitStreamCacheInitialized() {
        this.initCondition.block();
    }

    public StreamFileCache getStreamCache() {
        if (this.streamCache == null) {
            synchronized (this.lock) {
                if (this.streamCache == null) {
                    this.streamCache = new StreamFileCache(this.context, this.currentUserId, "stream", this);
                }
            }
        }
        return this.streamCache;
    }

    public StreamFileCache getUnreadStreamCache() {
        if (this.streamUnreadCache == null) {
            synchronized (this.lock) {
                if (this.streamUnreadCache == null) {
                    this.streamUnreadCache = new StreamFileCache(this.context, this.currentUserId, "stream_unread", this);
                }
            }
        }
        return this.streamUnreadCache;
    }

    public LikeManager getLikeManager() {
        if (this.likeManager == null) {
            synchronized (this.lock) {
                if (this.likeManager == null) {
                    this.likeManager = new LikeManager(this.context, this.currentUserId, this.streamTrimListener.likeInitListener);
                }
            }
        }
        return this.likeManager;
    }

    public MarksManager getMarksManager() {
        if (this.marksManager == null) {
            synchronized (this.lock) {
                if (this.marksManager == null) {
                    this.marksManager = new MarksManager(this.context);
                }
            }
        }
        return this.marksManager;
    }

    public DeletedFeedsManager getDeletedFeedsManager() {
        if (this.deletedFeedsManager == null) {
            synchronized (this.lock) {
                if (this.deletedFeedsManager == null) {
                    this.deletedFeedsManager = new DeletedFeedsManager(this.context, this.currentUserId, this.streamTrimListener.deletedFeedsInitListner);
                }
            }
        }
        return this.deletedFeedsManager;
    }

    public DeletedPresentsManager getDeletedPresentsManager() {
        if (this.deletedPresentsManager == null) {
            synchronized (this.lock) {
                if (this.deletedPresentsManager == null) {
                    this.deletedPresentsManager = new DeletedPresentsManager(this.context, this.currentUserId);
                }
            }
        }
        return this.deletedPresentsManager;
    }

    public StreamSubscriptionManager getStreamSubscriptionManager() {
        if (this.streamSubscriptionManager == null) {
            synchronized (this.lock) {
                if (this.streamSubscriptionManager == null) {
                    this.streamSubscriptionManager = new StreamSubscriptionManager(this.context, this.currentUserId, this.streamTrimListener.subscriptionInitListener);
                }
            }
        }
        return this.streamSubscriptionManager;
    }

    public MtPollsManager getMtPollsManager() {
        if (this.mtPollsManager == null) {
            synchronized (this.lock) {
                if (this.mtPollsManager == null) {
                    this.mtPollsManager = new MtPollsManager(this.context, this.currentUserId, this.streamTrimListener.pollsInitListener);
                }
            }
        }
        return this.mtPollsManager;
    }

    public IFeedIdStorage getFeedIdStorage() {
        if (this.feedIdStorage == null) {
            synchronized (this.lock) {
                if (this.feedIdStorage == null) {
                    this.feedIdStorage = new SqliteFeedIdStorage(this.context);
                }
            }
        }
        return this.feedIdStorage;
    }

    public IFeedBannerStatsStorage getFeedBannerStatsStorage() {
        if (this.feedBannerStatsStorage == null) {
            synchronized (this.lock) {
                if (this.feedBannerStatsStorage == null) {
                    this.feedBannerStatsStorage = new SqliteFeedBannerStatsStorage(this.context, this.currentUserId);
                }
            }
        }
        return this.feedBannerStatsStorage;
    }

    public HolidaysFileCache getHolidaysCache() {
        if (this.holidaysCache == null) {
            synchronized (this.lock) {
                if (this.holidaysCache == null) {
                    this.holidaysCache = new HolidaysFileCache(this.context, this.currentUserId, "holidays");
                }
            }
        }
        return this.holidaysCache;
    }

    private Storages(Context context, String currentUserId) {
        this.lock = new Object();
        this.context = context.getApplicationContext();
        this.currentUserId = currentUserId;
        this.initCondition = new StreamCacheInitCondition(this);
        this.streamTrimListener = new StreamCacheInitListener(context, this.initCondition, this);
    }
}
