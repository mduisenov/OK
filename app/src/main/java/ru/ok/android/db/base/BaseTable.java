package ru.ok.android.db.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import ru.ok.android.db.SQLiteUtils;

public abstract class BaseTable {
    private Map<String, String> _columns;

    protected abstract void fillColumns(Map<String, String> map);

    public abstract String getTableName();

    public Map<String, String> getColumns() {
        if (this._columns == null) {
            this._columns = new LinkedHashMap();
            fillGeneralColumns(this._columns);
            fillColumns(this._columns);
        }
        return this._columns;
    }

    private void fillGeneralColumns(Map<String, String> columns) {
        columns.put("_last_update", "INTEGER");
    }

    public void fillUpgradeScript(SQLiteDatabase db, List<String> sqlCommands, int oldVersion, int newVersion) {
        if (oldVersion < 50 && newVersion >= 50) {
            sqlCommands.add("ALTER TABLE " + getTableName() + " ADD COLUMN " + "_last_update" + " INTEGER");
            sqlCommands.add("UPDATE " + getTableName() + " SET " + "_last_update" + " = " + System.currentTimeMillis());
        }
    }

    protected String getTableConstraint() {
        return null;
    }

    protected List<String> getIndexedColumnsNames() {
        return Collections.emptyList();
    }

    public String createBaseTableCreateScript() {
        return createBaseTableCreateScript(getTableName());
    }

    protected String createBaseTableCreateScript(String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append(" (");
        boolean appendComma = false;
        for (Entry<String, String> column : getColumns().entrySet()) {
            if (appendComma) {
                sb.append(", ");
            } else {
                appendComma = true;
            }
            sb.append((String) column.getKey()).append(' ').append((String) column.getValue());
        }
        String tableConstraint = getTableConstraint();
        if (!TextUtils.isEmpty(tableConstraint)) {
            sb.append(", ").append(tableConstraint);
        }
        sb.append(")");
        return sb.toString();
    }

    public static void serializeTable(BaseTable table, List<String> list) {
        for (Entry<String, String> column : table.getColumns().entrySet()) {
            list.add(table.getTableName() + '.' + ((String) column.getKey()) + " as " + table.getTableName() + (((String) column.getKey()).equals("_id") ? "" : "_") + ((String) column.getKey()));
        }
    }

    public List<String> createIndexesCreateScript() {
        List<String> indexedColumnsNames = getIndexedColumnsNames();
        if (indexedColumnsNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList();
        for (String columnName : indexedColumnsNames) {
            result.add("CREATE INDEX IF NOT EXISTS " + getTableName() + "_" + columnName.replaceAll("[,\\s]", "_") + " " + "ON " + getTableName() + " (" + columnName + ")");
        }
        return result;
    }

    protected void recreateTableAndCopyOldData(SQLiteDatabase db, List<String> sqlCommands) {
        String tableName = getTableName();
        Set<String> currentColumns = SQLiteUtils.queryTableColumns(db, tableName);
        String tmpTableName = "__" + tableName + "_tmp";
        currentColumns.retainAll(getColumns().keySet());
        String columns = TextUtils.join(",", currentColumns);
        sqlCommands.add(createBaseTableCreateScript(tmpTableName));
        sqlCommands.add("INSERT INTO " + tmpTableName + " (" + columns + ") SELECT " + columns + " FROM " + tableName);
        sqlCommands.add("DROP TABLE " + tableName);
        sqlCommands.add(createBaseTableCreateScript());
        sqlCommands.addAll(createIndexesCreateScript());
        sqlCommands.add("INSERT INTO " + tableName + " (" + columns + ") SELECT " + columns + " FROM " + tmpTableName);
        sqlCommands.add("DROP TABLE " + tmpTableName);
    }

    public void getOnAfterCreateStatements(List<String> list) {
    }

    public void getOnAfterUpgradeStatements(List<String> list, int oldVersion, int newVersion) {
    }

    public void onAfterUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
