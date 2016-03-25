package ru.ok.android.ui.stream.groups;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.LruCache;
import ru.ok.android.utils.bus.BusGroupsHelper;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;

public final class GroupsUsersHolder {
    private final LruCache<String, Pair<List<UserInfo>, GroupInfo>> friendsCache;
    private final Map<GroupsUsersHolderListener, Boolean> listeners;

    public interface GroupsUsersHolderListener {
        void onFriendsLoaded(String str, Pair<List<UserInfo>, GroupInfo> pair);
    }

    public GroupsUsersHolder() {
        this.friendsCache = new LruCache(20);
        this.listeners = new WeakHashMap();
        GlobalBus.register(this);
    }

    public void close() {
        GlobalBus.unregister(this);
        clear();
    }

    public void clear() {
        this.friendsCache.evictAll();
        this.listeners.clear();
    }

    public void addListener(GroupsUsersHolderListener listener) {
        this.listeners.put(listener, Boolean.TRUE);
    }

    public Pair<List<UserInfo>, GroupInfo> getFriends(String groupId) {
        if (this.friendsCache.containsKey(groupId)) {
            return (Pair) this.friendsCache.get(groupId);
        }
        BusGroupsHelper.friendsInGroup(groupId, true, true);
        return null;
    }

    @Subscribe(on = 2131623946, to = 2131624169)
    public void onFriendsResponse(BusEvent response) {
        String groupId = response.bundleInput.getString("GROUP_ID");
        if (response.resultCode != -1) {
            this.friendsCache.remove(groupId);
            Logger.m177e("Failed to fetch group friends: %s", groupId);
            return;
        }
        ArrayList<UserInfo> friends = response.bundleOutput.getParcelableArrayList("USER_INFOS");
        GroupInfo group = (GroupInfo) response.bundleOutput.getParcelable("GROUP_INFO");
        Pair<List<UserInfo>, GroupInfo> groupInfo = null;
        if (friends != null) {
            groupInfo = new Pair(friends, group);
            this.friendsCache.put(groupId, groupInfo);
        }
        for (GroupsUsersHolderListener listener : this.listeners.keySet()) {
            listener.onFriendsLoaded(groupId, groupInfo);
        }
    }
}
