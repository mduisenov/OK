package ru.ok.android.db.music;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.base.BaseTable;

public class AlbumsTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY ON CONFLICT REPLACE");
        columns.put("name", "TEXT");
        columns.put("image_url", "TEXT");
        columns.put("ensemble", "TEXT");
    }

    public String getTableName() {
        return "albums";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (newVersion < 61 || oldVersion >= 61) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
            return;
        }
        sqlCommands.add(DataBaseHelper.createBaseTableDeleteScript(this));
        sqlCommands.add(createBaseTableCreateScript());
    }
}
