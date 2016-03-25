package ru.ok.android.ui.utils;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

public abstract class ItemCountChangedDataObserver extends AdapterDataObserver {
    public abstract void onItemCountMayChange();

    public void onChanged() {
        onItemCountMayChange();
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
        onItemCountMayChange();
    }

    public void onItemRangeRemoved(int positionStart, int itemCount) {
        onItemCountMayChange();
    }

    public void onItemRangeChanged(int positionStart, int itemCount) {
        onItemCountMayChange();
    }

    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
    }
}
