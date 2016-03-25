package ru.ok.android.services.mediatopic_polls;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.HashSet;
import ru.ok.model.local.LocalModifs;

public class LocalMtPollVotes extends LocalModifs {
    @NonNull
    public final HashSet<String> allVotes;
    public final String logContext;
    @Nullable
    public final HashSet<String> notAddedVotes;
    @Nullable
    public final HashSet<String> notRemovedVotes;

    public LocalMtPollVotes(String pollId, @NonNull HashSet<String> allVotes, @Nullable HashSet<String> addedVotes, @Nullable HashSet<String> removedVotes, String logContext) {
        super(pollId, 1, 0, 0);
        this.allVotes = allVotes;
        this.notAddedVotes = addedVotes;
        this.notRemovedVotes = removedVotes;
        this.logContext = logContext;
    }

    LocalMtPollVotes(String pollId, @NonNull HashSet<String> allVotes, @Nullable HashSet<String> addedVotes, @Nullable HashSet<String> removedVotes, String logContext, int state, int failedAttempts, long syncedTs) {
        super(pollId, state, failedAttempts, syncedTs);
        this.allVotes = allVotes;
        this.notAddedVotes = addedVotes;
        this.notRemovedVotes = removedVotes;
        this.logContext = logContext;
    }

    public LocalMtPollVotes syncing() {
        return new LocalMtPollVotes(this.id, this.allVotes, this.notAddedVotes, this.notRemovedVotes, this.logContext, 2, this.failedAttemptsCount, 0);
    }

    public LocalMtPollVotes failedAttempt(int maxAttemptCount) {
        int attempts = this.failedAttemptsCount + 1;
        return new LocalMtPollVotes(this.id, this.allVotes, this.notAddedVotes, this.notRemovedVotes, this.logContext, attempts >= maxAttemptCount ? 4 : 1, attempts, 0);
    }

    public String toString() {
        return "LocalMtPollVotes[id=" + this.id + " state=" + LocalModifs.statusToString(this.syncStatus) + " attempts=" + this.failedAttemptsCount + " syncedTs=" + this.syncedTs + " allVotes=" + this.allVotes + " notAddedVotes=" + this.notAddedVotes + " notRemovedVotes=" + this.notRemovedVotes + " logContext=" + this.logContext + "]";
    }
}
