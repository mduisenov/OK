package ru.ok.android.ui.stream.list;

public class StreamLayoutConfig {
    public int collapsedOnlineFriendsWidth;
    public int expandedOnlineFriendsWidth;
    public boolean hasOnlineFriends;
    public boolean isOnlineFriendsExpanded;
    public boolean isTablet;
    public int listViewPortraitWidth;
    public int listViewWidth;
    public int screenOrientation;

    public int getExtraMarginForLandscapeAsInPortrait(boolean onlyForTablet) {
        if (this.screenOrientation != 2) {
            return 0;
        }
        if (onlyForTablet && !this.isTablet) {
            return 0;
        }
        int i = this.listViewPortraitWidth;
        int i2 = (!this.hasOnlineFriends || this.isOnlineFriendsExpanded) ? 0 : this.expandedOnlineFriendsWidth - this.collapsedOnlineFriendsWidth;
        return Math.max(0, this.listViewWidth - (i + i2)) / 2;
    }
}
