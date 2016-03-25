package ru.ok.model.wmf;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.Serializable;

public class Track implements Parcelable, Serializable {
    public static final Creator<Track> CREATOR;
    private static final long serialVersionUID = 1;
    @Nullable
    public final Album album;
    @Nullable
    public final Artist artist;
    public final int duration;
    public final String ensemble;
    public final boolean explicit;
    @Nullable
    public final String fullName;
    private transient int hashCode;
    public final long id;
    @Nullable
    public final String imageUrl;
    public final String name;

    /* renamed from: ru.ok.model.wmf.Track.1 */
    static class C16381 implements Creator<Track> {
        C16381() {
        }

        public Track createFromParcel(Parcel parcel) {
            return new Track(parcel);
        }

        public Track[] newArray(int count) {
            return new Track[count];
        }
    }

    public Track(long id, String name, String ensemble, @Nullable String imageUrl, @Nullable String fullName, @Nullable Album album, @Nullable Artist artist, boolean explicit, int duration) {
        this.hashCode = 0;
        this.id = id;
        this.name = name;
        this.ensemble = ensemble;
        this.imageUrl = imageUrl;
        this.fullName = fullName;
        this.album = album;
        this.artist = artist;
        this.explicit = explicit;
        this.duration = duration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }
        Track track = (Track) o;
        if (this.duration != track.duration) {
            return false;
        }
        if (this.explicit != track.explicit) {
            return false;
        }
        if (!TextUtils.equals(this.imageUrl, track.imageUrl)) {
            return false;
        }
        if (this.id != track.id) {
            return false;
        }
        if ((this.album == null && track.album != null) || (this.album != null && !this.album.equals(track.album))) {
            return false;
        }
        if ((this.artist == null && track.artist != null) || (this.artist != null && !this.artist.equals(track.artist))) {
            return false;
        }
        if (!TextUtils.equals(this.ensemble, track.ensemble)) {
            return false;
        }
        if (!TextUtils.equals(this.name, track.name)) {
            return false;
        }
        if (TextUtils.equals(this.fullName, track.fullName)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            int hashCode2;
            int hashCode3 = ((((int) (this.id ^ (this.id >>> 32))) * 31) + (this.name != null ? this.name.hashCode() : 0)) * 31;
            if (this.ensemble != null) {
                hashCode2 = this.ensemble.hashCode();
            } else {
                hashCode2 = 0;
            }
            hashCode3 = (hashCode3 + hashCode2) * 31;
            if (this.imageUrl != null) {
                hashCode2 = this.imageUrl.hashCode();
            } else {
                hashCode2 = 0;
            }
            hashCode3 = (hashCode3 + hashCode2) * 31;
            if (this.fullName != null) {
                hashCode2 = this.fullName.hashCode();
            } else {
                hashCode2 = 0;
            }
            hashCode3 = (hashCode3 + hashCode2) * 31;
            if (this.album != null) {
                hashCode2 = this.album.hashCode();
            } else {
                hashCode2 = 0;
            }
            hashCode3 = (hashCode3 + hashCode2) * 31;
            if (this.artist != null) {
                hashCode2 = this.artist.hashCode();
            } else {
                hashCode2 = 0;
            }
            hashCode2 = (hashCode3 + hashCode2) * 31;
            if (this.explicit) {
                i = 1;
            }
            hashCode = ((hashCode2 + i) * 31) + this.duration;
            if (hashCode == 0) {
                hashCode = 1;
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }

    public Track(Parcel parcel) {
        boolean z;
        this.hashCode = 0;
        this.id = parcel.readLong();
        this.name = parcel.readString();
        this.ensemble = parcel.readString();
        this.imageUrl = parcel.readString();
        this.fullName = parcel.readString();
        this.album = (Album) parcel.readParcelable(Album.class.getClassLoader());
        this.artist = (Artist) parcel.readParcelable(Artist.class.getClassLoader());
        if (parcel.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.explicit = z;
        this.duration = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
        parcel.writeString(this.ensemble);
        parcel.writeString(this.imageUrl);
        parcel.writeString(this.fullName);
        parcel.writeParcelable(this.album, i);
        parcel.writeParcelable(this.artist, i);
        parcel.writeInt(this.explicit ? 1 : 0);
        parcel.writeInt(this.duration);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C16381();
    }

    public String toString() {
        return "Track[\"" + this.name + "\" id=" + this.id + "]";
    }
}
