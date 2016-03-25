package ru.ok.android.services.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import ru.ok.android.services.local.LocalModifsManager;
import ru.ok.android.services.local.LocalModifsStorageConfig;
import ru.ok.android.services.local.LocalModifsStorageInitListener;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.JsonBooleanResultParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.stream.StreamDeleteRequest;
import ru.ok.java.api.request.stream.StreamMarkAsSpamRequest;

public class DeletedFeedsManager extends LocalModifsManager<LocalDeletedFeed> {
    private final ArrayList<WeakReference<DeleteFeedListener>> listeners;

    public interface DeleteFeedListener {
        void onFeedDeleted(String str);
    }

    public DeletedFeedsManager(Context context, String currentUserId, LocalModifsStorageInitListener initListener) {
        super(context, currentUserId, new SqliteDeletedFeedsStorage(context, currentUserId), new LocalModifsStorageConfig(20, 10), initListener);
        this.listeners = new ArrayList();
    }

    public void registerListener(@NonNull DeleteFeedListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(new WeakReference(listener));
        }
    }

    public boolean isFeedDeleted(@NonNull String deleteId) {
        return ((LocalDeletedFeed) getLocalModification(deleteId)) != null;
    }

    public void deleteFeed(@NonNull String deleteId, @Nullable String logContext, @Nullable String spamId) {
        Logger.m173d("deletedId=%s deletedFeed=%s spamId=%s", deleteId, new LocalDeletedFeed(deleteId, spamId, logContext), spamId);
        updateLocalModification(deletedFeed);
        notifyFeedDeleted(deleteId);
    }

    protected void notifyFeedDeleted(String deleteId) {
        synchronized (this.listeners) {
            for (int i = this.listeners.size() - 1; i >= 0; i--) {
                DeleteFeedListener l = (DeleteFeedListener) ((WeakReference) this.listeners.get(i)).get();
                if (l == null) {
                    this.listeners.remove(i);
                } else {
                    l.onFeedDeleted(deleteId);
                }
            }
        }
    }

    protected LocalDeletedFeed performSyncRequest(LocalDeletedFeed unsyncedItem) throws BaseApiException {
        BaseRequest request;
        Logger.m173d(">>> unsyncedItem=%s", unsyncedItem);
        if (unsyncedItem.spamId == null) {
            request = new StreamDeleteRequest(unsyncedItem.id, unsyncedItem.logContext);
        } else {
            request = new StreamMarkAsSpamRequest(unsyncedItem.spamId, unsyncedItem.id, unsyncedItem.logContext);
        }
        Logger.m173d("<<< result=%s", ((Boolean) new JsonBooleanResultParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request)).parse()).booleanValue() ? unsyncedItem.synced(System.currentTimeMillis()) : unsyncedItem.failedAttempt(5));
        return ((Boolean) new JsonBooleanResultParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request)).parse()).booleanValue() ? unsyncedItem.synced(System.currentTimeMillis()) : unsyncedItem.failedAttempt(5);
    }
}
