package ru.ok.android.db.messages;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class UserPrivacySettingsTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("uid", "TEXT NOT NULL");
        columns.put("privacy_setting_id", "INTEGER NOT NULL");
        columns.put("privacy_mode", "INTEGER NOT NULL");
    }

    public String getTableName() {
        return "user_privacy_settings";
    }

    protected String getTableConstraint() {
        return "UNIQUE (privacy_setting_id,uid) ON CONFLICT REPLACE";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 51 && newVersion >= 51) {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }
}
