package ru.ok.android.ui.stream.list.controller;

import android.app.Activity;
import ru.ok.android.ui.stream.ViewPagerStateHolder;
import ru.ok.android.ui.stream.friendship.FriendShipDataHolder;
import ru.ok.android.ui.stream.groups.GroupsUsersHolder;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.music.PlayerStateHolder;

public abstract class AbsStreamItemStateHolderViewController extends AbsStreamItemViewController {
    final FriendShipDataHolder friendShipDataHolder;
    final GroupsUsersHolder groupsFriendsHolder;
    final PlayerStateHolder playerStateHolder;
    final ViewPagerStateHolder viewPagerStateHolder;

    public AbsStreamItemStateHolderViewController(Activity activity, StreamAdapterListener listener, String logContext) {
        super(activity, listener, logContext);
        this.viewPagerStateHolder = new ViewPagerStateHolder();
        this.playerStateHolder = new PlayerStateHolder();
        this.friendShipDataHolder = new FriendShipDataHolder();
        this.groupsFriendsHolder = new GroupsUsersHolder();
    }

    public ViewPagerStateHolder getViewPagerStateHolder() {
        return this.viewPagerStateHolder;
    }

    public PlayerStateHolder getPlayerStateHolder() {
        return this.playerStateHolder;
    }

    public FriendShipDataHolder getFriendShipDataHolder() {
        return this.friendShipDataHolder;
    }

    public GroupsUsersHolder getGroupsFriendsHolder() {
        return this.groupsFriendsHolder;
    }

    public void clear() {
        this.playerStateHolder.clear();
        this.friendShipDataHolder.clear();
        this.groupsFriendsHolder.clear();
    }

    public void close() {
        this.playerStateHolder.close();
        this.friendShipDataHolder.close();
        this.groupsFriendsHolder.close();
    }
}
