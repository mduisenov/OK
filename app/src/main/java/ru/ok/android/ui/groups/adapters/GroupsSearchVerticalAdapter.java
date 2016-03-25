package ru.ok.android.ui.groups.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.ui.custom.cards.search.HeaderTitleViewsHolder;
import ru.ok.android.ui.groups.loaders.GroupsSearchLoader.HeaderFakeGroupInfo;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.onelog.groups.GroupsPageGroupClickFactory;
import ru.ok.onelog.groups.GroupsPageGroupClickSource;

public class GroupsSearchVerticalAdapter extends GroupsVerticalAdapter {
    public GroupsSearchVerticalAdapter(Context context, boolean hasJoin) {
        super(context, hasJoin, false);
    }

    public int getItemViewType(int position) {
        GroupInfo groupInfo = (GroupInfo) this.items.get(position);
        if (groupInfo == HeaderFakeGroupInfo.OWN || groupInfo == HeaderFakeGroupInfo.PORTAL) {
            return 2131624361;
        }
        return super.getItemViewType(position);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2131624361) {
            return new HeaderTitleViewsHolder(HeaderTitleViewsHolder.newView(parent));
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == 2131624361) {
            ((HeaderTitleViewsHolder) holder).titleView.setText(LocalizationManager.getString(holder.itemView.getContext(), this.items.get(position) == HeaderFakeGroupInfo.OWN ? 2131166245 : 2131165961));
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    public int getItemViewTypeMaxValue() {
        return Math.max(super.getItemViewTypeMaxValue(), 2131624361);
    }

    protected void logClick() {
        OneLog.log(GroupsPageGroupClickFactory.get(GroupsPageGroupClickSource.groups_page_search));
    }
}
