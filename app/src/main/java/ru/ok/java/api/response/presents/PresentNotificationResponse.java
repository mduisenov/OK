package ru.ok.java.api.response.presents;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.model.presents.PresentInfo;

public class PresentNotificationResponse implements Parcelable {
    public static final Creator<PresentNotificationResponse> CREATOR;
    @NonNull
    public final PresentInfo presentInfo;
    @NonNull
    public final String receiveText;
    @Nullable
    public final String sendText;

    public PresentNotificationResponse(@NonNull Parcel parcel) {
        this.presentInfo = (PresentInfo) parcel.readParcelable(getClass().getClassLoader());
        this.receiveText = parcel.readString();
        this.sendText = parcel.readString();
    }

    public PresentNotificationResponse(@NonNull PresentInfo presentInfo, @NonNull String receiveText, @Nullable String sendText) {
        this.presentInfo = presentInfo;
        this.receiveText = receiveText;
        this.sendText = sendText;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.presentInfo, flags);
        dest.writeString(this.receiveText);
        dest.writeString(this.sendText);
    }

    static {
        CREATOR = new 1();
    }
}
