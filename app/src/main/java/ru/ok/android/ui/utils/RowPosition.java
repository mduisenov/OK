package ru.ok.android.ui.utils;

public enum RowPosition {
    SINGLE_FIRST(2130838358, 2130838364, true, false),
    SINGLE_FIRST_DATE(2130838358, 2130838364, true, true),
    SINGLE(2130838357, 2130838361, false, false),
    SINGLE_DATE(2130838357, 2130838361, false, true),
    SINGLE_DATE_AVATAR(2130838358, 2130838364, true, true);
    
    private final boolean avatarVisible;
    private final int backgroundLeftResourceId;
    private final int backgroundRightResourceId;
    private final boolean dateVisible;

    private RowPosition(int backgroundLeftResourceLeft, int backgroundRightResourceId, boolean avatarVisible, boolean dateVisible) {
        this.backgroundLeftResourceId = backgroundLeftResourceLeft;
        this.backgroundRightResourceId = backgroundRightResourceId;
        this.avatarVisible = avatarVisible;
        this.dateVisible = dateVisible;
    }

    public int getBackgroundLeftResourceId() {
        return this.backgroundLeftResourceId;
    }

    public int getBackgroundRightResourceId() {
        return this.backgroundRightResourceId;
    }

    public boolean isAvatarVisible() {
        return this.avatarVisible;
    }

    public boolean isDateVisible() {
        return this.dateVisible;
    }
}
