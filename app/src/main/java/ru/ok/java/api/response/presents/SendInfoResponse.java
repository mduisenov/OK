package ru.ok.java.api.response.presents;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import ru.ok.model.UserInfo;
import ru.ok.model.presents.PresentInfo;

public class SendInfoResponse implements Parcelable {
    public static final Creator<SendInfoResponse> CREATOR;
    @NonNull
    public UserBalancesResponse balancesResponse;
    @NonNull
    public String localizedName;
    @NonNull
    public PresentInfo presentInfo;
    @NonNull
    public UserInfo userInfo;

    public SendInfoResponse(@NonNull Parcel parcel) {
        ClassLoader classLoader = getClass().getClassLoader();
        this.presentInfo = (PresentInfo) parcel.readParcelable(classLoader);
        this.userInfo = (UserInfo) parcel.readParcelable(classLoader);
        this.balancesResponse = (UserBalancesResponse) parcel.readParcelable(classLoader);
        this.localizedName = parcel.readString();
    }

    public SendInfoResponse(@NonNull UserInfo userInfo, @NonNull PresentInfo presentInfo, @NonNull String localizedName, @NonNull UserBalancesResponse balancesResponse) {
        this.userInfo = userInfo;
        this.presentInfo = presentInfo;
        this.localizedName = localizedName;
        this.balancesResponse = balancesResponse;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.presentInfo, flags);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeParcelable(this.balancesResponse, flags);
        dest.writeString(this.localizedName);
    }

    static {
        CREATOR = new 1();
    }
}
