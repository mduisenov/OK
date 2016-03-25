package ru.ok.android.db.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import java.util.Arrays;
import java.util.List;

public final class OdklContract {

    public interface BaseOdklColumns extends BaseColumns {
    }

    public interface AdStatisticColumns extends BaseOdklColumns {
    }

    public static final class AdStatistics implements AdStatisticColumns {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;
        private static Uri silentContentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".ad_stat";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".ad_stat";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "ad_stats");
            }
            return contentUri;
        }

        public static Uri getSilentContentUri() {
            if (silentContentUri == null) {
                silentContentUri = getContentUri().buildUpon().appendQueryParameter("silent", "true").build();
            }
            return silentContentUri;
        }
    }

    public interface AuthorizedUserColumns extends BaseOdklColumns {
    }

    public static class AuthorizedUsers implements AuthorizedUserColumns {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;
        private static Uri uidContentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".authorized_users";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".authorized_users";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "authorized_users");
            }
            return contentUri;
        }

        public static Uri getContentUri(String userId) {
            if (uidContentUri == null) {
                uidContentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "authorized_users/uid");
            }
            return uidContentUri.buildUpon().appendPath(userId).build();
        }
    }

    public interface FeedBannerColumns extends BaseOdklColumns {
        public static final List<String> ALL_COLUMNS;

        static {
            ALL_COLUMNS = Arrays.asList(new String[]{"banner_id", "banner_template", "banner_header", "banner_text", "banner_action_type", "banner_icon_type", "banner_icon_url", "banner_icon_url_hd", "banner_click_url", "banner_color", "banner_disclaimer", "banner_info", "banner_votes", "banner_users", "banner_rating", "banner_age_restriction", "banner_deep_link"});
        }
    }

    public static final class Banners implements FeedBannerColumns {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;
        private static Uri silentContentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".banner";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".banner";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "banners");
            }
            return contentUri;
        }

        public static Uri getSilentContentUri() {
            if (silentContentUri == null) {
                silentContentUri = getContentUri().buildUpon().appendQueryParameter("silent", "true").build();
            }
            return silentContentUri;
        }

        public static boolean hasBannerColumns(String[] projection) {
            if (projection == null) {
                return false;
            }
            for (String col : projection) {
                if (FeedBannerColumns.ALL_COLUMNS.contains(col)) {
                    return true;
                }
            }
            return false;
        }
    }

    public interface GroupMembersColumns extends BaseOdklColumns {
    }

    public static class GroupMembers implements GroupMembersColumns {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;
        private static Uri silentContentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".group_members";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".group_members";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "group_members");
            }
            return contentUri;
        }

        public static Uri getContentUri(String groupId) {
            return getContentUri().buildUpon().appendPath(groupId).build();
        }

        public static Uri getSilentContentUri() {
            if (silentContentUri == null) {
                silentContentUri = getContentUri().buildUpon().appendQueryParameter("silent", "true").build();
            }
            return silentContentUri;
        }
    }

    public static final class Groups {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".group";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".group";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "groups");
            }
            return contentUri;
        }

        public static Uri getUri(String gid) {
            return Uri.withAppendedPath(getContentUri(), gid);
        }
    }

    public interface ImageUrlColumns extends BaseOdklColumns {
    }

    public static final class ImageUrls implements ImageUrlColumns {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;
        private static Uri silentContentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".image_url";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".image_url";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "image_urls");
            }
            return contentUri;
        }

        public static Uri getSilentContentUri() {
            if (silentContentUri == null) {
                silentContentUri = getContentUri().buildUpon().appendQueryParameter("silent", "true").build();
            }
            return silentContentUri;
        }

        public static boolean hasImageUrlColumns(String[] projection) {
            if (projection == null) {
                return false;
            }
            for (String col : projection) {
                if ("iu_width".equals(col) || "iu_height".equals(col) || "iu_url".equals(col) || "iu_tag".equals(col)) {
                    return true;
                }
            }
            return false;
        }
    }

    public interface PromoLinkColumns extends BaseOdklColumns {
    }

    public static final class PromoLinks implements PromoLinkColumns {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;
        private static Uri silentContentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".promo_link";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".promo_link";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "promo_links");
            }
            return contentUri;
        }

        public static Uri getSilentContentUri() {
            if (silentContentUri == null) {
                silentContentUri = getContentUri().buildUpon().appendQueryParameter("silent", "true").build();
            }
            return silentContentUri;
        }
    }

    public interface UserColumns extends BaseOdklColumns {
    }

    public static final class UserPrivacySettings {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".privacy_settings";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".privacy_settings";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "privacy_settings");
            }
            return contentUri;
        }

        public static Uri getUri(int setting) {
            return Uri.withAppendedPath(getContentUri(), Integer.toString(setting));
        }

        public static Uri getUri(int setting, String uid) {
            return Uri.withAppendedPath(getUri(setting), uid);
        }
    }

    public static final class Users implements UserColumns {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".user";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".user";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "users");
            }
            return contentUri;
        }

        public static Uri getUri(String uid) {
            return Uri.withAppendedPath(getContentUri(), uid);
        }
    }

    public interface VideoBannerDataColumns extends BaseOdklColumns {
    }

    public static final class VideoBannerData implements VideoBannerDataColumns {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;
        private static Uri silentContentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".vide_banner_data";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".vide_banner_data";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "video_banner_data");
            }
            return contentUri;
        }

        public static Uri getSilentContentUri() {
            if (silentContentUri == null) {
                silentContentUri = getContentUri().buildUpon().appendQueryParameter("silent", "true").build();
            }
            return silentContentUri;
        }
    }

    public interface VideoStatColumns extends BaseOdklColumns {
    }

    public static final class VideoStats implements VideoStatColumns {
        public static final String CONTENT_ITEM_TYPE;
        public static final String CONTENT_TYPE;
        private static Uri contentUri;
        private static Uri silentContentUri;

        static {
            CONTENT_TYPE = "vnd.android.cursor.dir/" + OdklContract.getAuthority() + ".video_stat";
            CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + OdklContract.getAuthority() + ".video_stat";
        }

        public static Uri getContentUri() {
            if (contentUri == null) {
                contentUri = Uri.parse("content://" + OdklContract.getAuthority() + "/" + "video_stats");
            }
            return contentUri;
        }

        public static Uri getSilentContentUri() {
            if (silentContentUri == null) {
                silentContentUri = getContentUri().buildUpon().appendQueryParameter("silent", "true").build();
            }
            return silentContentUri;
        }

        public static boolean hasVideoStatColumns(String[] projection) {
            if (projection == null) {
                return false;
            }
            for (String column : projection) {
                if ("vstat_type".equals(column) || "vstat_url".equals(column) || "vstat_param".equals(column)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static String getAuthority() {
        return "ru.ok.android.provider";
    }
}
