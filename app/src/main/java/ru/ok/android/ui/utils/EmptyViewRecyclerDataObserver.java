package ru.ok.android.ui.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;

public class EmptyViewRecyclerDataObserver extends ItemCountChangedDataObserver {
    private final Adapter adapter;
    private final View emptyView;

    public EmptyViewRecyclerDataObserver(View emptyView, Adapter adapter) {
        this.emptyView = emptyView;
        this.adapter = adapter;
    }

    public void onItemCountMayChange() {
        boolean isEmpty;
        int i = 0;
        if (this.adapter.getItemCount() == 0) {
            isEmpty = true;
        } else {
            isEmpty = false;
        }
        View view = this.emptyView;
        if (!isEmpty) {
            i = 8;
        }
        view.setVisibility(i);
        Context context = this.emptyView.getContext();
        if (isEmpty && (context instanceof BaseCompatToolbarActivity)) {
            ((BaseCompatToolbarActivity) context).appBarExpandAnimated();
        }
    }
}
