package ru.ok.android.ui.stream.friendship;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.LruCache;
import ru.ok.model.UserInfo;

public final class FriendShipDataHolder {
    private final Map<FriendshipDataHolderListener, Boolean> listeners;
    private final LruCache<String, List<UserInfo>> mutualFriendsCache;

    public interface FriendshipDataHolderListener {
        void onMutualFriendsLoaded(String str, List<UserInfo> list);
    }

    public FriendShipDataHolder() {
        this.mutualFriendsCache = new LruCache(20);
        this.listeners = new WeakHashMap();
        GlobalBus.register(this);
    }

    public void close() {
        GlobalBus.unregister(this);
        clear();
    }

    public void clear() {
        this.mutualFriendsCache.evictAll();
        this.listeners.clear();
    }

    public void addListener(FriendshipDataHolderListener listener) {
        this.listeners.put(listener, Boolean.TRUE);
    }

    public List<UserInfo> getMutualFriends(String userId) {
        if (this.mutualFriendsCache.containsKey(userId)) {
            return (List) this.mutualFriendsCache.get(userId);
        }
        Bundle bundle = new Bundle();
        bundle.putString("source_id", OdnoklassnikiApplication.getCurrentUser().uid);
        bundle.putString("target_id", userId);
        GlobalBus.send(2131624092, new BusEvent(bundle));
        return null;
    }

    @Subscribe(on = 2131623946, to = 2131624232)
    public void onMutualFriendsResponse(BusEvent response) {
        String sourceId = response.bundleInput.getString("source_id");
        String targetId = response.bundleInput.getString("target_id");
        if (response.resultCode != -1) {
            this.mutualFriendsCache.remove(targetId);
            Logger.m177e("Failed to fetch mutual friends: %s <-> %s", sourceId, targetId);
            return;
        }
        ArrayList<UserInfo> mutualFriends = response.bundleOutput.getParcelableArrayList("mutual_friends");
        this.mutualFriendsCache.put(targetId, mutualFriends);
        for (FriendshipDataHolderListener listener : this.listeners.keySet()) {
            listener.onMutualFriendsLoaded(targetId, mutualFriends);
        }
    }
}
