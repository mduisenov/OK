package ru.ok.android.services.presents;

import android.support.annotation.NonNull;
import ru.ok.model.local.LocalModifs;

public class LocalDeletedPresent extends LocalModifs {
    public LocalDeletedPresent(@NonNull String presentId) {
        this(presentId, 1, 0, 0);
    }

    public LocalDeletedPresent(@NonNull String presentId, int syncStatus, int failedAttemptsCount, long syncedTs) {
        super(presentId, syncStatus, failedAttemptsCount, syncedTs);
    }

    public LocalDeletedPresent syncing() {
        return new LocalDeletedPresent(this.id, 2, this.failedAttemptsCount, this.syncedTs);
    }

    public LocalDeletedPresent failedAttempt(int maxAttemptCount) {
        int attemptCount = this.failedAttemptsCount + 1;
        return new LocalDeletedPresent(this.id, attemptCount >= maxAttemptCount ? 4 : 1, attemptCount, this.syncedTs);
    }

    public LocalDeletedPresent synced(long syncedTs) {
        return new LocalDeletedPresent(this.id, 3, this.failedAttemptsCount, syncedTs);
    }
}
