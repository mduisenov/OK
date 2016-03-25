package ru.ok.android.db.access;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.fillers.GroupInfoValueFiller;
import ru.ok.android.db.provider.OdklContract;
import ru.ok.android.db.provider.OdklContract.GroupMembers;
import ru.ok.android.db.provider.OdklContract.Groups;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.response.groups.GroupCounters;
import ru.ok.model.Address;
import ru.ok.model.GroupInfo;
import ru.ok.model.GroupSubCategory;
import ru.ok.model.GroupType;
import ru.ok.model.GroupUserStatus;
import ru.ok.model.GroupUserStatusInfo;
import ru.ok.model.Location;

public class GroupsStorageFacade {
    private static final String[] PROJECTION_GROUP_INFO;

    public static void insertGroups(List<GroupInfo> groups, GroupInfoValueFiller filter) {
        ContentValues[] contentValues = new ContentValues[groups.size()];
        for (int i = 0; i < contentValues.length; i++) {
            contentValues[i] = convertGroupIntoCV((GroupInfo) groups.get(i), filter);
        }
        OdnoklassnikiApplication.getContext().getContentResolver().bulkInsert(Groups.getContentUri(), contentValues);
    }

    public static void insertGroupUsersStatus(List<GroupUserStatusInfo> groupsInfoList) {
        ContentValues[] contentValues = new ContentValues[groupsInfoList.size()];
        for (int i = 0; i < contentValues.length; i++) {
            contentValues[i] = convertGroupUserStatusCV((GroupUserStatusInfo) groupsInfoList.get(i));
        }
        OdnoklassnikiApplication.getContext().getContentResolver().bulkInsert(OdklProvider.groupUsersStatusUri(), contentValues);
    }

    public static final ContentValues convertGroupIntoCV(GroupInfo groupInfo, GroupInfoValueFiller filter) {
        ContentValues result = new ContentValues();
        filter.fillValues(result, groupInfo);
        return result;
    }

    public static ContentValues convertGroupUserStatusCV(GroupUserStatusInfo groupStatusInfo) {
        ContentValues result = new ContentValues();
        result.put("user_id", groupStatusInfo.uid);
        result.put("group_id", groupStatusInfo.groupId);
        result.put(NotificationCompat.CATEGORY_STATUS, groupStatusInfo.status.getStrValue());
        return result;
    }

    public static void updateGroupCounters(String groupId, GroupCounters counters) {
        ContentValues cv = new ContentValues();
        cv.put("themes", Integer.valueOf(counters.themes));
        cv.put("photo_albums", Integer.valueOf(counters.photoAlbums));
        cv.put("members", Integer.valueOf(counters.members));
        cv.put("videos", Integer.valueOf(counters.videos));
        cv.put("news", Integer.valueOf(counters.news));
        cv.put("links", Integer.valueOf(counters.links));
        cv.put("presents", Integer.valueOf(counters.presents));
        cv.put("black_list", Integer.valueOf(counters.black_list));
        cv.put(DeliveryReceiptRequest.ELEMENT, Integer.valueOf(counters.requests));
        OdnoklassnikiApplication.getContext().getContentResolver().insert(OdklProvider.groupCountersUri(groupId), cv);
    }

    public static void updateStatusToGroup(String userId, String groupId, GroupUserStatus status) {
        ContentValues cv = new ContentValues();
        cv.put(NotificationCompat.CATEGORY_STATUS, status.getStrValue());
        cv.put("group_id", groupId);
        cv.put("user_id", userId);
        OdnoklassnikiApplication.getContext().getContentResolver().update(OdklProvider.groupUsersStatusUri(), cv, null, null);
    }

    public static GroupCounters cursor2Counters(Cursor cursor) {
        return new GroupCounters(cursor.getInt(cursor.getColumnIndex("themes")), cursor.getInt(cursor.getColumnIndex("photo_albums")), cursor.getInt(cursor.getColumnIndex("members")), cursor.getInt(cursor.getColumnIndex("videos")), cursor.getInt(cursor.getColumnIndex("news")), cursor.getInt(cursor.getColumnIndex("links")), cursor.getInt(cursor.getColumnIndex("presents")), cursor.getInt(cursor.getColumnIndex(DeliveryReceiptRequest.ELEMENT)), cursor.getInt(cursor.getColumnIndex("black_list")));
    }

    @Nullable
    public static GroupCounters queryGroupCounters(@NonNull String groupId) {
        GroupCounters groupCounters = null;
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.groupCountersUri(groupId), null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    groupCounters = cursor2Counters(cursor);
                } else {
                    cursor.close();
                }
            } finally {
                cursor.close();
            }
        }
        return groupCounters;
    }

    public static void syncSubscribeGroupStreamRelations(ArrayList<Pair<String, Boolean>> subscribeList) throws Exception {
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        Iterator i$ = subscribeList.iterator();
        while (i$.hasNext()) {
            Pair<String, Boolean> elem = (Pair) i$.next();
            ContentValues cv = new ContentValues();
            cv.put("GROUP_ID", (String) elem.first);
            if (((Boolean) elem.second).booleanValue()) {
                operations.add(ContentProviderOperation.newInsert(OdklProvider.groupStreamSubscribeUri((String) elem.first)).withValues(cv).build());
            } else {
                operations.add(ContentProviderOperation.newDelete(OdklProvider.groupStreamSubscribeUri()).build());
            }
        }
        OdnoklassnikiApplication.getContext().getContentResolver().applyBatch(OdklProvider.AUTHORITY, operations);
    }

    public static GroupInfo queryGroup(ContentResolver cr, String gid) {
        Cursor cursor = null;
        GroupInfo fromCursor;
        try {
            cursor = cr.query(Groups.getUri(gid), PROJECTION_GROUP_INFO, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                IOUtils.closeSilently(cursor);
                return null;
            }
            fromCursor = fromCursor(cursor);
            return fromCursor;
        } catch (Throwable e) {
            fromCursor = "Failed to query group";
            Logger.m179e(e, (String) fromCursor);
        } finally {
            IOUtils.closeSilently(cursor);
        }
    }

    private static GroupInfo fromCursor(Cursor cursor) {
        GroupInfo group = new GroupInfo();
        group.setId(cursor.getString(0));
        group.setName(cursor.getString(1));
        group.setDescription(cursor.getString(2));
        group.setMembersCount(cursor.getInt(4));
        String picUrl = cursor.getString(3);
        if (!TextUtils.isEmpty(picUrl)) {
            group.setAvatarUrl(picUrl);
        }
        group.setFlags(cursor.getInt(5));
        group.setPhotoId(cursor.getString(7));
        group.setBigPicUrl(cursor.getString(6));
        if (cursor.isNull(8)) {
            group.setType(GroupType.OTHER);
        } else {
            group.setType(GroupType.fromCategoryId(cursor.getInt(8)));
        }
        group.setAdminUid(cursor.getString(9));
        group.setCreatedMs(cursor.getLong(10));
        if (!cursor.isNull(14)) {
            String city;
            String address = cursor.getString(14);
            if (cursor.isNull(13)) {
                city = null;
            } else {
                city = cursor.getString(13);
            }
            group.setAddress(new Address(null, null, city, address, null, null));
        }
        if (!(cursor.isNull(11) || cursor.isNull(12))) {
            group.setLocation(new Location(Double.valueOf(cursor.getDouble(11)), Double.valueOf(cursor.getDouble(12))));
        }
        if (!cursor.isNull(15)) {
            group.setScope(cursor.getString(15));
        }
        group.setStartDate(cursor.getLong(16));
        group.setEndDate(cursor.getLong(17));
        group.setWebUrl(cursor.getString(18));
        group.setPhone(cursor.getString(19));
        group.setBusiness(cursor.getInt(20) > 0);
        String subcategoryId = cursor.getString(21);
        if (!TextUtils.isEmpty(subcategoryId)) {
            GroupSubCategory subCategory = new GroupSubCategory(subcategoryId);
            subCategory.setName(cursor.getString(22));
            group.setSubCategory(subCategory);
        }
        group.setAllDataAvailable(cursor.getInt(23) > 0);
        group.setStatus(cursor.getString(24));
        return group;
    }

    static {
        PROJECTION_GROUP_INFO = new String[]{"g_id", "g_name", "g_descr", "g_avatar_url", "g_mmbr_cnt", "g_flags", "g_big_photo_url", "g_photo_id", "g_category", "g_admin_uid", "g_created", "g_lat", "g_lng", "g_city", "g_address", "g_scope", "g_start_date", "g_end_date", "g_home_page_url", "g_phone", "g_business", "g_subcategory_id", "g_subcategory_name", "is_all_info_available", "g_status"};
    }

    public static void updateGroupFriendsMembers(String groupId, List<String> userIds) {
        ContentResolver cr = OdnoklassnikiApplication.getContext().getContentResolver();
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        operations.add(ContentProviderOperation.newDelete(GroupMembers.getSilentContentUri()).withSelection("gm_group_id = ?", new String[]{groupId}).build());
        if (userIds != null) {
            for (String userId : userIds) {
                operations.add(ContentProviderOperation.newInsert(GroupMembers.getSilentContentUri()).withValue("gm_group_id", groupId).withValue("gm_user_id", userId).build());
            }
        }
        try {
            cr.applyBatch(OdklContract.getAuthority(), operations);
            cr.notifyChange(GroupMembers.getContentUri(groupId), null);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }
}
