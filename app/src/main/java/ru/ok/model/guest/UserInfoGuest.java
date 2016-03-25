package ru.ok.model.guest;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.model.UserInfo;

public class UserInfoGuest extends UserInfo {
    public static final Creator<UserInfoGuest> CREATOR;
    public int commons;
    public long date;
    public boolean invisible;
    public boolean isNew;

    /* renamed from: ru.ok.model.guest.UserInfoGuest.1 */
    static class C15281 implements Creator<UserInfoGuest> {
        C15281() {
        }

        public UserInfoGuest createFromParcel(Parcel parcel) {
            return new UserInfoGuest(parcel);
        }

        public UserInfoGuest[] newArray(int count) {
            return new UserInfoGuest[count];
        }
    }

    public UserInfoGuest(UserInfo userInfo) {
        super(userInfo);
        this.invisible = false;
        this.date = 0;
        this.commons = -1;
    }

    public UserInfoGuest(Parcel parcel) {
        boolean z = false;
        super(parcel);
        this.invisible = false;
        this.date = 0;
        this.commons = -1;
        if (parcel.readInt() != 0) {
            z = true;
        }
        this.invisible = z;
        this.date = parcel.readLong();
        this.commons = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(this.invisible ? 1 : 0);
        parcel.writeLong(this.date);
        parcel.writeInt(this.commons);
    }

    static {
        CREATOR = new C15281();
    }
}
