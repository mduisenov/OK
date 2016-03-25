package ru.ok.android.ui.groups.data;

import java.util.Collections;
import java.util.List;
import ru.ok.java.api.response.groups.GroupCounters;
import ru.ok.model.GroupInfo;
import ru.ok.model.GroupUserStatus;
import ru.ok.model.UserInfo;

public final class GroupProfileInfo {
    public final UserInfo admin;
    public final GroupCounters counters;
    public final List<UserInfo> friendsInGroup;
    public final GroupInfo groupInfo;
    public final boolean isSubscribeToStream;
    public final GroupUserStatus userStatus;

    public GroupProfileInfo(GroupInfo groupInfo, GroupCounters counters, GroupUserStatus status, List<UserInfo> friendsInGroup, boolean isSubscribeToStream, UserInfo admin) {
        this.groupInfo = groupInfo;
        this.counters = counters;
        this.userStatus = status;
        this.friendsInGroup = Collections.unmodifiableList(friendsInGroup);
        this.isSubscribeToStream = isSubscribeToStream;
        this.admin = admin;
    }
}
