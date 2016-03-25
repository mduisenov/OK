package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class LikeInfo extends ActionCountInfo {
    public static final Creator<LikeInfo> CREATOR;
    private static final long serialVersionUID = 1;
    public final String likeId;
    public final boolean likePossible;
    public final boolean unlikePossible;

    /* renamed from: ru.ok.model.stream.LikeInfo.1 */
    static class C15991 implements Creator<LikeInfo> {
        C15991() {
        }

        public LikeInfo createFromParcel(Parcel source) {
            return new LikeInfo(source);
        }

        public LikeInfo[] newArray(int size) {
            return new LikeInfo[size];
        }
    }

    public static class Builder {
        int count;
        long lastDate;
        String likeId;
        boolean likePossible;
        boolean self;
        boolean unlikePossible;

        public Builder(LikeInfo likeInfo) {
            if (likeInfo != null) {
                this.count = likeInfo.count;
                this.self = likeInfo.self;
                this.likeId = likeInfo.likeId;
                this.lastDate = likeInfo.lastDate;
                this.likePossible = likeInfo.likePossible;
                this.unlikePossible = likeInfo.unlikePossible;
            }
        }

        public Builder incrementCount() {
            if (this.count < Integer.MAX_VALUE) {
                this.count++;
            }
            return this;
        }

        public Builder decrementCount() {
            if (this.count > 0) {
                this.count--;
            }
            return this;
        }

        public Builder setSelf(boolean self) {
            this.self = self;
            return this;
        }

        public LikeInfo build() {
            return new LikeInfo(this.count, this.self, this.lastDate, this.likeId, this.likePossible, this.unlikePossible);
        }
    }

    public LikeInfo(int count, boolean self, long lastDate, String likeId, boolean likePossible, boolean unlikePossible) {
        super(count, self, lastDate);
        this.likeId = likeId;
        this.likePossible = likePossible;
        this.unlikePossible = unlikePossible;
    }

    public String toString() {
        return "LikeInfo[count=" + this.count + " self=" + this.self + " likeId=" + this.likeId + " lastDate=" + this.lastDate + " " + "likePossible=" + this.likePossible + " unlikePossible=" + this.unlikePossible + "]";
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        return TextUtils.equals(this.likeId, ((LikeInfo) o).likeId);
    }

    public int hashCode() {
        return (this.likeId == null ? 0 : 817504243 * this.likeId.hashCode()) + super.hashCode();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        super.writeToParcel(dest, flags);
        dest.writeString(this.likeId);
        if (this.likePossible) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.unlikePossible) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    protected LikeInfo(Parcel src) {
        boolean z;
        boolean z2 = true;
        super(src);
        this.likeId = src.readString();
        if (src.readInt() > 0) {
            z = true;
        } else {
            z = false;
        }
        this.likePossible = z;
        if (src.readInt() <= 0) {
            z2 = false;
        }
        this.unlikePossible = z2;
    }

    static {
        CREATOR = new C15991();
    }
}
