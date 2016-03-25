package ru.ok.android.ui.image.pick;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public final class DeviceGalleryInfo implements Parcelable {
    public static final Creator<DeviceGalleryInfo> CREATOR;
    public final int id;
    public final String name;
    public final List<GalleryImageInfo> photos;

    /* renamed from: ru.ok.android.ui.image.pick.DeviceGalleryInfo.1 */
    static class C09871 implements Creator<DeviceGalleryInfo> {
        C09871() {
        }

        public DeviceGalleryInfo createFromParcel(Parcel source) {
            return new DeviceGalleryInfo(source);
        }

        public DeviceGalleryInfo[] newArray(int size) {
            return new DeviceGalleryInfo[size];
        }
    }

    public DeviceGalleryInfo(int id, String name) {
        this.photos = new ArrayList();
        this.id = id;
        this.name = name;
    }

    public DeviceGalleryInfo(Parcel src) {
        this.photos = new ArrayList();
        this.id = src.readInt();
        this.name = src.readString();
        src.readTypedList(this.photos, GalleryImageInfo.CREATOR);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.photos);
    }

    static {
        CREATOR = new C09871();
    }
}
