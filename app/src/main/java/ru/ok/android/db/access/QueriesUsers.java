package ru.ok.android.db.access;

import java.util.Locale;
import ru.ok.model.UserInfo.UserOnlineType;

public final class QueriesUsers {

    public static final class BestFriends {
        public static final String QUERY_RESET;
        public static final String QUERY_SET;

        static {
            QUERY_RESET = String.format("UPDATE %s SET %s = 0", new Object[]{"friends", "is_best_friend"});
            QUERY_SET = String.format("UPDATE %s SET %s = 1, %s = ? WHERE %s = ?", new Object[]{"friends", "is_best_friend", "best_friend_index", "friend_id"});
        }
    }

    public static final class FriendInsert {
        public static final String QUERY;

        static {
            QUERY = String.format("INSERT INTO %s (%s) VALUES (?)", new Object[]{"friends", "friend_id"});
        }
    }

    public static final class FriendsDelete {
        public static final String QUERY;

        static {
            QUERY = String.format("DELETE FROM %s", new Object[]{"friends"});
        }
    }

    public static final class FriendsLastUpdate {
        public static final String QUERY_FRIENDS;
        public static final String QUERY_USERS;

        static {
            QUERY_USERS = String.format("UPDATE %s SET %s = ? WHERE %s IN (SELECT %s FROM %s)", new Object[]{"users", "_last_update", "user_id", "friend_id", "friends"});
            QUERY_FRIENDS = String.format("UPDATE %s SET %s = ?", new Object[]{"friends", "_last_update"});
        }
    }

    public static final class FriendsList {
        public static final String QUERY;

        static {
            QUERY = String.format("SELECT u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, f.%s, f.%s, r.%s, r.%s FROM %s u INNER JOIN %s f ON f.%s = u.%s LEFT JOIN %s r ON r.%s = u.%s", new Object[]{"user_id", "user_name", "user_first_name", "user_last_name", "user_avatar_url", "user_gender", "user_online", "user_last_online", "user_can_call", "can_vmail", "private", "show_lock", "is_best_friend", "best_friend_index", "type", "subtype", "users", "friends", "friend_id", "user_id", "relatives", "uid", "user_id"});
        }
    }

    public static final class NameAvatarGender {
        public static int INDEX_GENDER;
        public static int INDEX_NAME;
        public static int INDEX_PIC_URL;
        public static final String QUERY;

        static {
            QUERY = String.format("SELECT %s, %s, %s FROM %s WHERE %s = ?", new Object[]{"user_name", "user_avatar_url", "user_gender", "users", "user_id"});
            INDEX_NAME = 0;
            INDEX_PIC_URL = 1;
            INDEX_GENDER = 2;
        }
    }

    public static final class OnlineList {
        public static final String QUERY;

        static {
            QUERY = String.format(Locale.US, "SELECT u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, r.%s FROM %s u INNER JOIN %s f ON f.%s = u.%s LEFT JOIN %s r ON r.%s = u.%s WHERE u.%s NOT NULL AND u.%s <> '%s' AND CAST((u.%s + %d) as INTEGER) > ? ORDER BY u.%s DESC", new Object[]{"user_id", "user_name", "user_first_name", "user_last_name", "user_avatar_url", "user_last_online", "user_online", "user_gender", "show_lock", "type", "users", "friends", "friend_id", "user_id", "relatives", "uid", "user_id", "user_online", "user_online", UserOnlineType.OFFLINE, "user_last_online", Long.valueOf(1200000), "user_last_online"});
        }
    }

    public static final class QueryById {
        public static final String QUERY;

        static {
            QUERY = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?", new Object[]{"user_first_name", "user_last_name", "user_name", "user_avatar_url", "big_pic_url", "user_online", "users", "user_id"});
        }
    }

    public static final class QueryByIds {
        public static final String QUERY;

        static {
            QUERY = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s IN ('%%s')", new Object[]{"user_first_name", "user_last_name", "user_name", "user_avatar_url", "big_pic_url", "user_online", "user_id", "users", "user_id"});
        }
    }

    public static final class QueryFriend {
        public static final String QUERY;

        static {
            QUERY = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", new Object[]{"friends", "friend_id"});
        }
    }

    public static final class RelationsDelete {
        public static final String QUERY;

        static {
            QUERY = String.format("DELETE FROM %s WHERE %s  = ?", new Object[]{"relatives", "uid"});
        }
    }

    public static final class RelationsInsert {
        public static final String QUERY;

        static {
            QUERY = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", new Object[]{"relatives", "uid", "type", "subtype"});
        }
    }

    public static final class Update4Conversations {
        public static final String QUERY_INSERT;
        public static final String QUERY_UPDATE;

        static {
            QUERY_UPDATE = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", new Object[]{"users", "user_first_name", "user_last_name", "user_name", "user_gender", "user_avatar_url", "user_can_call", "can_vmail", "user_online", "user_n_first_name", "user_n_last_name", "show_lock", "private", "user_last_online", "user_id"});
            QUERY_INSERT = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{"users", "user_first_name", "user_last_name", "user_name", "user_gender", "user_avatar_url", "user_can_call", "can_vmail", "user_online", "user_n_first_name", "user_n_last_name", "show_lock", "private", "user_last_online", "user_id"});
        }
    }

    public static final class Update4Messages {
        public static final String QUERY_INSERT;
        public static final String QUERY_UPDATE;

        static {
            QUERY_UPDATE = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", new Object[]{"users", "user_first_name", "user_last_name", "user_name", "user_last_online", "user_online", "user_avatar_url", "user_gender", "user_n_first_name", "user_n_last_name", "user_id"});
            QUERY_INSERT = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{"users", "user_first_name", "user_last_name", "user_name", "user_last_online", "user_online", "user_n_first_name", "user_n_last_name", "user_avatar_url", "user_gender", "user_id"});
        }
    }

    public static final class UpdateOnline {
        public static final String QUERY_INSERT;
        public static final String QUERY_RESET;
        public static final String QUERY_UPDATE;

        static {
            QUERY_UPDATE = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", new Object[]{"users", "user_can_call", "can_vmail", "user_online", "user_last_online", "user_id"});
            QUERY_INSERT = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?)", new Object[]{"users", "user_can_call", "can_vmail", "user_online", "user_last_online", "user_id", "user_avatar_url", "user_name"});
            QUERY_RESET = String.format("UPDATE %s SET %s = NULL WHERE %s IN (SELECT %s FROM %s)", new Object[]{"users", "user_online", "user_id", "friend_id", "friends"});
        }
    }
}
