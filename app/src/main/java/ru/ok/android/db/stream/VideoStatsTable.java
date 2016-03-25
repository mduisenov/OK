package ru.ok.android.db.stream;

import android.database.sqlite.SQLiteDatabase;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class VideoStatsTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("vstat_banner_id", "TEXT NOT NULL REFERENCES banners(banner_id) ON DELETE CASCADE");
        columns.put("vstat_type", "INTEGER NOT NULL");
        columns.put("vstat_url", "TEXT NOT NULL");
        columns.put("vstat_param", "TEXT");
    }

    protected List<String> getIndexedColumnsNames() {
        return Collections.singletonList("vstat_banner_id");
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 64) {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }

    public String getTableName() {
        return "video_stats";
    }
}
