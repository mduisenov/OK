package ru.ok.model.places;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlaceCategory implements Parcelable, Serializable {
    public static final Creator<PlaceCategory> CREATOR;
    private static final long serialVersionUID = 1;
    public String id;
    public String in;
    public List<PlaceCategory> subCategories;
    public String text;

    /* renamed from: ru.ok.model.places.PlaceCategory.1 */
    static class C15651 implements Creator<PlaceCategory> {
        C15651() {
        }

        public PlaceCategory createFromParcel(Parcel parcel) {
            return new PlaceCategory(parcel);
        }

        public PlaceCategory[] newArray(int count) {
            return new PlaceCategory[count];
        }
    }

    public PlaceCategory(String id) {
        this.subCategories = new ArrayList();
        this.id = id;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.id);
        parcel.writeString(this.in);
        parcel.writeString(this.text);
        parcel.writeTypedList(this.subCategories);
    }

    public PlaceCategory(Parcel parcel) {
        this.subCategories = new ArrayList();
        this.id = parcel.readString();
        this.in = parcel.readString();
        this.text = parcel.readString();
        parcel.readTypedList(this.subCategories, CREATOR);
    }

    public void addSubCategory(PlaceCategory category) {
        this.subCategories.add(category);
    }

    public boolean hasSubcategories() {
        return this.subCategories.size() > 0;
    }

    static {
        CREATOR = new C15651();
    }

    public boolean equals(Object o) {
        return (o instanceof Place) && TextUtils.equals(this.id, ((Place) o).id);
    }

    public int hashCode() {
        return this.id == null ? 0 : this.id.hashCode();
    }
}
