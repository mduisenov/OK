package ru.ok.android.db;

import android.content.ContentValues;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class FriendsSuggestTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("suggestion", "TEXT NOT NULL UNIQUE");
    }

    public String getTableName() {
        return "friends_suggestion";
    }

    public static ContentValues fillValues(String suggest) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("suggestion", suggest);
        return contentValues;
    }
}
