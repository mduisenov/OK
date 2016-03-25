package ru.ok.android.db.base;

import java.util.Arrays;
import java.util.List;

public final class OfflineTable {

    public enum Status {
        RECEIVED(-1),
        WAITING(2130838125),
        WAITING_ATTACHMENT(2130838125),
        UPLOADING_ATTACHMENTS(2130838124),
        LOCKED(2130838125),
        SENDING(2130838124),
        SENT(2130838106),
        FAILED(2130838123),
        SERVER_ERROR(2130838121),
        OVERDUE(2130838123);
        
        public static List<Status> AUTO_RESEND_POSSIBLE;
        public static List<Status> CANT_BECOME_OVERDUE;
        public static List<Status> DELETE_ALLOWED;
        public static List<Status> RESEND_POSSIBLE;
        private final int iconResourceId;

        static {
            CANT_BECOME_OVERDUE = Arrays.asList(new Status[]{SENT, SENDING, LOCKED, RECEIVED, SERVER_ERROR, OVERDUE});
            RESEND_POSSIBLE = Arrays.asList(new Status[]{FAILED, OVERDUE, WAITING, SERVER_ERROR});
            AUTO_RESEND_POSSIBLE = Arrays.asList(new Status[]{WAITING, FAILED});
            DELETE_ALLOWED = Arrays.asList(new Status[]{WAITING, RECEIVED, SENT, FAILED, SERVER_ERROR, OVERDUE, UPLOADING_ATTACHMENTS, WAITING_ATTACHMENT});
        }

        private Status(int iconResourceId) {
            this.iconResourceId = iconResourceId;
        }

        public int getIconResourceId() {
            return this.iconResourceId;
        }
    }
}
