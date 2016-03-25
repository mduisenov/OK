package ru.ok.android.ui.groups.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;
import ru.ok.android.ui.groups.GroupUtils;
import ru.ok.android.ui.groups.holders.GroupHorizontalViewHolder;
import ru.ok.model.GroupInfo;
import ru.ok.onelog.groups.GroupsPageGroupClickFactory;
import ru.ok.onelog.groups.GroupsPageGroupClickSource;

public class GroupsHorizontalAdapter extends GroupsRecyclerAdapter<ViewHolder> {
    private Drawable placeHolderDrawable;

    public GroupsHorizontalAdapter(Context context) {
        Resources resources = context.getResources();
        RoundedBitmapDrawable drawable = GroupUtils.getRoundedDrawable(context, 2130838689, resources.getDimensionPixelSize(2131231017));
        drawable.setStroke((float) resources.getDimensionPixelSize(2131231015), resources.getColor(2131493004));
        this.placeHolderDrawable = drawable;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GroupHorizontalViewHolder viewHolder = new GroupHorizontalViewHolder(LayoutInflater.from(parent.getContext()).inflate(2130903228, parent, false));
        ((GenericDraweeHierarchy) viewHolder.image.getHierarchy()).setPlaceholderImage(this.placeHolderDrawable);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupInfo groupInfo = (GroupInfo) this.items.get(position);
        GroupHorizontalViewHolder groupHorizontalViewHolder = (GroupHorizontalViewHolder) holder;
        if (GroupsVerticalAdapter.bindImage(groupHorizontalViewHolder.image, groupInfo)) {
            groupHorizontalViewHolder.title.setVisibility(8);
        } else {
            String name = groupInfo.getName();
            groupHorizontalViewHolder.title.setText(name.substring(0, Math.min(3, name.length())));
            groupHorizontalViewHolder.title.setVisibility(0);
        }
        GroupsVerticalAdapter.bindUnreadEvents(groupHorizontalViewHolder.unreadEvents, groupInfo);
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    protected void logClick() {
        OneLog.log(GroupsPageGroupClickFactory.get(GroupsPageGroupClickSource.groups_page_combo_own));
    }
}
