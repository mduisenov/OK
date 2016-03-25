package ru.ok.android.db.stream;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class PromoLinksTable extends BaseTable {
    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        } else {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }

    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("pmlk_type", "INTEGER NOT NULL");
        columns.put("pmlk_id", "TEXT");
        columns.put("pmlk_banner_id", "TEXT NOT NULL REFERENCES banners(banner_id) ON DELETE CASCADE");
    }

    protected String getTableConstraint() {
        return "UNIQUE (pmlk_type,pmlk_id) ON CONFLICT REPLACE";
    }

    public String getTableName() {
        return "promo_links";
    }
}
