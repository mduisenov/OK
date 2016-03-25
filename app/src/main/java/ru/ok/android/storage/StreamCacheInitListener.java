package ru.ok.android.storage;

import android.content.Context;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.local.LocalModifsStorageInitListener;
import ru.ok.android.utils.Logger;

final class StreamCacheInitListener {
    private final StreamCacheInitCondition condition;
    private final Context context;
    final LocalModifsStorageInitListener deletedFeedsInitListner;
    final LocalModifsStorageInitListener likeInitListener;
    final LocalModifsStorageInitListener pollsInitListener;
    private final Storages storages;
    final LocalModifsStorageInitListener subscriptionInitListener;

    /* renamed from: ru.ok.android.storage.StreamCacheInitListener.1 */
    class C05291 implements LocalModifsStorageInitListener {
        C05291() {
        }

        public void onInitializedLocalModifsStorage(long trimTs) {
            Logger.m172d("");
            try {
                StreamCacheInitListener.this.onSyncStorageTrimmed(trimTs);
            } finally {
                StreamCacheInitListener.this.condition.onLikeStorageInitialized();
            }
        }
    }

    /* renamed from: ru.ok.android.storage.StreamCacheInitListener.2 */
    class C05302 implements LocalModifsStorageInitListener {
        C05302() {
        }

        public void onInitializedLocalModifsStorage(long trimTs) {
            Logger.m172d("");
            try {
                StreamCacheInitListener.this.onSyncStorageTrimmed(trimTs);
            } finally {
                StreamCacheInitListener.this.condition.onDeletedFeedsStorageInitialized();
            }
        }
    }

    /* renamed from: ru.ok.android.storage.StreamCacheInitListener.3 */
    class C05313 implements LocalModifsStorageInitListener {
        C05313() {
        }

        public void onInitializedLocalModifsStorage(long trimTs) {
            Logger.m172d("");
            try {
                StreamCacheInitListener.this.onSyncStorageTrimmed(trimTs);
            } finally {
                StreamCacheInitListener.this.condition.onSubscriptionStorageInitialized();
            }
        }
    }

    /* renamed from: ru.ok.android.storage.StreamCacheInitListener.4 */
    class C05324 implements LocalModifsStorageInitListener {
        C05324() {
        }

        public void onInitializedLocalModifsStorage(long trimTs) {
            Logger.m172d("");
            try {
                StreamCacheInitListener.this.onSyncStorageTrimmed(trimTs);
            } finally {
                StreamCacheInitListener.this.condition.onMtPollsStorageInitialized();
            }
        }
    }

    public StreamCacheInitListener(Context context, StreamCacheInitCondition condition, Storages storages) {
        this.likeInitListener = new C05291();
        this.deletedFeedsInitListner = new C05302();
        this.subscriptionInitListener = new C05313();
        this.pollsInitListener = new C05324();
        this.context = context.getApplicationContext();
        this.condition = condition;
        this.storages = storages;
    }

    private void onSyncStorageTrimmed(long trimmedSyncTs) {
        Logger.m173d("onSyncStorageTrimmed: trimmedSyncTs=%d", Long.valueOf(trimmedSyncTs));
        if (trimmedSyncTs <= 0) {
            return;
        }
        if (TextUtils.isEmpty(OdnoklassnikiApplication.getCurrentUser().getId())) {
            Logger.m176e("currentUserId is empty");
            return;
        }
        try {
            this.storages.getStreamCache().trim(trimmedSyncTs);
        } catch (StorageException e) {
            Logger.m180e(e, "Failed to trim stream cache: %s", e);
        }
        try {
            this.storages.getUnreadStreamCache().trim(trimmedSyncTs);
        } catch (StorageException e2) {
            Logger.m180e(e2, "Failed to trim stream cache: %s", e2);
        }
        try {
            this.storages.getFeedBannerStatsStorage().removeOldRecords(trimmedSyncTs);
        } catch (StorageException e22) {
            Logger.m180e(e22, "Failed to trim feed banner stats storage: %s", e22);
        }
    }
}
