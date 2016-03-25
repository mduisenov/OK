package ru.ok.android.model.image;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;

public class PhotoOwner implements Parcelable {
    public static final Creator<PhotoOwner> CREATOR;
    private String id;
    private Parcelable ownerInfo;
    private int type;

    /* renamed from: ru.ok.android.model.image.PhotoOwner.1 */
    static class C03701 implements Creator<PhotoOwner> {
        C03701() {
        }

        public PhotoOwner createFromParcel(Parcel source) {
            PhotoOwner photoOwner = new PhotoOwner();
            photoOwner.readFromParcel(source);
            return photoOwner;
        }

        public PhotoOwner[] newArray(int size) {
            return new PhotoOwner[size];
        }
    }

    public PhotoOwner(String id, int type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Parcelable getOwnerInfo() {
        return this.ownerInfo;
    }

    public void setOwnerInfo(Parcelable ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public void tryPopulateOwner() {
        if (this.ownerInfo != null || this.type != 0) {
            return;
        }
        if (TextUtils.isEmpty(this.id) || OdnoklassnikiApplication.getCurrentUser().uid.equals(this.id)) {
            this.ownerInfo = new UserInfo(OdnoklassnikiApplication.getCurrentUser());
            this.id = ((UserInfo) this.ownerInfo).uid;
        }
    }

    public final boolean isCurrentUser() {
        return this.type == 0 && this.id.equals(OdnoklassnikiApplication.getCurrentUser().uid);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhotoOwner that = (PhotoOwner) o;
        if (this.type == that.type && TextUtils.equals(this.id, that.id)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((this.id != null ? this.id.hashCode() : 0) * 31) + this.type;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.type);
        dest.writeParcelable(this.ownerInfo, flags);
    }

    public void readFromParcel(Parcel src) {
        this.id = src.readString();
        this.type = src.readInt();
        this.ownerInfo = src.readParcelable(this.type == 0 ? UserInfo.class.getClassLoader() : GroupInfo.class.getClassLoader());
    }

    static {
        CREATOR = new C03701();
    }
}
