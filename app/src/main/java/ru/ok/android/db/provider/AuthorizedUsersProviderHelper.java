package ru.ok.android.db.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.db.provider.OdklContract.AuthorizedUsers;

public final class AuthorizedUsersProviderHelper extends BasicUpsertProviderHelper {
    private static final String[] PROJECTION_ROW_ID;

    AuthorizedUsersProviderHelper(ContentResolver contentResolver) {
        super(contentResolver, "authorized_users", AuthorizedUsers.getContentUri(), "uid");
    }

    Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        if (!values.containsKey("timestamp")) {
            values.put("timestamp", Long.valueOf(System.currentTimeMillis()));
        }
        Uri rowUri = super.insert(db, uri, values);
        if (rowUri != null) {
            deleteRedundant(db);
        }
        return rowUri;
    }

    int bulkInsert(SQLiteDatabase db, Uri uri, ContentValues[] values) {
        long now = System.currentTimeMillis();
        for (ContentValues v : values) {
            if (!v.containsKey("timestamp")) {
                v.put("timestamp", Long.valueOf(now));
            }
        }
        int insertedRowCount = super.bulkInsert(db, uri, values);
        if (insertedRowCount > 0) {
            deleteRedundant(db);
        }
        return insertedRowCount;
    }

    int deleteByUid(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs, String uid) {
        return delete(db, uri, addSelectUid(selection, uid), selectionArgs, null);
    }

    int updateByUid(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs, String uid) {
        return update(db, uri, values, addSelectUid(selection, uid), selectionArgs, null);
    }

    Cursor queryByUid(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String uid) {
        return query(db, uri, projection, addSelectUid(selection, uid), selectionArgs, sortOrder, null);
    }

    private static String addSelectUid(String selection, String uid) {
        if (TextUtils.isEmpty(uid)) {
            return selection;
        }
        StringBuilder sb = new StringBuilder();
        if (selection != null) {
            sb.append('(').append(selection).append(") AND ");
        }
        sb.append("uid").append("='").append(uid).append('\'');
        return sb.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void deleteRedundant(android.database.sqlite.SQLiteDatabase r14) {
        /*
        r13 = this;
        r3 = 0;
        r1 = r13.table;
        r2 = PROJECTION_ROW_ID;
        r7 = "timestamp ASC";
        r0 = r14;
        r4 = r3;
        r5 = r3;
        r6 = r3;
        r9 = r0.query(r1, r2, r3, r4, r5, r6, r7);
        if (r9 == 0) goto L_0x0051;
    L_0x0012:
        r11 = r9.getCount();	 Catch:{ Exception -> 0x004a }
        r0 = 0;
        r1 = r11 + -9;
        r12 = java.lang.Math.max(r0, r1);	 Catch:{ Exception -> 0x004a }
        if (r12 <= 0) goto L_0x0052;
    L_0x001f:
        r0 = r9.moveToFirst();	 Catch:{ Exception -> 0x004a }
        if (r0 == 0) goto L_0x0052;
    L_0x0025:
        r0 = 1;
        r8 = new java.lang.String[r0];	 Catch:{ Exception -> 0x004a }
    L_0x0028:
        r0 = r9.isAfterLast();	 Catch:{ Exception -> 0x004a }
        if (r0 != 0) goto L_0x0052;
    L_0x002e:
        if (r12 <= 0) goto L_0x0052;
    L_0x0030:
        r0 = 0;
        r1 = 0;
        r2 = r9.getLong(r1);	 Catch:{ Exception -> 0x004a }
        r1 = java.lang.Long.toString(r2);	 Catch:{ Exception -> 0x004a }
        r8[r0] = r1;	 Catch:{ Exception -> 0x004a }
        r0 = r13.table;	 Catch:{ Exception -> 0x004a }
        r1 = "_id=?";
        r14.delete(r0, r1, r8);	 Catch:{ Exception -> 0x004a }
        r12 = r12 + -1;
        r9.moveToNext();	 Catch:{ Exception -> 0x004a }
        goto L_0x0028;
    L_0x004a:
        r10 = move-exception;
        ru.ok.android.utils.Logger.m178e(r10);	 Catch:{ all -> 0x0056 }
        ru.ok.android.utils.IOUtils.closeSilently(r9);
    L_0x0051:
        return;
    L_0x0052:
        ru.ok.android.utils.IOUtils.closeSilently(r9);
        goto L_0x0051;
    L_0x0056:
        r0 = move-exception;
        ru.ok.android.utils.IOUtils.closeSilently(r9);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.provider.AuthorizedUsersProviderHelper.deleteRedundant(android.database.sqlite.SQLiteDatabase):void");
    }

    static {
        PROJECTION_ROW_ID = new String[]{"_id"};
    }
}
