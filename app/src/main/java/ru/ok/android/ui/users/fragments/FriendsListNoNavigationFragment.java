package ru.ok.android.ui.users.fragments;

import android.app.Activity;
import android.view.View;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.model.UserInfo;

public final class FriendsListNoNavigationFragment extends FriendsListFilteredFragment {
    public void onGoToMainPageSelect(UserInfo userInfo, View view) {
        Activity activity = getActivity();
        if (activity != null) {
            NavigationHelper.showUserInfo(activity, userInfo.getId());
        }
    }
}
