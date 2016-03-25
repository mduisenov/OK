package ru.ok.model.wmf;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.Serializable;

public class Album implements Parcelable, Serializable {
    public static final Creator<Album> CREATOR;
    private static final long serialVersionUID = 1;
    public final String ensemble;
    private transient int hashCode;
    public final long id;
    public final String imageUrl;
    public final String name;

    public Album(long id, String name, String imageUrl, String ensemble) {
        this.hashCode = 0;
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.ensemble = ensemble;
    }

    public Album(Parcel parcel) {
        this.hashCode = 0;
        this.id = parcel.readLong();
        this.name = parcel.readString();
        this.imageUrl = parcel.readString();
        this.ensemble = parcel.readString();
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Album other = (Album) o;
        if (this.id == other.id && TextUtils.equals(this.name, other.name) && TextUtils.equals(this.imageUrl, other.imageUrl) && TextUtils.equals(this.ensemble, other.ensemble)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            int hashCode2 = ((((((int) (this.id ^ (this.id >>> 32))) * 31) + (this.name == null ? 0 : this.name.hashCode())) * 31) + (this.imageUrl == null ? 0 : this.imageUrl.hashCode())) * 31;
            if (this.ensemble != null) {
                i = this.ensemble.hashCode();
            }
            hashCode = hashCode2 + i;
            if (hashCode == 0) {
                hashCode = 1;
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
        parcel.writeString(this.imageUrl);
        parcel.writeString(this.ensemble);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new 1();
    }
}
