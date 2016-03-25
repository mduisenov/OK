package ru.ok.model.places;

import android.annotation.TargetApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.Serializable;
import ru.ok.model.Address;
import ru.ok.model.Location;

public class Place implements Parcelable, Serializable {
    public static final Creator<Place> CREATOR;
    private static final long serialVersionUID = 1;
    public Address address;
    public PlaceCategory category;
    public int distance;
    public final String id;
    public Location location;
    public String name;

    /* renamed from: ru.ok.model.places.Place.1 */
    static class C15641 implements Creator<Place> {
        C15641() {
        }

        public Place createFromParcel(Parcel parcel) {
            return new Place(null);
        }

        public Place[] newArray(int count) {
            return new Place[count];
        }
    }

    public static class Builder {
        private Place place;

        public Builder(String id) {
            this.place = new Place(id);
        }

        public Builder setName(String name) {
            this.place.name = name;
            return this;
        }

        public Builder setLocation(Location location) {
            this.place.location = location;
            return this;
        }

        public Builder setCategory(PlaceCategory category) {
            this.place.category = category;
            return this;
        }

        public Builder setAddress(Address address) {
            this.place.address = address;
            return this;
        }

        public Builder setDistance(int distance) {
            this.place.distance = distance;
            return this;
        }

        public Place build() {
            return this.place;
        }
    }

    public Place(String id) {
        this.id = id;
    }

    public int describeContents() {
        return 0;
    }

    @TargetApi(5)
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
        parcel.writeParcelable(this.location, flags);
        parcel.writeParcelable(this.category, flags);
        parcel.writeParcelable(this.address, flags);
        parcel.writeInt(this.distance);
    }

    @TargetApi(5)
    private Place(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.location = (Location) parcel.readParcelable(Location.class.getClassLoader());
        this.category = (PlaceCategory) parcel.readParcelable(PlaceCategory.class.getClassLoader());
        this.address = (Address) parcel.readParcelable(Address.class.getClassLoader());
        this.distance = parcel.readInt();
    }

    static {
        CREATOR = new C15641();
    }

    public boolean equals(Object o) {
        return (o instanceof Place) && TextUtils.equals(this.id, ((Place) o).id);
    }

    public int hashCode() {
        return this.id == null ? 0 : this.id.hashCode();
    }
}
