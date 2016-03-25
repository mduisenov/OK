package ru.ok.android.db.access;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.SQLiteUtils;
import ru.ok.android.db.access.QueriesUsers.FriendInsert;
import ru.ok.android.db.access.QueriesUsers.FriendsDelete;
import ru.ok.android.db.access.QueriesUsers.FriendsLastUpdate;
import ru.ok.android.db.access.QueriesUsers.QueryById;
import ru.ok.android.db.access.QueriesUsers.QueryByIds;
import ru.ok.android.db.access.QueriesUsers.QueryFriend;
import ru.ok.android.db.access.QueriesUsers.RelationsDelete;
import ru.ok.android.db.access.QueriesUsers.RelationsInsert;
import ru.ok.android.db.access.QueriesUsers.Update4Conversations;
import ru.ok.android.db.access.QueriesUsers.Update4Messages;
import ru.ok.android.db.access.QueriesUsers.UpdateOnline;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.db.provider.OdklContract.GroupMembers;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.ui.users.fragments.data.UserMergedPresent;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.filter.TranslateNormalizer;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.java.api.request.users.FriendRelativeType;
import ru.ok.java.api.response.users.FriendRelation;
import ru.ok.java.api.response.users.UserCounters;
import ru.ok.java.api.response.users.UserPresent;
import ru.ok.java.api.response.users.UserPresentsResponse;
import ru.ok.java.api.response.users.UserRelationInfoResponse;
import ru.ok.java.api.response.users.UserSentPresent;
import ru.ok.java.api.utils.DateUtils;
import ru.ok.model.Relative;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.Builder;
import ru.ok.model.UserInfo.Location;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;
import ru.ok.model.UserStatus;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.presents.AnimationProperties;

public final class UsersStorageFacade {
    private static final String[] PROJECTION_COUNT;
    public static final String[] PROJECTION_FRIENDS;
    private static final String[] PROJECTION_USERS;
    private static final String[] PROJ_RELATIONS;

    static {
        PROJECTION_COUNT = new String[]{"COUNT(*)"};
        PROJECTION_FRIENDS = new String[]{"user_id", "user_name", "user_first_name", "user_last_name", "user_avatar_url", "user_gender", "user_online", "user_last_online", "user_can_call", "can_vmail", "_id", "private", "show_lock", "big_pic_url"};
        PROJ_RELATIONS = new String[]{"uid", "type", "subtype"};
        PROJECTION_USERS = new String[]{"user_id", "user_first_name", "user_last_name", "user_name", "user_avatar_url", "user_gender", "user_last_online", "user_online", "user_can_call", "can_vmail", "location_code", "location_city", "location_country", "age", "photo_id", "big_pic_url", "private", "premium", "invisible", "status_id", "status_text", "status_date", "status_track_id", "birthday", "is_all_info_available", "show_lock"};
    }

    public static boolean isUserFriend(String userId) {
        boolean z;
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.friendUri(userId), PROJECTION_COUNT, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                    z = true;
                    IOUtils.closeSilently(cursor);
                    return z;
                }
            } catch (Throwable th) {
                IOUtils.closeSilently(cursor);
            }
        }
        z = false;
        IOUtils.closeSilently(cursor);
        return z;
    }

    public static List<UserInfo> queryFriendsInGroup(String groupId) {
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(GroupMembers.getContentUri(), null, "gm_group_id = ?", new String[]{groupId}, null);
        List<UserInfo> result = new ArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    result.add(cursor2User(cursor));
                } finally {
                    cursor.close();
                }
            }
        }
        return result;
    }

    public static void insertUsers(Collection<? extends UserInfo> users, UserInfoValuesFiller filler) {
        ContentValues[] cvs = new ContentValues[users.size()];
        int i = 0;
        for (UserInfo user : users) {
            int i2 = i + 1;
            cvs[i] = convertUserIntoCV(user, filler);
            i = i2;
        }
        OdnoklassnikiApplication.getContext().getContentResolver().bulkInsert(Users.getContentUri(), cvs);
    }

    public static ContentValues convertUserIntoCV(UserInfo userInfo, UserInfoValuesFiller filler) {
        ContentValues cv = new ContentValues();
        filler.fillValues(cv, userInfo);
        return cv;
    }

    public static void insertUserRelations(String userId, FriendRelation relation) throws Exception {
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        operations.add(ContentProviderOperation.newDelete(OdklProvider.userRelationsUri(userId)).build());
        if (relation != null) {
            operations.add(ContentProviderOperation.newInsert(OdklProvider.userRelationsUri(userId)).withValue("user1", userId).withValue("user2", relation.userId).withValue(Message.ELEMENT, relation.message).withValue("relation_type", relation.relationType.name()).build());
        }
        OdnoklassnikiApplication.getContext().getContentResolver().applyBatch(OdklProvider.AUTHORITY, operations);
    }

    public static void syncSubscribeUserStreamRelations(ArrayList<Pair<String, Boolean>> subscribeList) throws Exception {
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        Iterator i$ = subscribeList.iterator();
        while (i$.hasNext()) {
            Pair<String, Boolean> elem = (Pair) i$.next();
            ContentValues cv = new ContentValues();
            cv.put("USER_ID", (String) elem.first);
            if (((Boolean) elem.second).booleanValue()) {
                operations.add(ContentProviderOperation.newInsert(OdklProvider.userStreamSubscribeUri((String) elem.first)).withValues(cv).build());
            } else {
                operations.add(ContentProviderOperation.newDelete(OdklProvider.userStreamSubscribeUri()).build());
            }
        }
        OdnoklassnikiApplication.getContext().getContentResolver().applyBatch(OdklProvider.AUTHORITY, operations);
    }

    public static Map<FriendRelativeType, List<FriendRelation>> queryUserRelations(String userId) {
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.userRelationsUri(userId), null, null, null, null);
        Map<FriendRelativeType, List<FriendRelation>> result = new HashMap();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    FriendRelativeType type = FriendRelativeType.safeValueOf(cursor.getString(cursor.getColumnIndex("relation_type")));
                    List<FriendRelation> relations = (List) result.get(type);
                    if (relations == null) {
                        relations = new ArrayList();
                        result.put(type, relations);
                    }
                    relations.add(new FriendRelation(cursor.getString(cursor.getColumnIndex("user2")), type, cursor.getString(cursor.getColumnIndex(Message.ELEMENT))));
                } finally {
                    cursor.close();
                }
            }
        }
        return result;
    }

    public static Cursor queryUsers(Set<String> ids, String[] projection) {
        return OdnoklassnikiApplication.getContext().getContentResolver().query(Users.getContentUri(), projection, "user_id IN ('" + TextUtils.join("','", ids) + "')", null, null);
    }

    @Deprecated
    public static List<UserInfo> queryFriends() {
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.friendsUri(), PROJECTION_FRIENDS, null, null, "user_n_first_name, user_n_last_name");
        if (cursor == null) {
            return Collections.emptyList();
        }
        List<UserInfo> result = new ArrayList();
        while (cursor.moveToNext()) {
            try {
                String uid = cursor.getString(0);
                String name = cursor.getString(1);
                result.add(new UserInfo(uid, cursor.getString(2), cursor.getString(3), name, cursor.getString(4), null, null, null, 0, null, UserOnlineType.safeValueOf(cursor.getString(6)), cursor.getLong(7), UserGenderType.byInteger(cursor.getInt(5)), cursor.getInt(8) > 0, cursor.getInt(9) > 0, null, null, cursor.getString(13), cursor.getInt(11) != 0, false, false, null, null, false, cursor.getInt(12) != 0));
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    public static void fillUserRelations(@NonNull Map<RelativesType, Set<String>> relationsMap, @NonNull Map<String, Set<RelativesType>> subRelationsMap) {
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.relativesUri(), PROJ_RELATIONS, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String uid = cursor.getString(0);
                    RelativesType relationType = RelativesType.valueOf(cursor.getString(1));
                    String subtypeStr = cursor.getString(2);
                    RelativesType relativeSubtype = null;
                    if (!TextUtils.isEmpty(subtypeStr)) {
                        relativeSubtype = RelativesType.valueOf(subtypeStr);
                    }
                    if (relationType == RelativesType.SPOUSE) {
                        relativeSubtype = RelativesType.SPOUSE;
                        relationType = RelativesType.RELATIVE;
                    }
                    Set<String> userIds = (Set) relationsMap.get(relationType);
                    if (userIds == null) {
                        userIds = new HashSet();
                        relationsMap.put(relationType, userIds);
                    }
                    userIds.add(uid);
                    if (relativeSubtype != null) {
                        Set<RelativesType> subTypes = (Set) subRelationsMap.get(uid);
                        if (subTypes == null) {
                            subTypes = new HashSet();
                            subRelationsMap.put(uid, subTypes);
                        }
                        subTypes.add(relativeSubtype);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
    }

    public static UserInfo queryUser(String userId) {
        UserInfo userInfo = null;
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(Users.getUri(userId), null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    userInfo = cursor2User(cursor);
                } else {
                    cursor.close();
                }
            } finally {
                cursor.close();
            }
        }
        return userInfo;
    }

    private static void removeExistingUsersFromList(List<? extends UserInfo> users) {
        UserInfo user;
        Set userIds = new HashSet();
        Map<String, UserInfo> usersMap = new HashMap();
        for (UserInfo user2 : users) {
            userIds.add(user2.uid);
            usersMap.put(user2.uid, user2);
        }
        Cursor cursor = queryUsers(userIds, new String[]{"user_id", "user_first_name", "user_last_name", "user_name", "user_avatar_url", "user_online", "user_last_online"});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String userId = cursor.getString(0);
                    user2 = (UserInfo) usersMap.get(userId);
                    if (user2 != null && doesColumnEquals(cursor, 1, user2.firstName) && doesColumnEquals(cursor, 2, user2.lastName) && doesColumnEquals(cursor, 3, user2.name) && doesColumnEquals(cursor, 4, user2.picUrl)) {
                        if (doesColumnEquals(cursor, 5, user2.online != null ? user2.online.name() : null) && doesColumnEquals(cursor, 6, String.valueOf(user2.lastOnline))) {
                            users.remove(user2);
                            usersMap.remove(userId);
                        }
                    }
                } finally {
                    IOUtils.closeSilently(cursor);
                }
            }
        }
    }

    private static boolean doesColumnEquals(Cursor cursor, int columnIndex, String columnValue) {
        return cursor != null && TextUtils.equals(cursor.getString(columnIndex), columnValue);
    }

    public static void updateUserCounters(String userId, UserCounters counters) {
        ContentValues cv = new ContentValues();
        cv.put("photos_personal", Integer.valueOf(counters.photosPersonal));
        cv.put("photos_in_photo_albums", Integer.valueOf(counters.photosInPhotoAlbums));
        cv.put("photo_albums", Integer.valueOf(counters.photoAlbums));
        cv.put("presents", Integer.valueOf(counters.presents));
        cv.put("friends", Integer.valueOf(counters.friends));
        cv.put("groups", Integer.valueOf(counters.groups));
        cv.put("communities", Integer.valueOf(counters.communities));
        cv.put("schools", Integer.valueOf(counters.schools));
        cv.put("statuses", Integer.valueOf(counters.statuses));
        cv.put("applications", Integer.valueOf(counters.applications));
        cv.put("happenings", Integer.valueOf(counters.happenings));
        cv.put("holidays", Integer.valueOf(counters.holidays));
        OdnoklassnikiApplication.getContext().getContentResolver().insert(OdklProvider.userCountersUri(userId), cv);
    }

    public static UserInfo cursor2User(Cursor cursor) {
        if (cursor == null) {
            return new UserInfo(null, null, null, null, null, null, null, null, 0, null, UserOnlineType.OFFLINE, 0, UserGenderType.MALE, false, false, "", null, null, false, false, false, null, null, false, false);
        }
        String id = cursor.getString(cursor.getColumnIndex("user_id"));
        String firstName = getStringSafe(cursor, "user_first_name");
        String lastName = getStringSafe(cursor, "user_last_name");
        String name = getStringSafe(cursor, "user_name");
        long lastOnline = getLongSafe(cursor, "user_last_online");
        String picUrl = getStringSafe(cursor, "user_avatar_url");
        UserOnlineType onlineType = UserOnlineType.safeValueOf(getStringSafe(cursor, "user_online"));
        UserGenderType gender = UserGenderType.byInteger(getIntSafe(cursor, "user_gender"));
        boolean canCall = getIntSafe(cursor, "user_can_call") > 0;
        boolean canVMail = getIntSafe(cursor, "can_vmail") > 0;
        int age = getIntSafe(cursor, "age", -1);
        Location location = new Location(getStringSafe(cursor, "location_code"), getStringSafe(cursor, "location_country"), getStringSafe(cursor, "location_city"));
        String pid = getStringSafe(cursor, "photo_id");
        String bigPic = getStringSafe(cursor, "big_pic_url");
        boolean isPrivate = getIntSafe(cursor, "private") > 0;
        boolean isPremium = getIntSafe(cursor, "premium") > 0;
        boolean isShowLock = getIntSafe(cursor, "show_lock") > 0;
        boolean hasServiceInvisible = getIntSafe(cursor, "invisible") > 0;
        UserStatus status = null;
        String statusId = getStringSafe(cursor, "status_id");
        if (!TextUtils.isEmpty(statusId)) {
            status = new UserStatus(statusId, getStringSafe(cursor, "status_text"), getLongSafe(cursor, "status_date"), getLongSafe(cursor, "status_track_id"));
        }
        Date birthday = null;
        String birthdayStr = getStringSafe(cursor, "birthday");
        if (!TextUtils.isEmpty(birthdayStr)) {
            try {
                birthday = DateUtils.getBirthdayFormat().parse(birthdayStr);
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
        return new UserInfo(id, firstName, lastName, name, picUrl, null, null, null, age, location, onlineType, lastOnline, gender, canCall, canVMail, "", pid, bigPic, isPrivate, isPremium, hasServiceInvisible, status, birthday, getIntSafe(cursor, "is_all_info_available") > 0, isShowLock);
    }

    private static String getStringSafe(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getString(index) : null;
    }

    private static long getLongSafe(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getLong(index) : 0;
    }

    private static int getIntSafe(Cursor cursor, String columnName) {
        return getIntSafe(cursor, columnName, 0);
    }

    private static int getIntSafe(Cursor cursor, String columnName, int defValue) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getInt(index) : defValue;
    }

    public static UserCounters cursor2Counters(Cursor cursor) {
        return new UserCounters(cursor.getInt(cursor.getColumnIndex("photos_personal")), cursor.getInt(cursor.getColumnIndex("photos_in_photo_albums")), cursor.getInt(cursor.getColumnIndex("photo_albums")), cursor.getInt(cursor.getColumnIndex("presents")), cursor.getInt(cursor.getColumnIndex("friends")), cursor.getInt(cursor.getColumnIndex("groups")), cursor.getInt(cursor.getColumnIndex("communities")), cursor.getInt(cursor.getColumnIndex("schools")), cursor.getInt(cursor.getColumnIndex("statuses")), cursor.getInt(cursor.getColumnIndex("applications")), cursor.getInt(cursor.getColumnIndex("happenings")), cursor.getInt(cursor.getColumnIndex("friends_online")), cursor.getInt(cursor.getColumnIndex("holidays")));
    }

    public static void insertOrRewriteFriends(List<? extends UserInfo> users, boolean rewrite, UserInfoValuesFiller filler) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        ContentResolver resolver = OdnoklassnikiApplication.getContext().getContentResolver();
        if (rewrite) {
            operations.add(ContentProviderOperation.newDelete(OdklProvider.friendsUri().buildUpon().appendQueryParameter("silent", "").build()).build());
        }
        for (UserInfo user : users) {
            operations.add(ContentProviderOperation.newInsert(OdklProvider.friendUri(user.uid).buildUpon().appendQueryParameter("silent", "").build()).build());
        }
        removeExistingUsersFromList(users);
        for (UserInfo user2 : users) {
            operations.add(ContentProviderOperation.newInsert(Users.getUri(user2.uid).buildUpon().appendQueryParameter("silent", "").build()).withValues(convertUserIntoCV(user2, filler)).build());
        }
        resolver.applyBatch(OdklProvider.AUTHORITY, operations);
        Logger.m173d("Notify uri: '%s'", Users.getContentUri());
        resolver.notifyChange(Users.getContentUri(), null);
        Logger.m173d("Notify uri: '%s'", OdklProvider.friendsUri());
        resolver.notifyChange(OdklProvider.friendsUri(), null);
    }

    public static void rewriteRelatives(SQLiteDatabase db, List<UserInfo> users, List<Relative> relatives) {
        if (users != null && !users.isEmpty()) {
            SQLiteStatement delete = DBStatementsFactory.getStatement(db, RelationsDelete.QUERY);
            for (UserInfo user : users) {
                delete.bindString(1, user.uid);
                delete.execute();
            }
            if (relatives != null && !relatives.isEmpty()) {
                SQLiteStatement insert = DBStatementsFactory.getStatement(db, RelationsInsert.QUERY);
                for (Relative relative : relatives) {
                    if (relative.uids == null || relative.uids.length != 1) {
                        Logger.m184w("Realtive has non-single array of uids");
                    } else {
                        insert.bindString(1, relative.uids[0]);
                        SQLiteUtils.safeBindString(insert, 2, relative.typeId);
                        SQLiteUtils.safeBindString(insert, 3, relative.subtypeId);
                        insert.execute();
                    }
                }
            }
        }
    }

    public static void insertFriend(String userId) {
        OdnoklassnikiApplication.getContext().getContentResolver().insert(OdklProvider.friendUri(userId), null);
    }

    public static void deleteFriend(String userId) {
        OdnoklassnikiApplication.getContext().getContentResolver().delete(OdklProvider.friendUri(userId), null, null);
    }

    public static Map<String, UserInfo> queryRelationalUsers(Map<FriendRelativeType, List<FriendRelation>> relations) {
        Set userIds = new HashSet();
        for (List<FriendRelation> relationsList : relations.values()) {
            for (FriendRelation relation : relationsList) {
                userIds.add(relation.userId);
            }
        }
        Map<String, UserInfo> result = new HashMap();
        Cursor cursor = queryUsers(userIds, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    UserInfo userInfo = cursor2User(cursor);
                    result.put(userInfo.uid, userInfo);
                } finally {
                    cursor.close();
                }
            }
        }
        return result;
    }

    public static void insertUserPresents(String userId, UserPresentsResponse presents) {
        ContentValues[] cvs = new ContentValues[presents.sentPresents.size()];
        for (int i = 0; i < cvs.length; i++) {
            ContentValues cv = new ContentValues();
            UserSentPresent present = (UserSentPresent) presents.sentPresents.get(i);
            if (!TextUtils.isEmpty(present.senderRef)) {
                cv.put("SENDER_ID", present.senderRef.split(":")[1]);
            }
            UserPresent userPresent = findPresentByRef(present.presentRef, presents.presents);
            if (userPresent != null) {
                cv.put("PRESENT_ID", Long.valueOf(userPresent.id));
                cv.put("PICTURE", findPresentPicture(present.presentRef, presents.presents));
                cv.put("IS_BIG", Boolean.valueOf(findPresentIsBig(present.presentRef, presents.presents)));
                cv.put("TRACK_ID", Long.valueOf(present.trackId));
                cv.put("IS_ANIMATED", Boolean.valueOf(userPresent.isAnimated));
                if (userPresent.isAnimated) {
                    cv.put("SPRITE", userPresent.sprite.getUrl());
                    cv.put("SPRITE_SIZE", Integer.valueOf(userPresent.sprite.getWidth()));
                    cv.put("ANIMATION_DURATION", Integer.valueOf(userPresent.animationProperties.duration));
                    cv.put("ANIMATION_FRAMES_COUNT", Integer.valueOf(userPresent.animationProperties.framesCount));
                    cv.put("ANIMATION_REPLAY_DELAY", Integer.valueOf(userPresent.animationProperties.replayDelay));
                }
            }
            cvs[i] = cv;
        }
        OdnoklassnikiApplication.getContext().getContentResolver().bulkInsert(OdklProvider.userPresentsUri(userId), cvs);
    }

    private static String findPresentPicture(String presentRef, List<UserPresent> presents) {
        for (UserPresent present : presents) {
            if (TextUtils.equals(presentRef, present.presentRef)) {
                return present.picture;
            }
        }
        return null;
    }

    private static boolean findPresentIsBig(String presentRef, List<UserPresent> presents) {
        for (UserPresent present : presents) {
            if (TextUtils.equals(presentRef, present.presentRef)) {
                return present.isBig;
            }
        }
        return false;
    }

    private static UserPresent findPresentByRef(String presentRef, List<UserPresent> presents) {
        for (UserPresent present : presents) {
            if (TextUtils.equals(presentRef, present.presentRef)) {
                return present;
            }
        }
        return null;
    }

    public static List<UserMergedPresent> queryPresents(String userId) {
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.userPresentsUri(userId), null, null, null, null);
        List<UserMergedPresent> result = new ArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex("PRESENT_ID"));
                String senderId = cursor.getString(cursor.getColumnIndex("SENDER_ID"));
                String picture = cursor.getString(cursor.getColumnIndex("PICTURE"));
                boolean isBig = cursor.getInt(cursor.getColumnIndex("IS_BIG")) != 0;
                long trackId = cursor.getLong(cursor.getColumnIndex("TRACK_ID"));
                boolean isAnimated = cursor.getInt(cursor.getColumnIndex("IS_ANIMATED")) != 0;
                AnimationProperties animationProperties = null;
                PhotoSize photoSize = null;
                if (isAnimated) {
                    int spriteSize = cursor.getInt(cursor.getColumnIndex("SPRITE_SIZE"));
                    String sprite = cursor.getString(cursor.getColumnIndex("SPRITE"));
                    animationProperties = new AnimationProperties(cursor.getInt(cursor.getColumnIndex("ANIMATION_FRAMES_COUNT")), cursor.getInt(cursor.getColumnIndex("ANIMATION_DURATION")), cursor.getInt(cursor.getColumnIndex("ANIMATION_REPLAY_DELAY")));
                    photoSize = new PhotoSize(sprite, spriteSize);
                }
                if (TextUtils.isEmpty(picture)) {
                    try {
                        Logger.m185w("Present has no picture: id=%s", id);
                    } finally {
                        cursor.close();
                    }
                } else {
                    result.add(new UserMergedPresent(id, senderId, picture, isBig, trackId, isAnimated, photoSize, animationProperties));
                }
            }
        }
        return result;
    }

    public static List<UserInfo> queryMutualFriends(String userId) {
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.mutualFriendsUri(userId), null, null, null, null);
        List<UserInfo> result = new ArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    result.add(cursor2User(cursor));
                } finally {
                    cursor.close();
                }
            }
        }
        return result;
    }

    public static void insertUserRelationInfo(String userId, UserRelationInfoResponse relationInfo) {
        int i;
        int i2 = 1;
        ContentValues cv = new ContentValues();
        cv.put("friend_invitation_sent", Integer.valueOf(relationInfo.isFriendInvitationSent ? 1 : 0));
        String str = "can_send_message";
        if (relationInfo.canSendMessage) {
            i = 1;
        } else {
            i = 0;
        }
        cv.put(str, Integer.valueOf(i));
        str = "is_block";
        if (relationInfo.isBlocks) {
            i = 1;
        } else {
            i = 0;
        }
        cv.put(str, Integer.valueOf(i));
        str = "can_group_invite";
        if (relationInfo.canGroupsInvite) {
            i = 1;
        } else {
            i = 0;
        }
        cv.put(str, Integer.valueOf(i));
        String str2 = "can_friend_invite";
        if (!relationInfo.canFriendInvite) {
            i2 = 0;
        }
        cv.put(str2, Integer.valueOf(i2));
        OdnoklassnikiApplication.getContext().getContentResolver().insert(OdklProvider.userRelationInfoUri(userId), cv);
        if (relationInfo.isFriend) {
            insertFriend(userId);
        } else {
            deleteFriend(userId);
        }
    }

    public static void insertUserMutualFriends(String userId, ArrayList<UserInfo> mutualFriends) {
        if (mutualFriends.size() != 0) {
            ContentValues[] cvs = new ContentValues[mutualFriends.size()];
            for (int i = 0; i < mutualFriends.size(); i++) {
                UserInfo user = (UserInfo) mutualFriends.get(i);
                ContentValues cv = new ContentValues();
                cv.put("base_user_id", userId);
                cv.put("friend_id", user.uid);
                cvs[i] = cv;
            }
            OdnoklassnikiApplication.getContext().getContentResolver().bulkInsert(OdklProvider.mutualFriendsUri(userId), cvs);
        }
    }

    public static UserRelationInfoResponse queryUserRelationInfo(String userId) {
        UserRelationInfoResponse userRelationInfoResponse;
        boolean canFriendInvite = true;
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.userRelationInfoUri(userId), null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    boolean isFriendInvitationSent;
                    boolean canSendMessage;
                    boolean isBlock;
                    boolean canGroupInvite;
                    if (cursor.getInt(cursor.getColumnIndex("friend_invitation_sent")) > 0) {
                        isFriendInvitationSent = true;
                    } else {
                        isFriendInvitationSent = false;
                    }
                    if (cursor.getInt(cursor.getColumnIndex("can_send_message")) > 0) {
                        canSendMessage = true;
                    } else {
                        canSendMessage = false;
                    }
                    if (cursor.getInt(cursor.getColumnIndex("is_block")) > 0) {
                        isBlock = true;
                    } else {
                        isBlock = false;
                    }
                    if (cursor.getInt(cursor.getColumnIndex("can_group_invite")) > 0) {
                        canGroupInvite = true;
                    } else {
                        canGroupInvite = false;
                    }
                    if (cursor.getInt(cursor.getColumnIndex("can_friend_invite")) <= 0) {
                        canFriendInvite = false;
                    }
                    userRelationInfoResponse = new UserRelationInfoResponse(userId, false, isFriendInvitationSent, isBlock, canGroupInvite, canSendMessage, canFriendInvite);
                    if (cursor != null) {
                        cursor.close();
                    }
                    return userRelationInfoResponse;
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        userRelationInfoResponse = new UserRelationInfoResponse(userId, true, false, false, false, false, false);
        if (cursor != null) {
            cursor.close();
        }
        return userRelationInfoResponse;
    }

    public static void updateRelationInfoInvitation(String userId) {
        ContentValues cv = new ContentValues();
        cv.put("friend_invitation_sent", Integer.valueOf(1));
        OdnoklassnikiApplication.getContext().getContentResolver().update(OdklProvider.userRelationInfoUri(userId), cv, null, null);
    }

    public static void updateUsers(SQLiteDatabase db, List<UserInfo> users) {
        SQLiteStatement update = DBStatementsFactory.getStatement(db, Update4Conversations.QUERY_UPDATE);
        for (UserInfo user : users) {
            bindValues4UserInfo(update, user);
            if (update.executeUpdateDelete() <= 0) {
                SQLiteStatement insert = DBStatementsFactory.getStatement(db, Update4Conversations.QUERY_INSERT);
                bindValues4UserInfo(insert, user);
                insert.execute();
            }
        }
    }

    private static void bindValues4UserInfo(SQLiteStatement statement, UserInfo user) {
        long toInteger;
        long j = 1;
        SQLiteUtils.safeBindString(statement, 1, user.firstName);
        SQLiteUtils.safeBindString(statement, 2, user.lastName);
        SQLiteUtils.safeBindString(statement, 9, TranslateNormalizer.normalizeText4Sorting(user.firstName));
        SQLiteUtils.safeBindString(statement, 10, TranslateNormalizer.normalizeText4Sorting(user.lastName));
        SQLiteUtils.safeBindString(statement, 3, user.name);
        if (user.genderType != null) {
            toInteger = (long) user.genderType.toInteger();
        } else {
            toInteger = 0;
        }
        statement.bindLong(4, toInteger);
        SQLiteUtils.safeBindString(statement, 5, user.picUrl);
        if (Utils.userCanCall(user)) {
            toInteger = 1;
        } else {
            toInteger = 0;
        }
        statement.bindLong(6, toInteger);
        if (Utils.canSendVideoMailTo(user)) {
            toInteger = 1;
        } else {
            toInteger = 0;
        }
        statement.bindLong(7, toInteger);
        if (user.showLock) {
            toInteger = 1;
        } else {
            toInteger = 0;
        }
        statement.bindLong(11, toInteger);
        if (!user.privateProfile) {
            j = 0;
        }
        statement.bindLong(12, j);
        statement.bindLong(13, user.lastOnline);
        UserOnlineType online = user.online;
        SQLiteUtils.safeBindString(statement, 8, online != null ? online.name() : null);
        statement.bindString(14, user.uid);
    }

    public static void updateFriendsOnline(SQLiteDatabase db, Collection<UserInfo> users) {
        DBStatementsFactory.getStatement(db, UpdateOnline.QUERY_RESET).execute();
        SQLiteStatement insert = null;
        SQLiteStatement queryFriend = null;
        SQLiteStatement insertFriend = null;
        SQLiteStatement update = DBStatementsFactory.getStatement(db, UpdateOnline.QUERY_UPDATE);
        for (UserInfo user : users) {
            update.bindLong(1, user.getAvailableCall() ? 1 : 0);
            update.bindLong(2, user.getAvailableVMail() ? 1 : 0);
            update.bindLong(4, user.lastOnline);
            update.bindLong(4, user.lastOnline);
            SQLiteUtils.safeBindString(update, 3, user.online != null ? user.online.name() : null);
            update.bindString(5, user.uid);
            if (update.executeUpdateDelete() <= 0) {
                if (insert == null) {
                    insert = DBStatementsFactory.getStatement(db, UpdateOnline.QUERY_INSERT);
                    queryFriend = DBStatementsFactory.getStatement(db, QueryFriend.QUERY);
                }
                insert.bindLong(1, user.getAvailableCall() ? 1 : 0);
                insert.bindLong(2, user.getAvailableVMail() ? 1 : 0);
                insert.bindLong(4, user.lastOnline);
                SQLiteUtils.safeBindString(insert, 3, user.online != null ? user.online.name() : null);
                insert.bindString(5, user.uid);
                SQLiteUtils.safeBindString(insert, 7, user.getAnyName());
                SQLiteUtils.safeBindString(insert, 6, user.picUrl);
                insert.execute();
                queryFriend.bindString(1, user.uid);
                if (queryFriend.simpleQueryForLong() <= 0) {
                    if (insertFriend == null) {
                        insertFriend = DBStatementsFactory.getStatement(db, FriendInsert.QUERY);
                    }
                    insertFriend.bindString(1, user.uid);
                    insertFriend.execute();
                }
            }
        }
    }

    public static void updateUsersOnline(SQLiteDatabase db, Collection<UserInfo> users) {
        SQLiteStatement update = DBStatementsFactory.getStatement(db, UpdateOnline.QUERY_UPDATE);
        for (UserInfo user : users) {
            long j;
            update.bindLong(1, user.getAvailableCall() ? 1 : 0);
            if (user.getAvailableVMail()) {
                j = 1;
            } else {
                j = 0;
            }
            update.bindLong(2, j);
            update.bindLong(4, user.lastOnline);
            SQLiteUtils.safeBindString(update, 3, user.online != null ? user.online.name() : null);
            update.bindString(5, user.uid);
            update.execute();
        }
    }

    public static void updateUsersForMessage(SQLiteDatabase db, List<UserInfo> users) {
        SQLiteStatement update = DBStatementsFactory.getStatement(db, Update4Messages.QUERY_UPDATE);
        for (UserInfo user : users) {
            bind4Messages(update, user);
            if (update.executeUpdateDelete() <= 0) {
                SQLiteStatement insert = DBStatementsFactory.getStatement(db, Update4Messages.QUERY_INSERT);
                bind4Messages(insert, user);
                insert.execute();
            }
        }
    }

    private static void bind4Messages(SQLiteStatement statement, UserInfo user) {
        UserOnlineType online = user.online;
        SQLiteUtils.safeBindString(statement, 1, user.firstName);
        SQLiteUtils.safeBindString(statement, 2, user.lastName);
        SQLiteUtils.safeBindString(statement, 8, TranslateNormalizer.normalizeText4Sorting(user.firstName));
        SQLiteUtils.safeBindString(statement, 9, TranslateNormalizer.normalizeText4Sorting(user.lastName));
        SQLiteUtils.safeBindString(statement, 3, user.name);
        statement.bindLong(4, user.lastOnline);
        SQLiteUtils.safeBindString(statement, 5, online != null ? online.name() : null);
        SQLiteUtils.safeBindString(statement, 6, user.picUrl);
        statement.bindLong(7, user.genderType != null ? (long) user.genderType.toInteger() : 0);
        statement.bindString(10, user.uid);
    }

    public static void updateFriends(SQLiteDatabase db, @Nullable List<String> friendIds) {
        DBStatementsFactory.getStatement(db, FriendsDelete.QUERY).execute();
        if (friendIds != null && !friendIds.isEmpty()) {
            SQLiteStatement insertFriend = DBStatementsFactory.getStatement(db, FriendInsert.QUERY);
            for (String friendId : friendIds) {
                insertFriend.bindString(1, friendId);
                insertFriend.execute();
            }
        }
    }

    public static void updateFriendsLastUpdate(SQLiteDatabase db) {
        long time = System.currentTimeMillis();
        SQLiteStatement statement = DBStatementsFactory.getStatement(db, FriendsLastUpdate.QUERY_USERS);
        statement.bindLong(1, time);
        statement.execute();
        statement = DBStatementsFactory.getStatement(db, FriendsLastUpdate.QUERY_FRIENDS);
        statement.bindLong(1, time);
        statement.execute();
    }

    public static UserInfo queryUser(SQLiteDatabase db, String userId) {
        Cursor cursor = db.rawQuery(QueryById.QUERY, new String[]{userId});
        try {
            if (cursor.moveToFirst()) {
                UserInfo build = fillBuilderByCursor(userId, cursor, new Builder()).build();
                return build;
            }
            cursor.close();
            return null;
        } finally {
            cursor.close();
        }
    }

    @NonNull
    private static Builder fillBuilderByCursor(String userId, Cursor cursor, Builder builder) {
        String firstName = cursor.getString(0);
        String lastName = cursor.getString(1);
        String name = cursor.getString(2);
        String picUrl = cursor.getString(3);
        builder.setUid(userId).setFirstName(firstName).setLastName(lastName).setName(name).setPicUrl(picUrl).setBigPicUrl(cursor.getString(4)).setOnline(UserOnlineType.safeValueOf(cursor.getString(5)));
        return builder;
    }

    public static List<UserInfo> queryUsers(SQLiteDatabase db, Collection<String> userIds) {
        Cursor cursor = db.rawQuery(String.format(QueryByIds.QUERY, new Object[]{TextUtils.join("','", userIds)}), null);
        try {
            List<UserInfo> result = new ArrayList();
            Builder builder = new Builder();
            while (cursor.moveToNext()) {
                result.add(fillBuilderByCursor(cursor.getString(6), cursor, builder).build());
            }
            return result;
        } finally {
            cursor.close();
        }
    }
}
