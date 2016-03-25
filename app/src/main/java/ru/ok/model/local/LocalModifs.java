package ru.ok.model.local;

import ru.ok.android.proto.MessagesProto.Message;

public abstract class LocalModifs {
    public final int failedAttemptsCount;
    public final String id;
    public final int syncStatus;
    public final long syncedTs;

    public abstract <T extends LocalModifs> T failedAttempt(int i);

    public abstract <T extends LocalModifs> T syncing();

    protected LocalModifs(String id, int syncStatus, int failedAttemptsCount, long syncedTs) {
        this.id = id;
        this.syncStatus = syncStatus;
        this.failedAttemptsCount = failedAttemptsCount;
        this.syncedTs = syncedTs;
    }

    public static String statusToString(int status) {
        switch (status) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "DIRTY";
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return "SYNCING";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "SYNCED";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "FAILED";
            default:
                return "UNKNOWN(" + status + ")";
        }
    }
}
