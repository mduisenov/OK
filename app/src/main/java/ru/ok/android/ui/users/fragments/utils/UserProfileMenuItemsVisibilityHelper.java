package ru.ok.android.ui.users.fragments.utils;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import ru.ok.android.ui.users.fragments.data.UserProfileInfo;
import ru.ok.android.utils.Utils;
import ru.ok.java.api.response.users.UserRelationInfoResponse;

public final class UserProfileMenuItemsVisibilityHelper {
    private MenuItem callItem;
    private MenuItem deleteFromFriendsItem;
    private UserProfileInfo info;
    private MenuItem inviteFriendItem;
    private MenuItem inviteGroupItem;
    private UserRelationInfoResponse relationInfo;
    private MenuItem sendMessagesItem;
    private MenuItem setRelationItem;
    private MenuItem showPhotoItem;

    public void setUserProfileInfo(UserProfileInfo info) {
        this.info = info;
    }

    public void setRelationInfo(UserRelationInfoResponse relationInfo) {
        this.relationInfo = relationInfo;
    }

    public void configureMenu(Menu menu) {
        this.callItem = menu.findItem(2131625261);
        this.sendMessagesItem = menu.findItem(2131624538);
        this.deleteFromFriendsItem = menu.findItem(2131625513);
        this.inviteGroupItem = menu.findItem(2131625514);
        this.setRelationItem = menu.findItem(2131625515);
        this.inviteFriendItem = menu.findItem(2131624693);
        updateVisibility();
    }

    public void updateVisibility() {
        if (!(this.callItem == null || this.info == null || this.info.userInfo == null)) {
            this.callItem.setVisible(Utils.userCanCall(this.info.userInfo));
        }
        if (!(this.showPhotoItem == null || this.info == null || this.info.userInfo == null)) {
            this.showPhotoItem.setVisible(!TextUtils.isEmpty(this.info.userInfo.pid));
        }
        if (!(this.sendMessagesItem == null || this.relationInfo == null)) {
            this.sendMessagesItem.setVisible(this.relationInfo.canSendMessage);
        }
        if (!(this.deleteFromFriendsItem == null || this.info == null || this.info.userInfo == null)) {
            this.deleteFromFriendsItem.setVisible(this.info.relationInfo.isFriend);
        }
        if (!(this.inviteGroupItem == null || this.info == null)) {
            if (this.info.relationInfo.isFriend) {
                this.inviteGroupItem.setVisible(true);
            } else if (this.info.userInfo == null || this.info.userInfo.isPrivateProfile()) {
                this.inviteGroupItem.setVisible(false);
            } else {
                this.inviteGroupItem.setVisible(true);
            }
        }
        if (!(this.setRelationItem == null || this.info == null || this.info.relationInfo == null)) {
            this.setRelationItem.setVisible(this.info.relationInfo.isFriend);
        }
        if (this.inviteFriendItem == null) {
            return;
        }
        if (this.info == null || this.info.relationInfo == null || !this.info.userInfo.isPremiumProfile() || this.info.isFriend() || this.info.relationInfo.isFriendInvitationSent) {
            this.inviteFriendItem.setVisible(false);
        } else {
            this.inviteFriendItem.setVisible(true);
        }
    }
}
