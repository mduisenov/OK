package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class LikeInfoContext extends LikeInfo {
    public static final Creator<LikeInfoContext> CREATOR;
    private static final long serialVersionUID = 166117404018711409L;
    public final String entityId;
    public final int entityType;

    /* renamed from: ru.ok.model.stream.LikeInfoContext.1 */
    static class C16001 implements Creator<LikeInfoContext> {
        C16001() {
        }

        public LikeInfoContext createFromParcel(Parcel source) {
            return new LikeInfoContext(source);
        }

        public LikeInfoContext[] newArray(int size) {
            return new LikeInfoContext[size];
        }
    }

    public LikeInfoContext(LikeInfo likeInfo, int entityType, String entityId) {
        this(likeInfo.count, likeInfo.self, likeInfo.lastDate, likeInfo.likeId, likeInfo.likePossible, likeInfo.unlikePossible, entityType, entityId);
    }

    public LikeInfoContext(int count, boolean self, long lastDate, String likeId, boolean likePossible, boolean unlikePossible, int entityType, String entityId) {
        super(count, self, lastDate, likeId, likePossible, unlikePossible);
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String toString() {
        return "LikeInfoContext[type=" + this.entityType + " id=" + this.entityId + "]";
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.entityType);
        dest.writeString(this.entityId);
    }

    protected LikeInfoContext(Parcel src) {
        super(src);
        this.entityType = src.readInt();
        this.entityId = src.readString();
    }

    static {
        CREATOR = new C16001();
    }
}
