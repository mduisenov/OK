package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class GroupDiscussion extends Discussion {
    public static final Creator<GroupDiscussion> CREATOR;
    public final String groupId;

    /* renamed from: ru.ok.model.GroupDiscussion.1 */
    static class C15131 implements Creator<GroupDiscussion> {
        C15131() {
        }

        public GroupDiscussion createFromParcel(Parcel source) {
            return new GroupDiscussion(source);
        }

        public GroupDiscussion[] newArray(int size) {
            return new GroupDiscussion[size];
        }
    }

    public GroupDiscussion(String discussionId, String discussionType, String groupId) {
        super(discussionId, discussionType);
        this.groupId = groupId;
    }

    public boolean equals(Object o) {
        return super.equals(o) && TextUtils.equals(((GroupDiscussion) o).groupId, this.groupId);
    }

    public int hashCode() {
        return new StringBuilder().append(super.hashCode() * 192635453).append(this.groupId).toString() == null ? 0 : this.groupId.hashCode();
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(this.groupId);
    }

    GroupDiscussion(Parcel parcel) {
        super(parcel);
        this.groupId = parcel.readString();
    }

    static {
        CREATOR = new C15131();
    }
}
