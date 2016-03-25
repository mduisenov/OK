package ru.ok.android.utils;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

public abstract class SingleOnChangeRecyclerAdapterDataObserver extends AdapterDataObserver {
    public abstract void onDataSetChanged();

    public void onChanged() {
        onDataSetChanged();
    }

    public void onItemRangeChanged(int positionStart, int itemCount) {
        onChanged();
    }

    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        onChanged();
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
        onChanged();
    }

    public void onItemRangeRemoved(int positionStart, int itemCount) {
        onChanged();
    }

    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        onChanged();
    }
}
