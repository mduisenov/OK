package ru.ok.android.ui.users.fragments.data;

import java.util.List;
import ru.ok.model.UserInfo;

public final class FriendsSearchBundle {
    public final List<UserInfoExtended> friends;
    public final List<UserInfo> users;

    public FriendsSearchBundle(List<UserInfoExtended> friends, List<UserInfo> users) {
        this.friends = friends;
        this.users = users;
    }
}
