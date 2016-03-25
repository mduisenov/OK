package ru.ok.android.services.feeds.subscribe;

import android.support.annotation.Nullable;
import ru.ok.model.local.LocalModifs;

public class LocalStreamSubscription extends LocalModifs {
    public final boolean isSubscribed;
    @Nullable
    public final String logContext;
    public final String ownerId;
    public final int ownerType;

    static boolean isValidEntityType(int type) {
        return type == 2 || type == 1;
    }

    static final String createId(int ownerType, String ownerId) {
        return ownerType + "-" + ownerId;
    }

    LocalStreamSubscription(boolean isSubscribed, int ownerType, String ownerId, String logContext) {
        this(createId(ownerType, ownerId), 1, 0, 0, isSubscribed, ownerType, ownerId, logContext);
    }

    LocalStreamSubscription(String id, int syncStatus, int failedAttemptsCount, long syncedTs, boolean isSubscribed, int ownerType, String ownerId, String logContext) {
        super(id, syncStatus, failedAttemptsCount, syncedTs);
        this.isSubscribed = isSubscribed;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.logContext = logContext;
    }

    public LocalStreamSubscription syncing() {
        return new LocalStreamSubscription(this.id, 2, this.failedAttemptsCount, 0, this.isSubscribed, this.ownerType, this.ownerId, this.logContext);
    }

    public LocalStreamSubscription failedAttempt(int maxAttemptCount) {
        int attemptCount = this.failedAttemptsCount + 1;
        return new LocalStreamSubscription(this.id, attemptCount >= maxAttemptCount ? 4 : 1, attemptCount, 0, this.isSubscribed, this.ownerType, this.ownerId, this.logContext);
    }

    public LocalStreamSubscription synced(long syncedTs) {
        return new LocalStreamSubscription(this.id, 3, this.failedAttemptsCount, syncedTs, this.isSubscribed, this.ownerType, this.ownerId, this.logContext);
    }

    public String toString() {
        return "LocalStreamSubscription[id=" + this.id + " status=" + LocalModifs.statusToString(this.syncStatus) + " attempts=" + this.failedAttemptsCount + " syncedTs=" + this.syncedTs + " isSubscribed=" + this.isSubscribed + " ownerType=" + this.ownerType + " ownerId=" + this.ownerId + " logContext=" + this.logContext + "]";
    }
}
