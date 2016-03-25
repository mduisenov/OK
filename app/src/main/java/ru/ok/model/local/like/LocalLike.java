package ru.ok.model.local.like;

import ru.ok.model.local.LocalModifs;

public final class LocalLike extends LocalModifs {
    public final boolean self;

    public LocalLike(String likeId, boolean self) {
        this(likeId, self, 1, 0, 0);
    }

    public LocalLike(String likeId, boolean self, int syncStatus, int failedAttemptsCount) {
        this(likeId, self, syncStatus, failedAttemptsCount, 0);
    }

    public LocalLike(String likeId, boolean self, int syncStatus, int failedAttemptsCount, long syncedTs) {
        super(likeId, syncStatus, failedAttemptsCount, syncedTs);
        this.self = self;
    }

    public String toString() {
        return "LocalLike[likeId=" + this.id + " self=" + this.self + " syncStatus=" + LocalModifs.statusToString(this.syncStatus) + " failedAttemptsCount=" + this.failedAttemptsCount + " syncedTs=" + this.syncedTs + "]";
    }

    public LocalLike syncing() {
        return new LocalLike(this.id, this.self, 2, this.failedAttemptsCount, 0);
    }

    public LocalLike failedAttempt(int maxAttemptCount) {
        int attemptCount = this.failedAttemptsCount + 1;
        return new LocalLike(this.id, this.self, attemptCount >= maxAttemptCount ? 4 : 1, attemptCount);
    }

    public LocalLike synced(long syncedTs) {
        return new LocalLike(this.id, this.self, 3, this.failedAttemptsCount, syncedTs);
    }
}
