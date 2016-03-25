package ru.ok.android.ui.users.fragments.data.strategy;

import java.util.ArrayList;
import java.util.List;
import ru.ok.model.UserInfo;

public final class FriendsArrayGridStrategy extends FriendsArrayBaseStrategy<List<UserInfo>> {
    private final int columnsCount;
    private final List<List<UserInfo>> usersStrip;

    public FriendsArrayGridStrategy(int columnsCount) {
        this.usersStrip = new ArrayList();
        this.columnsCount = columnsCount;
    }

    protected int getItemsCountInternal() {
        return this.usersStrip.size();
    }

    public List<UserInfo> getItem(int position) {
        return (List) this.usersStrip.get(position);
    }

    public void updateUsers(List<UserInfo> users) {
        FriendsSearchGridStrategy.buildUserStrips(this.usersStrip, users, this.columnsCount);
        this.adapter.notifyDataSetChanged();
    }
}
