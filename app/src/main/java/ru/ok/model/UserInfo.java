package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Parcelable, Serializable, Comparable<UserInfo>, GeneralUserInfo {
    public static final Creator<UserInfo> CREATOR;
    private static final long serialVersionUID = 1;
    public int age;
    public boolean availableCall;
    public boolean availableVMail;
    public String bigPicUrl;
    public Date birthday;
    private transient String concatNameCached;
    public String firstName;
    public UserGenderType genderType;
    public boolean hasServiceInvisible;
    public boolean isAllDataAvailable;
    public String lastName;
    public long lastOnline;
    public Location location;
    public String name;
    @Nullable
    public UserOnlineType online;
    public String pic224;
    public String pic288;
    public String pic600;
    public String picUrl;
    public String pid;
    public boolean premiumProfile;
    public boolean privateProfile;
    public boolean showLock;
    public UserStatus status;
    String tag;
    public String uid;

    /* renamed from: ru.ok.model.UserInfo.1 */
    static class C15191 implements Creator<UserInfo> {
        C15191() {
        }

        public UserInfo createFromParcel(Parcel parcel) {
            return new UserInfo(parcel);
        }

        public UserInfo[] newArray(int count) {
            return new UserInfo[count];
        }
    }

    public static final class Builder {
        private String bigPicUrl;
        private boolean canCall;
        private boolean canVMail;
        private String firstName;
        private UserGenderType genderType;
        private boolean isPrivate;
        private String lastName;
        private long lastOnline;
        private String name;
        private UserOnlineType online;
        private String pic224;
        private String pic288;
        private String pic600;
        private String picUrl;
        private boolean showLock;
        private String uid;

        public UserInfo build() {
            return new UserInfo(this.uid, this.firstName, this.lastName, this.name, this.picUrl, this.pic224, this.pic288, this.pic600, 0, null, this.online, this.lastOnline, this.genderType, this.canCall, this.canVMail, null, null, this.bigPicUrl, this.isPrivate, false, false, null, null, false, this.showLock);
        }

        public Builder setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPicUrl(String picUrl) {
            this.picUrl = picUrl;
            return this;
        }

        public Builder setBigPicUrl(String bigPicUrl) {
            this.bigPicUrl = bigPicUrl;
            return this;
        }

        public Builder setOnline(UserOnlineType online) {
            this.online = online;
            return this;
        }

        public Builder setLastOnline(long lastOnline) {
            this.lastOnline = lastOnline;
            return this;
        }

        public Builder setCanCall(boolean canCall) {
            this.canCall = canCall;
            return this;
        }

        public Builder setCanVMail(boolean canVMail) {
            this.canVMail = canVMail;
            return this;
        }

        public Builder setGenderType(UserGenderType genderType) {
            this.genderType = genderType;
            return this;
        }

        public Builder setIsPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public Builder setShowLock(boolean showLock) {
            this.showLock = showLock;
            return this;
        }
    }

    public static class Location implements Parcelable, Serializable {
        public static final Creator<Location> CREATOR;
        private static final long serialVersionUID = 1;
        public final String city;
        public final String country;
        public final String countryCode;

        /* renamed from: ru.ok.model.UserInfo.Location.1 */
        static class C15201 implements Creator<Location> {
            C15201() {
            }

            public Location createFromParcel(Parcel parcel) {
                return new Location(parcel);
            }

            public Location[] newArray(int count) {
                return new Location[count];
            }
        }

        public Location(String countryCode, String country, String city) {
            this.countryCode = countryCode;
            this.country = country;
            this.city = city;
        }

        public Location(Parcel parcel) {
            this.countryCode = parcel.readString();
            this.country = parcel.readString();
            this.city = parcel.readString();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.countryCode);
            dest.writeString(this.country);
            dest.writeString(this.city);
        }

        static {
            CREATOR = new C15201();
        }

        public String toString() {
            return "Location{country='" + this.country + '\'' + ", city='" + this.city + '\'' + '}';
        }
    }

    public enum UserGenderType {
        MALE,
        FEMALE;

        public static UserGenderType byInteger(int integer) {
            return integer > 0 ? MALE : FEMALE;
        }

        public int toInteger() {
            return this == MALE ? 1 : 0;
        }

        public static UserGenderType byParserString(String str) {
            return "male".equalsIgnoreCase(str) ? MALE : FEMALE;
        }
    }

    public enum UserOnlineType {
        OFFLINE,
        WEB,
        MOBILE;

        public static UserOnlineType safeValueOf(String value) {
            if (TextUtils.isEmpty(value)) {
                return OFFLINE;
            }
            return valueOf(value);
        }
    }

    public UserInfo(UserInfo userInfo) {
        this.age = -1;
        this.uid = userInfo.uid;
        this.firstName = userInfo.firstName;
        this.lastName = userInfo.lastName;
        this.name = userInfo.name;
        this.picUrl = userInfo.picUrl;
        this.bigPicUrl = userInfo.bigPicUrl;
        this.age = userInfo.age;
        this.location = userInfo.location;
        this.online = userInfo.online;
        this.lastOnline = userInfo.lastOnline;
        this.genderType = userInfo.genderType;
        this.availableCall = userInfo.availableCall;
        this.availableVMail = userInfo.availableVMail;
        this.tag = userInfo.tag;
        this.pid = userInfo.pid;
        this.premiumProfile = userInfo.premiumProfile;
        this.hasServiceInvisible = userInfo.hasServiceInvisible;
        this.status = userInfo.status;
        this.birthday = userInfo.birthday;
        this.isAllDataAvailable = userInfo.isAllDataAvailable;
        this.showLock = userInfo.showLock;
    }

    public UserInfo(String uid, String firstName, String lastName, String name, String picUrl, String pic224, String pic288, String pic600, int age, Location location, UserOnlineType online, long lastOnline, UserGenderType genderType, boolean availableCall, boolean availableVMail, String tag, String pid, String bigPicUrl, boolean isPrivate, boolean isPremium, boolean hasServiceInvisible, UserStatus status, Date birthday, boolean isAllDataAvailable, boolean isShowLock) {
        this.age = -1;
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.picUrl = picUrl;
        this.pic224 = pic224;
        this.pic288 = pic288;
        this.pic600 = pic600;
        this.age = age;
        this.location = location;
        if (online == null) {
            online = UserOnlineType.OFFLINE;
        }
        this.online = online;
        this.lastOnline = lastOnline;
        this.genderType = genderType;
        this.availableCall = availableCall;
        this.availableVMail = availableVMail;
        this.tag = tag;
        this.pid = pid;
        this.bigPicUrl = bigPicUrl;
        this.privateProfile = isPrivate;
        this.premiumProfile = isPremium;
        this.hasServiceInvisible = hasServiceInvisible;
        this.status = status;
        this.birthday = birthday;
        this.isAllDataAvailable = isAllDataAvailable;
        this.showLock = isShowLock;
    }

    public UserInfo(String uid) {
        this(uid, null, null, null, null, null, null, null, 0, null, UserOnlineType.OFFLINE, 0, UserGenderType.MALE, false, false, null, null, null, false, false, false, null, null, false, false);
    }

    public int getObjectType() {
        return 0;
    }

    public String getTag() {
        return this.tag;
    }

    public boolean getAvailableCall() {
        return this.availableCall;
    }

    public boolean getAvailableVMail() {
        return this.availableVMail;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getConcatName() {
        if (this.concatNameCached == null) {
            StringBuilder sb = new StringBuilder();
            boolean firstEmpty = TextUtils.isEmpty(this.firstName);
            boolean lastEmpty = TextUtils.isEmpty(this.lastName);
            if (!firstEmpty) {
                sb.append(this.firstName);
            }
            if (!(firstEmpty || lastEmpty)) {
                sb.append(" ");
            }
            if (!lastEmpty) {
                sb.append(this.lastName);
            }
            this.concatNameCached = sb.toString();
        }
        return this.concatNameCached;
    }

    public String getAnyName() {
        return TextUtils.isEmpty(this.name) ? getConcatName() : this.name;
    }

    public int compareTo(UserInfo user) {
        return this.firstName.compareTo(user.firstName);
    }

    public String getId() {
        return this.uid;
    }

    public String getName() {
        return getAnyName();
    }

    public String getPicUrl() {
        return this.picUrl;
    }

    public String getPic288() {
        return this.pic288;
    }

    public String getPic600() {
        return this.pic600;
    }

    public boolean isPrivateProfile() {
        return this.privateProfile;
    }

    public boolean isPremiumProfile() {
        return this.premiumProfile;
    }

    public boolean isShowLock() {
        return this.showLock;
    }

    public String getAnyPicUrl() {
        return !TextUtils.isEmpty(this.bigPicUrl) ? this.bigPicUrl : this.picUrl;
    }

    public String toString() {
        return "UserInfo{'uid='" + this.uid + "', firstName='" + this.firstName + "', lastName='" + this.lastName + "'}";
    }

    public UserInfo(Parcel parcel) {
        boolean z;
        boolean z2 = true;
        this.age = -1;
        this.uid = parcel.readString();
        this.firstName = parcel.readString();
        this.lastName = parcel.readString();
        this.name = parcel.readString();
        this.picUrl = parcel.readString();
        this.pic224 = parcel.readString();
        this.pic288 = parcel.readString();
        this.pic600 = parcel.readString();
        this.online = (UserOnlineType) parcel.readSerializable();
        this.lastOnline = parcel.readLong();
        this.genderType = (UserGenderType) parcel.readSerializable();
        this.availableCall = parcel.readInt() != 0;
        if (parcel.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.availableVMail = z;
        this.tag = parcel.readString();
        this.pid = parcel.readString();
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.privateProfile = z;
        this.age = parcel.readInt();
        this.location = (Location) parcel.readParcelable(Location.class.getClassLoader());
        this.bigPicUrl = parcel.readString();
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.premiumProfile = z;
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.hasServiceInvisible = z;
        this.status = (UserStatus) parcel.readParcelable(UserInfo.class.getClassLoader());
        this.birthday = (Date) parcel.readSerializable();
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.isAllDataAvailable = z;
        if (parcel.readByte() != (byte) 1) {
            z2 = false;
        }
        this.showLock = z2;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        int i;
        int i2 = 1;
        parcel.writeString(this.uid);
        parcel.writeString(this.firstName);
        parcel.writeString(this.lastName);
        parcel.writeString(this.name);
        parcel.writeString(this.picUrl);
        parcel.writeString(this.pic224);
        parcel.writeString(this.pic288);
        parcel.writeString(this.pic600);
        parcel.writeSerializable(this.online);
        parcel.writeLong(this.lastOnline);
        parcel.writeSerializable(this.genderType);
        parcel.writeInt(this.availableCall ? 1 : 0);
        if (this.availableVMail) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeInt(i);
        parcel.writeString(this.tag);
        parcel.writeString(this.pid);
        if (this.privateProfile) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeByte((byte) i);
        parcel.writeInt(this.age);
        parcel.writeParcelable(this.location, flags);
        parcel.writeString(this.bigPicUrl);
        if (this.premiumProfile) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeByte((byte) i);
        if (this.hasServiceInvisible) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeByte((byte) i);
        parcel.writeParcelable(this.status, flags);
        parcel.writeSerializable(this.birthday);
        if (this.isAllDataAvailable) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeByte((byte) i);
        if (!this.showLock) {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new C15191();
    }

    public boolean equals(Object o) {
        return (o instanceof UserInfo) && TextUtils.equals(this.uid, ((UserInfo) o).uid);
    }

    public int hashCode() {
        return this.uid == null ? 0 : this.uid.hashCode();
    }

    public boolean isMan() {
        return this.genderType == UserGenderType.MALE;
    }
}
