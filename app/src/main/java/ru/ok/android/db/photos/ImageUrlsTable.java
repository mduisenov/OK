package ru.ok.android.db.photos;

import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class ImageUrlsTable extends BaseTable {
    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.fillUpgradeScript(db, sqlCommands, oldVersion, newVersion);
        } else {
            sqlCommands.add(createBaseTableCreateScript());
        }
    }

    protected void fillColumns(Map<String, String> columns) {
        columns.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put("iu_entity_id", "TEXT NOT NULL");
        columns.put("iu_entity_type", "INTEGER NOT NULL");
        columns.put("ui_entity_key_param", "INTEGER");
        columns.put("iu_width", "INTEGER NOT NULL");
        columns.put("iu_height", "INTEGER");
        columns.put("iu_url", "TEXT NOT NULL");
        columns.put("iu_tag", "TEXT");
    }

    protected String getTableConstraint() {
        return "UNIQUE (iu_entity_type,iu_entity_id,ui_entity_key_param,iu_width,iu_height) ON CONFLICT REPLACE";
    }

    public void getOnAfterCreateStatements(List<String> outStmnts) {
        outStmnts.add(createTrigger("trigger_iu_banners", "banners", 14, "banner_id", null));
    }

    public void getOnAfterUpgradeStatements(List<String> outStmnts, int oldVersion, int newVersion) {
        if (oldVersion >= 59 || newVersion < 59) {
            super.getOnAfterUpgradeStatements(outStmnts, oldVersion, newVersion);
        } else {
            getOnAfterCreateStatements(outStmnts);
        }
    }

    private static String createTrigger(String triggerName, String frgnTable, int entityType, String idColumn, String keyParamColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TRIGGER ").append(triggerName).append(" AFTER DELETE ON ").append(frgnTable).append(" BEGIN DELETE FROM ").append("image_urls").append(" WHERE ").append("iu_entity_type").append("=").append(entityType).append(" AND ").append("iu_entity_id").append("=OLD.").append(idColumn);
        if (keyParamColumn != null) {
            sb.append(" AND ").append("ui_entity_key_param").append("=OLD.").append(keyParamColumn);
        }
        sb.append("; END");
        return sb.toString();
    }

    public String getTableName() {
        return "image_urls";
    }
}
