package ru.ok.model.groups;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GroupsTopCategoryItem implements Parcelable {
    public static final Creator<GroupsTopCategoryItem> CREATOR;
    public final String id;
    public String name;
    public final String nameKey;

    /* renamed from: ru.ok.model.groups.GroupsTopCategoryItem.1 */
    static class C15271 implements Creator<GroupsTopCategoryItem> {
        C15271() {
        }

        public GroupsTopCategoryItem createFromParcel(Parcel source) {
            return new GroupsTopCategoryItem(source);
        }

        public GroupsTopCategoryItem[] newArray(int size) {
            return new GroupsTopCategoryItem[size];
        }
    }

    public GroupsTopCategoryItem(String id, String nameKey) {
        this.id = id;
        this.nameKey = nameKey;
    }

    public GroupsTopCategoryItem(Parcel parcel) {
        this(parcel.readString(), parcel.readString());
        this.name = parcel.readString();
    }

    public String toString() {
        return "GroupsTopCategoryItem{id='" + this.id + '\'' + ", nameKey='" + this.nameKey + '\'' + ", name='" + this.name + '\'' + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nameKey);
        dest.writeString(this.name);
    }

    static {
        CREATOR = new C15271();
    }
}
