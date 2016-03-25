package ru.ok.model.guest;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public class UsersResult implements Parcelable {
    public static final Creator<UsersResult> CREATOR;
    public boolean hasMore;
    public final long last_view_date;
    public final String pagingAnchor;
    public final int totalCount;
    public final ArrayList<UserInfoGuest> users;

    /* renamed from: ru.ok.model.guest.UsersResult.1 */
    static class C15291 implements Creator<UsersResult> {
        C15291() {
        }

        public UsersResult createFromParcel(Parcel parcel) {
            return new UsersResult(parcel);
        }

        public UsersResult[] newArray(int count) {
            return new UsersResult[count];
        }
    }

    public UsersResult(ArrayList<UserInfoGuest> arrayList, boolean hasMore, int totalCount, String pagingAnchor, long last_view_date) {
        ArrayList arrayList2;
        if (arrayList == null) {
            arrayList2 = new ArrayList(0);
        }
        this.users = arrayList2;
        this.hasMore = hasMore;
        this.totalCount = totalCount;
        this.pagingAnchor = pagingAnchor;
        this.last_view_date = last_view_date;
    }

    public UsersResult(Parcel parcel) {
        this.users = parcel.readArrayList(UserInfoGuest.class.getClassLoader());
        this.hasMore = parcel.readInt() != 0;
        this.totalCount = parcel.readInt();
        this.pagingAnchor = parcel.readString();
        this.last_view_date = parcel.readLong();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(this.users);
        parcel.writeInt(this.hasMore ? 1 : 0);
        parcel.writeInt(this.totalCount);
        parcel.writeString(this.pagingAnchor);
        parcel.writeLong(this.last_view_date);
    }

    static {
        CREATOR = new C15291();
    }
}
