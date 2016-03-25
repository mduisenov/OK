package ru.ok.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UpdateProfileFieldsFlags implements Parcelable {
    public static final Creator<UpdateProfileFieldsFlags> CREATOR;
    public boolean isAvatarSeparately;
    public boolean isAvatarVisible;
    public boolean isBackButtonDisabled;
    public boolean isBirthdayRequired;
    public boolean isFirstNameLastNameRequired;

    /* renamed from: ru.ok.android.model.UpdateProfileFieldsFlags.1 */
    static class C03471 implements Creator<UpdateProfileFieldsFlags> {
        C03471() {
        }

        public UpdateProfileFieldsFlags createFromParcel(Parcel parcel) {
            return new UpdateProfileFieldsFlags(parcel);
        }

        public UpdateProfileFieldsFlags[] newArray(int size) {
            return new UpdateProfileFieldsFlags[size];
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeInt(this.isFirstNameLastNameRequired ? 1 : 0);
        if (this.isBirthdayRequired) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.isBackButtonDisabled) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.isAvatarVisible) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.isAvatarSeparately) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    public UpdateProfileFieldsFlags(Parcel parcel) {
        boolean z;
        boolean z2 = true;
        this.isFirstNameLastNameRequired = parcel.readInt() != 0;
        if (parcel.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.isBirthdayRequired = z;
        if (parcel.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.isBackButtonDisabled = z;
        if (parcel.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.isAvatarVisible = z;
        if (parcel.readInt() == 0) {
            z2 = false;
        }
        this.isAvatarSeparately = z2;
    }

    static {
        CREATOR = new C03471();
    }
}
