package ru.ok.android.db.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import ru.ok.android.db.provider.OdklContract.GroupMembers;

public final class ProviderGroupMembersHelper extends BasicUpsertProviderHelper {
    ProviderGroupMembersHelper(ContentResolver contentResolver) {
        super(contentResolver, "group_members", GroupMembers.getContentUri(), "_id");
    }

    Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String rowId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("users JOIN group_members ON group_members.gm_user_id=users.user_id");
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
}
