package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public class MutualFriendsPreviewInfo implements Parcelable {
    public static final Creator<MutualFriendsPreviewInfo> CREATOR;
    public int totalCount;
    public ArrayList<UserInfo> users;

    /* renamed from: ru.ok.model.MutualFriendsPreviewInfo.1 */
    static class C15181 implements Creator<MutualFriendsPreviewInfo> {
        C15181() {
        }

        public MutualFriendsPreviewInfo createFromParcel(Parcel source) {
            return new MutualFriendsPreviewInfo(source);
        }

        public MutualFriendsPreviewInfo[] newArray(int size) {
            return new MutualFriendsPreviewInfo[size];
        }
    }

    public MutualFriendsPreviewInfo(int totalCount, ArrayList<UserInfo> users) {
        this.totalCount = totalCount;
        this.users = users;
    }

    public MutualFriendsPreviewInfo(Parcel parcel) {
        this.totalCount = parcel.readInt();
        this.users = new ArrayList();
        parcel.readTypedList(this.users, UserInfo.CREATOR);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.totalCount);
        dest.writeTypedList(this.users);
    }

    static {
        CREATOR = new C15181();
    }
}
