package ru.ok.android.db.provider;

import android.content.ContentResolver;
import java.util.Map;
import ru.ok.android.db.provider.OdklContract.Banners;
import ru.ok.android.db.provider.OdklContract.FeedBannerColumns;

public class BannersProviderHelper extends JoinImageUrlsProviderHelper {
    protected BannersProviderHelper(ContentResolver contentResolver) {
        ContentResolver contentResolver2 = contentResolver;
        super(contentResolver2, "banners", Banners.getContentUri(), 14, "banner_id", null, "banner_id");
    }

    protected void fillOwnProjectionMap(Map<String, String> map) {
        for (String column : FeedBannerColumns.ALL_COLUMNS) {
            map.put(column, column);
        }
    }
}
