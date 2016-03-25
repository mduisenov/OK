package ru.ok.android.ui.custom.mediacomposer;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.model.mediatopics.MediaItemType;
import ru.ok.model.places.Place;

public class PlaceItem extends MediaItem {
    public static final Creator<PlaceItem> CREATOR;
    private final Place place;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.PlaceItem.1 */
    static class C06791 implements Creator<PlaceItem> {
        C06791() {
        }

        public PlaceItem createFromParcel(Parcel source) {
            return new PlaceItem(source);
        }

        public PlaceItem[] newArray(int size) {
            return new PlaceItem[size];
        }
    }

    public PlaceItem(Place place) {
        super(MediaItemType.PLACE);
        this.place = place;
    }

    PlaceItem(Parcel source) {
        super(MediaItemType.PLACE, source);
        this.place = (Place) source.readParcelable(Place.class.getClassLoader());
    }

    public Place getPlace() {
        return this.place;
    }

    public boolean isEmpty() {
        return this.place == null;
    }

    public String toString() {
        if (this.place != null) {
            return "PlaceItem[" + this.place.name + " address: " + this.place.address.getStringAddress() + "]";
        }
        return super.toString();
    }

    public String getSampleText() {
        if (this.place == null) {
            return "";
        }
        StringBuilder placeText = new StringBuilder(this.place.id);
        placeText.append(this.place.name);
        placeText.append(", ");
        placeText.append(this.place.distance);
        if (this.place.address != null) {
            placeText.append(", ");
            placeText.append(this.place.address.getStringAddress());
        }
        return placeText.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.place, flags);
    }

    static {
        CREATOR = new C06791();
    }
}
