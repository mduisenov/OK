package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;

public class ActionCountInfo implements Parcelable, Serializable {
    public static final Creator<ActionCountInfo> CREATOR;
    private static final long serialVersionUID = -7024565075601911779L;
    public final int count;
    public final long lastDate;
    public final boolean self;

    /* renamed from: ru.ok.model.stream.ActionCountInfo.1 */
    static class C15941 implements Creator<ActionCountInfo> {
        C15941() {
        }

        public ActionCountInfo createFromParcel(Parcel source) {
            return new ActionCountInfo(source);
        }

        public ActionCountInfo[] newArray(int size) {
            return new ActionCountInfo[size];
        }
    }

    public ActionCountInfo(int count, boolean self, long lastDate) {
        this.count = count;
        this.self = self;
        this.lastDate = lastDate;
    }

    public String toString() {
        return "ActionCountInfo[count=" + this.count + " self=" + this.self + " lastDate=" + this.lastDate + "]";
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        ActionCountInfo other = (ActionCountInfo) o;
        if (this.count == other.count && this.self == other.self && this.lastDate == other.lastDate) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((this.self ? 1739458477 : 0) + (1405240271 * this.count)) + ((int) (this.lastDate * 829084771));
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeInt(this.self ? 1 : 0);
        dest.writeLong(this.lastDate);
    }

    protected ActionCountInfo(Parcel src) {
        this.count = src.readInt();
        this.self = src.readInt() != 0;
        this.lastDate = src.readLong();
    }

    static {
        CREATOR = new C15941();
    }
}
