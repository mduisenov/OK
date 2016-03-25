package ru.ok.model.photo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import ru.ok.model.stream.LikeInfo;
import ru.ok.model.stream.LikeInfoContext;

public final class PhotoAlbumInfo implements Parcelable, Serializable {
    public static final Creator<PhotoAlbumInfo> CREATOR;
    private static final long serialVersionUID = 1;
    protected boolean canAddPhoto;
    protected boolean canDelete;
    protected boolean canLike;
    protected boolean canModify;
    protected int commentsCount;
    protected String created;
    protected String description;
    protected String groupId;
    protected String id;
    LikeInfoContext likeInfo;
    protected int likesCount;
    protected transient PhotoInfo mainPhotoInfo;
    protected OwnerType ownerType;
    protected int photoCount;
    protected String title;
    protected AccessType type;
    protected boolean typeChangeEnabled;
    protected List<AccessType> types;
    protected String userId;
    protected boolean viewerLiked;
    boolean virtual;

    /* renamed from: ru.ok.model.photo.PhotoAlbumInfo.1 */
    static class C15481 implements Creator<PhotoAlbumInfo> {
        C15481() {
        }

        public PhotoAlbumInfo createFromParcel(Parcel source) {
            PhotoAlbumInfo photoAlbumInfo = new PhotoAlbumInfo();
            photoAlbumInfo.readFromParcel(source);
            return photoAlbumInfo;
        }

        public PhotoAlbumInfo[] newArray(int size) {
            return new PhotoAlbumInfo[size];
        }
    }

    public enum AccessType {
        PUBLIC {
            public String getApiJsonParamValue() {
                return "public";
            }
        },
        FRIENDS {
            public String getApiJsonParamValue() {
                return "friends";
            }
        },
        RELATIVE {
            public String getApiJsonParamValue() {
                return "relative";
            }
        },
        LOVE {
            public String getApiJsonParamValue() {
                return "love";
            }
        },
        CLOSE_FRIEND {
            public String getApiJsonParamValue() {
                return "close_friend";
            }
        },
        COLLEAGUE {
            public String getApiJsonParamValue() {
                return "colleague";
            }
        },
        CLASSMATE {
            public String getApiJsonParamValue() {
                return "classmate";
            }
        },
        COURSEMATE {
            public String getApiJsonParamValue() {
                return "cursemate";
            }
        },
        COMPANION_IN_ARMS {
            public String getApiJsonParamValue() {
                return "companion_in_arms";
            }
        };

        public abstract String getApiJsonParamValue();

        public static int[] asIntArray(List<AccessType> accessTypes) {
            if (accessTypes == null || accessTypes.isEmpty()) {
                return null;
            }
            int size = accessTypes.size();
            int[] iArr = new int[size];
            for (int i = 0; i < size; i++) {
                iArr[i] = ((AccessType) accessTypes.get(i)).ordinal();
            }
            return iArr;
        }

        public static List<AccessType> asList(int[] accessTypeInts) {
            List<AccessType> accessTypes = new ArrayList(accessTypeInts.length);
            AccessType[] values = values();
            for (int type : accessTypeInts) {
                accessTypes.add(values[type]);
            }
            return accessTypes;
        }
    }

    public enum OwnerType {
        UNKNOWN,
        GROUP,
        USER
    }

    public PhotoAlbumInfo() {
        this.type = AccessType.PUBLIC;
        this.ownerType = OwnerType.UNKNOWN;
    }

    public PhotoAlbumInfo clone() {
        PhotoAlbumInfo cloned = new PhotoAlbumInfo();
        cloned.id = this.id;
        cloned.title = this.title;
        cloned.description = this.description;
        cloned.created = this.created;
        cloned.type = this.type;
        cloned.types = this.types;
        cloned.typeChangeEnabled = this.typeChangeEnabled;
        cloned.photoCount = this.photoCount;
        cloned.commentsCount = this.commentsCount;
        cloned.likesCount = this.likesCount;
        cloned.viewerLiked = this.viewerLiked;
        cloned.mainPhotoInfo = this.mainPhotoInfo;
        cloned.canLike = this.canLike;
        cloned.canModify = this.canModify;
        cloned.canDelete = this.canDelete;
        cloned.canAddPhoto = this.canAddPhoto;
        cloned.ownerType = this.ownerType;
        cloned.userId = this.userId;
        cloned.groupId = this.groupId;
        cloned.virtual = this.virtual;
        cloned.likeInfo = this.likeInfo;
        return cloned;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated() {
        return this.created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public AccessType getType() {
        return this.type;
    }

    public void setType(AccessType type) {
        this.type = type;
    }

    public List<AccessType> getTypes() {
        return this.types;
    }

    public void setTypes(List<AccessType> types) {
        this.types = types;
    }

    public int getPhotoCount() {
        return this.photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public int getCommentsCount() {
        return this.commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {
        return this.likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isViewerLiked() {
        return this.viewerLiked;
    }

    public void setViewerLiked(boolean viewerLiked) {
        this.viewerLiked = viewerLiked;
    }

    public PhotoInfo getMainPhotoInfo() {
        return this.mainPhotoInfo;
    }

    public void setMainPhotoInfo(PhotoInfo mainPhotoInfo) {
        this.mainPhotoInfo = mainPhotoInfo;
    }

    public boolean isCanLike() {
        return this.canLike;
    }

    public void setCanLike(boolean canLike) {
        this.canLike = canLike;
    }

    public boolean isCanModify() {
        return this.canModify;
    }

    public void setCanModify(boolean canModify) {
        this.canModify = canModify;
    }

    public boolean isCanDelete() {
        return this.canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanAddPhoto() {
        return this.canAddPhoto;
    }

    public void setCanAddPhoto(boolean canAddPhoto) {
        this.canAddPhoto = canAddPhoto;
    }

    public OwnerType getOwnerType() {
        return this.ownerType;
    }

    public void setOwnerType(OwnerType ownerType) {
        this.ownerType = ownerType;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setLikeInfo(LikeInfoContext likeInfo) {
        this.likeInfo = likeInfo;
    }

    public LikeInfoContext getLikeInfo() {
        return this.likeInfo;
    }

    public boolean isVirtual() {
        return TextUtils.isEmpty(this.id) || TextUtils.equals(this.id, "tags");
    }

    public String getPicUrl() {
        return getMainPhotoInfo() != null ? getMainPhotoInfo().getLargestSize().getUrl() : null;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PhotoAlbumInfo)) {
            return false;
        }
        PhotoAlbumInfo other = (PhotoAlbumInfo) obj;
        if (TextUtils.equals(this.id, other.id) && this.ownerType == other.ownerType && TextUtils.equals(this.groupId, other.groupId)) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "PhotoAlbumInfo[id=" + this.id + " groupId=" + this.groupId + " title=" + this.title + "]";
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (this.id == null ? 0 : this.id.hashCode() * 846704231) + (this.ownerType == null ? 0 : this.ownerType.hashCode() * 1646554433);
        if (this.groupId != null) {
            i = this.groupId.hashCode() * 1302704197;
        }
        return hashCode + i;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.created);
        dest.writeInt(this.type == null ? 0 : this.type.ordinal());
        if (this.typeChangeEnabled) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        dest.writeInt(this.photoCount);
        dest.writeInt(this.commentsCount);
        dest.writeInt(this.likesCount);
        if (this.viewerLiked) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        dest.writeParcelable(this.mainPhotoInfo, 0);
        if (this.virtual) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        if (this.canLike) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        if (this.canModify) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        if (this.canDelete) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        if (!this.canAddPhoto) {
            i2 = 0;
        }
        dest.writeByte((byte) i2);
        dest.writeString(this.userId);
        dest.writeString(this.groupId);
        dest.writeInt(this.ownerType.ordinal());
        dest.writeParcelable(this.likeInfo, 0);
        int size = this.types == null ? -1 : this.types.size();
        dest.writeInt(size);
        if (size > 0) {
            dest.writeIntArray(AccessType.asIntArray(this.types));
        }
    }

    public final void readFromParcel(Parcel parcel) {
        boolean z;
        boolean z2 = true;
        ClassLoader cl = PhotoAlbumInfo.class.getClassLoader();
        this.id = parcel.readString();
        this.title = parcel.readString();
        this.description = parcel.readString();
        this.created = parcel.readString();
        this.type = AccessType.values()[parcel.readInt()];
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.typeChangeEnabled = z;
        this.photoCount = parcel.readInt();
        this.commentsCount = parcel.readInt();
        this.likesCount = parcel.readInt();
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.viewerLiked = z;
        this.mainPhotoInfo = (PhotoInfo) parcel.readParcelable(cl);
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.virtual = z;
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.canLike = z;
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.canModify = z;
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.canDelete = z;
        if (parcel.readByte() != (byte) 1) {
            z2 = false;
        }
        this.canAddPhoto = z2;
        this.userId = parcel.readString();
        this.groupId = parcel.readString();
        this.ownerType = OwnerType.values()[parcel.readInt()];
        this.likeInfo = (LikeInfoContext) parcel.readParcelable(LikeInfo.class.getClassLoader());
        int size = parcel.readInt();
        if (size > 0) {
            int[] storedTypes = new int[size];
            parcel.readIntArray(storedTypes);
            this.types = AccessType.asList(storedTypes);
            return;
        }
        this.types = new ArrayList();
    }

    static {
        CREATOR = new C15481();
    }
}
