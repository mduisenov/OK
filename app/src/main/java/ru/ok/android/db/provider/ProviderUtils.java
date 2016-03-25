package ru.ok.android.db.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public final class ProviderUtils {
    static Cursor queryGeneral(Context context, SQLiteDatabase db, String tableName, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, boolean distinct) {
        Cursor cursor = db.query(distinct, tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    static Cursor queryGeneral(Context context, SQLiteDatabase db, String tableName, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return queryGeneral(context, db, tableName, uri, projection, selection, selectionArgs, sortOrder, true);
    }

    static Cursor rawQueryGeneral(Context context, SQLiteDatabase db, Uri uri, String sql, String[] arguments) {
        Cursor cursor = db.rawQuery(sql, arguments);
        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    static boolean isRowExist(SQLiteDatabase db, String tableName, String id) {
        SQLiteDatabase sQLiteDatabase = db;
        String str = tableName;
        Cursor cursor = sQLiteDatabase.query(str, new String[]{"COUNT (*)"}, "_id = ?", new String[]{id}, null, null, null);
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

    static boolean isRowExist(SQLiteDatabase db, String tableName, String selection, String[] args) {
        Cursor cursor = db.query(tableName, new String[]{"COUNT (*)"}, selection, args, null, null, null);
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

    public static int update(SQLiteDatabase db, String table, ContentValues cv, String where, String[] whereArgs) {
        cv.put("_last_update", Long.valueOf(System.currentTimeMillis()));
        return db.update(table, cv, where, whereArgs);
    }

    public static long insert(SQLiteDatabase db, String table, ContentValues cv) {
        cv.put("_last_update", Long.valueOf(System.currentTimeMillis()));
        return db.insert(table, null, cv);
    }

    public static long insertOrThrow(SQLiteDatabase db, String table, ContentValues cv) {
        cv.put("_last_update", Long.valueOf(System.currentTimeMillis()));
        return db.insertOrThrow(table, null, cv);
    }
}
