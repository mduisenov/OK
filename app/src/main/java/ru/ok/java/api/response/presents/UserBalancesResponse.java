package ru.ok.java.api.response.presents;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;

public class UserBalancesResponse implements Parcelable {
    public static final Creator<UserBalancesResponse> CREATOR;
    public final int freeGiftsCount;
    public final int userBalanceInOks;

    protected UserBalancesResponse(Parcel in) {
        this.userBalanceInOks = in.readInt();
        this.freeGiftsCount = in.readInt();
    }

    public UserBalancesResponse(int userBalanceInOks, int freeGiftsCount) {
        this.userBalanceInOks = userBalanceInOks;
        this.freeGiftsCount = freeGiftsCount;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.userBalanceInOks);
        dest.writeInt(this.freeGiftsCount);
    }

    static {
        CREATOR = new 1();
    }
}
