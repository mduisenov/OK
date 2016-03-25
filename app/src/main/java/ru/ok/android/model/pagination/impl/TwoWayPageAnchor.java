package ru.ok.android.model.pagination.impl;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import ru.ok.android.model.pagination.PageAnchor;

public class TwoWayPageAnchor implements PageAnchor {
    public static final Creator<TwoWayPageAnchor> CREATOR;
    private final String anchor;

    /* renamed from: ru.ok.android.model.pagination.impl.TwoWayPageAnchor.1 */
    static class C03771 implements Creator<TwoWayPageAnchor> {
        C03771() {
        }

        public TwoWayPageAnchor createFromParcel(Parcel source) {
            return new TwoWayPageAnchor(source.readString());
        }

        public TwoWayPageAnchor[] newArray(int size) {
            return new TwoWayPageAnchor[size];
        }
    }

    public TwoWayPageAnchor(@NonNull String anchor) {
        this.anchor = anchor;
    }

    @NonNull
    public String getBackwardAnchor() {
        return this.anchor;
    }

    @NonNull
    public String getForwardAnchor() {
        return this.anchor;
    }

    public String toString() {
        return "TwoWayPageAnchor{anchor='" + this.anchor + '\'' + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.anchor);
    }

    static {
        CREATOR = new C03771();
    }
}
