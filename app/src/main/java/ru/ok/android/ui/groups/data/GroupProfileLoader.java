package ru.ok.android.ui.groups.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.GeneralDataLoader;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.GroupsStorageFacade;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.provider.OdklContract.GroupMembers;
import ru.ok.android.db.provider.OdklContract.Groups;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.utils.IOUtils;
import ru.ok.java.api.response.groups.GroupCounters;
import ru.ok.model.GroupInfo;
import ru.ok.model.GroupUserStatus;
import ru.ok.model.GroupUserStatus.ParseGroupUserStatusException;
import ru.ok.model.UserInfo;

public final class GroupProfileLoader extends GeneralDataLoader<GroupProfileInfo> {
    private final String groupId;

    public GroupProfileLoader(Context context, String groupId) {
        super(context);
        this.groupId = groupId;
    }

    protected GroupProfileInfo loadData() {
        GroupInfo groupInfo = loadGroupInfo();
        GroupCounters counters = queryCounters();
        GroupUserStatus status = queryStatus();
        UserInfo admin = null;
        if (!(groupInfo == null || TextUtils.isEmpty(groupInfo.getAdminUid()))) {
            admin = UsersStorageFacade.queryUser(groupInfo.getAdminUid());
        }
        return new GroupProfileInfo(groupInfo, counters, status, UsersStorageFacade.queryFriendsInGroup(this.groupId), queryIsStreamSubscribe(), admin);
    }

    private GroupCounters queryCounters() {
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.groupCountersUri(this.groupId), null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    GroupCounters cursor2Counters = GroupsStorageFacade.cursor2Counters(cursor);
                    return cursor2Counters;
                }
                cursor.close();
            } finally {
                cursor.close();
            }
        }
        return new GroupCounters(0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    private GroupUserStatus queryStatus() {
        GroupUserStatus groupUserStatus = null;
        String[] args = new String[]{OdnoklassnikiApplication.getCurrentUser().uid, this.groupId};
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.groupUsersStatusUri(), null, "user_id =  ? and group_id = ?", args, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    groupUserStatus = GroupUserStatus.getGroupUsersStatus(cursor.getString(cursor.getColumnIndex(NotificationCompat.CATEGORY_STATUS)));
                } else {
                    cursor.close();
                }
            } catch (ParseGroupUserStatusException e) {
            } finally {
                cursor.close();
            }
        }
        return groupUserStatus;
    }

    private boolean queryIsStreamSubscribe() {
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.groupStreamSubscribeUri(this.groupId), null, null, null, null);
        if (cursor == null) {
            return false;
        }
        if (cursor != null) {
            try {
                boolean moveToFirst = cursor.moveToFirst();
                return moveToFirst;
            } finally {
                IOUtils.closeSilently(cursor);
            }
        } else {
            IOUtils.closeSilently(cursor);
            return false;
        }
    }

    private GroupInfo loadGroupInfo() {
        return GroupsStorageFacade.queryGroup(OdnoklassnikiApplication.getContext().getContentResolver(), this.groupId);
    }

    protected List<Uri> observableUris(GroupProfileInfo data) {
        return Arrays.asList(new Uri[]{Groups.getUri(this.groupId), OdklProvider.groupCountersUri(this.groupId), OdklProvider.groupUsersStatusUri(), OdklProvider.groupStreamSubscribeUri(this.groupId), GroupMembers.getContentUri(this.groupId)});
    }
}
