package ru.ok.model;

import android.text.TextUtils;

public enum GroupUserStatus {
    ACTIVE("ACTIVE"),
    BLOCKED("BLOCKED"),
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    PASSIVE("PASSIVE"),
    UNKNOWN("UNKNOWN"),
    MAYBE("MAYBE");
    
    String strValue;

    public static class ParseGroupUserStatusException extends Exception {
    }

    private GroupUserStatus(String strValue) {
        this.strValue = strValue;
    }

    public String getStrValue() {
        return this.strValue;
    }

    public static GroupUserStatus getGroupUsersStatus(String name) throws ParseGroupUserStatusException {
        if (!TextUtils.isEmpty(name)) {
            for (GroupUserStatus status : values()) {
                if (name.equals(status.strValue)) {
                    return status;
                }
            }
        }
        throw new ParseGroupUserStatusException();
    }
}
