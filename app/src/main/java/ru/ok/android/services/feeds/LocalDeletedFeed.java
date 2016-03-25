package ru.ok.android.services.feeds;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.model.local.LocalModifs;

public class LocalDeletedFeed extends LocalModifs {
    @Nullable
    public final String logContext;
    @Nullable
    public final String spamId;

    LocalDeletedFeed(@NonNull String id, @Nullable String logContext, @Nullable String spamId) {
        this(id, logContext, spamId, 1, 0, 0);
    }

    public LocalDeletedFeed(@NonNull String id, @Nullable String logContext, @Nullable String spamId, int syncStatus, int failedAttemptsCount, long syncedTs) {
        super(id, syncStatus, failedAttemptsCount, syncedTs);
        this.logContext = logContext;
        this.spamId = spamId;
    }

    public LocalDeletedFeed syncing() {
        return new LocalDeletedFeed(this.id, this.logContext, this.spamId, 2, this.failedAttemptsCount, this.syncedTs);
    }

    public LocalDeletedFeed failedAttempt(int maxAttemptCount) {
        int attemptCount = this.failedAttemptsCount + 1;
        return new LocalDeletedFeed(this.id, this.logContext, this.spamId, attemptCount >= maxAttemptCount ? 4 : 1, attemptCount, 0);
    }

    public LocalDeletedFeed synced(long syncedTs) {
        return new LocalDeletedFeed(this.id, this.logContext, this.spamId, 3, this.failedAttemptsCount, syncedTs);
    }

    public String toString() {
        return "LocalDeletedFeed[deleteId=" + this.id + " logContext=" + this.logContext + " spamId=" + this.spamId + " syncStatus=" + LocalModifs.statusToString(this.syncStatus) + " failedAttemptsCount=" + this.failedAttemptsCount + " syncedTs=" + this.syncedTs + "]";
    }
}
