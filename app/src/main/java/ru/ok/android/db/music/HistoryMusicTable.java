package ru.ok.android.db.music;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class HistoryMusicTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("time", "INTEGER");
        columns.put("track_id", "INTEGER");
    }

    public String getTableName() {
        return "music_history";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 54 && newVersion >= 54) {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }
}
