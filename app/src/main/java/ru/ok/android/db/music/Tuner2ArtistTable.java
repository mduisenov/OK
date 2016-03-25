package ru.ok.android.db.music;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class Tuner2ArtistTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("artist_id", "INTEGER");
        columns.put("tuner_data", "TEXT");
    }

    public String getTableName() {
        return "tuner2artist";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 54 && newVersion >= 54) {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }
}
