package ru.ok.android.ui.users.fragments.utils;

import android.view.Menu;
import android.view.MenuItem;
import ru.ok.android.ui.groups.data.GroupProfileInfo;
import ru.ok.model.GroupType;
import ru.ok.model.GroupUserStatus;

public class GroupProfileMenuItemsVisibilityHelper {
    private MenuItem changePhotoMenu;
    private MenuItem complaintMenu;
    private GroupProfileInfo groupProfileInfo;
    private MenuItem leaveItem;
    private MenuItem mayInviteMenu;
    private MenuItem settingsItem;
    private MenuItem subscribeItem;

    public void setGroupProfileInfo(GroupProfileInfo groupProfileInfo) {
        this.groupProfileInfo = groupProfileInfo;
        updateVisibility();
    }

    public void configureMenu(Menu menu) {
        this.leaveItem = menu.findItem(2131625465);
        this.settingsItem = menu.findItem(2131625466);
        this.subscribeItem = menu.findItem(2131625467);
        this.changePhotoMenu = menu.findItem(2131625453);
        this.complaintMenu = menu.findItem(2131625468);
        this.mayInviteMenu = menu.findItem(2131625464);
        updateVisibility();
    }

    private void updateVisibility() {
        GroupUserStatus userStatus = this.groupProfileInfo != null ? this.groupProfileInfo.userStatus : null;
        if (this.leaveItem != null) {
            boolean leaveItemVisible;
            if (userStatus == GroupUserStatus.ACTIVE || userStatus == GroupUserStatus.MODERATOR) {
                leaveItemVisible = true;
            } else {
                leaveItemVisible = false;
            }
            this.leaveItem.setVisible(leaveItemVisible);
        }
        if (this.complaintMenu != null) {
            boolean complaintVisible;
            if (userStatus != GroupUserStatus.ADMIN) {
                complaintVisible = true;
            } else {
                complaintVisible = false;
            }
            this.complaintMenu.setVisible(complaintVisible);
        }
        if (this.settingsItem != null) {
            boolean settingsVisible;
            if (userStatus == GroupUserStatus.ADMIN) {
                settingsVisible = true;
            } else {
                settingsVisible = false;
            }
            this.settingsItem.setVisible(settingsVisible);
        }
        if (this.changePhotoMenu != null) {
            boolean changePhotoVisible;
            if (userStatus == GroupUserStatus.ADMIN) {
                changePhotoVisible = true;
            } else {
                changePhotoVisible = false;
            }
            this.changePhotoMenu.setVisible(changePhotoVisible);
        }
        if (this.subscribeItem != null) {
            boolean subscribeVisible;
            if (this.groupProfileInfo == null || this.groupProfileInfo.isSubscribeToStream) {
                subscribeVisible = false;
            } else {
                subscribeVisible = true;
            }
            this.subscribeItem.setVisible(subscribeVisible);
        }
        if (this.mayInviteMenu != null) {
            boolean mayInviteVisible;
            if (userStatus != GroupUserStatus.ACTIVE || this.groupProfileInfo == null || this.groupProfileInfo.groupInfo == null || this.groupProfileInfo.groupInfo.getType() != GroupType.HAPPENING) {
                mayInviteVisible = false;
            } else {
                mayInviteVisible = true;
            }
            this.mayInviteMenu.setVisible(mayInviteVisible);
        }
    }
}
