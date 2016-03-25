package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;

public class ImageUrl implements Parcelable, Serializable {
    public static final Creator<ImageUrl> CREATOR;
    private static final long serialVersionUID = 1;
    final int height;
    final String urlPrefix;
    final int width;

    /* renamed from: ru.ok.model.ImageUrl.1 */
    static class C15161 implements Creator<ImageUrl> {
        C15161() {
        }

        public ImageUrl createFromParcel(Parcel source) {
            return new ImageUrl(source);
        }

        public ImageUrl[] newArray(int size) {
            return new ImageUrl[size];
        }
    }

    public ImageUrl(String imageUrl, int width, int height) {
        this.urlPrefix = imageUrl;
        this.width = width;
        this.height = height;
    }

    public ImageUrl(Parcel parcel) {
        this.urlPrefix = parcel.readString();
        this.width = parcel.readInt();
        this.height = parcel.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.urlPrefix);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C15161();
    }

    public String getUrlPrefix() {
        return this.urlPrefix;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public String toString() {
        return "{BaseUrl: " + this.urlPrefix + "; Width: " + this.width + "; Height:" + this.height + "}";
    }
}
