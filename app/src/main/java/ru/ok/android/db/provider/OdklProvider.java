package ru.ok.android.db.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.Uri.Builder;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.SQLiteUtils;
import ru.ok.android.db.provider.OdklContract.AdStatistics;
import ru.ok.android.db.provider.OdklContract.AuthorizedUsers;
import ru.ok.android.db.provider.OdklContract.Banners;
import ru.ok.android.db.provider.OdklContract.GroupMembers;
import ru.ok.android.db.provider.OdklContract.Groups;
import ru.ok.android.db.provider.OdklContract.ImageUrls;
import ru.ok.android.db.provider.OdklContract.PromoLinks;
import ru.ok.android.db.provider.OdklContract.UserPrivacySettings;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.db.provider.OdklContract.VideoBannerData;
import ru.ok.android.db.provider.OdklContract.VideoStats;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;

public final class OdklProvider extends ContentProvider {
    public static final String AUTHORITY;
    public static final Uri CONTENT_URI;
    private static AtomicInteger requestNumber;
    private static final UriMatcher uriMatcher;
    private BasicProviderHelper adStatsProviderHelper;
    private AuthorizedUsersProviderHelper authorizedUsersProviderHelper;
    private BasicProviderHelper bannersProviderHelper;
    private BasicProviderHelper groupMembersProviderHelper;
    private BasicProviderHelper imageUrlsProviderHelper;
    private BasicProviderHelper promoLinksProviderHelper;
    private BasicProviderHelper videoBannerDataProviderHelper;
    private BasicProviderHelper videoStatsProviderHelper;

    static {
        AUTHORITY = OdklContract.getAuthority();
        CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");
        uriMatcher = new UriMatcher(-1);
        uriMatcher.addURI(AUTHORITY, "discussions/comments", 1);
        uriMatcher.addURI(AUTHORITY, "discussions/comments/#", 2);
        uriMatcher.addURI(AUTHORITY, "friends", 3);
        uriMatcher.addURI(AUTHORITY, "friends/*", 4);
        uriMatcher.addURI(AUTHORITY, "user_relations/*", 8);
        uriMatcher.addURI(AUTHORITY, "user_counters/*", 11);
        uriMatcher.addURI(AUTHORITY, "user_communities/*", 12);
        uriMatcher.addURI(AUTHORITY, "user_interests/*", 13);
        uriMatcher.addURI(AUTHORITY, "user_presents/*", 14);
        uriMatcher.addURI(AUTHORITY, "user_relation_info/*", 15);
        uriMatcher.addURI(AUTHORITY, "users/*", 57);
        uriMatcher.addURI(AUTHORITY, "users", 58);
        uriMatcher.addURI(AUTHORITY, "playlist", 73);
        uriMatcher.addURI(AUTHORITY, "tracks", 74);
        uriMatcher.addURI(AUTHORITY, "albums", 75);
        uriMatcher.addURI(AUTHORITY, "artists", 76);
        uriMatcher.addURI(AUTHORITY, "group_counters/*", 32);
        uriMatcher.addURI(AUTHORITY, "group_user_status", 86);
        uriMatcher.addURI(AUTHORITY, "groups/*", 77);
        uriMatcher.addURI(AUTHORITY, "groups", 78);
        uriMatcher.addURI(AUTHORITY, "group_members", 20);
        uriMatcher.addURI(AUTHORITY, "image_urls/#", 116);
        uriMatcher.addURI(AUTHORITY, "image_urls", 117);
        uriMatcher.addURI(AUTHORITY, "music/my", 88);
        uriMatcher.addURI(AUTHORITY, "music/my/*", 49);
        uriMatcher.addURI(AUTHORITY, "music/my_max_position/*", 93);
        uriMatcher.addURI(AUTHORITY, "music/collections", 46);
        uriMatcher.addURI(AUTHORITY, "music/collections_relations", 87);
        uriMatcher.addURI(AUTHORITY, "music/collections_relations/*", 98);
        uriMatcher.addURI(AUTHORITY, "music/collections/*", 47);
        uriMatcher.addURI(AUTHORITY, "music/max_collections_position/*", 99);
        uriMatcher.addURI(AUTHORITY, "music/collection_tracks", 50);
        uriMatcher.addURI(AUTHORITY, "music/collections_pop", 89);
        uriMatcher.addURI(AUTHORITY, "music/friends_music", 52);
        uriMatcher.addURI(AUTHORITY, "music/tuners", 90);
        uriMatcher.addURI(AUTHORITY, "music/tuners_artists", 91);
        uriMatcher.addURI(AUTHORITY, "music/history", 92);
        uriMatcher.addURI(AUTHORITY, "music/tuners_tracks", 94);
        uriMatcher.addURI(AUTHORITY, "music/tuners_tracks/*", 95);
        uriMatcher.addURI(AUTHORITY, "music/pop_tracks", 96);
        uriMatcher.addURI(AUTHORITY, "music/extension", 97);
        uriMatcher.addURI(AUTHORITY, "privacy_settings/#/*", 85);
        uriMatcher.addURI(AUTHORITY, "privacy_settings/#", 84);
        uriMatcher.addURI(AUTHORITY, "privacy_settings", 83);
        uriMatcher.addURI(AUTHORITY, "relatives", C0206R.styleable.Theme_radioButtonStyle);
        uriMatcher.addURI(AUTHORITY, "friends_suggest", C0206R.styleable.Theme_ratingBarStyle);
        uriMatcher.addURI(AUTHORITY, "banners/#", 147);
        uriMatcher.addURI(AUTHORITY, "banners", 148);
        uriMatcher.addURI(AUTHORITY, "promo_links/#", 149);
        uriMatcher.addURI(AUTHORITY, "promo_links", 150);
        uriMatcher.addURI(AUTHORITY, "ad_stats", 151);
        uriMatcher.addURI(AUTHORITY, "user_steam_subscribe", 17);
        uriMatcher.addURI(AUTHORITY, "user_steam_subscribe/*", 16);
        uriMatcher.addURI(AUTHORITY, "group_steam_subscribe/*", 18);
        uriMatcher.addURI(AUTHORITY, "group_steam_subscribe", 19);
        uriMatcher.addURI(AUTHORITY, "mutual_friends/*", 158);
        uriMatcher.addURI(AUTHORITY, "all_tables", 159);
        uriMatcher.addURI(AUTHORITY, "video_banner_data/#", 162);
        uriMatcher.addURI(AUTHORITY, "video_banner_data", 163);
        uriMatcher.addURI(AUTHORITY, "video_stats/#", 164);
        uriMatcher.addURI(AUTHORITY, "video_stats", 165);
        uriMatcher.addURI(AUTHORITY, "authorized_users/uid/*", 167);
        uriMatcher.addURI(AUTHORITY, "authorized_users", 166);
        requestNumber = new AtomicInteger();
    }

    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        ContentResolver cr = context.getContentResolver();
        this.imageUrlsProviderHelper = new BasicUpsertProviderHelper(cr, "image_urls", ImageUrls.getContentUri(), "iu_entity_type", "iu_entity_id", "ui_entity_key_param", "iu_width", "iu_height");
        this.groupMembersProviderHelper = new ProviderGroupMembersHelper(cr);
        this.bannersProviderHelper = new BannersProviderHelper(cr);
        this.promoLinksProviderHelper = new PromoLinksProviderHelper(cr);
        this.adStatsProviderHelper = new BasicProviderHelper(cr, "ad_stats", AdStatistics.getContentUri());
        this.videoBannerDataProviderHelper = new VideoBannerDataProviderHelper(cr);
        this.videoStatsProviderHelper = new BasicProviderHelper(cr, "video_stats", VideoStats.getContentUri());
        this.authorizedUsersProviderHelper = new AuthorizedUsersProviderHelper(cr);
    }

    public static Uri friendsSuggest() {
        return Uri.withAppendedPath(CONTENT_URI, "friends_suggest");
    }

    public static Uri relativesUri() {
        return Uri.withAppendedPath(CONTENT_URI, "relatives");
    }

    public static Uri commentUri(long commentId) {
        return Uri.withAppendedPath(CONTENT_URI, "discussions/comments/" + commentId);
    }

    public static Uri commentsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "discussions/comments");
    }

    public static Uri commentsSilentUri() {
        return commentsUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri friendsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "friends");
    }

    public static Uri friendUri(String userId) {
        return Uri.withAppendedPath(CONTENT_URI, "friends/" + userId);
    }

    public static Uri conversationUri(String id) {
        return Uri.withAppendedPath(CONTENT_URI, "conversations/" + id);
    }

    public static Uri conversationsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "conversations");
    }

    public static Uri userRelationsUri(String userId) {
        return Uri.withAppendedPath(CONTENT_URI, "user_relations/" + userId);
    }

    public static Uri userCountersUri(String userId) {
        return Uri.withAppendedPath(CONTENT_URI, "user_counters/" + userId);
    }

    public static Uri userInterestsUri(String userId) {
        return Uri.withAppendedPath(CONTENT_URI, "user_interests/" + userId);
    }

    public static Uri userPresentsUri(String userId) {
        return Uri.withAppendedPath(CONTENT_URI, "user_presents/" + userId);
    }

    public static Uri userRelationInfoUri(String userId) {
        return Uri.withAppendedPath(CONTENT_URI, "user_relation_info/" + userId);
    }

    public static Uri tracksUri() {
        return Uri.withAppendedPath(CONTENT_URI, "tracks");
    }

    public static Uri tracksSilentUri() {
        return tracksUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri trackUri(long trackId) {
        return Uri.withAppendedPath(CONTENT_URI, "tracks/" + trackId);
    }

    public static Uri artistUri(long artistId) {
        return Uri.withAppendedPath(CONTENT_URI, "artists/" + artistId);
    }

    public static Uri albumUri(long albumId) {
        return Uri.withAppendedPath(CONTENT_URI, "albums/" + albumId);
    }

    public static Uri artistsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "artists");
    }

    public static Uri artistsSilentUri() {
        return CONTENT_URI.buildUpon().appendPath("artists").appendQueryParameter("silent", "true").build();
    }

    public static Uri albumsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "albums");
    }

    public static Uri albumsSilentUri() {
        return CONTENT_URI.buildUpon().appendPath("albums").appendQueryParameter("silent", "true").build();
    }

    public static Uri playListUri() {
        return Uri.withAppendedPath(CONTENT_URI, "playlist");
    }

    public static Uri playListSilentUri() {
        return playListUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri collectionUri(long collectionId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/collections/" + collectionId);
    }

    public static Uri collectionsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/collections");
    }

    public static Uri maxPositionCollectionsUri(String userId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/max_collections_position/" + userId);
    }

    public static Uri collectionRelationsUri(String userId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/collections_relations/" + userId);
    }

    public static Uri collectionRelationsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/collections_relations");
    }

    public static Uri popCollectionRelationUri(long relationID) {
        return Uri.withAppendedPath(CONTENT_URI, "music/collections_pop/" + relationID);
    }

    public static Uri popCollectionsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/collections_pop");
    }

    public static Uri tunersUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/tuners");
    }

    public static Uri tunersSilentUri() {
        return tunersUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri tunersUri(String tunerId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/tuners/" + tunerId);
    }

    public static Uri tunersArtistsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/tuners_artists");
    }

    public static Uri tunersArtistsSilentUri() {
        return tunersArtistsUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri tunersArtistsUri(String tunerId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/tuners_artists/" + tunerId);
    }

    public static Uri tunersTracksUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/tuners_tracks");
    }

    public static Uri popTracksUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/pop_tracks");
    }

    public static Uri popTracksSilentUri() {
        return popTracksUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri popTracksUri(long rowId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/pop_tracks/" + rowId);
    }

    public static Uri tunersTracksUri(String tunerId) {
        Builder builder = new Builder().scheme(CONTENT_URI.getScheme()).authority(CONTENT_URI.getHost());
        builder.appendPath("music").appendPath("tuners_tracks").appendPath(tunerId);
        return builder.build();
    }

    public static Uri tunersTracksSilentUri(String tunerId) {
        return tunersTracksUri(tunerId).buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri userTracksUri(String uid) {
        return Uri.withAppendedPath(CONTENT_URI, "music/my/" + uid);
    }

    public static Uri userTracksUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/my");
    }

    public static Uri userTracksSilentUri() {
        return userTracksUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri userMaxPositionTracksUri(String uid) {
        return Uri.withAppendedPath(CONTENT_URI, "music/my_max_position/" + uid);
    }

    public static Uri collectionTracksUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/collection_tracks");
    }

    public static Uri collectionTracksSilentUri() {
        return collectionTracksUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri collectionTracksUri(long rowId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/collection_tracks/" + rowId);
    }

    public static Uri musicFriendsUri(String userId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/friends_music/" + userId);
    }

    public static Uri musicFriendsUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/friends_music");
    }

    public static Uri musicHistoryUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/history");
    }

    public static Uri musicHistorySilentUri() {
        return musicHistoryUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri musicHistoryUri(long rowId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/history/" + rowId);
    }

    public static Uri musicExtensionUri() {
        return Uri.withAppendedPath(CONTENT_URI, "music/extension");
    }

    public static Uri musicExtensionSilentUri() {
        return musicExtensionUri().buildUpon().appendQueryParameter("silent", "true").build();
    }

    public static Uri musicExtensionUri(long rowId) {
        return Uri.withAppendedPath(CONTENT_URI, "music/extension" + rowId);
    }

    public static Uri groupCountersUri(String groupId) {
        return Uri.withAppendedPath(CONTENT_URI, "group_counters/" + groupId);
    }

    public static Uri groupUserStatusUri(String userId, String groupId) {
        return Uri.withAppendedPath(CONTENT_URI, "group_user_status/" + userId + "/" + groupId);
    }

    public static Uri groupUsersStatusUri() {
        return Uri.withAppendedPath(CONTENT_URI, "group_user_status");
    }

    public static Uri userStreamSubscribeUri(String uid) {
        return Uri.withAppendedPath(CONTENT_URI, "user_steam_subscribe/" + uid);
    }

    public static Uri userStreamSubscribeUri() {
        return Uri.withAppendedPath(CONTENT_URI, "user_steam_subscribe");
    }

    public static Uri groupStreamSubscribeUri(String gid) {
        return Uri.withAppendedPath(CONTENT_URI, "group_steam_subscribe/" + gid);
    }

    public static Uri groupStreamSubscribeUri() {
        return Uri.withAppendedPath(CONTENT_URI, "group_steam_subscribe");
    }

    public static Uri mutualFriendsUri(String uid) {
        return Uri.withAppendedPath(CONTENT_URI, "mutual_friends/" + uid);
    }

    public static Uri allTablesSilentUri() {
        return Uri.withAppendedPath(CONTENT_URI, "all_tables").buildUpon().appendQueryParameter("silent", "true").build();
    }

    public boolean onCreate() {
        return true;
    }

    @Nullable
    private SQLiteDatabase getDatabaseOrNull() {
        return OdnoklassnikiApplication.getDatabase(getContext());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.database.Cursor query(android.net.Uri r31, java.lang.String[] r32, java.lang.String r33, java.lang.String[] r34, java.lang.String r35) {
        /*
        r30 = this;
        r2 = requestNumber;
        r25 = r2.getAndIncrement();
        r28 = java.lang.System.currentTimeMillis();
        r4 = "(%d): uri: '%s', selection: %s, args: %s";
        r2 = 4;
        r5 = new java.lang.Object[r2];
        r2 = 0;
        r6 = java.lang.Integer.valueOf(r25);
        r5[r2] = r6;
        r2 = 1;
        r5[r2] = r31;
        r2 = 2;
        r5[r2] = r33;
        r6 = 3;
        r2 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r2 == 0) goto L_0x0035;
    L_0x0024:
        r2 = java.util.Arrays.toString(r34);
    L_0x0028:
        r5[r6] = r2;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        r3 = r30.getDatabaseOrNull();
        if (r3 != 0) goto L_0x0037;
    L_0x0033:
        r2 = 0;
    L_0x0034:
        return r2;
    L_0x0035:
        r2 = 0;
        goto L_0x0028;
    L_0x0037:
        r27 = 0;
        r26 = 0;
        r16 = 0;
        r2 = uriMatcher;	 Catch:{ all -> 0x005b }
        r0 = r31;
        r2 = r2.match(r0);	 Catch:{ all -> 0x005b }
        switch(r2) {
            case 1: goto L_0x00a8;
            case 2: goto L_0x007e;
            case 3: goto L_0x00d9;
            case 4: goto L_0x010a;
            case 8: goto L_0x052a;
            case 11: goto L_0x057c;
            case 12: goto L_0x05a5;
            case 13: goto L_0x05d0;
            case 14: goto L_0x05fb;
            case 15: goto L_0x0553;
            case 16: goto L_0x0626;
            case 18: goto L_0x0651;
            case 20: goto L_0x0855;
            case 32: goto L_0x067a;
            case 46: goto L_0x0253;
            case 48: goto L_0x04f9;
            case 50: goto L_0x0313;
            case 52: goto L_0x0375;
            case 57: goto L_0x0160;
            case 58: goto L_0x018b;
            case 73: goto L_0x01c6;
            case 77: goto L_0x01f7;
            case 78: goto L_0x0222;
            case 83: goto L_0x096d;
            case 84: goto L_0x0973;
            case 85: goto L_0x06d4;
            case 86: goto L_0x06a3;
            case 87: goto L_0x0344;
            case 88: goto L_0x02e2;
            case 89: goto L_0x02b1;
            case 90: goto L_0x03a6;
            case 91: goto L_0x03d7;
            case 92: goto L_0x0439;
            case 93: goto L_0x04cc;
            case 94: goto L_0x0408;
            case 96: goto L_0x046a;
            case 97: goto L_0x049b;
            case 99: goto L_0x0284;
            case 104: goto L_0x071d;
            case 105: goto L_0x074f;
            case 116: goto L_0x0781;
            case 117: goto L_0x0785;
            case 147: goto L_0x07b7;
            case 148: goto L_0x07bb;
            case 149: goto L_0x07ed;
            case 150: goto L_0x07f1;
            case 151: goto L_0x0823;
            case 158: goto L_0x0135;
            case 162: goto L_0x0887;
            case 163: goto L_0x088b;
            case 164: goto L_0x08bd;
            case 165: goto L_0x08c1;
            case 166: goto L_0x092e;
            case 167: goto L_0x08f3;
            default: goto L_0x0048;
        };	 Catch:{ all -> 0x005b }
    L_0x0048:
        r2 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x005b }
        r4 = "Can't match '%s' uri";
        r5 = 1;
        r5 = new java.lang.Object[r5];	 Catch:{ all -> 0x005b }
        r6 = 0;
        r5[r6] = r31;	 Catch:{ all -> 0x005b }
        r4 = java.lang.String.format(r4, r5);	 Catch:{ all -> 0x005b }
        r2.<init>(r4);	 Catch:{ all -> 0x005b }
        throw r2;	 Catch:{ all -> 0x005b }
    L_0x005b:
        r2 = move-exception;
        r7 = r26;
        r8 = r27;
    L_0x0060:
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        throw r2;
    L_0x007e:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r1 = r32;
        r2 = ru.ok.android.db.provider.ProviderDiscussionsHelper.queryDiscussionComment(r2, r3, r0, r1);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x00a8:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderDiscussionsHelper.queryDiscussionComments(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x00d9:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryFriends(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x010a:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r1 = r32;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryFriend(r2, r3, r0, r1);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0135:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r1 = r32;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryMutualFriends(r2, r3, r0, r1);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0160:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r1 = r32;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryUser(r2, r3, r0, r1);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x018b:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = "join_conversations";
        r5 = 0;
        r0 = r31;
        r9 = r0.getBooleanQueryParameter(r4, r5);	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryUsers(r2, r3, r4, r5, r6, r7, r8, r9);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x01c6:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryPlayList(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x01f7:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r1 = r32;
        r2 = ru.ok.android.db.provider.ProviderGroupsHelper.queryGroup(r2, r3, r0, r1);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0222:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderGroupsHelper.queryGroups(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0253:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryCollections(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0284:
        r8 = r31.getLastPathSegment();	 Catch:{ all -> 0x005b }
        r2 = r30.getContext();	 Catch:{ all -> 0x0965 }
        r0 = r31;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryMaxPositionCollections(r2, r3, r0, r8);	 Catch:{ all -> 0x0965 }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x02b1:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryPopCollections(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x02e2:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryUserTracks(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0313:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryCollectionTracks(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0344:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryRelationCollectionsUsers(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0375:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryMusicFriends(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x03a6:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryTuners(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x03d7:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryTunersArtists(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0408:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryTunersTracks(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0439:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryHistoryTracks(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x046a:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryPopTracks(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x049b:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryExtensionTracks(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x04cc:
        r8 = r31.getLastPathSegment();	 Catch:{ all -> 0x005b }
        r2 = r30.getContext();	 Catch:{ all -> 0x0965 }
        r0 = r31;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryMaxPositionTracks(r2, r3, r0, r8);	 Catch:{ all -> 0x0965 }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x04f9:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.queryRelationCollectionsUsers(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x052a:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryUserRelations(r2, r3, r0);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0553:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryUserRelationInfo(r2, r3, r0);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x057c:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryCounters(r2, r3, r0);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x05a5:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r1 = r35;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryCommunities(r2, r3, r0, r1);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x05d0:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r1 = r35;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryInterests(r2, r3, r0, r1);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x05fb:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r1 = r35;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryPresents(r2, r3, r0, r1);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0626:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r1 = r35;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.queryUsersStreamSubscribe(r2, r3, r0, r1);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0651:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r2 = ru.ok.android.db.provider.ProviderGroupsHelper.queryGroupsStreamSubscribe(r2, r3, r0);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x067a:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r0 = r31;
        r2 = ru.ok.android.db.provider.ProviderGroupsHelper.queryCounters(r2, r3, r0);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x06a3:
        r2 = r30.getContext();	 Catch:{ all -> 0x005b }
        r4 = r31;
        r5 = r32;
        r6 = r33;
        r7 = r34;
        r8 = r35;
        r2 = ru.ok.android.db.provider.ProviderGroupsHelper.queryGroupsUsersStatus(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x06d4:
        r8 = r31.getLastPathSegment();	 Catch:{ all -> 0x005b }
    L_0x06d8:
        r2 = r31.getPathSegments();	 Catch:{ all -> 0x0965 }
        r4 = 1;
        r2 = r2.get(r4);	 Catch:{ all -> 0x0965 }
        r2 = (java.lang.String) r2;	 Catch:{ all -> 0x0965 }
        r7 = java.lang.Integer.parseInt(r2);	 Catch:{ all -> 0x0965 }
    L_0x06e7:
        r2 = r30.getContext();	 Catch:{ all -> 0x096a }
        r4 = r2.getContentResolver();	 Catch:{ all -> 0x096a }
        r5 = r31;
        r6 = r3;
        r9 = r32;
        r10 = r33;
        r11 = r34;
        r12 = r35;
        r2 = ru.ok.android.db.provider.ProviderUserPrivacySettingsHelper.query(r4, r5, r6, r7, r8, r9, r10, r11, r12);	 Catch:{ all -> 0x096a }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x071d:
        r9 = r30.getContext();	 Catch:{ all -> 0x005b }
        r10 = r3;
        r11 = r31;
        r12 = r32;
        r13 = r33;
        r14 = r34;
        r15 = r35;
        r2 = ru.ok.android.db.provider.ProviderRelativesHelper.query(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x074f:
        r9 = r30.getContext();	 Catch:{ all -> 0x005b }
        r10 = r3;
        r11 = r31;
        r12 = r32;
        r13 = r33;
        r14 = r34;
        r15 = r35;
        r2 = ru.ok.android.db.provider.ProviderFriendsSuggestHelper.query(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0781:
        r16 = r31.getLastPathSegment();	 Catch:{ all -> 0x005b }
    L_0x0785:
        r0 = r30;
        r9 = r0.imageUrlsProviderHelper;	 Catch:{ all -> 0x005b }
        r10 = r3;
        r11 = r31;
        r12 = r32;
        r13 = r33;
        r14 = r34;
        r15 = r35;
        r2 = r9.query(r10, r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x07b7:
        r16 = r31.getLastPathSegment();	 Catch:{ all -> 0x005b }
    L_0x07bb:
        r0 = r30;
        r9 = r0.bannersProviderHelper;	 Catch:{ all -> 0x005b }
        r10 = r3;
        r11 = r31;
        r12 = r32;
        r13 = r33;
        r14 = r34;
        r15 = r35;
        r2 = r9.query(r10, r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x07ed:
        r16 = r31.getLastPathSegment();	 Catch:{ all -> 0x005b }
    L_0x07f1:
        r0 = r30;
        r9 = r0.promoLinksProviderHelper;	 Catch:{ all -> 0x005b }
        r10 = r3;
        r11 = r31;
        r12 = r32;
        r13 = r33;
        r14 = r34;
        r15 = r35;
        r2 = r9.query(r10, r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0823:
        r0 = r30;
        r9 = r0.adStatsProviderHelper;	 Catch:{ all -> 0x005b }
        r10 = r3;
        r11 = r31;
        r12 = r32;
        r13 = r33;
        r14 = r34;
        r15 = r35;
        r2 = r9.query(r10, r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0855:
        r0 = r30;
        r9 = r0.groupMembersProviderHelper;	 Catch:{ all -> 0x005b }
        r10 = r3;
        r11 = r31;
        r12 = r32;
        r13 = r33;
        r14 = r34;
        r15 = r35;
        r2 = r9.query(r10, r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0887:
        r16 = r31.getLastPathSegment();	 Catch:{ all -> 0x005b }
    L_0x088b:
        r0 = r30;
        r9 = r0.videoBannerDataProviderHelper;	 Catch:{ all -> 0x005b }
        r10 = r3;
        r11 = r31;
        r12 = r32;
        r13 = r33;
        r14 = r34;
        r15 = r35;
        r2 = r9.query(r10, r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x08bd:
        r16 = r31.getLastPathSegment();	 Catch:{ all -> 0x005b }
    L_0x08c1:
        r0 = r30;
        r9 = r0.videoStatsProviderHelper;	 Catch:{ all -> 0x005b }
        r10 = r3;
        r11 = r31;
        r12 = r32;
        r13 = r33;
        r14 = r34;
        r15 = r35;
        r2 = r9.query(r10, r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x08f3:
        r8 = r31.getLastPathSegment();	 Catch:{ all -> 0x005b }
        r0 = r30;
        r0 = r0.authorizedUsersProviderHelper;	 Catch:{ all -> 0x0965 }
        r17 = r0;
        r18 = r3;
        r19 = r31;
        r20 = r32;
        r21 = r33;
        r22 = r34;
        r23 = r35;
        r24 = r8;
        r2 = r17.queryByUid(r18, r19, r20, r21, r22, r23, r24);	 Catch:{ all -> 0x0965 }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x092e:
        r0 = r30;
        r0 = r0.authorizedUsersProviderHelper;	 Catch:{ all -> 0x005b }
        r17 = r0;
        r24 = 0;
        r18 = r3;
        r19 = r31;
        r20 = r32;
        r21 = r33;
        r22 = r34;
        r23 = r35;
        r2 = r17.query(r18, r19, r20, r21, r22, r23, r24);	 Catch:{ all -> 0x005b }
        r4 = "(%d): duration: %d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r9 = java.lang.Integer.valueOf(r25);
        r5[r6] = r9;
        r6 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r28;
        r9 = java.lang.Long.valueOf(r10);
        r5[r6] = r9;
        ru.ok.android.utils.Logger.m173d(r4, r5);
        goto L_0x0034;
    L_0x0965:
        r2 = move-exception;
        r7 = r26;
        goto L_0x0060;
    L_0x096a:
        r2 = move-exception;
        goto L_0x0060;
    L_0x096d:
        r7 = r26;
        r8 = r27;
        goto L_0x06e7;
    L_0x0973:
        r8 = r27;
        goto L_0x06d8;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.provider.OdklProvider.query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String):android.database.Cursor");
    }

    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "vnd.android.cursor.dir/discussion_comments";
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return "vnd.android.cursor.item/discussion_comment";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "vnd.android.cursor.dir/friends";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "vnd.android.cursor.item/friend";
            case C0206R.styleable.Toolbar_navigationIcon /*20*/:
                return GroupMembers.CONTENT_TYPE;
            case C0206R.styleable.Theme_dropdownListPreferredItemHeight /*46*/:
                return "vnd.android.cursor.dir/music/collections";
            case C0206R.styleable.Theme_buttonBarStyle /*50*/:
                return "vnd.android.cursor.dir/music/collection_tracks";
            case C0206R.styleable.Theme_selectableItemBackground /*52*/:
                return "vnd.android.cursor.dir/music/friends_music";
            case C0206R.styleable.Theme_activityChooserViewStyle /*57*/:
                return Users.CONTENT_ITEM_TYPE;
            case C0206R.styleable.Theme_toolbarStyle /*58*/:
                return Users.CONTENT_TYPE;
            case C0206R.styleable.Theme_dropDownListViewStyle /*73*/:
                return "vnd.android.cursor.dir/playlist";
            case C0206R.styleable.Theme_listPopupWindowStyle /*74*/:
                return "vnd.android.cursor.dir/tracks";
            case C0206R.styleable.Theme_textAppearanceListItem /*75*/:
                return "vnd.android.cursor.dir/albums";
            case C0206R.styleable.Theme_textAppearanceListItemSmall /*76*/:
                return "vnd.android.cursor.dir/albums";
            case C0206R.styleable.Theme_panelBackground /*77*/:
                return Groups.CONTENT_ITEM_TYPE;
            case C0206R.styleable.Theme_panelMenuListWidth /*78*/:
                return Groups.CONTENT_TYPE;
            case C0206R.styleable.Theme_colorAccent /*83*/:
            case C0206R.styleable.Theme_colorControlNormal /*84*/:
                return UserPrivacySettings.CONTENT_TYPE;
            case C0206R.styleable.Theme_colorControlActivated /*85*/:
                return UserPrivacySettings.CONTENT_ITEM_TYPE;
            case C0206R.styleable.Theme_colorButtonNormal /*87*/:
                return "vnd.android.cursor.dir/music/collections_relations";
            case C0206R.styleable.Theme_colorSwitchThumbNormal /*88*/:
                return "vnd.android.cursor.dir/music/my";
            case C0206R.styleable.Theme_controlBackground /*89*/:
                return "vnd.android.cursor.dir/music/collections_pop";
            case C0206R.styleable.Theme_alertDialogStyle /*90*/:
                return "vnd.android.cursor.dir/music/tuners";
            case C0206R.styleable.Theme_alertDialogButtonGroupStyle /*91*/:
                return "vnd.android.cursor.dir/music/tuners_artists";
            case C0206R.styleable.Theme_alertDialogCenterButtons /*92*/:
                return "vnd.android.cursor.dir/music/history";
            case C0206R.styleable.Theme_alertDialogTheme /*93*/:
                return "vnd.android.cursor.dir/music/my_max_position";
            case C0206R.styleable.Theme_textColorAlertDialogListItem /*94*/:
                return "vnd.android.cursor.dir/music/tuners_tracks";
            case C0206R.styleable.Theme_radioButtonStyle /*104*/:
                return "vnd.android.cursor.dir/relatives";
            case C0206R.styleable.Theme_ratingBarStyle /*105*/:
                return "vnd.android.cursor.dir/friends_suggest";
            case 116:
                return ImageUrls.CONTENT_ITEM_TYPE;
            case 117:
                return ImageUrls.CONTENT_TYPE;
            case 147:
                return Banners.CONTENT_ITEM_TYPE;
            case 148:
                return Banners.CONTENT_TYPE;
            case 149:
                return PromoLinks.CONTENT_ITEM_TYPE;
            case 150:
                return PromoLinks.CONTENT_TYPE;
            case 151:
                return AdStatistics.CONTENT_TYPE;
            case 162:
                return VideoBannerData.CONTENT_ITEM_TYPE;
            case 163:
                return VideoBannerData.CONTENT_TYPE;
            case 164:
                return VideoStats.CONTENT_ITEM_TYPE;
            case 165:
                return VideoStats.CONTENT_TYPE;
            case 166:
                return AuthorizedUsers.CONTENT_TYPE;
            case 167:
                return AuthorizedUsers.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.net.Uri insert(android.net.Uri r17, android.content.ContentValues r18) {
        /*
        r16 = this;
        r10 = requestNumber;
        r3 = r10.getAndIncrement();
        r6 = java.lang.System.currentTimeMillis();
        r10 = "(%d) : uri: '%s'";
        r11 = 2;
        r11 = new java.lang.Object[r11];
        r12 = 0;
        r13 = java.lang.Integer.valueOf(r3);
        r11[r12] = r13;
        r12 = 1;
        r13 = r17.toString();
        r11[r12] = r13;
        ru.ok.android.utils.Logger.m173d(r10, r11);
        r2 = r16.getDatabaseOrNull();
        if (r2 != 0) goto L_0x0029;
    L_0x0027:
        r4 = 0;
    L_0x0028:
        return r4;
    L_0x0029:
        r9 = 0;
        r5 = 0;
        r10 = uriMatcher;
        r0 = r17;
        r10 = r10.match(r0);
        switch(r10) {
            case 1: goto L_0x0049;
            case 4: goto L_0x00a6;
            case 8: goto L_0x00b3;
            case 11: goto L_0x00c3;
            case 15: goto L_0x00ba;
            case 16: goto L_0x012d;
            case 18: goto L_0x0105;
            case 20: goto L_0x019a;
            case 32: goto L_0x00f3;
            case 49: goto L_0x011d;
            case 50: goto L_0x01c0;
            case 57: goto L_0x0125;
            case 58: goto L_0x009f;
            case 73: goto L_0x00e3;
            case 74: goto L_0x00cc;
            case 75: goto L_0x00d3;
            case 76: goto L_0x00db;
            case 78: goto L_0x00fd;
            case 83: goto L_0x0148;
            case 84: goto L_0x0139;
            case 85: goto L_0x0135;
            case 87: goto L_0x010d;
            case 88: goto L_0x0115;
            case 90: goto L_0x00eb;
            case 91: goto L_0x01c8;
            case 92: goto L_0x01a8;
            case 95: goto L_0x01d0;
            case 96: goto L_0x01b8;
            case 97: goto L_0x01b0;
            case 104: goto L_0x0152;
            case 105: goto L_0x015a;
            case 117: goto L_0x0162;
            case 148: goto L_0x0170;
            case 150: goto L_0x017e;
            case 151: goto L_0x018c;
            case 163: goto L_0x01dc;
            case 165: goto L_0x01ea;
            case 166: goto L_0x01f8;
            default: goto L_0x0036;
        };
    L_0x0036:
        r10 = new java.lang.IllegalArgumentException;
        r11 = "Can't match '%s' uri";
        r12 = 1;
        r12 = new java.lang.Object[r12];
        r13 = 0;
        r12[r13] = r17;
        r11 = java.lang.String.format(r11, r12);
        r10.<init>(r11);
        throw r10;
    L_0x0049:
        r10 = r16.getContext();
        r11 = 1;
        r0 = r17;
        r1 = r18;
        r4 = ru.ok.android.db.provider.ProviderDiscussionsHelper.insertComment(r10, r2, r0, r1, r11);
    L_0x0056:
        if (r4 == 0) goto L_0x0082;
    L_0x0058:
        r10 = "silent";
        r0 = r17;
        r10 = r0.getQueryParameter(r10);
        if (r10 != 0) goto L_0x0082;
    L_0x0063:
        r10 = "Notify uri (%d): '%s'";
        r11 = 2;
        r11 = new java.lang.Object[r11];
        r12 = 0;
        r13 = java.lang.Integer.valueOf(r3);
        r11[r12] = r13;
        r12 = 1;
        r11[r12] = r4;
        ru.ok.android.utils.Logger.m173d(r10, r11);
        r10 = r16.getContext();
        r10 = r10.getContentResolver();
        r11 = 0;
        r10.notifyChange(r4, r11);
    L_0x0082:
        r10 = "(%d): duration: %d";
        r11 = 2;
        r11 = new java.lang.Object[r11];
        r12 = 0;
        r13 = java.lang.Integer.valueOf(r3);
        r11[r12] = r13;
        r12 = 1;
        r14 = java.lang.System.currentTimeMillis();
        r14 = r14 - r6;
        r13 = java.lang.Long.valueOf(r14);
        r11[r12] = r13;
        ru.ok.android.utils.Logger.m173d(r10, r11);
        goto L_0x0028;
    L_0x009f:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderUsersHelper.insertUser(r2, r0);
        goto L_0x0056;
    L_0x00a6:
        r10 = r16.getContext();
        r0 = r17;
        r1 = r18;
        r4 = ru.ok.android.db.provider.ProviderUsersHelper.insertFriend(r10, r2, r0, r1);
        goto L_0x0056;
    L_0x00b3:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderUsersHelper.insertUserRelation(r2, r0);
        goto L_0x0056;
    L_0x00ba:
        r0 = r18;
        r1 = r17;
        r4 = ru.ok.android.db.provider.ProviderUsersHelper.insertUserRelationInfo(r2, r0, r1);
        goto L_0x0056;
    L_0x00c3:
        r0 = r17;
        r1 = r18;
        r4 = ru.ok.android.db.provider.ProviderUsersHelper.insertCounters(r2, r0, r1);
        goto L_0x0056;
    L_0x00cc:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertTrack(r2, r0);
        goto L_0x0056;
    L_0x00d3:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertAlbum(r2, r0);
        goto L_0x0056;
    L_0x00db:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertArtist(r2, r0);
        goto L_0x0056;
    L_0x00e3:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertPlayListTrack(r2, r0);
        goto L_0x0056;
    L_0x00eb:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertTuner(r2, r0);
        goto L_0x0056;
    L_0x00f3:
        r0 = r17;
        r1 = r18;
        r4 = ru.ok.android.db.provider.ProviderGroupsHelper.insertCounters(r2, r0, r1);
        goto L_0x0056;
    L_0x00fd:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderGroupsHelper.insertGroup(r2, r0);
        goto L_0x0056;
    L_0x0105:
        r0 = r17;
        r4 = ru.ok.android.db.provider.ProviderGroupsHelper.insertGroupsStreamSubscribe(r2, r0);
        goto L_0x0056;
    L_0x010d:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertCollectionRelation(r2, r0);
        goto L_0x0056;
    L_0x0115:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertUserMusic(r2, r0);
        goto L_0x0056;
    L_0x011d:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertUserMusic(r2, r0);
        goto L_0x0056;
    L_0x0125:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderUsersHelper.insertUser(r2, r0);
        goto L_0x0056;
    L_0x012d:
        r0 = r17;
        r4 = ru.ok.android.db.provider.ProviderUsersHelper.insertUsersStreamSubscribe(r2, r0);
        goto L_0x0056;
    L_0x0135:
        r9 = r17.getLastPathSegment();
    L_0x0139:
        r10 = r17.getPathSegments();
        r11 = 1;
        r10 = r10.get(r11);
        r10 = (java.lang.String) r10;
        r5 = java.lang.Integer.parseInt(r10);
    L_0x0148:
        r0 = r17;
        r1 = r18;
        r4 = ru.ok.android.db.provider.ProviderUserPrivacySettingsHelper.insert(r2, r0, r5, r9, r1);
        goto L_0x0056;
    L_0x0152:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderRelativesHelper.insert(r2, r0);
        goto L_0x0056;
    L_0x015a:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderFriendsSuggestHelper.insert(r2, r0);
        goto L_0x0056;
    L_0x0162:
        r0 = r16;
        r10 = r0.imageUrlsProviderHelper;
        r0 = r17;
        r1 = r18;
        r4 = r10.insert(r2, r0, r1);
        goto L_0x0056;
    L_0x0170:
        r0 = r16;
        r10 = r0.bannersProviderHelper;
        r0 = r17;
        r1 = r18;
        r4 = r10.insert(r2, r0, r1);
        goto L_0x0056;
    L_0x017e:
        r0 = r16;
        r10 = r0.promoLinksProviderHelper;
        r0 = r17;
        r1 = r18;
        r4 = r10.insert(r2, r0, r1);
        goto L_0x0056;
    L_0x018c:
        r0 = r16;
        r10 = r0.adStatsProviderHelper;
        r0 = r17;
        r1 = r18;
        r4 = r10.insert(r2, r0, r1);
        goto L_0x0056;
    L_0x019a:
        r0 = r16;
        r10 = r0.groupMembersProviderHelper;
        r0 = r17;
        r1 = r18;
        r4 = r10.insert(r2, r0, r1);
        goto L_0x0056;
    L_0x01a8:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertHistoryMusic(r2, r0);
        goto L_0x0056;
    L_0x01b0:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertExtensionMusic(r2, r0);
        goto L_0x0056;
    L_0x01b8:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertPopMusic(r2, r0);
        goto L_0x0056;
    L_0x01c0:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertCollectionTrack(r2, r0);
        goto L_0x0056;
    L_0x01c8:
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertTunerArtist(r2, r0);
        goto L_0x0056;
    L_0x01d0:
        r8 = r17.getLastPathSegment();
        r0 = r18;
        r4 = ru.ok.android.db.provider.ProviderMusicHelper.insertTunerTracks(r2, r0, r8);
        goto L_0x0056;
    L_0x01dc:
        r0 = r16;
        r10 = r0.videoBannerDataProviderHelper;
        r0 = r17;
        r1 = r18;
        r4 = r10.insert(r2, r0, r1);
        goto L_0x0056;
    L_0x01ea:
        r0 = r16;
        r10 = r0.videoStatsProviderHelper;
        r0 = r17;
        r1 = r18;
        r4 = r10.insert(r2, r0, r1);
        goto L_0x0056;
    L_0x01f8:
        r0 = r16;
        r10 = r0.authorizedUsersProviderHelper;
        r0 = r17;
        r1 = r18;
        r4 = r10.insert(r2, r0, r1);
        goto L_0x0056;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.provider.OdklProvider.insert(android.net.Uri, android.content.ContentValues):android.net.Uri");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int bulkInsert(android.net.Uri r17, android.content.ContentValues[] r18) {
        /*
        r16 = this;
        r10 = requestNumber;
        r4 = r10.getAndIncrement();
        r6 = java.lang.System.currentTimeMillis();
        r11 = "(%d): uri: '%s', values: %s";
        r10 = 3;
        r12 = new java.lang.Object[r10];
        r10 = 0;
        r13 = java.lang.Integer.valueOf(r4);
        r12[r10] = r13;
        r10 = 1;
        r13 = r17.toString();
        r12[r10] = r13;
        r13 = 2;
        r10 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r10 == 0) goto L_0x0036;
    L_0x0025:
        r10 = java.util.Arrays.toString(r18);
    L_0x0029:
        r12[r13] = r10;
        ru.ok.android.utils.Logger.m173d(r11, r12);
        r3 = r16.getDatabaseOrNull();
        if (r3 != 0) goto L_0x0038;
    L_0x0034:
        r2 = 0;
    L_0x0035:
        return r2;
    L_0x0036:
        r10 = 0;
        goto L_0x0029;
    L_0x0038:
        r9 = 0;
        r5 = 0;
        r10 = uriMatcher;
        r0 = r17;
        r10 = r10.match(r0);
        switch(r10) {
            case 1: goto L_0x0058;
            case 3: goto L_0x00d8;
            case 12: goto L_0x00bd;
            case 13: goto L_0x00c6;
            case 14: goto L_0x00cf;
            case 46: goto L_0x011b;
            case 49: goto L_0x0149;
            case 50: goto L_0x0155;
            case 52: goto L_0x015d;
            case 58: goto L_0x00b6;
            case 73: goto L_0x00eb;
            case 74: goto L_0x00f3;
            case 75: goto L_0x00fb;
            case 76: goto L_0x0103;
            case 78: goto L_0x010b;
            case 83: goto L_0x01ac;
            case 84: goto L_0x019d;
            case 85: goto L_0x0199;
            case 86: goto L_0x0113;
            case 87: goto L_0x0123;
            case 88: goto L_0x0140;
            case 89: goto L_0x0138;
            case 90: goto L_0x0165;
            case 91: goto L_0x016d;
            case 92: goto L_0x0181;
            case 95: goto L_0x0175;
            case 96: goto L_0x0189;
            case 97: goto L_0x0191;
            case 98: goto L_0x012c;
            case 104: goto L_0x01b6;
            case 105: goto L_0x01be;
            case 117: goto L_0x01c6;
            case 148: goto L_0x01d4;
            case 150: goto L_0x01e2;
            case 151: goto L_0x01f0;
            case 158: goto L_0x00df;
            case 163: goto L_0x01fe;
            case 165: goto L_0x020c;
            case 166: goto L_0x021a;
            default: goto L_0x0045;
        };
    L_0x0045:
        r10 = new java.lang.IllegalArgumentException;
        r11 = "Can't match '%s' uri";
        r12 = 1;
        r12 = new java.lang.Object[r12];
        r13 = 0;
        r12[r13] = r17;
        r11 = java.lang.String.format(r11, r12);
        r10.<init>(r11);
        throw r10;
    L_0x0058:
        r10 = r16.getContext();
        r0 = r17;
        r1 = r18;
        r2 = ru.ok.android.db.provider.ProviderDiscussionsHelper.insertComments(r10, r3, r0, r1);
    L_0x0064:
        if (r2 <= 0) goto L_0x0092;
    L_0x0066:
        r10 = "silent";
        r0 = r17;
        r10 = r0.getQueryParameter(r10);
        if (r10 != 0) goto L_0x0092;
    L_0x0071:
        r10 = "Notify uri (%d): '%s'";
        r11 = 2;
        r11 = new java.lang.Object[r11];
        r12 = 0;
        r13 = java.lang.Integer.valueOf(r4);
        r11[r12] = r13;
        r12 = 1;
        r11[r12] = r17;
        ru.ok.android.utils.Logger.m183v(r10, r11);
        r10 = r16.getContext();
        r10 = r10.getContentResolver();
        r11 = 0;
        r0 = r17;
        r10.notifyChange(r0, r11);
    L_0x0092:
        r10 = "(%d): duration: %d, count: %d";
        r11 = 3;
        r11 = new java.lang.Object[r11];
        r12 = 0;
        r13 = java.lang.Integer.valueOf(r4);
        r11[r12] = r13;
        r12 = 1;
        r14 = java.lang.System.currentTimeMillis();
        r14 = r14 - r6;
        r13 = java.lang.Long.valueOf(r14);
        r11[r12] = r13;
        r12 = 2;
        r13 = java.lang.Integer.valueOf(r2);
        r11[r12] = r13;
        ru.ok.android.utils.Logger.m173d(r10, r11);
        goto L_0x0035;
    L_0x00b6:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.insertUsers(r3, r0);
        goto L_0x0064;
    L_0x00bd:
        r0 = r17;
        r1 = r18;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.insertUserCommunities(r3, r0, r1);
        goto L_0x0064;
    L_0x00c6:
        r0 = r17;
        r1 = r18;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.insertUserInterests(r3, r0, r1);
        goto L_0x0064;
    L_0x00cf:
        r0 = r17;
        r1 = r18;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.insertUserPresents(r3, r0, r1);
        goto L_0x0064;
    L_0x00d8:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.insertFriends(r3, r0);
        goto L_0x0064;
    L_0x00df:
        r9 = r17.getLastPathSegment();
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderUsersHelper.insertMutualFriends(r3, r0, r9);
        goto L_0x0064;
    L_0x00eb:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertPlayList(r3, r0);
        goto L_0x0064;
    L_0x00f3:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertTracks(r3, r0);
        goto L_0x0064;
    L_0x00fb:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertAlbums(r3, r0);
        goto L_0x0064;
    L_0x0103:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertArtists(r3, r0);
        goto L_0x0064;
    L_0x010b:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderGroupsHelper.insertGroups(r3, r0);
        goto L_0x0064;
    L_0x0113:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderGroupsHelper.insertGroupUserStatus(r3, r0);
        goto L_0x0064;
    L_0x011b:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertCollections(r3, r0);
        goto L_0x0064;
    L_0x0123:
        r10 = 0;
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertCollectionRelations(r3, r0, r10);
        goto L_0x0064;
    L_0x012c:
        r9 = r17.getLastPathSegment();
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertCollectionRelations(r3, r0, r9);
        goto L_0x0064;
    L_0x0138:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertPopCollectionRelations(r3, r0);
        goto L_0x0064;
    L_0x0140:
        r10 = 0;
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertUserTracks(r3, r0, r10);
        goto L_0x0064;
    L_0x0149:
        r9 = r17.getLastPathSegment();
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertUserTracks(r3, r0, r9);
        goto L_0x0064;
    L_0x0155:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertCollectionTracks(r3, r0);
        goto L_0x0064;
    L_0x015d:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertMusicFriends(r3, r0);
        goto L_0x0064;
    L_0x0165:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertMusicTuners(r3, r0);
        goto L_0x0064;
    L_0x016d:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertTunersArtists(r3, r0);
        goto L_0x0064;
    L_0x0175:
        r8 = r17.getLastPathSegment();
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertTunerTracks(r3, r0, r8);
        goto L_0x0064;
    L_0x0181:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertHistoryMusic(r3, r0);
        goto L_0x0064;
    L_0x0189:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertPopMusic(r3, r0);
        goto L_0x0064;
    L_0x0191:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderMusicHelper.insertExtensionMusic(r3, r0);
        goto L_0x0064;
    L_0x0199:
        r9 = r17.getLastPathSegment();
    L_0x019d:
        r10 = r17.getPathSegments();
        r11 = 1;
        r10 = r10.get(r11);
        r10 = (java.lang.String) r10;
        r5 = java.lang.Integer.parseInt(r10);
    L_0x01ac:
        r0 = r17;
        r1 = r18;
        r2 = ru.ok.android.db.provider.ProviderUserPrivacySettingsHelper.bulkInsert(r3, r0, r5, r9, r1);
        goto L_0x0064;
    L_0x01b6:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderRelativesHelper.bulkInsert(r3, r0);
        goto L_0x0064;
    L_0x01be:
        r0 = r18;
        r2 = ru.ok.android.db.provider.ProviderFriendsSuggestHelper.bulkInsert(r3, r0);
        goto L_0x0064;
    L_0x01c6:
        r0 = r16;
        r10 = r0.imageUrlsProviderHelper;
        r0 = r17;
        r1 = r18;
        r2 = r10.bulkInsert(r3, r0, r1);
        goto L_0x0064;
    L_0x01d4:
        r0 = r16;
        r10 = r0.bannersProviderHelper;
        r0 = r17;
        r1 = r18;
        r2 = r10.bulkInsert(r3, r0, r1);
        goto L_0x0064;
    L_0x01e2:
        r0 = r16;
        r10 = r0.promoLinksProviderHelper;
        r0 = r17;
        r1 = r18;
        r2 = r10.bulkInsert(r3, r0, r1);
        goto L_0x0064;
    L_0x01f0:
        r0 = r16;
        r10 = r0.adStatsProviderHelper;
        r0 = r17;
        r1 = r18;
        r2 = r10.bulkInsert(r3, r0, r1);
        goto L_0x0064;
    L_0x01fe:
        r0 = r16;
        r10 = r0.videoBannerDataProviderHelper;
        r0 = r17;
        r1 = r18;
        r2 = r10.bulkInsert(r3, r0, r1);
        goto L_0x0064;
    L_0x020c:
        r0 = r16;
        r10 = r0.videoStatsProviderHelper;
        r0 = r17;
        r1 = r18;
        r2 = r10.bulkInsert(r3, r0, r1);
        goto L_0x0064;
    L_0x021a:
        r0 = r16;
        r10 = r0.authorizedUsersProviderHelper;
        r0 = r17;
        r1 = r18;
        r2 = r10.bulkInsert(r3, r0, r1);
        goto L_0x0064;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.provider.OdklProvider.bulkInsert(android.net.Uri, android.content.ContentValues[]):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int delete(android.net.Uri r29, java.lang.String r30, java.lang.String[] r31) {
        /*
        r28 = this;
        r4 = requestNumber;
        r23 = r4.getAndIncrement();
        r26 = java.lang.System.currentTimeMillis();
        r6 = "(%d): uri: '%s', '%s', '%s'";
        r4 = 4;
        r7 = new java.lang.Object[r4];
        r4 = 0;
        r8 = java.lang.Integer.valueOf(r23);
        r7[r4] = r8;
        r4 = 1;
        r7[r4] = r29;
        r4 = 2;
        r7[r4] = r30;
        r8 = 3;
        r4 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r4 == 0) goto L_0x0036;
    L_0x0024:
        r4 = java.util.Arrays.toString(r31);
    L_0x0028:
        r7[r8] = r4;
        ru.ok.android.utils.Logger.m173d(r6, r7);
        r5 = r28.getDatabaseOrNull();
        if (r5 != 0) goto L_0x0038;
    L_0x0033:
        r22 = 0;
    L_0x0035:
        return r22;
    L_0x0036:
        r4 = 0;
        goto L_0x0028;
    L_0x0038:
        ru.ok.android.db.SQLiteUtils.beginTransaction(r5);
        r15 = 0;
        r24 = 0;
        r9 = 0;
        r4 = uriMatcher;	 Catch:{ all -> 0x005d }
        r0 = r29;
        r4 = r4.match(r0);	 Catch:{ all -> 0x005d }
        switch(r4) {
            case 1: goto L_0x0062;
            case 2: goto L_0x00c4;
            case 3: goto L_0x00d4;
            case 4: goto L_0x0184;
            case 8: goto L_0x017c;
            case 16: goto L_0x00dd;
            case 17: goto L_0x00dd;
            case 18: goto L_0x0104;
            case 19: goto L_0x0104;
            case 20: goto L_0x0219;
            case 46: goto L_0x0118;
            case 49: goto L_0x0190;
            case 52: goto L_0x012c;
            case 58: goto L_0x00cb;
            case 73: goto L_0x00fa;
            case 74: goto L_0x010e;
            case 75: goto L_0x00dd;
            case 76: goto L_0x00e6;
            case 78: goto L_0x00f0;
            case 83: goto L_0x01ad;
            case 84: goto L_0x019e;
            case 85: goto L_0x019a;
            case 87: goto L_0x015e;
            case 88: goto L_0x0122;
            case 89: goto L_0x0154;
            case 90: goto L_0x0136;
            case 91: goto L_0x0140;
            case 92: goto L_0x0168;
            case 94: goto L_0x014a;
            case 96: goto L_0x0233;
            case 97: goto L_0x0172;
            case 104: goto L_0x01b9;
            case 105: goto L_0x01c3;
            case 116: goto L_0x01cd;
            case 117: goto L_0x01d1;
            case 147: goto L_0x01e1;
            case 148: goto L_0x01e5;
            case 149: goto L_0x01f5;
            case 150: goto L_0x01f9;
            case 151: goto L_0x0209;
            case 159: goto L_0x0229;
            case 162: goto L_0x023d;
            case 163: goto L_0x0241;
            case 164: goto L_0x0251;
            case 165: goto L_0x0255;
            case 166: goto L_0x027a;
            case 167: goto L_0x0265;
            default: goto L_0x004a;
        };	 Catch:{ all -> 0x005d }
    L_0x004a:
        r4 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x005d }
        r6 = "Can't match '%s' uri";
        r7 = 1;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x005d }
        r8 = 0;
        r7[r8] = r29;	 Catch:{ all -> 0x005d }
        r6 = java.lang.String.format(r6, r7);	 Catch:{ all -> 0x005d }
        r4.<init>(r6);	 Catch:{ all -> 0x005d }
        throw r4;	 Catch:{ all -> 0x005d }
    L_0x005d:
        r4 = move-exception;
        r5.endTransaction();
        throw r4;
    L_0x0062:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderDiscussionsHelper.deleteDiscussionComments(r5, r0, r1);	 Catch:{ all -> 0x005d }
    L_0x006a:
        r5.setTransactionSuccessful();	 Catch:{ all -> 0x005d }
        r5.endTransaction();
        if (r22 <= 0) goto L_0x009e;
    L_0x0072:
        r4 = "silent";
        r0 = r29;
        r4 = r0.getQueryParameter(r4);
        if (r4 != 0) goto L_0x009e;
    L_0x007d:
        r4 = "Notify uri (%d): '%s'";
        r6 = 2;
        r6 = new java.lang.Object[r6];
        r7 = 0;
        r8 = java.lang.Integer.valueOf(r23);
        r6[r7] = r8;
        r7 = 1;
        r6[r7] = r29;
        ru.ok.android.utils.Logger.m183v(r4, r6);
        r4 = r28.getContext();
        r4 = r4.getContentResolver();
        r6 = 0;
        r0 = r29;
        r4.notifyChange(r0, r6);
    L_0x009e:
        r4 = "(%d): duration: %d, count: %d";
        r6 = 3;
        r6 = new java.lang.Object[r6];
        r7 = 0;
        r8 = java.lang.Integer.valueOf(r23);
        r6[r7] = r8;
        r7 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r26;
        r8 = java.lang.Long.valueOf(r10);
        r6[r7] = r8;
        r7 = 2;
        r8 = java.lang.Integer.valueOf(r22);
        r6[r7] = r8;
        ru.ok.android.utils.Logger.m173d(r4, r6);
        goto L_0x0035;
    L_0x00c4:
        r0 = r29;
        r22 = ru.ok.android.db.provider.ProviderDiscussionsHelper.deleteDiscussionComment(r5, r0);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x00cb:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderUsersHelper.deleteUsers(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x00d4:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderUsersHelper.deleteFriends(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x00dd:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteAlbums(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x00e6:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteArtists(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x00f0:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderGroupsHelper.deleteGroups(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x00fa:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deletePlayList(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0104:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderGroupsHelper.deleteGroupStreamSubscribe(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x010e:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteTracks(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0118:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteCollections(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0122:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteUserTracks(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x012c:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteFriendsInfo(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0136:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteTuners(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0140:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteTunersToArtist(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x014a:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteTunerTracks(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0154:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deletePopCollections(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x015e:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteUser2Collections(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0168:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteHistoryMusic(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0172:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteExtensionMusic(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x017c:
        r0 = r29;
        r22 = ru.ok.android.db.provider.ProviderUsersHelper.deleteUserRelation(r5, r0);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0184:
        r4 = r28.getContext();	 Catch:{ all -> 0x005d }
        r0 = r29;
        r22 = ru.ok.android.db.provider.ProviderUsersHelper.deleteFriend(r4, r5, r0);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0190:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deleteUserTracks(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x019a:
        r15 = r29.getLastPathSegment();	 Catch:{ all -> 0x005d }
    L_0x019e:
        r4 = r29.getPathSegments();	 Catch:{ all -> 0x005d }
        r6 = 1;
        r4 = r4.get(r6);	 Catch:{ all -> 0x005d }
        r4 = (java.lang.String) r4;	 Catch:{ all -> 0x005d }
        r24 = java.lang.Integer.parseInt(r4);	 Catch:{ all -> 0x005d }
    L_0x01ad:
        r0 = r24;
        r1 = r30;
        r2 = r31;
        r22 = ru.ok.android.db.provider.ProviderUserPrivacySettingsHelper.delete(r5, r0, r15, r1, r2);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x01b9:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderRelativesHelper.delete(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x01c3:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderFriendsSuggestHelper.delete(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x01cd:
        r9 = r29.getLastPathSegment();	 Catch:{ all -> 0x005d }
    L_0x01d1:
        r0 = r28;
        r4 = r0.imageUrlsProviderHelper;	 Catch:{ all -> 0x005d }
        r6 = r29;
        r7 = r30;
        r8 = r31;
        r22 = r4.delete(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x01e1:
        r9 = r29.getLastPathSegment();	 Catch:{ all -> 0x005d }
    L_0x01e5:
        r0 = r28;
        r4 = r0.bannersProviderHelper;	 Catch:{ all -> 0x005d }
        r6 = r29;
        r7 = r30;
        r8 = r31;
        r22 = r4.delete(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x01f5:
        r9 = r29.getLastPathSegment();	 Catch:{ all -> 0x005d }
    L_0x01f9:
        r0 = r28;
        r4 = r0.promoLinksProviderHelper;	 Catch:{ all -> 0x005d }
        r6 = r29;
        r7 = r30;
        r8 = r31;
        r22 = r4.delete(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0209:
        r0 = r28;
        r4 = r0.adStatsProviderHelper;	 Catch:{ all -> 0x005d }
        r6 = r29;
        r7 = r30;
        r8 = r31;
        r22 = r4.delete(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0219:
        r0 = r28;
        r4 = r0.groupMembersProviderHelper;	 Catch:{ all -> 0x005d }
        r6 = r29;
        r7 = r30;
        r8 = r31;
        r22 = r4.delete(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0229:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderAllTablesHelper.delete(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0233:
        r0 = r30;
        r1 = r31;
        r22 = ru.ok.android.db.provider.ProviderMusicHelper.deletePopMusic(r5, r0, r1);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x023d:
        r9 = r29.getLastPathSegment();	 Catch:{ all -> 0x005d }
    L_0x0241:
        r0 = r28;
        r4 = r0.videoBannerDataProviderHelper;	 Catch:{ all -> 0x005d }
        r6 = r29;
        r7 = r30;
        r8 = r31;
        r22 = r4.delete(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0251:
        r9 = r29.getLastPathSegment();	 Catch:{ all -> 0x005d }
    L_0x0255:
        r0 = r28;
        r4 = r0.videoStatsProviderHelper;	 Catch:{ all -> 0x005d }
        r6 = r29;
        r7 = r30;
        r8 = r31;
        r22 = r4.delete(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x0265:
        r15 = r29.getLastPathSegment();	 Catch:{ all -> 0x005d }
        r0 = r28;
        r10 = r0.authorizedUsersProviderHelper;	 Catch:{ all -> 0x005d }
        r11 = r5;
        r12 = r29;
        r13 = r30;
        r14 = r31;
        r22 = r10.deleteByUid(r11, r12, r13, r14, r15);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
    L_0x027a:
        r0 = r28;
        r0 = r0.authorizedUsersProviderHelper;	 Catch:{ all -> 0x005d }
        r16 = r0;
        r21 = 0;
        r17 = r5;
        r18 = r29;
        r19 = r30;
        r20 = r31;
        r22 = r16.delete(r17, r18, r19, r20, r21);	 Catch:{ all -> 0x005d }
        goto L_0x006a;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.provider.OdklProvider.delete(android.net.Uri, java.lang.String, java.lang.String[]):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int update(android.net.Uri r27, android.content.ContentValues r28, java.lang.String r29, java.lang.String[] r30) {
        /*
        r26 = this;
        r7 = requestNumber;
        r22 = r7.getAndIncrement();
        r24 = java.lang.System.currentTimeMillis();
        r8 = "(%d): uri: '%s', cv: %s, selection: %s";
        r7 = 4;
        r9 = new java.lang.Object[r7];
        r7 = 0;
        r10 = java.lang.Integer.valueOf(r22);
        r9[r7] = r10;
        r7 = 1;
        r10 = r27.toString();
        r9[r7] = r10;
        r7 = 2;
        r9[r7] = r28;
        r10 = 3;
        r7 = ru.ok.android.utils.Logger.isLoggingEnable();
        if (r7 == 0) goto L_0x003a;
    L_0x0028:
        r7 = java.util.Arrays.toString(r30);
    L_0x002c:
        r9[r10] = r7;
        ru.ok.android.utils.Logger.m173d(r8, r9);
        r4 = r26.getDatabaseOrNull();
        if (r4 != 0) goto L_0x003c;
    L_0x0037:
        r21 = 0;
    L_0x0039:
        return r21;
    L_0x003a:
        r7 = 0;
        goto L_0x002c;
    L_0x003c:
        r6 = 0;
        r5 = 0;
        r13 = 0;
        r7 = uriMatcher;
        r0 = r27;
        r7 = r7.match(r0);
        switch(r7) {
            case 1: goto L_0x00b8;
            case 2: goto L_0x005d;
            case 3: goto L_0x012c;
            case 15: goto L_0x0102;
            case 57: goto L_0x00f8;
            case 83: goto L_0x00ec;
            case 84: goto L_0x00dd;
            case 85: goto L_0x00d9;
            case 86: goto L_0x0124;
            case 88: goto L_0x00c3;
            case 94: goto L_0x00ce;
            case 104: goto L_0x010c;
            case 105: goto L_0x0118;
            case 116: goto L_0x0138;
            case 117: goto L_0x013c;
            case 147: goto L_0x014f;
            case 148: goto L_0x0153;
            case 149: goto L_0x0166;
            case 150: goto L_0x016a;
            case 162: goto L_0x01ab;
            case 163: goto L_0x01af;
            case 164: goto L_0x01c2;
            case 165: goto L_0x01c6;
            case 166: goto L_0x0196;
            case 167: goto L_0x017d;
            default: goto L_0x004a;
        };
    L_0x004a:
        r7 = new java.lang.IllegalArgumentException;
        r8 = "Can't match '%s' uri";
        r9 = 1;
        r9 = new java.lang.Object[r9];
        r10 = 0;
        r9[r10] = r27;
        r8 = java.lang.String.format(r8, r9);
        r7.<init>(r8);
        throw r7;
    L_0x005d:
        r0 = r27;
        r1 = r28;
        r21 = ru.ok.android.db.provider.ProviderDiscussionsHelper.updateComment(r4, r0, r1);
    L_0x0065:
        if (r21 <= 0) goto L_0x0093;
    L_0x0067:
        r7 = "silent";
        r0 = r27;
        r7 = r0.getQueryParameter(r7);
        if (r7 != 0) goto L_0x0093;
    L_0x0072:
        r7 = "Notify uri (%d): '%s'";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r22);
        r8[r9] = r10;
        r9 = 1;
        r8[r9] = r27;
        ru.ok.android.utils.Logger.m183v(r7, r8);
        r7 = r26.getContext();
        r7 = r7.getContentResolver();
        r8 = 0;
        r0 = r27;
        r7.notifyChange(r0, r8);
    L_0x0093:
        r7 = "(%d): duration: %d, count: %d";
        r8 = 3;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r22);
        r8[r9] = r10;
        r9 = 1;
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r24;
        r10 = java.lang.Long.valueOf(r10);
        r8[r9] = r10;
        r9 = 2;
        r10 = java.lang.Integer.valueOf(r21);
        r8[r9] = r10;
        ru.ok.android.utils.Logger.m173d(r7, r8);
        goto L_0x0039;
    L_0x00b8:
        r0 = r28;
        r1 = r29;
        r2 = r30;
        r21 = ru.ok.android.db.provider.ProviderDiscussionsHelper.updateComments(r4, r0, r1, r2);
        goto L_0x0065;
    L_0x00c3:
        r0 = r28;
        r1 = r29;
        r2 = r30;
        r21 = ru.ok.android.db.provider.ProviderMusicHelper.updateUserTracks(r4, r0, r1, r2);
        goto L_0x0065;
    L_0x00ce:
        r0 = r28;
        r1 = r29;
        r2 = r30;
        r21 = ru.ok.android.db.provider.ProviderMusicHelper.updateTunerTracks(r4, r0, r1, r2);
        goto L_0x0065;
    L_0x00d9:
        r6 = r27.getLastPathSegment();
    L_0x00dd:
        r7 = r27.getPathSegments();
        r8 = 1;
        r7 = r7.get(r8);
        r7 = (java.lang.String) r7;
        r5 = java.lang.Integer.parseInt(r7);
    L_0x00ec:
        r7 = r29;
        r8 = r30;
        r9 = r28;
        r21 = ru.ok.android.db.provider.ProviderUserPrivacySettingsHelper.update(r4, r5, r6, r7, r8, r9);
        goto L_0x0065;
    L_0x00f8:
        r0 = r27;
        r1 = r28;
        r21 = ru.ok.android.db.provider.ProviderUsersHelper.updateUser(r4, r0, r1);
        goto L_0x0065;
    L_0x0102:
        r0 = r27;
        r1 = r28;
        r21 = ru.ok.android.db.provider.ProviderUsersHelper.updateUserRelationInfo(r4, r0, r1);
        goto L_0x0065;
    L_0x010c:
        r0 = r28;
        r1 = r29;
        r2 = r30;
        r21 = ru.ok.android.db.provider.ProviderRelativesHelper.update(r4, r0, r1, r2);
        goto L_0x0065;
    L_0x0118:
        r0 = r28;
        r1 = r29;
        r2 = r30;
        r21 = ru.ok.android.db.provider.ProviderFriendsSuggestHelper.update(r4, r0, r1, r2);
        goto L_0x0065;
    L_0x0124:
        r0 = r28;
        r21 = ru.ok.android.db.provider.ProviderGroupsHelper.updateGroupUserStatus(r4, r0);
        goto L_0x0065;
    L_0x012c:
        r0 = r29;
        r1 = r30;
        r2 = r28;
        r21 = ru.ok.android.db.provider.ProviderUsersHelper.updateFriends(r4, r0, r1, r2);
        goto L_0x0065;
    L_0x0138:
        r13 = r27.getLastPathSegment();
    L_0x013c:
        r0 = r26;
        r7 = r0.imageUrlsProviderHelper;
        r8 = r4;
        r9 = r27;
        r10 = r28;
        r11 = r29;
        r12 = r30;
        r21 = r7.update(r8, r9, r10, r11, r12, r13);
        goto L_0x0065;
    L_0x014f:
        r13 = r27.getLastPathSegment();
    L_0x0153:
        r0 = r26;
        r7 = r0.bannersProviderHelper;
        r8 = r4;
        r9 = r27;
        r10 = r28;
        r11 = r29;
        r12 = r30;
        r21 = r7.update(r8, r9, r10, r11, r12, r13);
        goto L_0x0065;
    L_0x0166:
        r13 = r27.getLastPathSegment();
    L_0x016a:
        r0 = r26;
        r7 = r0.promoLinksProviderHelper;
        r8 = r4;
        r9 = r27;
        r10 = r28;
        r11 = r29;
        r12 = r30;
        r21 = r7.update(r8, r9, r10, r11, r12, r13);
        goto L_0x0065;
    L_0x017d:
        r6 = r27.getLastPathSegment();
        r0 = r26;
        r14 = r0.authorizedUsersProviderHelper;
        r15 = r4;
        r16 = r27;
        r17 = r28;
        r18 = r29;
        r19 = r30;
        r20 = r6;
        r21 = r14.updateByUid(r15, r16, r17, r18, r19, r20);
        goto L_0x0065;
    L_0x0196:
        r0 = r26;
        r14 = r0.authorizedUsersProviderHelper;
        r20 = 0;
        r15 = r4;
        r16 = r27;
        r17 = r28;
        r18 = r29;
        r19 = r30;
        r21 = r14.update(r15, r16, r17, r18, r19, r20);
        goto L_0x0065;
    L_0x01ab:
        r13 = r27.getLastPathSegment();
    L_0x01af:
        r0 = r26;
        r7 = r0.videoBannerDataProviderHelper;
        r8 = r4;
        r9 = r27;
        r10 = r28;
        r11 = r29;
        r12 = r30;
        r21 = r7.update(r8, r9, r10, r11, r12, r13);
        goto L_0x0065;
    L_0x01c2:
        r13 = r27.getLastPathSegment();
    L_0x01c6:
        r0 = r26;
        r7 = r0.videoStatsProviderHelper;
        r8 = r4;
        r9 = r27;
        r10 = r28;
        r11 = r29;
        r12 = r30;
        r21 = r7.update(r8, r9, r10, r11, r12, r13);
        goto L_0x0065;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.provider.OdklProvider.update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[]):int");
    }

    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        int request = requestNumber.getAndIncrement();
        long startTime = System.currentTimeMillis();
        Logger.m173d("(%d)", Integer.valueOf(request));
        int numOperations = operations.size();
        ContentProviderResult[] results = new ContentProviderResult[numOperations];
        SQLiteDatabase db = getDatabaseOrNull();
        int i;
        if (db == null) {
            for (i = 0; i < numOperations; i++) {
                results[i] = new ContentProviderResult(0);
            }
        } else {
            SQLiteUtils.beginTransaction(db);
            for (i = 0; i < numOperations; i++) {
                try {
                    results[i] = ((ContentProviderOperation) operations.get(i)).apply(this, results, i);
                } catch (Throwable e) {
                    Logger.m178e(e);
                } catch (Throwable th) {
                    db.endTransaction();
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        Logger.m173d("(%d): duration: %d", Integer.valueOf(request), Long.valueOf(System.currentTimeMillis() - startTime));
        return results;
    }
}
