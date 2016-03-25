package ru.ok.android.db.relatives;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import ru.ok.android.db.base.BaseTable;

public class RelativesTable extends BaseTable {
    protected void fillColumns(Map<String, String> columns) {
        columns.put("uid", "TEXT REFERENCES users(user_id) ON DELETE CASCADE");
        columns.put("type", "TEXT");
        columns.put("subtype", "TEXT");
    }

    protected List<String> getIndexedColumnsNames() {
        return Collections.singletonList("uid");
    }

    public String getTableName() {
        return "relatives";
    }
}
