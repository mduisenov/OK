package ru.ok.android.ui.users.fragments.data;

import java.util.List;
import java.util.Map;
import ru.ok.java.api.request.users.FriendRelativeType;
import ru.ok.java.api.response.users.FriendRelation;
import ru.ok.java.api.response.users.UserCounters;
import ru.ok.java.api.response.users.UserRelationInfoResponse;
import ru.ok.model.UserInfo;

public final class UserProfileInfo {
    public final UserCounters counters;
    public final boolean isStreamSubscribe;
    public final List<UserInfo> mutualFriends;
    public final List<UserMergedPresent> presents;
    public final UserRelationInfoResponse relationInfo;
    public final Map<String, UserInfo> relationalUsers;
    public final Map<FriendRelativeType, List<FriendRelation>> relations;
    public final UserInfo userInfo;

    public UserProfileInfo(UserInfo userInfo, UserCounters counters, UserRelationInfoResponse relationInfo, Map<FriendRelativeType, List<FriendRelation>> relations, Map<String, UserInfo> relationalUsers, List<UserMergedPresent> presents, boolean isStreamSubscribe, List<UserInfo> mutualFriends) {
        this.userInfo = userInfo;
        this.counters = counters;
        this.relationInfo = relationInfo;
        this.relations = relations;
        this.relationalUsers = relationalUsers;
        this.presents = presents;
        this.isStreamSubscribe = isStreamSubscribe;
        this.mutualFriends = mutualFriends;
    }

    public boolean isFriend() {
        return this.relationInfo != null ? this.relationInfo.isFriend : false;
    }

    public boolean isUserBlock() {
        return this.relationInfo != null ? this.relationInfo.isBlocks : false;
    }

    public boolean canSendMessages() {
        return this.relationInfo != null ? this.relationInfo.canSendMessage : false;
    }

    public boolean canFriendInvite() {
        return this.relationInfo != null ? this.relationInfo.canFriendInvite : false;
    }

    public boolean isCallAvailable() {
        return this.userInfo != null ? this.userInfo.getAvailableCall() : false;
    }

    public boolean isPremiumProfile() {
        return this.userInfo != null ? this.userInfo.isPremiumProfile() : false;
    }

    public boolean isPrivateProfile() {
        return this.userInfo != null ? this.userInfo.isPrivateProfile() : false;
    }

    public boolean isSentFriendInvitation() {
        return this.relationInfo != null ? this.relationInfo.isFriendInvitationSent : false;
    }
}
