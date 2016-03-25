package ru.ok.android.ui.adapters.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.adapters.friends.UserInfosController.UserInfoViewHolder;
import ru.ok.android.ui.adapters.friends.UserInfosController.UserInfosControllerListener;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.DividerItem;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.imageview.AvatarImageView.OnClickToUserImageListener;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.model.UserInfo;

public final class UserInfosAdapter extends Adapter<CardViewHolder> implements ItemClickListenerControllerProvider, AdapterItemViewTypeMaxValueProvider {
    private final Context context;
    private final List<UserInfo> infos;
    private final Map<String, UserInfo> infosMap;
    protected final RecyclerItemClickListenerController itemClickListenerController;
    private UserInfosController userInfosController;

    public UserInfosAdapter(Context context, OnClickToUserImageListener avatarListener, UserInfosControllerListener listener) {
        this.infos = new ArrayList();
        this.infosMap = new HashMap();
        this.itemClickListenerController = new RecyclerItemClickListenerController();
        this.context = context;
        this.userInfosController = new UserInfosController(context, avatarListener, listener, false, SelectionsMode.SINGLE, null, null, false, false);
    }

    public int getItemCount() {
        return this.infos.size() > 1 ? (this.infos.size() * 2) - 1 : this.infos.size();
    }

    public UserInfo getItem(int position) {
        return position % 2 == 0 ? (UserInfo) this.infos.get(position / 2) : null;
    }

    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return this.userInfosController.onCreateViewHolder(parent);
        }
        return new CardViewHolder(DividerItem.newView(parent));
    }

    public void onBindViewHolder(CardViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            this.userInfosController.bindView(position / 2, (UserInfoViewHolder) holder, getItem(position), getItemCount());
        }
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    public int getItemViewType(int position) {
        return position % 2 == 1 ? 6 : 0;
    }

    public long getItemId(int position) {
        return position % 2 == 1 ? (long) position : (long) getItem(position).uid.hashCode();
    }

    public Map<String, UserInfo> getUsers() {
        return this.infosMap;
    }

    public void setUsers(List<? extends UserInfo> users) {
        this.infos.clear();
        this.infos.addAll(users);
        this.infosMap.clear();
        for (UserInfo info : this.infos) {
            this.infosMap.put(info.uid, info);
        }
        notifyDataSetChanged();
    }

    public int getItemViewTypeMaxValue() {
        return Math.max(6, 0);
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return this.itemClickListenerController;
    }
}
