package ru.ok.android.ui.polls.choice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.widget.Checkable;

public abstract class SingleChoiceRecycleViewAdapter<T extends ViewHolder & Checkable> extends Adapter<T> {
    private T checkedItem;
    private int checkedPosition;
    protected final RecyclerView recyclerView;
    private final ViewHolderFinder viewHolderFinder;

    public interface ViewHolderFinder {
        @Nullable
        ViewHolder findViewHolderForPosition(RecyclerView recyclerView, int i);
    }

    public SingleChoiceRecycleViewAdapter(RecyclerView recyclerView, ViewHolderFinder finder) {
        this.checkedPosition = -1;
        this.recyclerView = recyclerView;
        this.viewHolderFinder = finder;
    }

    public void onChecked(int position) {
        ViewHolder item = this.viewHolderFinder.findViewHolderForPosition(this.recyclerView, position);
        if (item instanceof Checkable) {
            Checkable checkable = (Checkable) item;
            if (position != this.checkedPosition) {
                ViewHolder lastCheckedItem = this.viewHolderFinder.findViewHolderForPosition(this.recyclerView, this.checkedPosition);
                if ((lastCheckedItem == null || this.checkedPosition >= 0) && this.checkedItem != null && ((Checkable) this.checkedItem).isChecked()) {
                    lastCheckedItem = this.checkedItem;
                } else if (this.checkedItem != null && ((Checkable) this.checkedItem).isChecked()) {
                    ((Checkable) this.checkedItem).setChecked(false);
                }
                if ((lastCheckedItem instanceof Checkable) && this.checkedPosition >= 0) {
                    ((Checkable) lastCheckedItem).setChecked(false);
                }
                this.checkedPosition = position;
                checkable.setChecked(true);
            }
            this.checkedItem = item;
        }
    }

    public void onBindViewHolder(T t, int i) {
        if (i == this.checkedPosition) {
            this.checkedItem = t;
        }
    }

    public boolean isChecked(int position) {
        return position == this.checkedPosition;
    }

    public int getCheckedPosition() {
        return this.checkedPosition;
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("recycle_single_checked_position", this.checkedPosition);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            this.checkedPosition = bundle.getInt("recycle_single_checked_position", -1);
        }
    }
}
