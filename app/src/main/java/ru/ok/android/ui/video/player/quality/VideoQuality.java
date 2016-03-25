package ru.ok.android.ui.video.player.quality;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class VideoQuality implements Parcelable {
    public static final Creator<VideoQuality> CREATOR;
    final int nameResId;
    final int type;
    final String url;

    /* renamed from: ru.ok.android.ui.video.player.quality.VideoQuality.1 */
    static class C14091 implements Creator<VideoQuality> {
        C14091() {
        }

        public VideoQuality createFromParcel(Parcel source) {
            return new VideoQuality(source.readInt(), source.readString(), source.readInt());
        }

        public VideoQuality[] newArray(int size) {
            return new VideoQuality[size];
        }
    }

    public VideoQuality(int nameResId, String url, int type) {
        this.nameResId = nameResId;
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public int getType() {
        return this.type;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.nameResId);
        dest.writeString(this.url);
        dest.writeInt(this.type);
    }

    static {
        CREATOR = new C14091();
    }
}
