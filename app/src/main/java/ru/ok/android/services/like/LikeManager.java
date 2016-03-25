package ru.ok.android.services.like;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import ru.ok.android.services.local.LocalModifsManager;
import ru.ok.android.services.local.LocalModifsStorageConfig;
import ru.ok.android.services.local.LocalModifsStorageInitListener;
import ru.ok.android.services.processors.general.LikeProcessor;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.model.local.like.LocalLike;
import ru.ok.model.stream.LikeInfo;
import ru.ok.model.stream.LikeInfo.Builder;
import ru.ok.model.stream.LikeInfoContext;

public class LikeManager extends LocalModifsManager<LocalLike> {
    private final ArrayList<WeakReference<LikeListener>> listeners;

    public interface LikeListener {
        void onLikeChanged(String str);
    }

    public LikeManager(Context context, String currentUserId, LocalModifsStorageInitListener initListener) {
        super(context, currentUserId, new SqliteLikeStorage(context, currentUserId), new LocalModifsStorageConfig(20, 10), initListener);
        this.listeners = new ArrayList();
    }

    public void registerListener(@NonNull LikeListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(new WeakReference(listener));
        }
    }

    public void unregisterListener(@NonNull LikeListener listener) {
        synchronized (this.listeners) {
            for (int i = this.listeners.size() - 1; i >= 0; i--) {
                LikeListener l = (LikeListener) ((WeakReference) this.listeners.get(i)).get();
                if (l == null) {
                    this.listeners.remove(i);
                } else if (l == listener) {
                    this.listeners.remove(i);
                    break;
                }
            }
        }
    }

    protected LocalLike performSyncRequest(LocalLike unsyncedLike) throws BaseApiException {
        LocalLike result;
        Logger.m173d("performSyncRequest >>> %s", unsyncedLike);
        Logger.m173d("performSyncRequest: result from API: %s", LikeProcessor.performLikeRequest(unsyncedLike.id, unsyncedLike.self, null));
        if (LikeProcessor.performLikeRequest(unsyncedLike.id, unsyncedLike.self, null).self != unsyncedLike.self) {
            result = unsyncedLike.failedAttempt(5);
        } else {
            result = unsyncedLike.synced(System.currentTimeMillis());
        }
        Logger.m173d("performSyncRequest <<< %s", result);
        return result;
    }

    public LikeInfoContext like(LikeInfoContext likeInfo) {
        LikeInfo updatedLikeInfo = performLikeOperation(likeInfo, true);
        return updatedLikeInfo == likeInfo ? likeInfo : new LikeInfoContext(updatedLikeInfo, likeInfo.entityType, likeInfo.entityId);
    }

    public LikeInfoContext unlike(LikeInfoContext likeInfo) {
        LikeInfo updatedLikeInfo = performLikeOperation(likeInfo, false);
        return updatedLikeInfo == likeInfo ? likeInfo : new LikeInfoContext(updatedLikeInfo, likeInfo.entityType, likeInfo.entityId);
    }

    public LikeInfoContext toggle(LikeInfoContext likeInfo) {
        LikeInfo updatedLikeInfo = performLikeOperation(likeInfo, !likeInfo.self);
        return updatedLikeInfo == likeInfo ? likeInfo : new LikeInfoContext(updatedLikeInfo, likeInfo.entityType, likeInfo.entityId);
    }

    private LikeInfo performLikeOperation(LikeInfo likeInfo, boolean targetSelf) {
        String likeId = likeInfo == null ? null : likeInfo.likeId;
        if (TextUtils.isEmpty(likeId) || likeInfo.self == targetSelf) {
            return likeInfo;
        }
        if (targetSelf && !likeInfo.likePossible) {
            return likeInfo;
        }
        if (!targetSelf && !likeInfo.unlikePossible) {
            return likeInfo;
        }
        LocalLike localLike = (LocalLike) getLocalModification(likeId);
        if (localLike == null || localLike.self != targetSelf) {
            updateLocalModification(new LocalLike(likeId, targetSelf));
        }
        Builder newLike = new Builder(likeInfo);
        newLike.setSelf(targetSelf);
        if (targetSelf) {
            newLike.incrementCount();
        } else {
            newLike.decrementCount();
        }
        notifyLikeChanged(likeId);
        return newLike.build();
    }

    public LikeInfo getLikeInfo(LikeInfo likeInfo) {
        String likeId = likeInfo == null ? null : likeInfo.likeId;
        if (TextUtils.isEmpty(likeId)) {
            return likeInfo;
        }
        LocalLike localLike = (LocalLike) getLocalModification(likeId);
        if (!(localLike == null || localLike.self == likeInfo.self)) {
            Builder newLike = new Builder(likeInfo);
            newLike.setSelf(localLike.self);
            if (localLike.self) {
                newLike.incrementCount();
            } else {
                newLike.decrementCount();
            }
            likeInfo = newLike.build();
        }
        return likeInfo;
    }

    public LikeInfoContext getLikeInfo(LikeInfoContext likeInfo) {
        LikeInfo updatedLikeInfo = getLikeInfo((LikeInfo) likeInfo);
        return updatedLikeInfo == likeInfo ? likeInfo : new LikeInfoContext(updatedLikeInfo, likeInfo.entityType, likeInfo.entityId);
    }

    protected void notifyLikeChanged(String likeId) {
        synchronized (this.listeners) {
            for (int i = this.listeners.size() - 1; i >= 0; i--) {
                LikeListener l = (LikeListener) ((WeakReference) this.listeners.get(i)).get();
                if (l == null) {
                    this.listeners.remove(i);
                } else {
                    l.onLikeChanged(likeId);
                }
            }
        }
    }
}
