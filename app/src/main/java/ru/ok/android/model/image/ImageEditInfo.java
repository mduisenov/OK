package ru.ok.android.model.image;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import ru.ok.model.photo.PhotoAlbumInfo;

public class ImageEditInfo implements Parcelable, Serializable {
    public static final Creator<ImageEditInfo> CREATOR;
    public static int INDEX_FLAG_COMMENT_CHANGED = 0;
    public static int INDEX_FLAG_ORIENTATION_CHANGED = 0;
    public static int INDEX_FLAG_URI_CHANGED = 0;
    private static final long serialVersionUID = -6160169357313508283L;
    private AtomicBoolean[] flags;
    private int height;
    private PhotoAlbumInfo mAlbumInfo;
    private String mComment;
    private String mId;
    private String mOriginalComment;
    private int mOriginalRotation;
    private transient Uri mOriginalUri;
    private String mOriginalUriString;
    private int mRotation;
    private boolean mTemporary;
    private transient Uri mUri;
    private String mUriString;
    private boolean mWasEdited;
    private String mimeType;
    private int uploadTarget;
    private int width;

    /* renamed from: ru.ok.android.model.image.ImageEditInfo.1 */
    static class C03681 implements Creator<ImageEditInfo> {
        C03681() {
        }

        public ImageEditInfo createFromParcel(Parcel source) {
            return new ImageEditInfo(source);
        }

        public ImageEditInfo[] newArray(int size) {
            return new ImageEditInfo[size];
        }
    }

    public ImageEditInfo() {
        this.mWasEdited = true;
        this.flags = new AtomicBoolean[]{new AtomicBoolean(false), new AtomicBoolean(false), new AtomicBoolean(false)};
    }

    public ImageEditInfo(Parcel parcel) {
        this.mWasEdited = true;
        this.flags = new AtomicBoolean[]{new AtomicBoolean(false), new AtomicBoolean(false), new AtomicBoolean(false)};
        readFromParcel(parcel);
    }

    static {
        INDEX_FLAG_URI_CHANGED = 0;
        INDEX_FLAG_ORIENTATION_CHANGED = 1;
        INDEX_FLAG_COMMENT_CHANGED = 2;
        CREATOR = new C03681();
    }

    public AtomicBoolean[] getFlags() {
        return this.flags;
    }

    public void resetFlags() {
        for (AtomicBoolean flag : this.flags) {
            flag.set(false);
        }
    }

    public String toString() {
        return "EditedImage[uri=" + getUri() + " originalUri=" + getOriginalUri() + " wasEdited=" + this.mWasEdited + " album=" + this.mAlbumInfo + " comment=\"" + this.mComment + "\" rotation=" + this.mRotation + " width=" + this.width + " height=" + this.height + " temporary=" + this.mTemporary + " uploadTarget=" + this.uploadTarget + "]";
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ImageEditInfo)) {
            return false;
        }
        ImageEditInfo other = (ImageEditInfo) o;
        if (!TextUtils.equals(this.mUriString, other.mUriString) || this.mRotation != other.mRotation || !TextUtils.equals(this.mComment, other.mComment)) {
            return false;
        }
        if ((this.mAlbumInfo != null || other.mAlbumInfo != null) && (this.mAlbumInfo == null || !this.mAlbumInfo.equals(other.mAlbumInfo))) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (this.mComment == null ? 0 : this.mComment.hashCode() * 558804353) + ((this.mRotation * 852103757) + (this.mUriString == null ? 0 : this.mUriString.hashCode() * 1507153807));
        if (this.mAlbumInfo != null) {
            i = this.mAlbumInfo.hashCode() * 1843453921;
        }
        return hashCode + i;
    }

    public String getId() {
        if (TextUtils.isEmpty(this.mId)) {
            this.mId = UUID.randomUUID().toString();
        }
        return this.mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public Uri getUri() {
        if (this.mUri == null && !TextUtils.isEmpty(this.mUriString)) {
            this.mUri = Uri.parse(this.mUriString);
        }
        return this.mUri;
    }

    public void setUri(Uri uri) {
        this.mUri = uri;
        if (uri != null) {
            this.mUriString = uri.toString();
        }
    }

    public Uri getOriginalUri() {
        if (this.mOriginalUri == null && !TextUtils.isEmpty(this.mOriginalUriString)) {
            this.mOriginalUri = Uri.parse(this.mOriginalUriString);
        }
        return this.mOriginalUri;
    }

    public boolean wasEdited() {
        return this.mWasEdited;
    }

    public void setWasEdited(boolean wasEdited) {
        this.mWasEdited = wasEdited;
    }

    public PhotoAlbumInfo getAlbumInfo() {
        return this.mAlbumInfo;
    }

    public void setAlbumInfo(PhotoAlbumInfo albumInfo) {
        this.mAlbumInfo = albumInfo;
    }

    public String getComment() {
        return this.mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public int getRotation() {
        return this.mRotation;
    }

    public void setRotation(int rotation) {
        this.mRotation = rotation;
    }

    public boolean isTemporary() {
        return this.mTemporary;
    }

    public void setTemporary(boolean temporary) {
        this.mTemporary = temporary;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getUploadTarget() {
        return this.uploadTarget;
    }

    public void setUploadTarget(int uploadTarget) {
        this.uploadTarget = uploadTarget;
    }

    public int getOriginalRotation() {
        return this.mOriginalRotation;
    }

    public String getOriginalComment() {
        return this.mOriginalComment;
    }

    public void setOriginalRotation(int originalRotation) {
        this.mOriginalRotation = originalRotation;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i = 1;
        dest.writeString(this.mId);
        dest.writeString(this.mUriString);
        dest.writeString(this.mOriginalUriString);
        dest.writeInt(this.mWasEdited ? 1 : 0);
        dest.writeParcelable(this.mAlbumInfo, 0);
        dest.writeString(this.mComment);
        dest.writeInt(this.mRotation);
        if (!this.mTemporary) {
            i = 0;
        }
        dest.writeByte((byte) i);
        dest.writeInt(this.uploadTarget);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.mimeType);
    }

    public final void readFromParcel(Parcel parcel) {
        boolean z = true;
        this.mId = parcel.readString();
        this.mUriString = parcel.readString();
        this.mOriginalUriString = parcel.readString();
        this.mWasEdited = parcel.readInt() != 0;
        this.mAlbumInfo = (PhotoAlbumInfo) parcel.readParcelable(PhotoAlbumInfo.class.getClassLoader());
        this.mComment = parcel.readString();
        this.mOriginalComment = this.mComment;
        this.mRotation = parcel.readInt();
        if (parcel.readByte() != (byte) 1) {
            z = false;
        }
        this.mTemporary = z;
        this.uploadTarget = parcel.readInt();
        this.width = parcel.readInt();
        this.height = parcel.readInt();
        this.mimeType = parcel.readString();
    }
}
