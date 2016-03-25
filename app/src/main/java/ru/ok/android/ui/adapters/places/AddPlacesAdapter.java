package ru.ok.android.ui.adapters.places;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.places.fragments.AddPlaceViewAdapter;

public class AddPlacesAdapter extends PlacesAdapter {
    private boolean showAddItem;

    public AddPlacesAdapter(Context context) {
        super(context);
        this.showAddItem = false;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
        if (position < super.getCount()) {
            return super.getItemViewType(position);
        }
        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) != 1) {
            return super.getView(position, convertView, parent);
        }
        return new AddPlaceViewAdapter(this.context).createView();
    }

    public void showAdd() {
        if (!this.showAddItem) {
            this.showAddItem = true;
            notifyDataSetChanged();
        }
    }

    public boolean isShowAddItem() {
        return this.showAddItem;
    }

    public void hideAdd() {
        if (this.showAddItem) {
            this.showAddItem = false;
            notifyDataSetChanged();
        }
    }

    public int getCount() {
        if (this.showAddItem) {
            return super.getCount() + 1;
        }
        return super.getCount();
    }
}
