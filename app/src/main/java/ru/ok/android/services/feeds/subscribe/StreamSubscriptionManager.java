package ru.ok.android.services.feeds.subscribe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import ru.ok.android.services.local.LocalModifsManager;
import ru.ok.android.services.local.LocalModifsStorageConfig;
import ru.ok.android.services.local.LocalModifsStorageInitListener;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.stream.StreamGroupSubscribeRequest;
import ru.ok.java.api.request.stream.StreamUnsubscribeRequest;
import ru.ok.java.api.request.stream.StreamUserSubscribeRequest;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.entities.BaseEntity;

public class StreamSubscriptionManager extends LocalModifsManager<LocalStreamSubscription> {
    private final ArrayList<WeakReference<StreamSubscriptionListener>> listeners;

    public interface StreamSubscriptionListener {
        void onStreamSubscription(int i, String str, boolean z);
    }

    public StreamSubscriptionManager(Context context, String currentUserId, LocalModifsStorageInitListener initListener) {
        super(context, currentUserId, new SqliteStreamSubscriptionsStorage(context, currentUserId), new LocalModifsStorageConfig(10, 5), initListener);
        this.listeners = new ArrayList();
    }

    protected LocalStreamSubscription performSyncRequest(LocalStreamSubscription unsyncedItem) throws BaseApiException {
        Logger.m173d(">>> unsyncedItem=%s", unsyncedItem);
        try {
            LocalStreamSubscription result;
            if (JsonSessionTransportProvider.getInstance().execJsonHttpMethod(createRequest(unsyncedItem)).getResultAsObject().optBoolean("success")) {
                result = unsyncedItem.synced(System.currentTimeMillis());
            } else {
                result = unsyncedItem.failedAttempt(5);
            }
            Logger.m173d("<<< result=%s", result);
            return result;
        } catch (JSONException e) {
            throw new ResultParsingException(e);
        }
    }

    public void preload(@Nullable ArrayList<String> userIds, @Nullable ArrayList<String> groupIds, boolean fromAPI, long loadTs) {
        ArrayList<String> ids = makeIds(userIds, groupIds);
        if (ids != null) {
            if (fromAPI) {
                deleteSyncedOlder(ids, loadTs);
            }
            preload(ids);
        }
    }

    @Nullable
    private static ArrayList<String> makeIds(@Nullable ArrayList<String> userIds, @Nullable ArrayList<String> groupIds) {
        int i;
        ArrayList<String> ids = null;
        if (userIds != null) {
            for (i = userIds.size() - 1; i >= 0; i--) {
                if (ids == null) {
                    ids = new ArrayList();
                }
                ids.add(LocalStreamSubscription.createId(1, (String) userIds.get(i)));
            }
        }
        if (groupIds != null) {
            for (i = groupIds.size() - 1; i >= 0; i--) {
                if (ids == null) {
                    ids = new ArrayList();
                }
                ids.add(LocalStreamSubscription.createId(2, (String) groupIds.get(i)));
            }
        }
        return ids;
    }

    public void registerListener(@NonNull StreamSubscriptionListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(new WeakReference(listener));
        }
    }

    public boolean isUnsubscribedFeedOwner(@NonNull Feed feed) {
        ArrayList<? extends BaseEntity> feedOwners = feed.getFeedOwners();
        for (int i = feedOwners.size() - 1; i >= 0; i--) {
            BaseEntity feedOwner = (BaseEntity) feedOwners.get(i);
            int type = feedOwner.getType();
            if (type == 2 && isGroupUnsubscribed(feedOwner.getId())) {
                return true;
            }
            if (type == 7 && isUserUnsubscribed(feedOwner.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserUnsubscribed(@NonNull String userId) {
        return isUnsubscribed(1, userId);
    }

    public boolean isGroupUnsubscribed(@NonNull String groupId) {
        return isUnsubscribed(2, groupId);
    }

    boolean isUnsubscribed(int ownerType, String ownerId) {
        LocalStreamSubscription subscription = (LocalStreamSubscription) getLocalModification(LocalStreamSubscription.createId(ownerType, ownerId));
        return (subscription == null || subscription.isSubscribed) ? false : true;
    }

    public void unsubscribeUser(@NonNull String userId, String logContext) {
        unsubscribe(1, userId, logContext);
    }

    public void unsubscribeGroup(@NonNull String groupId, String logContext) {
        unsubscribe(2, groupId, logContext);
    }

    public void setSubscribedGroup(@NonNull String groupId, boolean isSubscribed) {
        updateLocalModification(new LocalStreamSubscription(isSubscribed, 2, groupId, null).synced(System.currentTimeMillis()));
        notifySubscription(2, groupId, isSubscribed);
    }

    public void setSubscribedUser(@NonNull String userId, boolean isSubscribed) {
        updateLocalModification(new LocalStreamSubscription(isSubscribed, 1, userId, null).synced(System.currentTimeMillis()));
        notifySubscription(1, userId, isSubscribed);
    }

    void unsubscribe(int ownerType, String ownerId, String logContext) {
        LocalStreamSubscription subscription = new LocalStreamSubscription(false, ownerType, ownerId, logContext);
        Logger.m173d("ownerType=%d ownerId=%s logContext=%s", Integer.valueOf(ownerType), ownerId, logContext);
        updateLocalModification(subscription);
        notifySubscription(ownerType, ownerId, false);
    }

    protected void notifySubscription(int ownerType, String ownerId, boolean isSubscribed) {
        synchronized (this.listeners) {
            for (int i = this.listeners.size() - 1; i >= 0; i--) {
                StreamSubscriptionListener l = (StreamSubscriptionListener) ((WeakReference) this.listeners.get(i)).get();
                if (l == null) {
                    this.listeners.remove(i);
                } else {
                    l.onStreamSubscription(ownerType, ownerId, isSubscribed);
                }
            }
        }
    }

    private BaseRequest createRequest(LocalStreamSubscription unsyncedItem) {
        if (unsyncedItem.isSubscribed) {
            List<String> ids = Collections.singletonList(unsyncedItem.ownerId);
            if (unsyncedItem.ownerType == 1) {
                return new StreamUserSubscribeRequest(ids);
            }
            return new StreamGroupSubscribeRequest(ids);
        }
        List<String> fids;
        List<String> gids;
        if (unsyncedItem.ownerType == 1) {
            fids = Collections.singletonList(unsyncedItem.ownerId);
            gids = null;
        } else {
            fids = null;
            gids = Collections.singletonList(unsyncedItem.ownerId);
        }
        return new StreamUnsubscribeRequest(fids, gids, unsyncedItem.logContext);
    }
}
