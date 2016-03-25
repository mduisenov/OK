package ru.ok.android.db.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import java.util.HashMap;
import java.util.Map;
import ru.ok.android.db.provider.OdklContract.Banners;
import ru.ok.android.db.provider.OdklContract.ImageUrls;
import ru.ok.android.db.provider.OdklContract.PromoLinks;

public class PromoLinksProviderHelper extends BasicUpsertProviderHelper {
    private static Map<String, String> joinBannersImageUrlsProjectionMap;
    private static Map<String, String> joinBannersProjectionMap;

    PromoLinksProviderHelper(ContentResolver contentResolver) {
        super(contentResolver, "promo_links", PromoLinks.getContentUri(), "pmlk_type", "pmlk_id");
    }

    Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String rowId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String joinOrder = null;
        StringBuilder sbTables = new StringBuilder();
        sbTables.append("promo_links");
        if (Banners.hasBannerColumns(projection)) {
            sbTables.append(" JOIN banners ON banner_id=pmlk_banner_id");
            if (ImageUrls.hasImageUrlColumns(projection)) {
                sbTables.append(" LEFT JOIN image_urls ON iu_entity_type=14 AND iu_entity_id=banner_id");
                qb.setProjectionMap(getJoinBannersImageUrlsProjectionMap());
            } else {
                qb.setProjectionMap(getJoinBannersProjectionMap());
            }
            joinOrder = "banner_id";
        }
        qb.setTables(sbTables.toString());
        if (rowId != null) {
            qb.appendWhere("promo_links._id");
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

    private static Map<String, String> getJoinBannersProjectionMap() {
        if (joinBannersProjectionMap == null) {
            Map<String, String> map = new HashMap();
            putPromoLinkColumns(map);
            putBannerColumns(map);
            joinBannersProjectionMap = map;
        }
        return joinBannersProjectionMap;
    }

    private static Map<String, String> getJoinBannersImageUrlsProjectionMap() {
        if (joinBannersImageUrlsProjectionMap == null) {
            Map<String, String> map = new HashMap();
            putPromoLinkColumns(map);
            putBannerColumns(map);
            putImageUrlColumns(map);
            joinBannersImageUrlsProjectionMap = map;
        }
        return joinBannersImageUrlsProjectionMap;
    }

    private static void putPromoLinkColumns(Map<String, String> map) {
        map.put("_id", "promo_links._id");
        map.put("_last_update", "promo_links._last_update");
        map.put("pmlk_type", "pmlk_type");
        map.put("pmlk_id", "pmlk_id");
        map.put("pmlk_banner_id", "pmlk_banner_id");
        map.put("_last_update", "promo_links._last_update");
    }

    private static void putBannerColumns(Map<String, String> map) {
        map.put("banner_icon_url_hd", "banner_icon_url_hd");
        map.put("banner_icon_type", "banner_icon_type");
        map.put("banner_icon_url", "banner_icon_url");
        map.put("banner_action_type", "banner_action_type");
        map.put("banner_id", "banner_id");
        map.put("banner_template", "banner_template");
        map.put("banner_click_url", "banner_click_url");
        map.put("banner_header", "banner_header");
        map.put("banner_text", "banner_text");
        map.put("banner_color", "banner_color");
        map.put("banner_disclaimer", "banner_disclaimer");
        map.put("banner_info", "banner_info");
        map.put("banner_votes", "banner_votes");
        map.put("banner_users", "banner_users");
        map.put("banner_rating", "banner_rating");
        map.put("banner_age_restriction", "banner_age_restriction");
        map.put("banner_deep_link", "banner_deep_link");
    }

    private static void putImageUrlColumns(Map<String, String> map) {
        map.put("iu_entity_id", "iu_entity_id");
        map.put("iu_entity_type", "iu_entity_type");
        map.put("iu_url", "iu_url");
        map.put("iu_width", "iu_width");
        map.put("iu_height", "iu_height");
        map.put("iu_tag", "iu_tag");
    }
}
