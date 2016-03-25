package ru.ok.android.db.access;

public class QueriesConversations {

    public static final class Delete {
        public static final String QUERY;

        static {
            QUERY = String.format("DELETE FROM %s WHERE %s = ?", new Object[]{"conversations", "server_id"});
        }
    }

    public static final class Insert {
        public static final String QUERY;

        static {
            QUERY = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)", new Object[]{"conversations", "data", "server_id"});
        }
    }

    public static final class LastUpdate {
        public static final String QUERY_CONVERSATION;

        static {
            QUERY_CONVERSATION = String.format("UPDATE %s SET %s = ?", new Object[]{"conversations", "_last_update"});
        }
    }

    public static final class List {
        public static final String QUERY;

        static {
            QUERY = String.format("SELECT %s FROM %s", new Object[]{"data", "conversations"});
        }
    }

    public static final class Single {
        public static final String QUERY;

        static {
            QUERY = String.format("SELECT %s FROM %s WHERE %s = ?", new Object[]{"data", "conversations", "server_id"});
        }
    }

    public static final class Update {
        public static final String QUERY;

        static {
            QUERY = String.format("UPDATE %s SET %s = ? WHERE %s = ?", new Object[]{"conversations", "data", "server_id"});
        }
    }
}
