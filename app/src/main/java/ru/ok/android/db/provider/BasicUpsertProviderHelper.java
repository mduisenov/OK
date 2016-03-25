package ru.ok.android.db.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import ru.ok.android.db.SQLiteUtils;

public class BasicUpsertProviderHelper extends BasicProviderHelper {
    final String[] keyColumns;

    BasicUpsertProviderHelper(ContentResolver contentResolver, String table, Uri contentUri, String... keyColumns) {
        super(contentResolver, table, contentUri);
        this.keyColumns = keyColumns;
    }

    Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        values.put("_last_update", Long.valueOf(System.currentTimeMillis()));
        long rowId = SQLiteUtils.upsert(db, this.table, values, this.keyColumns);
        if (rowId > 0) {
            return ContentUris.withAppendedId(this.contentUri, rowId);
        }
        return null;
    }
}
