package ru.ok.android.db.access;

import android.support.v4.app.NotificationCompat;

public final class QueriesComments {

    public static final class UpdateStatusAndDate {
        public static final String QUERY;

        static {
            QUERY = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ?", new Object[]{"discussions_comments", NotificationCompat.CATEGORY_STATUS, "_date", "_id"});
        }
    }
}
