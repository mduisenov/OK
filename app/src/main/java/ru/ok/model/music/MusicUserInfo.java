package ru.ok.model.music;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.Location;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;

public class MusicUserInfo extends UserInfo {
    public static final Creator<MusicUserInfo> CREATOR;
    public final long lastAddTime;
    public final int tracksCount;

    /* renamed from: ru.ok.model.music.MusicUserInfo.1 */
    static class C15471 implements Creator<MusicUserInfo> {
        C15471() {
        }

        public MusicUserInfo createFromParcel(Parcel parcel) {
            return new MusicUserInfo(parcel);
        }

        public MusicUserInfo[] newArray(int count) {
            return new MusicUserInfo[count];
        }
    }

    public MusicUserInfo(String uid, String firstName, String lastName, String name, String picUrl, int age, Location location, UserOnlineType online, long lastOnline, UserGenderType genderType, boolean availableCall, boolean availableVMail, String tag, int tracksCount, long lastTime, String pid) {
        super(uid, firstName, lastName, name, picUrl, null, null, null, age, location, online, lastOnline, genderType, availableCall, availableVMail, tag, pid, null, false, false, false, null, null, false, false);
        this.tracksCount = tracksCount;
        this.lastAddTime = lastTime;
    }

    public MusicUserInfo(Parcel parcel) {
        super(parcel);
        this.tracksCount = parcel.readInt();
        this.lastAddTime = parcel.readLong();
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(this.tracksCount);
        parcel.writeLong(this.lastAddTime);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C15471();
    }
}
