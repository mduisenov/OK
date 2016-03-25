package ru.ok.android.ui.places.fragments;

import android.content.Context;
import java.util.ArrayList;
import ru.ok.android.ui.adapters.spinner.BaseNavigationSpinnerAdapter;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.places.PlaceCategory;

public final class CategoriesSpinnerAdapter extends BaseNavigationSpinnerAdapter {
    private final ArrayList<PlaceCategory> categories;

    public CategoriesSpinnerAdapter(Context context) {
        super(context);
        this.categories = new ArrayList();
    }

    protected String getItemText(int position) {
        if (position == 0) {
            return LocalizationManager.getString(getContext(), 2131165490);
        }
        return ((PlaceCategory) getItem(position - 1)).text;
    }

    protected String getCountText(int position) {
        if (position == 0) {
            return null;
        }
        return Integer.toString(((PlaceCategory) getItem(position - 1)).subCategories.size());
    }

    public void setData(ArrayList<PlaceCategory> data) {
        this.categories.clear();
        this.categories.addAll(data);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.categories.size() + 1;
    }

    public Object getItem(int position) {
        return this.categories.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }
}
