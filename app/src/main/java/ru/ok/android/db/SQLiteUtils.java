package ru.ok.android.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import ru.ok.android.db.provider.ProviderUtils;
import ru.ok.android.utils.IOUtils;

public final class SQLiteUtils {
    private static final String[] PROJECTION_ID;

    public static Set<String> queryTableNames(SQLiteDatabase db) {
        Set<String> tables = new HashSet();
        try {
            Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            if (cursor == null || !cursor.moveToFirst()) {
                IOUtils.closeSilently(cursor);
                return tables;
            }
            do {
                tables.add(cursor.getString(0));
            } while (cursor.moveToNext());
            IOUtils.closeSilently(cursor);
            return tables;
        } catch (Throwable th) {
            IOUtils.closeSilently(null);
        }
    }

    public static Set<String> queryTableColumns(SQLiteDatabase db, String tableName) {
        Cursor tableInfo = null;
        Set<String> columns = new HashSet();
        try {
            tableInfo = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            if (tableInfo == null || !tableInfo.moveToFirst()) {
                throw new SQLiteException("Missing table " + tableName);
            }
            while (true) {
                columns.add(tableInfo.getString(1));
                if (!tableInfo.moveToNext()) {
                    break;
                }
            }
            return columns;
        } finally {
            IOUtils.closeSilently(tableInfo);
        }
    }

    public static long upsert(SQLiteDatabase db, String table, ContentValues values, String keyColumn) {
        return doUpsert(db, table, values, keyColumn + "=?", new String[]{values.getAsString(keyColumn)});
    }

    public static long upsert(SQLiteDatabase db, String table, ContentValues values, String[] keyColumns) {
        StringBuilder sb = new StringBuilder();
        String[] selectionArgs = new String[keyColumns.length];
        for (int i = 0; i < keyColumns.length; i++) {
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            String col = keyColumns[i];
            sb.append(col).append("=?");
            selectionArgs[i] = values.getAsString(col);
        }
        return doUpsert(db, table, values, sb.toString(), selectionArgs);
    }

    private static long doUpsert(SQLiteDatabase db, String table, ContentValues values, String selection, String[] selectionArgs) {
        if (ProviderUtils.update(db, table, values, selection, selectionArgs) > 0) {
            return getRowId(db, table, selection, selectionArgs);
        }
        return ProviderUtils.insert(db, table, values);
    }

    private static long getRowId(SQLiteDatabase db, String table, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            cursor = db.query(table, PROJECTION_ID, selection, selectionArgs, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                IOUtils.closeSilently(cursor);
                return -1;
            }
            long j = cursor.getLong(0);
            return j;
        } catch (Exception e) {
            return -1;
        } finally {
            IOUtils.closeSilently(cursor);
        }
    }

    static {
        PROJECTION_ID = new String[]{"_id"};
    }

    public static void safeBindString(@NonNull SQLiteStatement statement, int index, @Nullable String value) {
        if (value != null) {
            statement.bindString(index, value);
        } else {
            statement.bindNull(index);
        }
    }

    @SuppressLint({"NewApi"})
    public static void beginTransaction(@NonNull SQLiteDatabase db) {
        if (VERSION.SDK_INT < 16 || db.isWriteAheadLoggingEnabled()) {
            db.beginTransactionNonExclusive();
        } else {
            db.beginTransaction();
        }
    }
}
