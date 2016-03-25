package ru.ok.android.db.music;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.base.BaseTable;

public final class TracksTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY ON CONFLICT REPLACE");
        columns.put("name", "TEXT");
        columns.put("ensemble", "TEXT");
        columns.put("image_url", "TEXT");
        columns.put("full_name", "TEXT");
        columns.put("album", "INTEGER");
        columns.put("artist", "INTEGER");
        columns.put("explicit", "INTEGER");
        columns.put("duration", "INTEGER");
    }

    public String getTableName() {
        return "tracks";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (newVersion < 61 || oldVersion >= 61) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
            if (newVersion >= 67 && oldVersion < 67) {
                sqlCommands.add("ALTER TABLE tracks ADD COLUMN image_url TEXT");
            }
            if (newVersion >= 75 && oldVersion < 75) {
                sqlCommands.add("ALTER TABLE tracks ADD COLUMN full_name TEXT");
                return;
            }
            return;
        }
        sqlCommands.add(DataBaseHelper.createBaseTableDeleteScript(this));
        sqlCommands.add(createBaseTableCreateScript());
    }
}
