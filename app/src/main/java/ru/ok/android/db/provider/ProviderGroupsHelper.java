package ru.ok.android.db.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import ru.ok.android.db.SQLiteUtils;
import ru.ok.android.db.provider.OdklContract.Groups;
import ru.ok.android.utils.Logger;

final class ProviderGroupsHelper {
    static int insertGroupUserStatus(android.database.sqlite.SQLiteDatabase r6, android.content.ContentValues[] r7) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:? in {4, 10, 12, 14, 15, 18, 19} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r6.beginTransaction();
        r0 = r7;
        r4 = r0.length;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        r3 = 0;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
    L_0x0006:
        if (r3 >= r4) goto L_0x0010;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
    L_0x0008:
        r1 = r0[r3];	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        insertGroupUserStatus(r6, r1);	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        r3 = r3 + 1;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        goto L_0x0006;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
    L_0x0010:
        r6.setTransactionSuccessful();	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        r6.endTransaction();
        r5 = r7.length;
    L_0x0017:
        return r5;
    L_0x0018:
        r2 = move-exception;
        ru.ok.android.utils.Logger.m178e(r2);	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        r5 = 0;
        r6.endTransaction();
        goto L_0x0017;
    L_0x0021:
        r5 = move-exception;
        r6.endTransaction();
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.provider.ProviderGroupsHelper.insertGroupUserStatus(android.database.sqlite.SQLiteDatabase, android.content.ContentValues[]):int");
    }

    static int insertGroups(android.database.sqlite.SQLiteDatabase r6, android.content.ContentValues[] r7) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:? in {4, 10, 12, 14, 15, 18, 19} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        ru.ok.android.db.SQLiteUtils.beginTransaction(r6);
        r0 = r7;
        r4 = r0.length;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        r3 = 0;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
    L_0x0006:
        if (r3 >= r4) goto L_0x0010;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
    L_0x0008:
        r1 = r0[r3];	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        insertGroup(r6, r1);	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        r3 = r3 + 1;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        goto L_0x0006;	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
    L_0x0010:
        r6.setTransactionSuccessful();	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        r6.endTransaction();
        r5 = r7.length;
    L_0x0017:
        return r5;
    L_0x0018:
        r2 = move-exception;
        ru.ok.android.utils.Logger.m178e(r2);	 Catch:{ SQLException -> 0x0018, all -> 0x0021 }
        r5 = 0;
        r6.endTransaction();
        goto L_0x0017;
    L_0x0021:
        r5 = move-exception;
        r6.endTransaction();
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.provider.ProviderGroupsHelper.insertGroups(android.database.sqlite.SQLiteDatabase, android.content.ContentValues[]):int");
    }

    static Uri insertGroup(SQLiteDatabase db, ContentValues cv) {
        if (SQLiteUtils.upsert(db, "group_info", cv, "g_id") >= 0) {
            return Groups.getUri(cv.getAsString("g_id"));
        }
        return null;
    }

    static Uri insertGroupUserStatus(SQLiteDatabase db, ContentValues cv) {
        String userId = cv.getAsString("user_id");
        String groupId = cv.getAsString("group_id");
        String status = cv.getAsString(NotificationCompat.CATEGORY_STATUS);
        String selection = "user_id = ? and group_id = ?";
        String[] selectionArgs = new String[]{userId, groupId};
        if (ProviderUtils.isRowExist(db, "group_user_status", selection, selectionArgs)) {
            ProviderUtils.update(db, "group_user_status", cv, selection, selectionArgs);
        } else {
            ProviderUtils.insert(db, "group_user_status", cv);
        }
        return OdklProvider.groupUserStatusUri(userId, groupId);
    }

    static int updateGroupUserStatus(SQLiteDatabase db, ContentValues cv) {
        String userId = cv.getAsString("user_id");
        String groupId = cv.getAsString("group_id");
        return ProviderUtils.update(db, "group_user_status", cv, "user_id = ? and group_id = ?", new String[]{userId, groupId});
    }

    static Cursor queryGroup(Context context, SQLiteDatabase db, Uri uri, String[] projection) {
        return ProviderUtils.queryGeneral(context, db, "group_info", uri, projection, "g_id=?", new String[]{uri.getLastPathSegment()}, null);
    }

    static Cursor queryGroups(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "group_info", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Cursor queryGroupsUsersStatus(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "group_user_status", uri, projection, selection, selectionArgs, sortOrder);
    }

    static int deleteGroups(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("group_info", selection, selectionArgs);
    }

    public static Cursor queryCounters(Context context, SQLiteDatabase db, Uri uri) {
        return ProviderUtils.queryGeneral(context, db, "groups_counters", uri, null, "_id = ?", new String[]{uri.getLastPathSegment()}, null);
    }

    public static Uri insertCounters(SQLiteDatabase db, Uri uri, ContentValues cv) {
        String groupId = uri.getLastPathSegment();
        if (ProviderUtils.isRowExist(db, "groups_counters", groupId)) {
            ProviderUtils.update(db, "groups_counters", cv, "_id = ?", new String[]{groupId});
        } else {
            cv.put("_id", groupId);
            ProviderUtils.insert(db, "groups_counters", cv);
        }
        return OdklProvider.groupCountersUri(groupId);
    }

    static Cursor queryGroupsStreamSubscribe(Context context, SQLiteDatabase db, Uri uri) {
        String groupId = uri.getLastPathSegment();
        return db.query("groups_subscribe", new String[]{"COUNT (*)"}, "GROUP_ID = ?", new String[]{groupId}, null, null, null);
    }

    static Uri insertGroupsStreamSubscribe(SQLiteDatabase db, Uri uri) {
        String gid = uri.getLastPathSegment();
        if (gid == null) {
            Logger.m184w("Cannot insert group sb-s without gid!");
            return null;
        }
        if (!isSubscribeToGroupStream(db, gid)) {
            ContentValues cv = new ContentValues();
            cv.put("GROUP_ID", gid);
            ProviderUtils.insert(db, "groups_subscribe", cv);
        }
        return OdklProvider.groupStreamSubscribeUri(gid);
    }

    static boolean isSubscribeToGroupStream(SQLiteDatabase db, String groupId) {
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query("groups_subscribe", new String[]{"COUNT (*)"}, "GROUP_ID = ?", new String[]{groupId}, null, null, null);
        try {
            boolean moveToFirst = cursor.moveToFirst();
            return moveToFirst;
        } finally {
            cursor.close();
        }
    }

    public static int deleteGroupStreamSubscribe(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("groups_subscribe", selection, selectionArgs);
    }
}
