package ru.ok.android.db.users;

import android.database.sqlite.SQLiteDatabase;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.base.BaseTable;

public final class UserTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("user_id", "TEXT NOT NULL UNIQUE");
        columns.put("user_first_name", "TEXT");
        columns.put("user_last_name", "TEXT");
        columns.put("user_n_first_name", "TEXT");
        columns.put("user_n_last_name", "TEXT");
        columns.put("user_avatar_url", "TEXT");
        columns.put("user_gender", "INTEGER");
        columns.put("user_last_online", "INTEGER DEFAULT 0");
        columns.put("age", "INTEGER DEFAULT 0");
        columns.put("location_city", "TEXT");
        columns.put("location_country", "TEXT");
        columns.put("location_code", "TEXT");
        columns.put("user_name", "TEXT");
        columns.put("user_online", "TEXT");
        columns.put("user_can_call", "INTEGER");
        columns.put("can_vmail", "INTEGER");
        columns.put("photo_id", "TEXT");
        columns.put("big_pic_url", "TEXT");
        columns.put("private", "INTEGER DEFAULT 0");
        columns.put("premium", "INTEGER DEFAULT 0");
        columns.put("invisible", "INTEGER DEFAULT 0");
        columns.put("status_date", "INTEGER");
        columns.put("status_text", "TEXT");
        columns.put("status_id", "TEXT");
        columns.put("status_track_id", "INTEGER");
        columns.put("birthday", "TEXT");
        columns.put("is_all_info_available", "INTEGER DEFAULT 0");
        columns.put("show_lock", "INTEGER DEFAULT 0");
    }

    protected List<String> getIndexedColumnsNames() {
        return Collections.singletonList("user_id");
    }

    public String getTableName() {
        return "users";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlScripts, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlScripts, oldVersion, newVersion);
            if (oldVersion <= 51 && newVersion >= 53) {
                sqlScripts.add("ALTER TABLE users ADD COLUMN photo_id TEXT");
            }
            if (oldVersion < 61 && newVersion >= 61) {
                sqlScripts.add("ALTER TABLE users ADD COLUMN can_vmail TEXT");
            }
            if (oldVersion < 64 && newVersion >= 64) {
                sqlScripts.add("ALTER TABLE users ADD COLUMN is_all_info_available INTEGER DEFAULT 0");
            }
            if (oldVersion < 67 && newVersion >= 67) {
                sqlScripts.add("ALTER TABLE users ADD COLUMN show_lock INTEGER DEFAULT 0");
                return;
            }
            return;
        }
        sqlScripts.add(DataBaseHelper.createBaseTableDeleteScript(this));
        sqlScripts.add(createBaseTableCreateScript());
    }
}
