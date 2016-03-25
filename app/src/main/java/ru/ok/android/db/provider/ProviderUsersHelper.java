package ru.ok.android.db.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import io.github.eterverda.sntp.SNTP;
import java.util.Locale;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.SQLiteUtils;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.filter.TranslateNormalizer;
import ru.ok.model.UserInfo.UserOnlineType;

final class ProviderUsersHelper {
    private static final String QUERY_USERS_JOIN_CONVERSATIONS_AND_FRIENDS;

    static Cursor queryFriends(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        StringBuilder sb = new StringBuilder("SELECT DISTINCT ");
        if (projection != null) {
            sb.append(TextUtils.join(", ", projection));
        } else {
            sb.append("*");
        }
        sb.append(" FROM users u INNER JOIN friends f ON f.friend_id = u.user_id");
        if (!TextUtils.isEmpty(selection)) {
            sb.append(" WHERE ").append(selection);
        }
        if (!TextUtils.isEmpty(sortOrder)) {
            sb.append(" ORDER BY ").append(sortOrder);
        }
        return ProviderUtils.rawQueryGeneral(context, db, uri, sb.toString(), selectionArgs);
    }

    static {
        QUERY_USERS_JOIN_CONVERSATIONS_AND_FRIENDS = "SELECT DISTINCT %s FROM users u INNER JOIN friends f ON f.friend_id = u.user_id WHERE (u.user_online = '" + UserOnlineType.MOBILE + "' " + "OR u." + "user_online" + " = '" + UserOnlineType.WEB + "')" + "AND u." + "user_id" + " <> '%s' " + "AND u." + "user_last_online" + " + " + 1200000 + " > %d " + "UNION " + "SELECT DISTINCT %s FROM " + "users" + " u " + "WHERE (u." + "user_online" + " = '" + UserOnlineType.MOBILE + "' " + "OR u." + "user_online" + " = '" + UserOnlineType.WEB + "') " + "AND u." + "user_id" + " IN (%s) " + "AND u." + "user_last_online" + " + " + 1200000 + " > %d " + "ORDER BY u." + "user_last_online" + " DESC";
    }

    static Cursor queryUsers(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, boolean joinConversations) {
        if (joinConversations) {
            StringBuilder sb = new StringBuilder();
            if (projection != null) {
                for (String column : projection) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append("u.").append(column).append(" as ").append(column);
                }
            } else {
                sb.append("u.*");
            }
            String projectionString = sb.toString();
            String uid = OdnoklassnikiApplication.getCurrentUser().uid;
            String uids = uri.getQueryParameter("uids");
            long time = SNTP.safeCurrentTimeMillisFromCache();
            return ProviderUtils.rawQueryGeneral(context, db, uri, String.format(Locale.US, QUERY_USERS_JOIN_CONVERSATIONS_AND_FRIENDS, new Object[]{projectionString, uid, Long.valueOf(time), projectionString, uids, Long.valueOf(time)}), null);
        }
        return ProviderUtils.queryGeneral(context, db, "users", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryFriend(Context context, SQLiteDatabase db, Uri uri, String[] projection) {
        return ProviderUtils.queryGeneral(context, db, "users JOIN friends ON friends.friend_id=users.user_id", uri, projection, "users.user_id=?", new String[]{uri.getLastPathSegment()}, null);
    }

    static Cursor queryMutualFriends(Context context, SQLiteDatabase db, Uri uri, String[] projection) {
        return ProviderUtils.queryGeneral(context, db, "users JOIN mutual_friends ON mutual_friends.friend_id=users.user_id", uri, projection, "mutual_friends.base_user_id=?", new String[]{uri.getLastPathSegment()}, "_id");
    }

    static Cursor queryUser(Context context, SQLiteDatabase db, Uri uri, String[] projection) {
        return ProviderUtils.queryGeneral(context, db, "users", uri, projection, "users.user_id=?", new String[]{uri.getLastPathSegment()}, null);
    }

    static Uri insertUser(SQLiteDatabase db, ContentValues cv) {
        String uid = cv.getAsString("user_id");
        if (uid == null) {
            Logger.m184w("Cannot insert user without uid!");
            return null;
        }
        if (!cv.containsKey("user_n_first_name")) {
            cv.put("user_n_first_name", TranslateNormalizer.normalizeText4Sorting(cv.getAsString("user_first_name")));
        }
        if (!cv.containsKey("user_n_last_name")) {
            cv.put("user_n_last_name", TranslateNormalizer.normalizeText4Sorting(cv.getAsString("user_last_name")));
        }
        SQLiteUtils.upsert(db, "users", cv, "user_id");
        return Users.getUri(uid);
    }

    static int insertFriends(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        int count = values.length;
        db.delete("friends", null, null);
        try {
            for (ContentValues contentValues : values) {
                ProviderUtils.insert(db, "friends", contentValues);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            count = 0;
        } finally {
            db.endTransaction();
        }
        return count;
    }

    static int insertMutualFriends(SQLiteDatabase db, ContentValues[] values, String uid) {
        SQLiteUtils.beginTransaction(db);
        int count = values.length;
        db.delete("mutual_friends", "base_user_id=?", new String[]{uid});
        try {
            for (ContentValues contentValues : values) {
                ProviderUtils.insert(db, "mutual_friends", contentValues);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            count = 0;
        } finally {
            db.endTransaction();
        }
        return count;
    }

    static int insertUsers(SQLiteDatabase db, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        int count = 0;
        try {
            for (ContentValues contentValues : values) {
                if (insertUser(db, contentValues) != null) {
                    count++;
                }
            }
            db.setTransactionSuccessful();
            return count;
        } catch (SQLException ex) {
            Logger.m177e("Failed to insert users", ex);
            return count;
        } finally {
            db.endTransaction();
        }
    }

    public static int deleteUsers(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("users", selection, selectionArgs);
    }

    public static int deleteFriends(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("friends", selection, selectionArgs);
    }

    public static int updateUser(SQLiteDatabase db, Uri uri, ContentValues cv) {
        return ProviderUtils.update(db, "users", cv, "users.user_id=?", new String[]{uri.getLastPathSegment()});
    }

    public static int updateFriends(SQLiteDatabase db, String selection, String[] selectionArgs, ContentValues cv) {
        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(selection)) {
            selection = "user_id IN (SELECT friend_id FROM friends)";
        } else {
            selection = "(user_id IN (SELECT friend_id FROM friends)) AND (" + selection + ")";
        }
        return ProviderUtils.update(db, "users", cv, selection, selectionArgs);
    }

    public static Uri insertUserRelation(SQLiteDatabase db, ContentValues cv) {
        String userId = cv.getAsString("user1");
        ProviderUtils.insert(db, "users_relations", cv);
        return OdklProvider.userRelationsUri(userId);
    }

    public static Uri insertUserRelationInfo(SQLiteDatabase db, ContentValues cv, Uri uri) {
        String userId = uri.getLastPathSegment();
        if (ProviderUtils.isRowExist(db, "user_relation_info", userId)) {
            ProviderUtils.update(db, "user_relation_info", cv, "_id = ?", new String[]{userId});
        } else {
            cv.put("_id", userId);
            ProviderUtils.insert(db, "user_relation_info", cv);
        }
        return OdklProvider.userRelationsUri(userId);
    }

    public static int deleteUserRelation(SQLiteDatabase db, Uri uri) {
        return db.delete("users_relations", "user1 = ?", new String[]{uri.getLastPathSegment()});
    }

    public static Cursor queryUserRelations(Context context, SQLiteDatabase db, Uri uri) {
        return ProviderUtils.queryGeneral(context, db, "users_relations", uri, null, "user1 = ?", new String[]{uri.getLastPathSegment()}, null);
    }

    public static Cursor queryUserRelationInfo(Context context, SQLiteDatabase db, Uri uri) {
        return ProviderUtils.queryGeneral(context, db, "user_relation_info", uri, null, "_id = ?", new String[]{uri.getLastPathSegment()}, null);
    }

    public static Cursor queryCounters(Context context, SQLiteDatabase db, Uri uri) {
        return ProviderUtils.queryGeneral(context, db, "users_counters", uri, null, "_id = ?", new String[]{uri.getLastPathSegment()}, null);
    }

    public static Uri insertCounters(SQLiteDatabase db, Uri uri, ContentValues cv) {
        String userId = uri.getLastPathSegment();
        if (ProviderUtils.isRowExist(db, "users_counters", userId)) {
            ProviderUtils.update(db, "users_counters", cv, "_id = ?", new String[]{userId});
        } else {
            cv.put("_id", userId);
            ProviderUtils.insert(db, "users_counters", cv);
        }
        return OdklProvider.userCountersUri(userId);
    }

    private static boolean isFriendExist(Context context, SQLiteDatabase db, Uri uri) {
        Cursor cursor = queryFriend(context, db, uri, null);
        try {
            boolean moveToFirst = cursor.moveToFirst();
            return moveToFirst;
        } finally {
            cursor.close();
        }
    }

    public static Uri insertFriend(Context context, SQLiteDatabase db, Uri uri, ContentValues cv) {
        if (isFriendExist(context, db, uri)) {
            return uri;
        }
        if (cv == null) {
            cv = new ContentValues();
        }
        cv.put("friend_id", uri.getLastPathSegment());
        if (ProviderUtils.insert(db, "friends", cv) < 0) {
            return OdklProvider.friendUri(null);
        }
        return uri;
    }

    public static int deleteFriend(Context context, SQLiteDatabase db, Uri uri) {
        if (!isFriendExist(context, db, uri)) {
            return 0;
        }
        return db.delete("friends", "friend_id = ?", new String[]{uri.getLastPathSegment()});
    }

    public static Cursor queryCommunities(Context context, SQLiteDatabase db, Uri uri, String orderBy) {
        return ProviderUtils.queryGeneral(context, db, "user_communities", uri, null, "user_id = ?", new String[]{uri.getLastPathSegment()}, orderBy);
    }

    public static Cursor queryInterests(Context context, SQLiteDatabase db, Uri uri, String orderBy) {
        return ProviderUtils.queryGeneral(context, db, "user_interests", uri, null, "user_id = ?", new String[]{uri.getLastPathSegment()}, orderBy);
    }

    public static Cursor queryPresents(Context context, SQLiteDatabase db, Uri uri, String orderBy) {
        return ProviderUtils.queryGeneral(context, db, "user_presents", uri, null, "USER_ID = ?", new String[]{uri.getLastPathSegment()}, orderBy);
    }

    public static int insertUserCommunities(SQLiteDatabase db, Uri uri, ContentValues[] values) {
        String userId = uri.getLastPathSegment();
        int count = 0;
        db.beginTransaction();
        db.delete("user_communities", "user_id = ?", new String[]{userId});
        try {
            for (ContentValues value : values) {
                value.put("user_id", userId);
                if (ProviderUtils.insert(db, "user_communities", value) > 0) {
                    count++;
                }
            }
            db.setTransactionSuccessful();
            return count;
        } finally {
            db.endTransaction();
        }
    }

    public static int insertUserInterests(SQLiteDatabase db, Uri uri, ContentValues[] values) {
        String userId = uri.getLastPathSegment();
        int count = 0;
        db.beginTransaction();
        db.delete("user_interests", "USER_ID = ?", new String[]{userId});
        try {
            for (ContentValues value : values) {
                value.put("USER_ID", userId);
                if (ProviderUtils.insert(db, "user_interests", value) > 0) {
                    count++;
                }
            }
            db.setTransactionSuccessful();
            return count;
        } finally {
            db.endTransaction();
        }
    }

    public static int insertUserPresents(SQLiteDatabase db, Uri uri, ContentValues[] values) {
        String userId = uri.getLastPathSegment();
        int count = 0;
        db.beginTransaction();
        db.delete("user_presents", "USER_ID = ?", new String[]{userId});
        try {
            for (ContentValues value : values) {
                value.put("USER_ID", userId);
                if (ProviderUtils.insert(db, "user_presents", value) > 0) {
                    count++;
                }
            }
            db.setTransactionSuccessful();
            return count;
        } finally {
            db.endTransaction();
        }
    }

    public static int updateUserRelationInfo(SQLiteDatabase db, Uri uri, ContentValues cv) {
        cv.put("_id", uri.getLastPathSegment());
        return SQLiteUtils.upsert(db, "user_relation_info", cv, "_id") != -1 ? 1 : 0;
    }

    static Cursor queryUsersStreamSubscribe(Context context, SQLiteDatabase db, Uri uri, String orderBy) {
        return ProviderUtils.queryGeneral(context, db, "users_subscribe", uri, null, "USER_ID = ?", new String[]{uri.getLastPathSegment()}, orderBy);
    }

    static Uri insertUsersStreamSubscribe(SQLiteDatabase db, Uri uri) {
        String uid = uri.getLastPathSegment();
        if (uid == null) {
            Logger.m184w("Cannot insert sbs_b user without uid!");
            return null;
        }
        if (!isSubscribeToUserStream(db, uid)) {
            ContentValues cv = new ContentValues();
            cv.put("USER_ID", uid);
            ProviderUtils.insert(db, "users_subscribe", cv);
        }
        return OdklProvider.userStreamSubscribeUri(uid);
    }

    static boolean isSubscribeToUserStream(SQLiteDatabase db, String userId) {
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query("users_subscribe", new String[]{"COUNT (*)"}, "USER_ID = ?", new String[]{userId}, null, null, null);
        try {
            boolean z;
            if (!cursor.moveToFirst() || cursor.getInt(0) <= 0) {
                z = false;
            } else {
                z = true;
            }
            cursor.close();
            return z;
        } catch (Throwable th) {
            cursor.close();
        }
    }
}
