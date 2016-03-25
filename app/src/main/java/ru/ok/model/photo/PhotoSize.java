package ru.ok.model.photo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;

public final class PhotoSize implements Parcelable, Serializable, Comparable<PhotoSize> {
    public static final Creator<PhotoSize> CREATOR;
    private static final long serialVersionUID = 1;
    int height;
    String jsonKey;
    String url;
    int width;

    /* renamed from: ru.ok.model.photo.PhotoSize.1 */
    static class C15611 implements Creator<PhotoSize> {
        C15611() {
        }

        public PhotoSize createFromParcel(Parcel source) {
            return new PhotoSize(source);
        }

        public PhotoSize[] newArray(int size) {
            return new PhotoSize[size];
        }
    }

    PhotoSize() {
    }

    public PhotoSize(String url, int width) {
        this(url, width, -1, "defaultKey");
    }

    public PhotoSize(String url, int width, int height, String jsonKey) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.jsonKey = jsonKey;
    }

    public float getAspectRatio() {
        return ((float) this.width) / ((float) this.height);
    }

    public String getJsonKey() {
        return this.jsonKey;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getUrl() {
        return this.url;
    }

    public Uri getUri() {
        return Uri.parse(this.url);
    }

    public String toString() {
        return "PhotoSize[w=" + this.width + " h=" + this.height + " url=" + this.url + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.jsonKey);
    }

    protected PhotoSize(Parcel src) {
        this.url = src.readString();
        this.width = src.readInt();
        this.height = src.readInt();
        this.jsonKey = src.readString();
    }

    public int compareTo(PhotoSize ps) {
        if (this.width > ps.width) {
            return -1;
        }
        if (this.width < ps.width) {
            return 1;
        }
        if (this.height > ps.height) {
            return -1;
        }
        if (this.height < ps.height) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhotoSize photoSize = (PhotoSize) o;
        if (this.height == photoSize.height && this.width == photoSize.width) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.width * 31) + this.height;
    }

    static {
        CREATOR = new C15611();
    }
}
