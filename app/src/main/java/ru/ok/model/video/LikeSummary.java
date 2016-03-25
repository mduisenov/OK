package ru.ok.model.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class LikeSummary implements Parcelable {
    public static final Creator<LikeSummary> CREATOR;
    private int likeCount;
    private String likeId;
    private boolean likePossible;
    private long likeTimeMs;
    private boolean self;

    /* renamed from: ru.ok.model.video.LikeSummary.1 */
    static class C16351 implements Creator<LikeSummary> {
        C16351() {
        }

        public LikeSummary createFromParcel(Parcel parcel) {
            return new LikeSummary(parcel);
        }

        public LikeSummary[] newArray(int count) {
            return new LikeSummary[count];
        }
    }

    public LikeSummary(int likeCount, String likeId, long likeTimeMs, boolean self, boolean likePossible) {
        this.likeCount = likeCount;
        this.likeId = likeId;
        this.likeTimeMs = likeTimeMs;
        this.self = self;
        this.likePossible = likePossible;
    }

    public LikeSummary(Parcel parcel) {
        boolean z;
        boolean z2 = true;
        this.likeCount = parcel.readInt();
        this.likeId = parcel.readString();
        this.likeTimeMs = parcel.readLong();
        if (parcel.readByte() > null) {
            z = true;
        } else {
            z = false;
        }
        this.self = z;
        if (parcel.readByte() <= null) {
            z2 = false;
        }
        this.likePossible = z2;
    }

    public String getLikeId() {
        return this.likeId;
    }

    public boolean isSelf() {
        return this.self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public boolean isLikePossible() {
        return this.likePossible;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeInt(this.likeCount);
        dest.writeString(this.likeId);
        dest.writeLong(this.likeTimeMs);
        if (this.self) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        if (!this.likePossible) {
            i2 = 0;
        }
        dest.writeByte((byte) i2);
    }

    static {
        CREATOR = new C16351();
    }
}
