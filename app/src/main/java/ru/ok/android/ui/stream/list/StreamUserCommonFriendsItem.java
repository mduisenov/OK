package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.friendship.UserCommonFriendsViewHolder;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.UserInfo;
import ru.ok.model.stream.entities.FeedUserEntity;

public final class StreamUserCommonFriendsItem extends StreamItemAdjustablePaddings {
    private final FeedUserEntity userEntity;

    protected StreamUserCommonFriendsItem(FeedWithState feed, FeedUserEntity userEntity) {
        super(11, 3, 1, feed);
        this.userEntity = userEntity;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent, OnClickListener userClickListener) {
        View view = inflater.inflate(2130903510, parent, false);
        view.setOnClickListener(userClickListener);
        view.setTag(2131624343, "avatar_friendship");
        return view;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof UserCommonFriendsViewHolder) {
            UserCommonFriendsViewHolder viewHolder = (UserCommonFriendsViewHolder) holder;
            UserInfo user = this.userEntity.getUserInfo();
            viewHolder.setUser(user);
            viewHolder.itemView.setTag(2131624354, user);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static UserCommonFriendsViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new UserCommonFriendsViewHolder(view, streamItemViewController.getViewCache(), streamItemViewController.getFriendShipDataHolder(), streamItemViewController.getImageLoadBlocker());
    }

    boolean sharePressedState() {
        return false;
    }
}
