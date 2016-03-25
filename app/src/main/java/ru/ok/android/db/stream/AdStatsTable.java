package ru.ok.android.db.stream;

import android.database.sqlite.SQLiteDatabase;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class AdStatsTable extends BaseTable {
    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        } else {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }

    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("adst_type", "INTEGER NOT NULL");
        columns.put("adst_url", "TEXT NOT NULL");
        columns.put("adst_trgt_type", "INTEGER NOT NULL");
        columns.put("adst_trgt_id", "INTEGER NOT NULL");
    }

    public List<String> createIndexesCreateScript() {
        return Collections.singletonList("CREATE INDEX idx_ad_stats ON ad_stats(adst_trgt_type,adst_trgt_id)");
    }

    public void getOnAfterCreateStatements(List<String> outStmnts) {
        outStmnts.add("CREATE TRIGGER trigger_ad_stats_pmlks AFTER DELETE ON promo_links BEGIN DELETE FROM ad_stats WHERE adst_trgt_type=2 AND adst_trgt_id=OLD._id; END");
    }

    public void getOnAfterUpgradeStatements(List<String> outStmnts, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.getOnAfterUpgradeStatements(outStmnts, oldVersion, newVersion);
        } else {
            getOnAfterCreateStatements(outStmnts);
        }
        if (oldVersion < 78 && newVersion >= 78) {
            outStmnts.add("UPDATE ad_stats SET adst_type=adst_type-1 WHERE adst_type NOT NULL");
        }
    }

    public String getTableName() {
        return "ad_stats";
    }
}
