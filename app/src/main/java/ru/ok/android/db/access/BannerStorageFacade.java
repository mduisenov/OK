package ru.ok.android.db.access;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import java.util.List;
import ru.ok.android.db.provider.OdklContract.Banners;
import ru.ok.android.db.provider.OdklContract.ImageUrls;
import ru.ok.android.db.provider.OdklContract.VideoBannerData;
import ru.ok.android.db.provider.OdklContract.VideoStats;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.banner.BannerBuilder;
import ru.ok.model.stream.banner.VideoData;
import ru.ok.model.stream.banner.VideoProgressStat;
import ru.ok.model.stream.banner.VideoStat;

public class BannerStorageFacade {
    static final String[] PROJECTION_BANNER;
    private static final String[] PROJECTION_VIDEO_DATA;

    public static void getInsertBannerOps(BannerBuilder banner, List<ContentProviderOperation> outOps) {
        if (banner != null && outOps != null) {
            Builder insertBanner = ContentProviderOperation.newInsert(Banners.getSilentContentUri());
            insertBanner.withValue("banner_id", banner.getId());
            insertBanner.withValue("banner_template", Integer.valueOf(banner.getTemplate()));
            insertBanner.withValue("banner_header", banner.getHeader());
            insertBanner.withValue("banner_text", banner.getText());
            insertBanner.withValue("banner_action_type", Integer.valueOf(banner.getActionType()));
            insertBanner.withValue("banner_icon_type", Integer.valueOf(banner.getIconType()));
            insertBanner.withValue("banner_icon_url", banner.getIconUrl());
            insertBanner.withValue("banner_icon_url_hd", banner.getIconUrlHd());
            insertBanner.withValue("banner_color", Integer.valueOf(banner.getColor()));
            insertBanner.withValue("banner_click_url", banner.getClickUrl());
            insertBanner.withValue("banner_disclaimer", banner.getDisclaimer());
            insertBanner.withValue("banner_info", banner.getInfo());
            insertBanner.withValue("banner_votes", Integer.valueOf(banner.getVotes()));
            insertBanner.withValue("banner_users", Integer.valueOf(banner.getUsers()));
            insertBanner.withValue("banner_rating", Float.valueOf(banner.getRating()));
            insertBanner.withValue("banner_age_restriction", banner.getAgeRestriction());
            insertBanner.withValue("banner_deep_link", banner.getDeepLink());
            outOps.add(insertBanner.build());
            List<PhotoSize> pics = banner.getImages();
            if (pics != null) {
                for (PhotoSize pic : pics) {
                    Builder insertPic = ContentProviderOperation.newInsert(ImageUrls.getSilentContentUri());
                    insertPic.withValue("iu_entity_type", Integer.valueOf(14));
                    insertPic.withValue("iu_entity_id", banner.getId());
                    insertPic.withValue("iu_width", Integer.valueOf(pic.getWidth()));
                    insertPic.withValue("iu_height", Integer.valueOf(pic.getHeight()));
                    insertPic.withValue("iu_url", pic.getUrl());
                    insertPic.withValue("iu_tag", pic.getJsonKey());
                    outOps.add(insertPic.build());
                }
            }
            if (banner.getTemplate() == 5) {
                String[] selectionBannerId = new String[]{banner.getId()};
                getDeleteVideoStatsOps(outOps, selectionBannerId);
                VideoData videoData = banner.getVideoData();
                if (videoData != null) {
                    getInsertVideoDataOps(banner.getId(), videoData, outOps);
                } else {
                    getDeleteVideoDataOps(outOps, selectionBannerId);
                }
            }
        }
    }

    private static void getDeleteVideoDataOps(List<ContentProviderOperation> outOps, String[] selectionBannerId) {
        Builder deleteData = ContentProviderOperation.newDelete(VideoBannerData.getSilentContentUri());
        deleteData.withSelection("vbd_banner_id=?", selectionBannerId);
        outOps.add(deleteData.build());
    }

    private static void getDeleteVideoStatsOps(List<ContentProviderOperation> outOps, String[] selectionBannerId) {
        Builder deleteData = ContentProviderOperation.newDelete(VideoStats.getSilentContentUri());
        deleteData.withSelection("vstat_banner_id=?", selectionBannerId);
        outOps.add(deleteData.build());
    }

    private static void getInsertVideoDataOps(String bannerId, VideoData videoData, List<ContentProviderOperation> outOps) {
        Builder insertData = ContentProviderOperation.newInsert(VideoBannerData.getSilentContentUri());
        insertData.withValue("vbd_banner_id", bannerId);
        insertData.withValue("vbd_video_url", videoData.videoUrl);
        insertData.withValue("vbd_duration_sec", Integer.valueOf(videoData.durationSec));
        outOps.add(insertData.build());
        for (int statType = 0; statType < 5; statType++) {
            for (VideoStat stat : videoData.getStats(statType)) {
                Builder insertStat = ContentProviderOperation.newInsert(VideoStats.getSilentContentUri());
                insertStat.withValue("vstat_banner_id", bannerId);
                insertStat.withValue("vstat_type", Integer.valueOf(statType));
                insertStat.withValue("vstat_url", stat.url);
                if (stat instanceof VideoProgressStat) {
                    insertStat.withValue("vstat_param", Integer.valueOf(((VideoProgressStat) stat).positionSec));
                }
                outOps.add(insertStat.build());
            }
        }
    }

    static {
        PROJECTION_BANNER = new String[]{"banner_id", "banner_template", "banner_header", "banner_text", "banner_action_type", "banner_click_url", "banner_icon_type", "banner_icon_url", "banner_icon_url_hd", "banner_color", "banner_disclaimer", "banner_info", "banner_votes", "banner_users", "banner_rating", "banner_age_restriction", "banner_deep_link", "iu_width", "iu_height", "iu_url", "iu_tag", "_id"};
        PROJECTION_VIDEO_DATA = new String[]{"vbd_banner_id", "vbd_video_url", "vbd_duration_sec", "vstat_type", "vstat_url", "vstat_param"};
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static ru.ok.model.stream.banner.BannerBuilder parseBannerWithPics(android.database.Cursor r13) throws ru.ok.model.stream.FeedObjectException {
        /*
        r12 = 18;
        r11 = 17;
        r10 = 0;
        r7 = -1;
        r1 = new ru.ok.model.stream.banner.BannerBuilder;
        r1.<init>();
        r0 = r13.getString(r10);
        r1.withId(r0);
        r8 = 1;
        r8 = r13.getInt(r8);
        r1.withTemplate(r8);
        r8 = 2;
        r8 = r13.getString(r8);
        r1.withHeader(r8);
        r8 = 3;
        r8 = r13.getString(r8);
        r1.withText(r8);
        r8 = 4;
        r8 = r13.getInt(r8);
        r1.withActionType(r8);
        r8 = 6;
        r8 = r13.getInt(r8);
        r1.withIconType(r8);
        r8 = 7;
        r8 = r13.getString(r8);
        r1.withIconUrl(r8);
        r8 = 8;
        r8 = r13.getString(r8);
        r1.withIconUrlHd(r8);
        r8 = 5;
        r8 = r13.getString(r8);
        r1.withClickUrl(r8);
        r8 = 9;
        r8 = r13.getInt(r8);
        r1.withColor(r8);
        r8 = 10;
        r8 = r13.getString(r8);
        r1.withDisclaimer(r8);
        r8 = 11;
        r8 = r13.getString(r8);
        r1.withInfo(r8);
        r8 = 12;
        r8 = r13.getInt(r8);
        r1.withVotes(r8);
        r8 = 13;
        r8 = r13.getInt(r8);
        r1.withUsers(r8);
        r8 = 14;
        r8 = r13.getFloat(r8);
        r1.withRating(r8);
        r8 = 15;
        r8 = r13.getString(r8);
        r1.withAgeRestriction(r8);
        r8 = 21;
        r8 = r13.getLong(r8);
        r1.withDbId(r8);
        r8 = 16;
        r8 = r13.getString(r8);
        r1.withDeepLink(r8);
    L_0x00a4:
        r8 = r13.isNull(r11);
        if (r8 == 0) goto L_0x00d3;
    L_0x00aa:
        r6 = r7;
    L_0x00ab:
        r8 = r13.isNull(r12);
        if (r8 == 0) goto L_0x00d8;
    L_0x00b1:
        r2 = r7;
    L_0x00b2:
        r8 = 19;
        r5 = r13.getString(r8);
        r8 = 20;
        r4 = r13.getString(r8);
        r3 = new ru.ok.model.photo.PhotoSize;
        r3.<init>(r5, r6, r2, r4);
        r1.addImage(r3);
        r8 = r13.moveToNext();
        if (r8 == 0) goto L_0x00d2;
    L_0x00cc:
        r8 = r13.isAfterLast();
        if (r8 == 0) goto L_0x00dd;
    L_0x00d2:
        return r1;
    L_0x00d3:
        r6 = r13.getInt(r11);
        goto L_0x00ab;
    L_0x00d8:
        r2 = r13.getInt(r12);
        goto L_0x00b2;
    L_0x00dd:
        r8 = r13.getString(r10);
        r8 = android.text.TextUtils.equals(r0, r8);
        if (r8 != 0) goto L_0x00a4;
    L_0x00e7:
        goto L_0x00d2;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.access.BannerStorageFacade.parseBannerWithPics(android.database.Cursor):ru.ok.model.stream.banner.BannerBuilder");
    }
}
