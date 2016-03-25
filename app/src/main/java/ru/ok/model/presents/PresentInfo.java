package ru.ok.model.presents;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Date;
import ru.ok.model.UserInfo;

public class PresentInfo implements Parcelable {
    public static final Creator<PresentInfo> CREATOR;
    @Nullable
    public final String holidayId;
    @NonNull
    public final String id;
    public final boolean isMystery;
    public final boolean isPrivate;
    public final boolean isWrapped;
    @Nullable
    public final String message;
    @Nullable
    public final Date presentTime;
    @NonNull
    public final PresentType presentType;
    public final int price;
    public final int priceInFreePresents;
    @Nullable
    public final UserInfo receiver;
    @Nullable
    public final UserInfo sender;
    @Nullable
    public final String senderLabel;
    @Nullable
    public final String trackId;

    /* renamed from: ru.ok.model.presents.PresentInfo.1 */
    static class C15771 implements Creator<PresentInfo> {
        C15771() {
        }

        public PresentInfo createFromParcel(Parcel parcel) {
            return new PresentInfo(parcel);
        }

        public PresentInfo[] newArray(int size) {
            return new PresentInfo[size];
        }
    }

    public PresentInfo(@NonNull Parcel parcel) {
        boolean z;
        boolean z2 = true;
        ClassLoader classLoader = getClass().getClassLoader();
        this.id = parcel.readString();
        this.presentType = (PresentType) parcel.readParcelable(classLoader);
        this.sender = (UserInfo) parcel.readParcelable(classLoader);
        this.receiver = (UserInfo) parcel.readParcelable(classLoader);
        this.trackId = parcel.readString();
        this.message = parcel.readString();
        this.senderLabel = parcel.readString();
        this.holidayId = parcel.readString();
        this.presentTime = (Date) parcel.readSerializable();
        this.price = parcel.readInt();
        this.priceInFreePresents = parcel.readInt();
        if (parcel.readInt() == 1) {
            z = true;
        } else {
            z = false;
        }
        this.isPrivate = z;
        if (parcel.readInt() == 1) {
            z = true;
        } else {
            z = false;
        }
        this.isMystery = z;
        if (parcel.readInt() != 1) {
            z2 = false;
        }
        this.isWrapped = z2;
    }

    public PresentInfo(@NonNull String id, @NonNull PresentType presentType, @Nullable UserInfo sender, @Nullable UserInfo receiver, @Nullable String trackId, @Nullable String message, @Nullable String senderLabel, @Nullable String holidayId, @Nullable Date presentTime, int price, int priceInFreePresents, boolean isPrivate, boolean isMystery, boolean isWrapped) {
        this.id = id;
        this.presentType = presentType;
        this.sender = sender;
        this.receiver = receiver;
        this.trackId = trackId;
        this.message = message;
        this.senderLabel = senderLabel;
        this.holidayId = holidayId;
        this.presentTime = presentTime;
        this.price = price;
        this.priceInFreePresents = priceInFreePresents;
        this.isPrivate = isPrivate;
        this.isMystery = isMystery;
        this.isWrapped = isWrapped;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeString(this.id);
        dest.writeParcelable(this.presentType, flags);
        dest.writeParcelable(this.sender, flags);
        dest.writeParcelable(this.receiver, flags);
        dest.writeString(this.trackId);
        dest.writeString(this.message);
        dest.writeString(this.senderLabel);
        dest.writeString(this.holidayId);
        dest.writeSerializable(this.presentTime);
        dest.writeInt(this.price);
        dest.writeInt(this.priceInFreePresents);
        if (this.isPrivate) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.isMystery) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.isWrapped) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    static {
        CREATOR = new C15771();
    }
}
