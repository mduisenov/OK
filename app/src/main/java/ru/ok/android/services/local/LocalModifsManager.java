package ru.ok.android.services.local;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.ApiSyncService;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.model.local.LocalModifs;

public abstract class LocalModifsManager<TLocal extends LocalModifs> {
    private final LocalModifsConnectivityListener connectivityListener;
    private final Context context;
    protected final String currentUserId;
    protected final LocalModifsCache<TLocal> localModifsCache;
    protected final int maxSyncAttemptCount;
    private long syncDelayMs;
    private final ru.ok.android.services.local.LocalModifsManager$ru.ok.android.services.local.LocalModifsManager.LocalModifsSyncHandler syncHandler;

    class LocalModifsSyncHandler extends Handler {
        private boolean isSyncServiceOn;
        private ArrayList<TLocal> unsyncedLikes;

        LocalModifsSyncHandler(Looper looper) {
            super(looper);
            this.unsyncedLikes = new ArrayList();
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    syncAll();
                default:
            }
        }

        void postSyncAll() {
            Logger.m172d("");
            if (!hasMessages(1)) {
                sendEmptyMessage(1);
            }
        }

        void postSyncAllDelayed(long delayMs) {
            Logger.m172d("");
            removeMessages(1);
            sendEmptyMessageDelayed(1, delayMs);
        }

        private void syncAll() {
            boolean hasUnsynced;
            Logger.m172d("syncAll >>>");
            boolean hasInternet = NetUtils.isConnectionAvailable(LocalModifsManager.this.context, true);
            if (!hasInternet) {
                Logger.m184w("syncAll: no Internet connection, skip sync");
            }
            ArrayList<TLocal> unsyncedLikes = this.unsyncedLikes;
            if (unsyncedLikes == null) {
                unsyncedLikes = new ArrayList();
                this.unsyncedLikes = unsyncedLikes;
            }
            LocalModifsManager.this.localModifsCache.getUnsyncedItems(unsyncedLikes);
            Logger.m173d("syncAll: unsynced count=%d", Integer.valueOf(unsyncedLikes.size()));
            if (unsyncedLikes.isEmpty()) {
                hasUnsynced = false;
            } else {
                hasUnsynced = true;
            }
            if (hasInternet && hasUnsynced) {
                Logger.m173d("syncAll: %d likes remain unsynced", Integer.valueOf(sync(unsyncedLikes)));
                if (sync(unsyncedLikes) == 0) {
                    hasUnsynced = false;
                }
            }
            if (hasUnsynced) {
                Logger.m172d("Registering connectivity listener");
                GlobalBus.register(LocalModifsManager.this.connectivityListener);
            } else {
                Logger.m172d("Un-registering connectivity listener");
                GlobalBus.unregister(LocalModifsManager.this.connectivityListener);
            }
            unsyncedLikes.clear();
            Logger.m172d("syncAll <<<");
        }

        private int sync(ArrayList<TLocal> unsyncedItems) {
            if (!this.isSyncServiceOn) {
                this.isSyncServiceOn = true;
                ApiSyncService.startSync(LocalModifsManager.this.context);
            }
            int unsyncedCount = 0;
            try {
                int size = unsyncedItems.size();
                for (int i = 0; i < size; i++) {
                    TLocal resultItem = syncItem((LocalModifs) unsyncedItems.get(i));
                    if (!(resultItem.syncStatus == 3 || resultItem.syncStatus == 4)) {
                        unsyncedCount++;
                    }
                }
                return unsyncedCount;
            } finally {
                if (!hasMessages(1)) {
                    ApiSyncService.stopSync(LocalModifsManager.this.context);
                    this.isSyncServiceOn = false;
                }
            }
        }

        TLocal syncItem(TLocal unsyncedItem) {
            Logger.m173d("syncItem >>> %s", unsyncedItem);
            TLocal updatedLocalItem = null;
            try {
                updatedLocalItem = LocalModifsManager.this.performSyncRequest(unsyncedItem);
            } catch (TransportLevelException e) {
                Logger.m185w("syncItem: failed to sync: %s", e);
            } catch (BaseApiException e2) {
                Logger.m180e(e2, "syncItem: failed to sync: %s", e2);
                updatedLocalItem = unsyncedItem.failedAttempt(5);
            }
            if (updatedLocalItem != null) {
                LocalModifsManager.this.localModifsCache.updateOnSync(updatedLocalItem);
                unsyncedItem = updatedLocalItem;
            }
            Logger.m173d("syncItem <<< updated info: %s", unsyncedItem);
            return unsyncedItem;
        }
    }

    protected abstract TLocal performSyncRequest(TLocal tLocal) throws BaseApiException;

    protected LocalModifsManager(Context context, String currentUserId, ILocalModifsStorage<TLocal> storage, LocalModifsStorageConfig storageConfig, LocalModifsStorageInitListener initListener) {
        this.syncDelayMs = 1000;
        this.maxSyncAttemptCount = 5;
        this.context = context.getApplicationContext();
        this.currentUserId = currentUserId;
        this.localModifsCache = new LocalModifsCache(context, storage, storageConfig, initListener);
        this.connectivityListener = new LocalModifsConnectivityListener(context, this);
        HandlerThread thread = new HandlerThread("LikeManager.Sync", 10);
        thread.start();
        this.syncHandler = new LocalModifsSyncHandler(thread.getLooper());
        this.syncHandler.postSyncAll();
    }

    protected void setConflictResolver(LocalSyncConflictResolver<TLocal> conflictResolver) {
        this.localModifsCache.setConflictResolver(conflictResolver);
    }

    protected void setSyncDelayMs(long delayMs) {
        this.syncDelayMs = delayMs;
    }

    protected TLocal getLocalModification(String id) {
        return this.localModifsCache.get(id);
    }

    protected void updateLocalModification(TLocal localModif) {
        this.localModifsCache.update(localModif);
        this.syncHandler.postSyncAllDelayed(this.syncDelayMs);
    }

    public void sync() {
        Logger.m172d("sync");
        this.syncHandler.postSyncAll();
    }

    public void preload(@NonNull ArrayList<String> ids) {
        long startTime = System.currentTimeMillis();
        Logger.m173d("preload >>> ids=%s", ids);
        this.localModifsCache.preload(ids);
        Logger.m173d("preload <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
    }

    protected void deleteSyncedOlder(@NonNull ArrayList<String> ids, long limitTs) {
        long startTime = System.currentTimeMillis();
        Logger.m173d("deleteSyncedOlder >>> ids=%s limitTs=%d", ids, Long.valueOf(limitTs));
        this.localModifsCache.deleteSyncedOlder(ids, limitTs);
        Logger.m173d("deleteSyncedOlder <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
    }
}
