package ru.ok.android.db.music;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class TunersTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("name", "TEXT");
        columns.put("data", "TEXT NOT NULL UNIQUE");
    }

    public String getTableName() {
        return "tuners";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 54 && newVersion >= 54) {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }
}
