package ru.ok.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import java.io.Serializable;

public class GroupInfo implements Parcelable, Serializable, GeneralUserInfo {
    public static final Creator<GroupInfo> CREATOR;
    private static final long serialVersionUID = 1;
    protected Address address;
    protected String adminUid;
    protected String avatarUrl;
    protected String bigPicUrl;
    protected boolean business;
    protected long createdMs;
    protected String description;
    protected long end_date;
    protected int flags;
    protected String id;
    protected boolean isAllDataAvailable;
    protected Location location;
    protected int membersCount;
    protected String name;
    protected String phone;
    protected String photoId;
    protected String scope;
    protected long start_date;
    protected String status;
    protected GroupSubCategory subCategory;
    protected GroupType type;
    protected long unreadEventsCount;
    protected String webUrl;

    /* renamed from: ru.ok.model.GroupInfo.1 */
    static class C15141 implements Creator<GroupInfo> {
        C15141() {
        }

        public GroupInfo createFromParcel(Parcel source) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.readFromParcel(source);
            return groupInfo;
        }

        public GroupInfo[] newArray(int count) {
            return new GroupInfo[count];
        }
    }

    public int getObjectType() {
        return 1;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoId() {
        return this.photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getBigPicUrl() {
        return this.bigPicUrl;
    }

    public String getAnyPicUrl() {
        return !TextUtils.isEmpty(this.bigPicUrl) ? this.bigPicUrl : getPicUrl();
    }

    public void setBigPicUrl(String bigPicUrl) {
        this.bigPicUrl = bigPicUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCanAddAlbum() {
        return (this.flags & 4) == 4;
    }

    public void setCanAddAlbum(boolean canAddAlbum) {
        if (canAddAlbum) {
            this.flags |= 4;
        } else {
            this.flags &= -5;
        }
    }

    public boolean isCanAddFriends() {
        return (this.flags & 16) == 16;
    }

    public boolean isCanPostMediaTopic() {
        return (this.flags & 32) == 32;
    }

    public void setCanChangeAvatar(boolean canChangeAvatar) {
        if (canChangeAvatar) {
            this.flags |= 8;
        } else {
            this.flags &= -9;
        }
    }

    public void setCanAddFriends(boolean canAddFriends) {
        if (canAddFriends) {
            this.flags |= 16;
        } else {
            this.flags &= -17;
        }
    }

    public void setCanPostMediaTopic(boolean canPostMt) {
        if (canPostMt) {
            this.flags |= 32;
        } else {
            this.flags &= -33;
        }
    }

    private void setFlag(int flag, boolean value) {
        if (value) {
            this.flags |= flag;
        } else {
            this.flags &= flag ^ -1;
        }
    }

    private boolean hasFlag(int flag) {
        return (this.flags & flag) == flag;
    }

    public void setCanSuggestMediaTopic(boolean canSuggestMt) {
        setFlag(64, canSuggestMt);
    }

    public boolean isCanSuggestMediaTopic() {
        return hasFlag(64);
    }

    public void setCanPostDelayedMediaTopic(boolean canPostDelayedMt) {
        setFlag(NotificationCompat.FLAG_HIGH_PRIORITY, canPostDelayedMt);
    }

    public int getMembersCount() {
        return this.membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public Uri getAvatarUrl() {
        return this.avatarUrl == null ? null : Uri.parse(this.avatarUrl);
    }

    public String getPicUrl() {
        return this.avatarUrl;
    }

    public boolean isPrivateGroup() {
        return (this.flags & 1) == 1;
    }

    public boolean isDisabled() {
        return "DISABLED".equals(this.status);
    }

    public void setPrivateGroup(boolean privateGroup) {
        if (privateGroup) {
            this.flags |= 1;
        } else {
            this.flags &= -2;
        }
    }

    public boolean isPremium() {
        return (this.flags & 2) == 2;
    }

    public void setPremium(boolean premium) {
        if (premium) {
            this.flags |= 2;
        } else {
            this.flags &= -3;
        }
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public GroupType getType() {
        return this.type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public int describeContents() {
        return 0;
    }

    public String getAdminUid() {
        return this.adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
    }

    public long getCreatedMs() {
        return this.createdMs;
    }

    public void setCreatedMs(long createdMs) {
        this.createdMs = createdMs;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setStartDate(long date) {
        this.start_date = date;
    }

    public void setEndDate(long date) {
        this.end_date = date;
    }

    public long getStartDate() {
        return this.start_date;
    }

    public long getEndDate() {
        return this.end_date;
    }

    public String getWebUrl() {
        return this.webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public GroupSubCategory getSubCategory() {
        return this.subCategory;
    }

    public void setSubCategory(GroupSubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public boolean isBusiness() {
        return this.business;
    }

    public void setBusiness(boolean business) {
        this.business = business;
    }

    public boolean isAllDataAvailable() {
        return this.isAllDataAvailable;
    }

    public void setAllDataAvailable(boolean isAllDataAvailable) {
        this.isAllDataAvailable = isAllDataAvailable;
    }

    public void writeToParcel(Parcel dest, int flags) {
        byte b = (byte) 1;
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeInt(this.membersCount);
        dest.writeString(this.avatarUrl);
        dest.writeInt(flags);
        dest.writeString(this.photoId);
        dest.writeString(this.bigPicUrl);
        dest.writeByte(this.type == null ? (byte) 0 : (byte) 1);
        if (this.type != null) {
            dest.writeInt(this.type.ordinal());
        }
        dest.writeString(this.adminUid);
        dest.writeLong(this.createdMs);
        dest.writeParcelable(this.location, flags);
        dest.writeParcelable(this.address, flags);
        dest.writeString(this.scope);
        dest.writeLong(this.start_date);
        dest.writeLong(this.end_date);
        dest.writeString(this.webUrl);
        dest.writeString(this.phone);
        dest.writeParcelable(this.subCategory, flags);
        dest.writeByte(this.business ? (byte) 1 : (byte) 0);
        if (!this.isAllDataAvailable) {
            b = (byte) 0;
        }
        dest.writeByte(b);
        dest.writeLong(this.unreadEventsCount);
        dest.writeString(this.status);
    }

    public final void readFromParcel(Parcel src) {
        boolean z = true;
        this.id = src.readString();
        this.name = src.readString();
        this.description = src.readString();
        this.membersCount = src.readInt();
        this.avatarUrl = src.readString();
        this.flags = src.readInt();
        this.photoId = src.readString();
        this.bigPicUrl = src.readString();
        if (src.readByte() != null) {
            this.type = GroupType.values()[src.readInt()];
        } else {
            this.type = GroupType.OTHER;
        }
        this.adminUid = src.readString();
        this.createdMs = src.readLong();
        this.location = (Location) src.readParcelable(Location.class.getClassLoader());
        this.address = (Address) src.readParcelable(Address.class.getClassLoader());
        this.scope = src.readString();
        this.start_date = src.readLong();
        this.end_date = src.readLong();
        this.webUrl = src.readString();
        this.phone = src.readString();
        this.subCategory = (GroupSubCategory) src.readParcelable(GroupSubCategory.class.getClassLoader());
        this.business = src.readByte() != null;
        if (src.readByte() == null) {
            z = false;
        }
        this.isAllDataAvailable = z;
        this.unreadEventsCount = src.readLong();
        this.status = src.readString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupInfo groupInfo = (GroupInfo) o;
        if (this.id != null) {
            if (this.id.equals(groupInfo.id)) {
                return true;
            }
        } else if (groupInfo.id == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    static {
        CREATOR = new C15141();
    }

    public String toString() {
        return "GroupInfo{id='" + this.id + '\'' + ", type=" + this.type + ", name='" + this.name + '\'' + '}';
    }

    public void setUnreadEventsCount(long unreadEventsCount) {
        this.unreadEventsCount = unreadEventsCount;
    }

    public long getUnreadEventsCount() {
        return this.unreadEventsCount;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
