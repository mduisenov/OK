package ru.ok.android.db.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import java.util.HashMap;
import java.util.Map;
import ru.ok.android.db.provider.OdklContract.ImageUrls;

public abstract class JoinImageUrlsProviderHelper extends BasicUpsertProviderHelper {
    private final int entityType;
    private final String idColumn;
    private Map<String, String> joinedProjectionMap;
    private String joinedTables;
    private final String keyParamColumn;
    private String rowIdColumn;

    protected abstract void fillOwnProjectionMap(Map<String, String> map);

    protected JoinImageUrlsProviderHelper(ContentResolver contentResolver, String tableName, Uri contentUri, int entityType, String idColumn, String keyParamColumn, String... keyColumns) {
        super(contentResolver, tableName, contentUri, keyColumns);
        this.entityType = entityType;
        this.idColumn = idColumn;
        this.keyParamColumn = keyParamColumn;
    }

    Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String rowId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String joinOrder = null;
        if (ImageUrls.hasImageUrlColumns(projection)) {
            qb.setTables(getJoinedTables());
            qb.setProjectionMap(getJoinedProjectionMap());
            joinOrder = getRowIdColumn();
        } else {
            qb.setTables(this.table);
        }
        if (rowId != null) {
            qb.appendWhere("_id");
            qb.appendWhere("=");
            qb.appendWhere(getRowIdColumn());
        }
        if (joinOrder != null) {
            if (sortOrder != null) {
                sortOrder = joinOrder + "," + sortOrder;
            } else {
                sortOrder = joinOrder;
            }
        }
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(this.contentResolver, uri);
        }
        return cursor;
    }

    private String getRowIdColumn() {
        if (this.rowIdColumn == null) {
            this.rowIdColumn = this.table + "." + "_id";
        }
        return this.rowIdColumn;
    }

    private String getJoinedTables() {
        if (this.joinedTables == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.table).append(" LEFT JOIN ").append("image_urls").append(" ON ").append("iu_entity_type").append("=").append(this.entityType).append(" AND ").append("iu_entity_id").append("=").append(this.idColumn);
            if (this.keyParamColumn != null) {
                sb.append(" AND ").append("ui_entity_key_param").append("=").append(this.keyParamColumn);
            }
            this.joinedTables = sb.toString();
        }
        return this.joinedTables;
    }

    private Map<String, String> getJoinedProjectionMap() {
        if (this.joinedProjectionMap == null) {
            HashMap map = new HashMap();
            map.put("_id", this.table + "." + "_id");
            map.put("_last_update", this.table + "." + "_last_update");
            fillOwnProjectionMap(map);
            fillImageUrlsProjectionMap(map);
            this.joinedProjectionMap = map;
        }
        return this.joinedProjectionMap;
    }

    private void fillImageUrlsProjectionMap(Map<String, String> map) {
        map.put("iu_entity_id", "iu_entity_id");
        map.put("iu_entity_type", "iu_entity_type");
        map.put("ui_entity_key_param", "ui_entity_key_param");
        map.put("iu_url", "iu_url");
        map.put("iu_width", "iu_width");
        map.put("iu_height", "iu_height");
        map.put("iu_tag", "iu_tag");
    }
}
