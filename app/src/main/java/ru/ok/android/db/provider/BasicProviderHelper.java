package ru.ok.android.db.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import ru.ok.android.db.SQLiteUtils;

class BasicProviderHelper {
    final ContentResolver contentResolver;
    final Uri contentUri;
    final String table;

    BasicProviderHelper(ContentResolver contentResolver, String table, Uri contentUri) {
        this.contentResolver = contentResolver;
        this.table = table;
        this.contentUri = contentUri;
    }

    Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String rowId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(this.table);
        if (rowId != null) {
            qb.appendWhere("_id");
            qb.appendWhere("=");
            qb.appendWhere(rowId);
        }
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(this.contentResolver, uri);
        }
        return cursor;
    }

    Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        long rowId = ProviderUtils.insert(db, this.table, values);
        if (rowId > 0) {
            return ContentUris.withAppendedId(this.contentUri, rowId);
        }
        return null;
    }

    int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs, String rowId) {
        if (rowId != null) {
            if (selection != null) {
                selection = "(_id=" + rowId + ") AND (" + selection + ")";
            } else {
                selection = "_id=" + rowId;
            }
        }
        return db.delete(this.table, selection, selectionArgs);
    }

    int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs, String rowId) {
        if (rowId != null) {
            if (selection != null) {
                selection = "(_id=" + rowId + ") AND (" + selection + ")";
            } else {
                selection = "_id=" + rowId;
            }
        }
        return ProviderUtils.update(db, this.table, values, selection, selectionArgs);
    }

    int bulkInsert(SQLiteDatabase db, Uri uri, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        int insertedCount = 0;
        try {
            for (ContentValues val : values) {
                if (insert(db, uri, val) != null) {
                    insertedCount++;
                }
            }
            db.setTransactionSuccessful();
            return insertedCount;
        } finally {
            db.endTransaction();
        }
    }
}
