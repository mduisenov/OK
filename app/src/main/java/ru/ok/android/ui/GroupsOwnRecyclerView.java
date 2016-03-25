package ru.ok.android.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import ru.ok.android.ui.groups.fragments.GroupsHorizontalLinearLayoutManager;

public class GroupsOwnRecyclerView extends RecyclerView {
    public GroupsOwnRecyclerView(Context context) {
        this(context, null);
    }

    public GroupsOwnRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupsOwnRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean fling(int velocityX, int velocityY) {
        return ((GroupsHorizontalLinearLayoutManager) getLayoutManager()).fling(this, velocityX, velocityY) || super.fling(velocityX, velocityY);
    }
}
