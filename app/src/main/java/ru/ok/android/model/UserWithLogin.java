package ru.ok.android.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.model.UserInfo;

public class UserWithLogin extends UserInfo {
    public static final Creator<UserWithLogin> CREATOR;
    public String login;

    /* renamed from: ru.ok.android.model.UserWithLogin.1 */
    static class C03481 implements Creator<UserWithLogin> {
        C03481() {
        }

        public UserWithLogin createFromParcel(Parcel parcel) {
            return new UserWithLogin(parcel);
        }

        public UserWithLogin[] newArray(int count) {
            return new UserWithLogin[count];
        }
    }

    public UserWithLogin(UserInfo userInfo) {
        super(userInfo);
    }

    public UserWithLogin(String uid) {
        super(uid);
    }

    public UserWithLogin(Parcel parcel) {
        super(parcel);
        this.login = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(this.login);
    }

    static {
        CREATOR = new C03481();
    }
}
