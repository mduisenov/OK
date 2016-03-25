package ru.ok.android.db.stream;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class BannersTable extends BaseTable {
    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
            if (oldVersion < 61 && newVersion >= 61) {
                sqlCommands.add("ALTER TABLE banners ADD COLUMN banner_disclaimer TEXT");
                sqlCommands.add("ALTER TABLE banners ADD COLUMN banner_info TEXT");
            }
            if (oldVersion < 65 && newVersion >= 65) {
                sqlCommands.add("ALTER TABLE banners ADD COLUMN banner_votes INTEGER");
                sqlCommands.add("ALTER TABLE banners ADD COLUMN banner_users INTEGER");
                sqlCommands.add("ALTER TABLE banners ADD COLUMN banner_rating REAL");
            }
            if (oldVersion < 77 && newVersion >= 77) {
                sqlCommands.add("ALTER TABLE banners ADD COLUMN banner_age_restriction TEXT");
            }
            if (oldVersion < 79 && newVersion >= 79) {
                sqlCommands.add("ALTER TABLE banners ADD COLUMN banner_deep_link TEXT");
                return;
            }
            return;
        }
        sqlCommands.add(createBaseTableCreateScript());
    }

    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("banner_id", "TEXT NOT NULL UNIQUE");
        columns.put("banner_template", "INTEGER");
        columns.put("banner_header", "TEXT");
        columns.put("banner_text", "TEXT");
        columns.put("banner_action_type", "INTEGER");
        columns.put("banner_icon_type", "INTEGER");
        columns.put("banner_icon_url", "TEXT");
        columns.put("banner_icon_url_hd", "TEXT");
        columns.put("banner_click_url", "TEXT");
        columns.put("banner_color", "INTEGER");
        columns.put("banner_disclaimer", "TEXT");
        columns.put("banner_info", "TEXT");
        columns.put("banner_votes", "INTEGER");
        columns.put("banner_users", "INTEGER");
        columns.put("banner_rating", "REAL");
        columns.put("banner_age_restriction", "TEXT");
        columns.put("banner_deep_link", "TEXT");
    }

    public String getTableName() {
        return "banners";
    }
}
