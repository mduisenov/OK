package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import java.io.Serializable;
import ru.ok.model.Discussion;

public final class DiscussionSummary implements Parcelable, Serializable {
    public static final Creator<DiscussionSummary> CREATOR;
    private static final long serialVersionUID = 1;
    public final int commentsCount;
    @NonNull
    public final Discussion discussion;

    /* renamed from: ru.ok.model.stream.DiscussionSummary.1 */
    static class C15951 implements Creator<DiscussionSummary> {
        C15951() {
        }

        public DiscussionSummary createFromParcel(Parcel source) {
            return new DiscussionSummary((Discussion) source.readParcelable(Discussion.class.getClassLoader()), source.readInt());
        }

        public DiscussionSummary[] newArray(int size) {
            return new DiscussionSummary[size];
        }
    }

    public DiscussionSummary(@NonNull Discussion discussion, int commentsCount) {
        this.discussion = discussion;
        this.commentsCount = commentsCount;
    }

    public String toString() {
        return "DiscussionSummary{discussion=" + this.discussion + ", commentsCount=" + this.commentsCount + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.discussion, flags);
        dest.writeInt(this.commentsCount);
    }

    static {
        CREATOR = new C15951();
    }
}
