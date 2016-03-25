package ru.ok.android.ui.users.fragments.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.GeneralDataLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.utils.IOUtils;
import ru.ok.java.api.request.users.FriendRelativeType;
import ru.ok.java.api.response.users.FriendRelation;
import ru.ok.java.api.response.users.UserCounters;
import ru.ok.java.api.response.users.UserRelationInfoResponse;
import ru.ok.model.UserInfo;

public final class UserProfileInfoLoader extends GeneralDataLoader<UserProfileInfo> {
    private static final String[] PROJECTION_COUNT;
    private final String userId;

    static {
        PROJECTION_COUNT = new String[]{"COUNT(*)"};
    }

    public UserProfileInfoLoader(Context context, String userId) {
        super(context);
        this.userId = userId;
    }

    protected UserProfileInfo loadData() {
        UserInfo userInfo = UsersStorageFacade.queryUser(this.userId);
        UserCounters counters = queryCounters();
        Map<FriendRelativeType, List<FriendRelation>> relations = UsersStorageFacade.queryUserRelations(this.userId);
        Map<String, UserInfo> relationalUsers = UsersStorageFacade.queryRelationalUsers(relations);
        List<UserMergedPresent> presents = UsersStorageFacade.queryPresents(this.userId);
        UserRelationInfoResponse relationInfo = UsersStorageFacade.queryUserRelationInfo(this.userId);
        return new UserProfileInfo(userInfo, counters, new UserRelationInfoResponse(this.userId, queryIsFriend(), relationInfo.isFriendInvitationSent, relationInfo.isBlocks, false, relationInfo.canSendMessage, relationInfo.canFriendInvite), relations, relationalUsers, presents, queryIsStreamSubscribe(), UsersStorageFacade.queryMutualFriends(this.userId));
    }

    private UserCounters queryCounters() {
        UserCounters cursor2Counters;
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.userCountersUri(this.userId), null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    cursor2Counters = UsersStorageFacade.cursor2Counters(cursor);
                    return cursor2Counters;
                }
            } finally {
                IOUtils.closeSilently(cursor);
            }
        }
        cursor2Counters = new UserCounters(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        IOUtils.closeSilently(cursor);
        return cursor2Counters;
    }

    private boolean queryIsFriend() {
        boolean z;
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.friendUri(this.userId), PROJECTION_COUNT, null, null, null);
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

    private boolean queryIsStreamSubscribe() {
        boolean z;
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.userStreamSubscribeUri(this.userId), null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
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

    protected List<Uri> observableUris(UserProfileInfo data) {
        return Arrays.asList(new Uri[]{Users.getUri(this.userId), OdklProvider.userRelationsUri(this.userId), OdklProvider.userCountersUri(this.userId), OdklProvider.friendUri(this.userId), OdklProvider.userInterestsUri(this.userId), OdklProvider.userPresentsUri(this.userId), OdklProvider.userRelationInfoUri(this.userId), OdklProvider.userStreamSubscribeUri(this.userId), OdklProvider.mutualFriendsUri(this.userId)});
    }
}
