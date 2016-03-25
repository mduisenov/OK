package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;

public class Location implements Parcelable, Serializable {
    public static final Creator<Location> CREATOR;
    private static final long serialVersionUID = 1;
    Double lat;
    Double lng;

    /* renamed from: ru.ok.model.Location.1 */
    static class C15171 implements Creator<Location> {
        C15171() {
        }

        public Location createFromParcel(Parcel parcel) {
            return new Location(Double.valueOf(parcel.readDouble()), Double.valueOf(parcel.readDouble()));
        }

        public Location[] newArray(int count) {
            return new Location[count];
        }
    }

    Location() {
    }

    public Location(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Location(android.location.Location location) {
        this(Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()));
    }

    public double getLatitude() {
        return this.lat.doubleValue();
    }

    public double getLongitude() {
        return this.lng.doubleValue();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(this.lat.doubleValue());
        parcel.writeDouble(this.lng.doubleValue());
    }

    static {
        CREATOR = new C15171();
    }
}
