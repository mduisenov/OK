package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class StreamPagingInfo implements Parcelable {
    public static final Creator<StreamPagingInfo> CREATOR;
    public final String anchor;
    public final int generation;
    public final boolean markAsRead;
    public final int offset;

    /* renamed from: ru.ok.model.stream.StreamPagingInfo.1 */
    static class C16031 implements Creator<StreamPagingInfo> {
        C16031() {
        }

        public StreamPagingInfo createFromParcel(Parcel source) {
            return new StreamPagingInfo(source);
        }

        public StreamPagingInfo[] newArray(int size) {
            return new StreamPagingInfo[size];
        }
    }

    public String toString() {
        return "StreamPagingInfo[gen=" + this.generation + " off=" + this.offset + " anchor=" + this.anchor + " markAsRead=" + this.markAsRead + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.generation);
        dest.writeInt(this.offset);
        dest.writeString(this.anchor);
        dest.writeInt(this.markAsRead ? 1 : 0);
    }

    protected StreamPagingInfo(Parcel src) {
        this.generation = src.readInt();
        this.offset = src.readInt();
        this.anchor = src.readString();
        this.markAsRead = src.readInt() != 0;
    }

    static {
        CREATOR = new C16031();
    }
}
