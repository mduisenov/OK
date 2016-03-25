package ru.ok.android.ui.stream.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import ru.ok.model.stream.StreamPageKey;

public class StreamListPosition implements Parcelable {
    public static final Creator<StreamListPosition> CREATOR;
    public final int adapterPosition;
    public final long itemId;
    @NonNull
    public final StreamPageKey pageKey;
    public final int viewTop;

    /* renamed from: ru.ok.android.ui.stream.data.StreamListPosition.1 */
    static class C12311 implements Creator<StreamListPosition> {
        C12311() {
        }

        public StreamListPosition createFromParcel(Parcel source) {
            return new StreamListPosition(source);
        }

        public StreamListPosition[] newArray(int size) {
            return new StreamListPosition[size];
        }
    }

    public StreamListPosition(@NonNull StreamPageKey pageKey, long itemId, int viewTop, int adapterPosition) {
        this.pageKey = pageKey;
        this.itemId = itemId;
        this.viewTop = viewTop;
        this.adapterPosition = adapterPosition;
    }

    public String toString() {
        return "StreamListPosition[pageKey=" + this.pageKey + " itemId=" + this.itemId + " viewTop=" + this.viewTop + " adapterPosition=" + this.adapterPosition + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.pageKey, flags);
        dest.writeLong(this.itemId);
        dest.writeInt(this.viewTop);
        dest.writeInt(this.adapterPosition);
    }

    StreamListPosition(Parcel src) {
        this.pageKey = (StreamPageKey) src.readParcelable(StreamListPosition.class.getClassLoader());
        this.itemId = src.readLong();
        this.viewTop = src.readInt();
        this.adapterPosition = src.readInt();
    }

    static {
        CREATOR = new C12311();
    }
}
