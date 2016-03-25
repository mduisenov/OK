package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GroupSubCategory implements Parcelable {
    public static final Creator<GroupSubCategory> CREATOR;
    String id;
    String name;

    /* renamed from: ru.ok.model.GroupSubCategory.1 */
    static class C15151 implements Creator<GroupSubCategory> {
        C15151() {
        }

        public GroupSubCategory createFromParcel(Parcel source) {
            return new GroupSubCategory(source);
        }

        public GroupSubCategory[] newArray(int count) {
            return new GroupSubCategory[count];
        }
    }

    GroupSubCategory() {
    }

    public GroupSubCategory(Parcel source) {
        readFromParcel(source);
    }

    public GroupSubCategory(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    public final void readFromParcel(Parcel src) {
        this.id = src.readString();
        this.name = src.readString();
    }

    static {
        CREATOR = new C15151();
    }
}
