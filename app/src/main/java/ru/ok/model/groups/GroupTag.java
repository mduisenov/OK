package ru.ok.model.groups;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GroupTag implements Parcelable {
    public static final Creator<GroupTag> CREATOR;
    public final String tag;
    public final long tagId;
    public final long topicsCount;

    /* renamed from: ru.ok.model.groups.GroupTag.1 */
    static class C15261 implements Creator<GroupTag> {
        C15261() {
        }

        public GroupTag createFromParcel(Parcel source) {
            return new GroupTag(source);
        }

        public GroupTag[] newArray(int size) {
            return new GroupTag[size];
        }
    }

    public GroupTag(String tag, long tagId, long topicsCount) {
        this.tag = tag;
        this.tagId = tagId;
        this.topicsCount = topicsCount;
    }

    public GroupTag(Parcel parcel) {
        this(parcel.readString(), parcel.readLong(), parcel.readLong());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tag);
        dest.writeLong(this.tagId);
        dest.writeLong(this.topicsCount);
    }

    static {
        CREATOR = new C15261();
    }
}
