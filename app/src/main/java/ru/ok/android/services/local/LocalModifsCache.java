package ru.ok.android.services.local;

import android.content.Context;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.storage.StorageException;
import ru.ok.android.utils.Logger;
import ru.ok.model.local.LocalModifs;

class LocalModifsCache<TLocal extends LocalModifs> {
    private final HashMap<String, TLocal> allLocalModifs;
    @Nullable
    private LocalSyncConflictResolver<TLocal> conflictResolver;
    private final LocalModifsStorageInitListener initListener;
    private final ru.ok.android.services.local.LocalModifsCache$ru.ok.android.services.local.LocalModifsCache.StorageHandler likeStorageHandler;
    private final ILocalModifsStorage<TLocal> localModifsStorage;
    private final Object lock;
    private final ConditionVariable preloadUnsyncedItemsCondition;
    private final LocalModifsStorageConfig storageConfig;
    private final HashSet<String> unsyncedIds;

    /* renamed from: ru.ok.android.services.local.LocalModifsCache.1 */
    class C04381 implements Runnable {
        C04381() {
        }

        public void run() {
            Logger.m173d("init.run: %s", getClass().getSimpleName());
            long trimTs = 0;
            try {
                trimTs = LocalModifsCache.this.trimSize();
            } finally {
                LocalModifsCache.this.notifyStorageInitialized(trimTs);
            }
        }
    }

    /* renamed from: ru.ok.android.services.local.LocalModifsCache.2 */
    class C04392 implements Runnable {
        C04392() {
        }

        public void run() {
            LocalModifsCache.this.preloadUnsynced();
            LocalModifsCache.this.preloadUnsyncedItemsCondition.open();
        }
    }

    class StorageHandler extends Handler {
        StorageHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    save((LocalModifs) msg.obj);
                default:
            }
        }

        void postSave(TLocal item) {
            Logger.m173d("postSave: %s", item);
            sendMessage(Message.obtain(this, 1, item));
        }

        void save(TLocal item) {
            Logger.m173d("save: %s", item);
            try {
                LocalModifsCache.this.localModifsStorage.update(item);
            } catch (StorageException e) {
                Logger.m180e(e, "save: failed to save local item: %s", e);
            }
        }
    }

    LocalModifsCache(Context context, ILocalModifsStorage<TLocal> localModifsStorage, @NonNull LocalModifsStorageConfig storageConfig, LocalModifsStorageInitListener initListener) {
        this.preloadUnsyncedItemsCondition = new ConditionVariable();
        this.allLocalModifs = new HashMap();
        this.unsyncedIds = new HashSet();
        this.lock = new Object();
        Logger.m173d("Ctor: %s", getClass().getSimpleName());
        this.localModifsStorage = localModifsStorage;
        this.storageConfig = storageConfig;
        this.initListener = initListener;
        HandlerThread thread = new HandlerThread(getClass().getSimpleName(), 10);
        thread.start();
        this.likeStorageHandler = new StorageHandler(thread.getLooper());
        this.likeStorageHandler.post(new C04381());
        this.likeStorageHandler.post(new C04392());
    }

    void setConflictResolver(LocalSyncConflictResolver<TLocal> conflictResolver) {
        this.conflictResolver = conflictResolver;
    }

    @NonNull
    private LocalSyncConflictResolver<TLocal> getConflictResolver() {
        if (this.conflictResolver == null) {
            this.conflictResolver = new DefaultConflictResolver();
        }
        return this.conflictResolver;
    }

    @Nullable
    TLocal get(String id) {
        LocalModifs localModifs;
        this.preloadUnsyncedItemsCondition.block();
        synchronized (this.lock) {
            localModifs = (LocalModifs) this.allLocalModifs.get(id);
        }
        return localModifs;
    }

    protected void preload(@NonNull ArrayList<String> ids) {
        try {
            addIfAbsent(this.localModifsStorage.getById(ids));
        } catch (StorageException e) {
            Logger.m180e(e, "Failed to load local modifs by ids: %s", e);
        }
    }

    void update(TLocal item) {
        this.preloadUnsyncedItemsCondition.block();
        synchronized (this.lock) {
            updateLocked(item);
        }
    }

    public void deleteSyncedOlder(@NonNull ArrayList<String> ids, long limitTs) {
        this.preloadUnsyncedItemsCondition.block();
        synchronized (this.lock) {
            Iterator<Entry<String, TLocal>> entryItr = this.allLocalModifs.entrySet().iterator();
            while (entryItr.hasNext()) {
                LocalModifs local = (LocalModifs) ((Entry) entryItr.next()).getValue();
                if (local.syncStatus == 3 && local.syncedTs <= limitTs && ids.contains(local.id)) {
                    Logger.m173d("Delete local subscription: %s", local);
                    entryItr.remove();
                }
            }
            try {
                this.localModifsStorage.deleteOlder(ids, limitTs);
            } catch (StorageException e) {
                Logger.m187w(e, "Failed to delete outdated entries: %s", e);
            }
        }
    }

    void updateOnSync(TLocal item) {
        this.preloadUnsyncedItemsCondition.block();
        synchronized (this.lock) {
            TLocal savedItem = (LocalModifs) this.allLocalModifs.get(item.id);
            if (savedItem == null || savedItem.syncStatus == 2) {
                updateLocked(item);
            } else {
                Logger.m173d("updateOnSync: item=%s, was changed while syncing, new value: %s", item, savedItem);
                TLocal resultItem = getConflictResolver().onConflictInSync(savedItem, item);
                Logger.m173d("updateOnSync: resultItem=%s", resultItem);
                if (resultItem != savedItem) {
                    updateLocked(resultItem);
                }
            }
        }
    }

    private void updateLocked(TLocal item) {
        String id = item.id;
        this.allLocalModifs.put(id, item);
        if (item.syncStatus == 3 || item.syncStatus == 4) {
            this.unsyncedIds.remove(id);
        } else {
            this.unsyncedIds.add(id);
        }
        this.likeStorageHandler.postSave(item);
    }

    private void addIfAbsent(ArrayList<TLocal> items) {
        this.preloadUnsyncedItemsCondition.block();
        addIfAbsentUnsafe(items);
    }

    private void addIfAbsentUnsafe(@Nullable ArrayList<TLocal> items) {
        if (items != null) {
            synchronized (this.lock) {
                int size = items.size();
                for (int i = 0; i < size; i++) {
                    LocalModifs item = (LocalModifs) items.get(i);
                    String id = item.id;
                    if (!this.allLocalModifs.containsKey(id)) {
                        this.allLocalModifs.put(id, item);
                        if (item.syncStatus != 3) {
                            this.unsyncedIds.add(id);
                        } else {
                            this.unsyncedIds.remove(id);
                        }
                    }
                }
            }
        }
    }

    private void preloadUnsynced() {
        long startTime = System.currentTimeMillis();
        Logger.m172d("preloadUnsynced >>>");
        ArrayList<TLocal> unsyncedItems = null;
        try {
            unsyncedItems = this.localModifsStorage.getByStatus(1);
            addIfAbsentUnsafe(unsyncedItems);
        } catch (StorageException e) {
            Logger.m180e(e, "preloadUnsynced: failed to load unsynced items: %s", e);
        }
        String str = "preloadUnsynced <<< pre-loaded %d unsynced items in %d ms";
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(unsyncedItems == null ? 0 : unsyncedItems.size());
        objArr[1] = Long.valueOf(System.currentTimeMillis() - startTime);
        Logger.m173d(str, objArr);
    }

    void getUnsyncedItems(ArrayList<TLocal> outItems) {
        this.preloadUnsyncedItemsCondition.block();
        synchronized (this.lock) {
            Iterator i$ = this.unsyncedIds.iterator();
            while (i$.hasNext()) {
                String id = (String) i$.next();
                LocalModifs item = (LocalModifs) this.allLocalModifs.get(id);
                if (item != null) {
                    TLocal item2 = item.syncing();
                    this.allLocalModifs.put(id, item2);
                    outItems.add(item2);
                }
            }
        }
    }

    private long trimSize() {
        long trimTs;
        synchronized (this.lock) {
            long startTime = System.currentTimeMillis();
            Logger.m172d("trimSize >>>");
            trimTs = 0;
            try {
                int storageSize = this.localModifsStorage.getSize();
                Logger.m173d("trimSize: storageSize=%d", Integer.valueOf(storageSize));
                if (storageSize > this.storageConfig.maxStorageSize) {
                    trimTs = doTrim(storageSize, this.storageConfig.trimSize);
                }
                Logger.m173d("trimSize <<< trimmed storage in %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
            } catch (StorageException e) {
                Logger.m180e(e, "Failed to trim storage: %s", e);
                Logger.m173d("trimSize <<< trimmed storage in %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
            } catch (Throwable th) {
                Logger.m173d("trimSize <<< trimmed storage in %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
            }
        }
        return trimTs;
    }

    private long doTrim(int storageSize, int trimSize) throws StorageException {
        int deletedCount = 0;
        long trimSyncedTs = 0;
        TLocal trimItem = this.localModifsStorage.getBySyncedTime(trimSize);
        if (trimItem != null) {
            trimSyncedTs = trimItem.syncedTs;
            if (trimItem.syncStatus != 3) {
                TLocal mostRecentSynced = this.localModifsStorage.getMostRecentSynced();
                trimSyncedTs = mostRecentSynced != null ? mostRecentSynced.syncedTs : Long.MAX_VALUE;
            }
            deletedCount = 0 + this.localModifsStorage.deleteOlder(trimSyncedTs);
        }
        storageSize -= deletedCount;
        if (storageSize > trimSize) {
            deletedCount += this.localModifsStorage.delete(4, storageSize - trimSize);
        }
        Logger.m173d("deleted %d records", Integer.valueOf(deletedCount));
        return trimSyncedTs;
    }

    private void notifyStorageInitialized(long trimSyncedTs) {
        Logger.m173d("notifyStorageInitialized: %s", getClass().getSimpleName());
        if (this.initListener != null) {
            this.initListener.onInitializedLocalModifsStorage(trimSyncedTs);
        }
    }
}
