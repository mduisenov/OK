package ru.ok.android.ui.users.fragments.data.strategy;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.utils.DateFormatter;
import ru.ok.model.UserInfo;

public class FriendsArrayListStrategy extends FriendsArrayBaseStrategy<UserInfo> {
    private final Context context;
    protected final List<UserInfo> users;

    public FriendsArrayListStrategy(Context context) {
        this.users = new ArrayList();
        this.context = context;
    }

    protected int getItemsCountInternal() {
        return this.users.size();
    }

    public UserInfo getItem(int position) {
        return (UserInfo) this.users.get(position);
    }

    public void updateUsers(List<UserInfo> users) {
        this.users.clear();
        if (users != null) {
            this.users.addAll(users);
        }
        this.adapter.notifyDataSetChanged();
    }

    public CharSequence buildInfoString(UserInfo user) {
        return DateFormatter.formatDeltaTimePast(this.context, user.lastOnline, false, false);
    }
}
