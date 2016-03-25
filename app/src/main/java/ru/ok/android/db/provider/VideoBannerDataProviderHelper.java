package ru.ok.android.db.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import java.util.Map;
import ru.ok.android.db.provider.OdklContract.VideoBannerData;
import ru.ok.android.db.provider.OdklContract.VideoStats;

public class VideoBannerDataProviderHelper extends BasicUpsertProviderHelper {
    private static Map<String, String> dataJoinStatsProjectionMap;

    VideoBannerDataProviderHelper(ContentResolver contentResolver) {
        super(contentResolver, "video_banner_data", VideoBannerData.getContentUri(), "vbd_banner_id");
    }

    Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String rowId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String joinOrder = null;
        if (VideoStats.hasVideoStatColumns(projection)) {
            qb.setTables("video_banner_data LEFT JOIN video_stats ON video_banner_data.vbd_banner_id=video_stats.vstat_banner_id");
            qb.setProjectionMap(getDataJoinStatsProjectionMap());
            joinOrder = "video_banner_data._id";
        } else {
            qb.setTables("video_banner_data");
        }
        if (rowId != null) {
            qb.appendWhere("video_banner_data._id");
            qb.appendWhere("=");
            qb.appendWhere(rowId);
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

    private static Map<String, String> getDataJoinStatsProjectionMap() {
        if (dataJoinStatsProjectionMap == null) {
            Map<String, String> map = new ArrayMap();
            map.put("_id", "video_banner_data._id");
            map.put("_last_update", "video_banner_data._last_update");
            map.put("vbd_duration_sec", "vbd_duration_sec");
            map.put("vbd_video_url", "vbd_video_url");
            map.put("vbd_banner_id", "vbd_banner_id");
            map.put("vstat_param", "vstat_param");
            map.put("vstat_url", "vstat_url");
            map.put("vstat_type", "vstat_type");
            map.put("vstat_banner_id", "vstat_banner_id");
            dataJoinStatsProjectionMap = map;
        }
        return dataJoinStatsProjectionMap;
    }
}
