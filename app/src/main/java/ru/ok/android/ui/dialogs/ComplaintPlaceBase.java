package ru.ok.android.ui.dialogs;

import ru.ok.model.places.Place;

public abstract class ComplaintPlaceBase {
    protected OnSelectItemDialogComplaintPlaceListener listener;
    protected Place place;

    public interface OnSelectItemDialogComplaintPlaceListener {
        void onComplaintSelectedItem(Place place);
    }

    public ComplaintPlaceBase(Place place) {
        this.place = place;
    }

    public void setOnSelectItemListener(OnSelectItemDialogComplaintPlaceListener listener) {
        this.listener = listener;
    }
}
