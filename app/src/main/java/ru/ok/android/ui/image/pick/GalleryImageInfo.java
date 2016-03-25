package ru.ok.android.ui.image.pick;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Comparator;

public final class GalleryImageInfo implements Parcelable {
    public static final Comparator<GalleryImageInfo> COMPARATOR_DATE_ADDED;
    public static final Creator<GalleryImageInfo> CREATOR;
    private final boolean broken;
    public final long dateAdded;
    public final int height;
    public final String mimeType;
    public final int rotation;
    public final Uri uri;
    public final int width;

    /* renamed from: ru.ok.android.ui.image.pick.GalleryImageInfo.1 */
    static class C09881 implements Comparator<GalleryImageInfo> {
        C09881() {
        }

        public int compare(GalleryImageInfo a, GalleryImageInfo b) {
            long aAdded = a.dateAdded;
            long bAdded = b.dateAdded;
            if (aAdded > bAdded) {
                return -1;
            }
            if (aAdded < bAdded) {
                return 1;
            }
            return 0;
        }
    }

    /* renamed from: ru.ok.android.ui.image.pick.GalleryImageInfo.2 */
    static class C09892 implements Creator<GalleryImageInfo> {
        C09892() {
        }

        public GalleryImageInfo createFromParcel(Parcel source) {
            boolean broken = true;
            Uri uri = (Uri) source.readParcelable(null);
            String mimeType = source.readString();
            int rotation = source.readInt();
            long added = source.readLong();
            int width = source.readInt();
            int height = source.readInt();
            if (source.readByte() != (byte) 1) {
                broken = false;
            }
            return new GalleryImageInfo(uri, mimeType, rotation, added, width, height, broken);
        }

        public GalleryImageInfo[] newArray(int size) {
            return new GalleryImageInfo[size];
        }
    }

    static {
        COMPARATOR_DATE_ADDED = new C09881();
        CREATOR = new C09892();
    }

    public GalleryImageInfo(Uri uri, String mimeType, int rotation, long dateAdded, int width, int height, boolean broken) {
        this.uri = uri;
        this.mimeType = mimeType;
        this.rotation = rotation;
        this.dateAdded = dateAdded;
        this.width = width;
        this.height = height;
        this.broken = broken;
    }

    public boolean isBroken() {
        return this.broken;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (this.uri != ((GalleryImageInfo) o).uri) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "GalleryImageInfo{uri=" + this.uri + ", mimeType='" + this.mimeType + '\'' + ", rotation=" + this.rotation + ", dateAdded=" + this.dateAdded + ", width=" + this.width + ", height=" + this.height + ", broken=" + this.broken + '}';
    }

    public int hashCode() {
        return this.uri.hashCode();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        byte b = (byte) 0;
        dest.writeParcelable(this.uri, 0);
        dest.writeString(this.mimeType);
        dest.writeInt(this.rotation);
        dest.writeLong(this.dateAdded);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        if (this.broken) {
            b = (byte) 1;
        }
        dest.writeByte(b);
    }
}
