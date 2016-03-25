package ru.ok.model.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class LiveStream implements Parcelable {
    public static final Creator<LiveStream> CREATOR;
    private long end;
    private long start;
    private String url;

    /* renamed from: ru.ok.model.video.LiveStream.1 */
    static class C16361 implements Creator<LiveStream> {
        C16361() {
        }

        public LiveStream createFromParcel(Parcel parcel) {
            return new LiveStream(parcel);
        }

        public LiveStream[] newArray(int count) {
            return new LiveStream[count];
        }
    }

    public LiveStream(long start, long end, String url) {
        this.start = start;
        this.end = end;
        this.url = url;
    }

    public LiveStream(Parcel parcel) {
        this.start = parcel.readLong();
        this.end = parcel.readLong();
        this.url = parcel.readString();
    }

    public long getStart() {
        return this.start;
    }

    public long getEnd() {
        return this.end;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.start);
        dest.writeLong(this.end);
        dest.writeString(this.url);
    }

    static {
        CREATOR = new C16361();
    }
}
