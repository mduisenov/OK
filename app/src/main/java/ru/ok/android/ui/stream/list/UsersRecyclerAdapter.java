package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.stream.entities.FeedUserEntity;

public class UsersRecyclerAdapter extends Adapter<ViewHolder> {
    private final HandleBlocker imageBlocker;
    private final LayoutInflater layoutInflater;
    private final int spacing;
    private final OnClickListener userClickListener;
    private List<FeedUserEntity> users;

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        private final RoundAvatarImageView avatarImageView;
        private final TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.avatarImageView = (RoundAvatarImageView) itemView.findViewById(2131624657);
            this.avatarImageView.setIsAlpha(true);
            this.nameTextView = (TextView) itemView.findViewById(2131625379);
        }
    }

    public UsersRecyclerAdapter(Context context, LayoutInflater layoutInflater, HandleBlocker imageBlocker, OnClickListener userClickListener) {
        this.layoutInflater = layoutInflater;
        this.imageBlocker = imageBlocker;
        this.spacing = context.getResources().getDimensionPixelOffset(2131231000);
        this.userClickListener = userClickListener;
    }

    public void setUsers(List<FeedUserEntity> users) {
        this.users = users;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = this.layoutInflater.inflate(2130903561, parent, false);
        itemView.setTag(2131624343, "avatar_friendship");
        itemView.setOnClickListener(this.userClickListener);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        UserInfo userInfo = ((FeedUserEntity) this.users.get(position)).getUserInfo();
        ImageViewManager.getInstance().displayImage(userInfo.picUrl, holder.avatarImageView, userInfo.genderType == UserGenderType.MALE, this.imageBlocker);
        holder.nameTextView.setText(userInfo.getConcatName());
        holder.avatarImageView.setTag(userInfo);
        holder.itemView.setPadding(0, 0, this.spacing, 0);
        holder.itemView.setTag(2131624354, userInfo);
    }

    public int getItemCount() {
        return this.users == null ? 0 : this.users.size();
    }
}
