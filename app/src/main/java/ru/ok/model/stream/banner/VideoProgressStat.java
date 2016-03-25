package ru.ok.model.stream.banner;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class VideoProgressStat extends VideoStat {
    public static final Creator<VideoProgressStat> CREATOR;
    public final int positionSec;

    /* renamed from: ru.ok.model.stream.banner.VideoProgressStat.1 */
    static class C16101 implements Creator<VideoProgressStat> {
        C16101() {
        }

        public VideoProgressStat createFromParcel(Parcel source) {
            return new VideoProgressStat(source);
        }

        public VideoProgressStat[] newArray(int size) {
            return new VideoProgressStat[size];
        }
    }

    public VideoProgressStat(String url, int positionSec) {
        super(1, url);
        this.positionSec = positionSec;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.positionSec);
    }

    protected VideoProgressStat(Parcel src) {
        super(src);
        this.positionSec = src.readInt();
    }

    static {
        CREATOR = new C16101();
    }
}
