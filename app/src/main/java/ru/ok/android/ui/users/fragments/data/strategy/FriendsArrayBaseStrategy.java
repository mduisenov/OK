package ru.ok.android.ui.users.fragments.data.strategy;

import android.support.v7.widget.RecyclerView.Adapter;
import java.util.List;
import ru.ok.model.UserInfo;

public abstract class FriendsArrayBaseStrategy<I> implements FriendsStrategy<I> {
    protected Adapter adapter;
    private boolean enabled;

    protected abstract int getItemsCountInternal();

    public abstract void updateUsers(List<UserInfo> list);

    public FriendsArrayBaseStrategy() {
        this.enabled = true;
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            this.adapter.notifyDataSetChanged();
        }
    }

    public final void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public final int getItemsCount() {
        return this.enabled ? getItemsCountInternal() : 0;
    }

    public CharSequence buildInfoString(I i) {
        return "";
    }

    public final String getItemHeader(int position) {
        return "";
    }
}
