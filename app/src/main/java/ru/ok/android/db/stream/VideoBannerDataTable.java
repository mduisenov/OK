package ru.ok.android.db.stream;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class VideoBannerDataTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("vbd_banner_id", "TEXT NOT NULL REFERENCES banners(banner_id) ON DELETE CASCADE");
        columns.put("vbd_video_url", "TEXT NOT NULL");
        columns.put("vbd_duration_sec", "INTEGER");
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 64) {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }

    public String getTableName() {
        return "video_banner_data";
    }
}
