package ru.ok.android.ui.users.fragments.profiles;

import android.app.Activity;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.ui.users.fragments.data.UserSectionItem;
import ru.ok.android.utils.NavigationHelper;

public final class UserProfileNavigationHandler extends BaseUserProfileNavigationHandler {
    private String userId;

    protected String getUserId() {
        return this.userId;
    }

    protected List<UserSectionItem> createItems() {
        return Arrays.asList(new UserSectionItem[]{UserSectionItem.FRIENDS, UserSectionItem.PHOTOS, UserSectionItem.GROUPS, UserSectionItem.NOTES, UserSectionItem.MUSIC, UserSectionItem.VIDEOS, UserSectionItem.PRESENTS, UserSectionItem.FRIEND_HOLIDAYS, UserSectionItem.ACHIEVEMENTS, UserSectionItem.FORUM});
    }

    UserProfileNavigationHandler(Activity activity) {
        super(activity);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    protected void onFriendsItemSelect() {
        NavigationHelper.showUserFriends(this.activity, getUserId(), null);
    }
}
