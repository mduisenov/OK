package ru.ok.android.ui.users.fragments.data.strategy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.users.fragments.data.UserInfoExtended;
import ru.ok.model.UserInfo;

public class FriendsFilterGridStrategy extends FriendsFilterBaseStrategy<List<UserInfo>> {
    private final int columnsCount;
    private final boolean splitByFirstLetter;
    private final List<Pair<String, List<UserInfo>>> userStrips;

    public FriendsFilterGridStrategy(Context context, int columnsCount, boolean splitByFirstLetter) {
        super(context);
        this.userStrips = new ArrayList();
        this.columnsCount = columnsCount;
        this.splitByFirstLetter = splitByFirstLetter;
    }

    void refilterUsers() {
        super.refilterUsers();
        updateStrips();
    }

    public void injectFilteredFriends(@Nullable List<UserInfoExtended> friends) {
        super.injectFilteredFriends(friends);
        if (friends != null && !friends.isEmpty()) {
            updateStrips();
        }
    }

    private void updateStrips() {
        this.userStrips.clear();
        Pair<String, List<UserInfo>> pair = null;
        for (UserInfoExtended friend : this.filteredUsers) {
            if (!(!this.splitByFirstLetter || pair == null || TextUtils.equals((CharSequence) pair.first, friend.firstLetter))) {
                pair = null;
            }
            if (pair == null) {
                pair = new Pair(friend.firstLetter, new ArrayList());
                this.userStrips.add(pair);
            }
            ((List) pair.second).add(friend.user);
            if (((List) pair.second).size() >= this.columnsCount) {
                pair = null;
            }
        }
    }

    public List<UserInfo> getItem(int position) {
        return (List) ((Pair) this.userStrips.get(position)).second;
    }

    public int getItemsCount() {
        return this.userStrips.size();
    }

    public CharSequence buildInfoString(List<UserInfo> list) {
        return "";
    }

    public String getItemHeader(int position) {
        return (String) ((Pair) this.userStrips.get(position)).first;
    }
}
