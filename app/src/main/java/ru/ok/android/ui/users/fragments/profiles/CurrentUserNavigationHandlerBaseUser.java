package ru.ok.android.ui.users.fragments.profiles;

import android.app.Activity;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.users.fragments.data.UserSectionItem;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public class CurrentUserNavigationHandlerBaseUser extends BaseUserProfileNavigationHandler {
    CurrentUserNavigationHandlerBaseUser(Activity activity) {
        super(activity);
    }

    protected String getUserId() {
        return OdnoklassnikiApplication.getCurrentUser().uid;
    }

    protected void onFriendsItemSelect() {
        NavigationHelper.showFriends(this.activity, false);
    }

    protected void onBaseWebItemSelect(Type itemType) {
        NavigationHelper.showExternalUrlPage(this.activity, WebUrlCreator.getUrl(itemType.getMethodName(), getUserId(), null), false);
    }

    protected List<UserSectionItem> createItems() {
        return Arrays.asList(new UserSectionItem[]{UserSectionItem.FRIENDS, UserSectionItem.PHOTOS, UserSectionItem.MY_GROUPS, UserSectionItem.NOTES, UserSectionItem.MUSIC, UserSectionItem.VIDEOS, UserSectionItem.MY_HOLIDAYS, UserSectionItem.GAMES, UserSectionItem.MY_PRESENTS, UserSectionItem.ACHIEVEMENTS, UserSectionItem.FORUM});
    }
}
