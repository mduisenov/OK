package ru.ok.model.stream.banner;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class VideoStat implements Parcelable {
    public static final Creator<VideoStat> CREATOR;
    public final int type;
    public final String url;

    /* renamed from: ru.ok.model.stream.banner.VideoStat.1 */
    static class C16111 implements Creator<VideoStat> {
        C16111() {
        }

        public VideoStat createFromParcel(Parcel source) {
            return new VideoStat(source);
        }

        public VideoStat[] newArray(int size) {
            return new VideoStat[size];
        }
    }

    public VideoStat(int type, String url) {
        this.type = type;
        this.url = url;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.url);
    }

    protected VideoStat(Parcel src) {
        this.type = src.readInt();
        this.url = src.readString();
    }

    static {
        CREATOR = new C16111();
    }
}
