package ru.ok.android.ui.users.fragments.data.strategy;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import ru.ok.model.UserInfo;

public final class FriendsSearchGridStrategy extends FriendsSearchBaseStrategy<List<UserInfo>> {
    private final int columnsCount;
    private final List<List<UserInfo>> usersStrip;

    public FriendsSearchGridStrategy(int columnsCount) {
        this.usersStrip = new ArrayList();
        this.columnsCount = columnsCount;
    }

    public int getItemsCount() {
        return this.usersStrip.size();
    }

    public List<UserInfo> getItem(int position) {
        return (List) this.usersStrip.get(position);
    }

    public void updateUsers(@Nullable List<UserInfo> users) {
        buildUserStrips(this.usersStrip, users, this.columnsCount);
        this.adapter.notifyDataSetChanged();
    }

    static void buildUserStrips(List<List<UserInfo>> usersStrip, @Nullable List<UserInfo> users, int columnsCount) {
        usersStrip.clear();
        if (users != null) {
            List<UserInfo> currentStrip = null;
            for (UserInfo user : users) {
                if (currentStrip == null) {
                    currentStrip = new ArrayList();
                    usersStrip.add(currentStrip);
                }
                currentStrip.add(user);
                if (currentStrip.size() >= columnsCount) {
                    currentStrip = null;
                }
            }
        }
    }
}
