package ru.ok.android.ui.users.fragments.data.strategy;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import ru.ok.model.UserInfo;

public final class FriendsSearchListStrategy extends FriendsSearchBaseStrategy<UserInfo> {
    private final List<UserInfo> users;

    public FriendsSearchListStrategy() {
        this.users = new ArrayList();
    }

    public int getItemsCount() {
        return this.users.size();
    }

    public UserInfo getItem(int position) {
        return (UserInfo) this.users.get(position);
    }

    public void updateUsers(@Nullable List<UserInfo> users) {
        this.users.clear();
        if (users != null) {
            this.users.addAll(users);
        }
        this.adapter.notifyDataSetChanged();
    }
}
