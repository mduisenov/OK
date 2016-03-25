package ru.ok.model.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Advertisement implements Parcelable {
    public static final Creator<Advertisement> CREATOR;
    private String contentId;
    private int duration;
    private int siteZone;
    private int slot;

    /* renamed from: ru.ok.model.video.Advertisement.1 */
    static class C16341 implements Creator<Advertisement> {
        C16341() {
        }

        public Advertisement createFromParcel(Parcel parcel) {
            return new Advertisement(parcel);
        }

        public Advertisement[] newArray(int count) {
            return new Advertisement[count];
        }
    }

    public Advertisement(int slot, int duration, int siteZone, String contentId) {
        this.slot = slot;
        this.duration = duration;
        this.siteZone = siteZone;
        this.contentId = contentId;
    }

    public int getSlot() {
        return this.slot;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getSiteZone() {
        return this.siteZone;
    }

    public String getContentId() {
        return this.contentId;
    }

    public Advertisement(Parcel parcel) {
        this.slot = parcel.readInt();
        this.duration = parcel.readInt();
        this.siteZone = parcel.readInt();
        this.contentId = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.slot);
        dest.writeInt(this.duration);
        dest.writeInt(this.siteZone);
        dest.writeString(this.contentId);
    }

    static {
        CREATOR = new C16341();
    }
}
