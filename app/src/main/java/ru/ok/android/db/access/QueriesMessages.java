package ru.ok.android.db.access;

import android.support.v4.app.NotificationCompat;
import java.util.Locale;
import ru.ok.android.proto.MessagesProto.Message.Status;

public final class QueriesMessages {

    public static final class CountServerId {
        public static final String QUERY;

        static {
            QUERY = String.format("SELECT COUNT (*) FROM %s WHERE %s = ?", new Object[]{"messages", "server_id"});
        }
    }

    public static final class Insert {
        public static final String QUERY;

        static {
            QUERY = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)", new Object[]{"messages", "conversation_id", "_date", "data", NotificationCompat.CATEGORY_STATUS});
        }
    }

    public static final class QueryToSend {
        public static final String QUERY;

        static {
            QUERY = String.format(Locale.US, "SELECT %s FROM %s WHERE %s IN (%d, %d, %d) OR %s IN (%d, %d, %d) ORDER BY %s", new Object[]{"_id", "messages", NotificationCompat.CATEGORY_STATUS, Integer.valueOf(Status.WAITING.getNumber()), Integer.valueOf(Status.FAILED.getNumber()), Integer.valueOf(Status.SENDING.getNumber()), "status_editing", Integer.valueOf(Status.WAITING.getNumber()), Integer.valueOf(Status.FAILED.getNumber()), Integer.valueOf(Status.SENDING.getNumber()), "_date"});
        }
    }
}
