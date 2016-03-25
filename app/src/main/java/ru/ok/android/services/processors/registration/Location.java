package ru.ok.android.services.processors.registration;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Location implements Parcelable {
    public static final Creator<Location> CREATOR;
    private String code;
    private String id;
    private String name;

    /* renamed from: ru.ok.android.services.processors.registration.Location.1 */
    static class C04911 implements Creator<Location> {
        C04911() {
        }

        public Location createFromParcel(Parcel parcel) {
            return new Location(parcel);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    }

    public Location(String name, String id, String code) {
        this.name = name;
        this.id = id;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        return this.name;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeString(this.code);
    }

    public Location(Parcel parcel) {
        this.name = parcel.readString();
        this.id = parcel.readString();
        this.code = parcel.readString();
    }

    static {
        CREATOR = new C04911();
    }
}
