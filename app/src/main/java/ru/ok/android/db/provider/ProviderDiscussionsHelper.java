package ru.ok.android.db.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.db.SQLiteUtils;
import ru.ok.android.utils.Logger;

final class ProviderDiscussionsHelper {
    static Cursor queryDiscussionComment(Context context, SQLiteDatabase db, Uri uri, String[] projection) {
        return ProviderUtils.queryGeneral(context, db, "discussions_comments", uri, projection, "_id = ?", new String[]{uri.getLastPathSegment()}, null);
    }

    static Cursor queryDiscussionComments(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return ProviderUtils.queryGeneral(context, db, "discussions_comments", uri, projection, selection, selectionArgs, sortOrder);
    }

    static Uri insertComment(Context context, SQLiteDatabase db, Uri uri, ContentValues cv, boolean isNotify) {
        long id;
        int existingId = getCommentIdByServerId(db, cv);
        if (existingId < 0) {
            id = ProviderUtils.insert(db, "discussions_comments", cv);
            if (id <= 0) {
                return null;
            }
        }
        id = (long) existingId;
        ProviderUtils.update(db, "discussions_comments", cv, "_id = ?", new String[]{String.valueOf(existingId)});
        if (isNotify) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return OdklProvider.commentUri(id);
    }

    static int insertComments(Context context, SQLiteDatabase db, Uri uri, ContentValues[] values) {
        SQLiteUtils.beginTransaction(db);
        int count = 0;
        try {
            for (ContentValues value : values) {
                if (insertComment(context, db, uri, value, false) != null) {
                    count++;
                }
            }
            db.setTransactionSuccessful();
            return count;
        } catch (Throwable e) {
            Logger.m178e(e);
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    private static int getCommentIdByServerId(SQLiteDatabase db, ContentValues cv) {
        if (!cv.containsKey("server_id")) {
            return -1;
        }
        if (TextUtils.isEmpty(cv.getAsString("server_id"))) {
            return -1;
        }
        SQLiteDatabase sQLiteDatabase = db;
        Cursor c = sQLiteDatabase.query("discussions_comments", new String[]{"_id"}, "server_id = ?", new String[]{cv.getAsString("server_id")}, null, null, null);
        try {
            if (c.moveToFirst()) {
                int i = c.getInt(c.getColumnIndex("_id"));
                return i;
            }
            c.close();
            return -1;
        } finally {
            c.close();
        }
    }

    static int deleteDiscussionComments(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete("discussions_comments", selection, selectionArgs);
    }

    static int deleteDiscussionComment(SQLiteDatabase db, Uri uri) {
        return db.delete("discussions_comments", "_id = ?", new String[]{uri.getLastPathSegment()});
    }

    static int updateComment(SQLiteDatabase db, Uri uri, ContentValues cv) {
        return ProviderUtils.update(db, "discussions_comments", cv, "_id = ?", new String[]{uri.getLastPathSegment()});
    }

    static int updateComments(SQLiteDatabase db, ContentValues cv, String selection, String[] selectionArgs) {
        return ProviderUtils.update(db, "discussions_comments", cv, selection, selectionArgs);
    }
}
