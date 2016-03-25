package ru.ok.model.photo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PhotoTag implements Parcelable {
    public static final Creator<PhotoTag> CREATOR;
    private String index;
    private String text;
    private String userId;
    private int f126x;
    private int f127y;

    /* renamed from: ru.ok.model.photo.PhotoTag.1 */
    static class C15621 implements Creator<PhotoTag> {
        C15621() {
        }

        public PhotoTag createFromParcel(Parcel source) {
            PhotoTag photoTag = new PhotoTag();
            photoTag.readFromParcel(source);
            return photoTag;
        }

        public PhotoTag[] newArray(int size) {
            return new PhotoTag[size];
        }
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getY() {
        return this.f127y;
    }

    public void setY(int y) {
        this.f127y = y;
    }

    public int getX() {
        return this.f126x;
    }

    public void setX(int x) {
        this.f126x = x;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.index);
        dest.writeInt(this.f126x);
        dest.writeInt(this.f127y);
        dest.writeString(this.text);
    }

    public final void readFromParcel(Parcel parcel) {
        this.userId = parcel.readString();
        this.index = parcel.readString();
        this.f126x = parcel.readInt();
        this.f127y = parcel.readInt();
        this.text = parcel.readString();
    }

    static {
        CREATOR = new C15621();
    }
}
