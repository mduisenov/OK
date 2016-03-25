package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class UnreadStreamPage extends StreamPage {
    public static final Creator<UnreadStreamPage> CREATOR;
    int totalUnreadFeedsCount;

    /* renamed from: ru.ok.model.stream.UnreadStreamPage.1 */
    static class C16041 implements Creator<UnreadStreamPage> {
        C16041() {
        }

        public UnreadStreamPage createFromParcel(Parcel source) {
            return new UnreadStreamPage(source);
        }

        public UnreadStreamPage[] newArray(int size) {
            return new UnreadStreamPage[size];
        }
    }

    public UnreadStreamPage(StreamPage page, int totalUnreadFeedsCount) {
        super(page.feeds, page.entities, page.key, page.bottomKey);
        setTopKey(page.topKey);
        this.totalUnreadFeedsCount = totalUnreadFeedsCount;
    }

    public int getTotalUnreadFeedsCount() {
        return this.totalUnreadFeedsCount;
    }

    UnreadStreamPage() {
    }

    UnreadStreamPage(Parcel src) {
        super(src);
        this.totalUnreadFeedsCount = src.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.totalUnreadFeedsCount);
    }

    static {
        CREATOR = new C16041();
    }
}
