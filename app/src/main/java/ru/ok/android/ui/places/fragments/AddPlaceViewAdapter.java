package ru.ok.android.ui.places.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import ru.ok.android.utils.localization.LocalizationManager;

public class AddPlaceViewAdapter {
    private final Context context;
    private LayoutInflater mInflater;

    public AddPlaceViewAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public View createView() {
        return LocalizationManager.inflate(this.context, 2130903246, null, false);
    }
}
