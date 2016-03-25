package ru.ok.model.wmf;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.Serializable;

public class Artist implements Parcelable, Serializable {
    public static final Creator<Artist> CREATOR;
    private static final long serialVersionUID = 1;
    private transient int hashCode;
    public final long id;
    public final String imageUrl;
    public final String name;

    public Artist(long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Artist(Parcel parcel) {
        this.id = parcel.readLong();
        this.name = parcel.readString();
        this.imageUrl = parcel.readString();
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Artist other = (Artist) o;
        if (this.id == other.id && TextUtils.equals(this.name, other.name) && TextUtils.equals(this.imageUrl, other.imageUrl)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            int hashCode2 = ((((int) (this.id ^ (this.id >>> 32))) * 31) + (this.name == null ? 0 : this.name.hashCode())) * 31;
            if (this.imageUrl != null) {
                i = this.imageUrl.hashCode();
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
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "Artist[id=" + this.id + " name=\"" + this.name + "\"]";
    }

    static {
        CREATOR = new 1();
    }
}
