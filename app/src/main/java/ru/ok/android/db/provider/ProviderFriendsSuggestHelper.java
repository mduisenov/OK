package ru.ok.android.db.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ProviderFriendsSuggestHelper {
    public static Cursor query(Context ctx, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(ctx, db, "friends_suggestion", uri, projection, selection, selectionArgs, sortOrder);
    }

    public static Uri insert(SQLiteDatabase db, ContentValues cv) {
        try {
            ProviderUtils.insertOrThrow(db, "friends_suggestion", cv);
        } catch (Exception e) {
        }
        return OdklProvider.friendsSuggest();
    }

    public static int delete(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("friends_suggestion", selection, selectionArgs);
    }

    public static int bulkInsert(SQLiteDatabase db, ContentValues[] values) {
        db.beginTransaction();
        try {
            for (ContentValues contentValues : values) {
                insert(db, contentValues);
            }
            db.setTransactionSuccessful();
            return values.length;
        } catch (SQLException e) {
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    public static int update(SQLiteDatabase db, ContentValues cv, String selection, String[] selectionArgs) {
        return ProviderUtils.update(db, "friends_suggestion", cv, selection, selectionArgs);
    }
}
