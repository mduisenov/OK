package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;

public class UserStatus implements Parcelable, Serializable {
    public static final Creator<UserStatus> CREATOR;
    private static final long serialVersionUID = 1;
    public final long date;
    public final String id;
    public final String text;
    public final long trackId;

    /* renamed from: ru.ok.model.UserStatus.1 */
    static class C15211 implements Creator<UserStatus> {
        C15211() {
        }

        public UserStatus createFromParcel(Parcel parcel) {
            return new UserStatus(parcel);
        }

        public UserStatus[] newArray(int count) {
            return new UserStatus[count];
        }
    }

    public UserStatus(String id, String text, long date, long trackId) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.trackId = trackId;
    }

    public UserStatus(Parcel parcel) {
        this.id = parcel.readString();
        this.text = parcel.readString();
        this.date = parcel.readLong();
        this.trackId = parcel.readLong();
    }

    public boolean isMusicStatus() {
        return this.trackId != 0;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.id);
        parcel.writeString(this.text);
        parcel.writeLong(this.date);
        parcel.writeLong(this.trackId);
    }

    static {
        CREATOR = new C15211();
    }
}
