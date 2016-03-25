package ru.ok.android.ui.groups.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.services.processors.groups.GroupsProcessor.GroupAdditionalInfo;
import ru.ok.android.ui.adapters.friends.ItemClickListenerControllerProvider;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.model.GroupInfo;

public abstract class GroupsRecyclerAdapter<T extends ViewHolder> extends Adapter<T> implements ItemClickListenerControllerProvider, OnItemClickListener, AdapterItemViewTypeMaxValueProvider {
    protected Map<String, GroupAdditionalInfo> groupAdditionalInfoMap;
    protected RecyclerItemClickListenerController itemClickListenerController;
    protected List<GroupInfo> items;
    protected Listener listener;
    protected boolean loading;

    public interface Listener {
        void onGroupInfoClick(GroupInfo groupInfo, GroupsRecyclerAdapter groupsRecyclerAdapter, int i);

        void onGroupInfoJoinClick(GroupInfo groupInfo);
    }

    public GroupsRecyclerAdapter() {
        this.groupAdditionalInfoMap = new HashMap();
        this.itemClickListenerController = new RecyclerItemClickListenerController();
        this.itemClickListenerController.addItemClickListener(this);
    }

    public int getItemCount() {
        return this.items == null ? 0 : this.items.size();
    }

    public void setItems(List<GroupInfo> items, List<GroupAdditionalInfo> groupsAdditionalInfos) {
        this.items = items;
        this.groupAdditionalInfoMap.clear();
        addAdditionalInfos(items, groupsAdditionalInfos);
    }

    private void addAdditionalInfos(List<GroupInfo> groups, List<GroupAdditionalInfo> additionalInfos) {
        if (groups != null && additionalInfos != null) {
            int size = groups.size();
            for (int i = 0; i < size; i++) {
                this.groupAdditionalInfoMap.put(((GroupInfo) groups.get(i)).getId(), additionalInfos.get(i));
            }
        }
    }

    public void setItems(List<GroupInfo> items) {
        setItems(items, null);
    }

    public int getItemViewType(int position) {
        return 2131624360;
    }

    public int getItemViewTypeMaxValue() {
        return 2131624360;
    }

    public void addItems(List<GroupInfo> groups, List<GroupAdditionalInfo> groupsAdditionalInfos) {
        this.items.addAll(groups);
        addAdditionalInfos(groups, groupsAdditionalInfos);
    }

    public void addItems(List<GroupInfo> groups) {
        addItems(groups, null);
    }

    public boolean isLoading() {
        return this.loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return getItemClickListenerController();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void onItemClick(View view, int position) {
        logClick();
        if (this.listener != null) {
            this.listener.onGroupInfoClick((GroupInfo) this.items.get(position), this, position);
        }
    }

    protected void logClick() {
    }
}
