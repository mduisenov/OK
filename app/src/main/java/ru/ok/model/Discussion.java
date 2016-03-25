package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.Serializable;

public class Discussion implements Parcelable, Serializable {
    public static final Creator<Discussion> CREATOR;
    private static final long serialVersionUID = 1;
    public final String id;
    public final String type;

    /* renamed from: ru.ok.model.Discussion.1 */
    static class C15121 implements Creator<Discussion> {
        C15121() {
        }

        public Discussion createFromParcel(Parcel parcel) {
            return new Discussion(parcel);
        }

        public Discussion[] newArray(int count) {
            return new Discussion[count];
        }
    }

    public Discussion(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = this.id == null ? 0 : this.id.hashCode();
        if (this.type != null) {
            i = this.type.hashCode();
        }
        return hashCode + i;
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Discussion discussion = (Discussion) o;
        if (TextUtils.equals(this.id, discussion.id) && TextUtils.equals(this.type, discussion.type)) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "Discussion{id='" + this.id + '\'' + ", type='" + this.type + '\'' + '}';
    }

    public Discussion(Parcel parcel) {
        this.id = parcel.readString();
        this.type = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.type);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C15121();
    }
}
