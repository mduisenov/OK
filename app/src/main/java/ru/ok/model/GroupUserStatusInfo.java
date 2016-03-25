package ru.ok.model;

public class GroupUserStatusInfo {
    public String groupId;
    public GroupUserStatus status;
    public String uid;

    public GroupUserStatusInfo(String uid, String groupId, GroupUserStatus status) {
        this.uid = uid;
        this.groupId = groupId;
        this.status = status;
    }
}
