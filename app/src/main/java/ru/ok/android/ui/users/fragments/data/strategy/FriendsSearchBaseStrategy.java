package ru.ok.android.ui.users.fragments.data.strategy;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.Adapter;
import java.util.List;
import ru.ok.model.UserInfo;

public abstract class FriendsSearchBaseStrategy<I> implements FriendsStrategy<I> {
    protected Adapter adapter;

    public abstract void updateUsers(@Nullable List<UserInfo> list);

    public final void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public final CharSequence buildInfoString(I i) {
        return "";
    }

    public final String getItemHeader(int position) {
        return "";
    }
}
