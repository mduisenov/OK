package ru.ok.android.db.group;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.base.BaseTable;

public class GroupInfoTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("g_id", "TEXT NOT NULL UNIQUE");
        columns.put("g_name", "TEXT");
        columns.put("g_descr", "TEXT");
        columns.put("g_avatar_url", "TEXT");
        columns.put("g_mmbr_cnt", "INTEGER");
        columns.put("g_flags", "INTEGER NOT NULL DEFAULT 0");
        columns.put("g_photo_id", "TEXT");
        columns.put("g_big_photo_url", "TEXT");
        columns.put("g_category", "INTEGER");
        columns.put("g_admin_uid", "TEXT");
        columns.put("g_created", "INTEGER");
        columns.put("g_city", "TEXT");
        columns.put("g_address", "TEXT");
        columns.put("g_lat", "REAL");
        columns.put("g_lng", "REAL");
        columns.put("g_scope", "TEXT");
        columns.put("g_start_date", "INTEGER");
        columns.put("g_end_date", "INTEGER");
        columns.put("g_home_page_url", "TEXT");
        columns.put("g_phone", "TEXT");
        columns.put("g_business", "INTEGER DEFAULT 0");
        columns.put("g_subcategory_id", "TEXT");
        columns.put("g_subcategory_name", "TEXT");
        columns.put("is_all_info_available", "INTEGER DEFAULT 0");
        columns.put("g_status", "TEXT");
    }

    public String getTableName() {
        return "group_info";
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
            if (oldVersion < 64 && newVersion >= 64) {
                sqlCommands.add("ALTER TABLE group_info ADD COLUMN is_all_info_available INTEGER DEFAULT 0");
            }
            if (oldVersion < 80 && newVersion >= 80) {
                sqlCommands.add("ALTER TABLE group_info ADD COLUMN g_status TEXT");
                return;
            }
            return;
        }
        sqlCommands.add(DataBaseHelper.createBaseTableDeleteScript(this));
        sqlCommands.add(createBaseTableCreateScript());
    }
}
