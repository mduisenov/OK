package ru.ok.android.db.music;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.base.BaseTable;

public final class PlayListTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER");
        columns.put("_index", "INTEGER PRIMARY KEY ON CONFLICT REPLACE");
    }

    public String getTableName() {
        return "playlist";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 79) {
            sqlCommands.add(DataBaseHelper.createBaseTableDeleteScript(this));
            sqlCommands.add(createBaseTableCreateScript());
            return;
        }
        super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
    }
}
