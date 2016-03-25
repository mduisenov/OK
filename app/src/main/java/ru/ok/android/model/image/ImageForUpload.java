package ru.ok.android.model.image;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.model.GroupInfo;
import ru.ok.model.photo.PhotoAlbumInfo;

public class ImageForUpload implements Parcelable {
    public static final Creator<ImageForUpload> CREATOR;
    private GroupInfo groupInfo;
    private PhotoAlbumInfo mAlbumInfo;
    private String mComment;
    private int mCurrentStatus;
    private ImageUploadException mError;
    private int mHeight;
    private String mId;
    private String mPhotoId;
    private int mPreviousStatus;
    private int mRotation;
    private Uri mUri;
    private int mWidth;
    private String mimeType;
    private int uploadTarget;

    /* renamed from: ru.ok.android.model.image.ImageForUpload.1 */
    static class C03691 implements Creator<ImageForUpload> {
        C03691() {
        }

        public ImageForUpload createFromParcel(Parcel source) {
            return new ImageForUpload(source);
        }

        public ImageForUpload[] newArray(int size) {
            return new ImageForUpload[size];
        }
    }

    public ImageForUpload(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getId() {
        return this.mId;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void setUri(Uri uri) {
        this.mUri = uri;
    }

    public int getCurrentStatus() {
        return this.mCurrentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.mCurrentStatus = currentStatus;
    }

    public int getPreviousStatus() {
        return this.mPreviousStatus;
    }

    public void setPreviousStatus(int previousStatus) {
        this.mPreviousStatus = previousStatus;
    }

    public ImageUploadException getError() {
        return this.mError;
    }

    public void setError(ImageUploadException error) {
        this.mError = error;
    }

    public int getRotation() {
        return this.mRotation;
    }

    public void setRotation(int rotation) {
        this.mRotation = rotation;
    }

    public GroupInfo getGroupInfo() {
        return this.groupInfo;
    }

    public PhotoAlbumInfo getAlbumInfo() {
        return this.mAlbumInfo;
    }

    public void setAlbumInfo(PhotoAlbumInfo albumInfo) {
        this.mAlbumInfo = albumInfo;
    }

    public void setPhotoId(String photoId) {
        this.mPhotoId = photoId;
    }

    public String getRemoteId() {
        return this.mPhotoId;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public int getUploadTarget() {
        return this.uploadTarget;
    }

    public void setUploadTarget(int uploadTarget) {
        this.uploadTarget = uploadTarget;
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
        dest.writeParcelable(this.mUri, 0);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mCurrentStatus);
        dest.writeInt(this.mPreviousStatus);
        dest.writeParcelable(this.mError, flags);
        dest.writeInt(this.mRotation);
        dest.writeParcelable(this.mAlbumInfo, 0);
        dest.writeString(this.mPhotoId);
        dest.writeParcelable(this.groupInfo, 0);
        dest.writeString(this.mComment);
        dest.writeInt(this.uploadTarget);
        dest.writeString(this.mimeType);
    }

    public final void readFromParcel(Parcel parcel) {
        this.mUri = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.mWidth = parcel.readInt();
        this.mHeight = parcel.readInt();
        this.mCurrentStatus = parcel.readInt();
        this.mPreviousStatus = parcel.readInt();
        this.mError = (ImageUploadException) parcel.readParcelable(ImageForUpload.class.getClassLoader());
        this.mRotation = parcel.readInt();
        this.mAlbumInfo = (PhotoAlbumInfo) parcel.readParcelable(PhotoAlbumInfo.class.getClassLoader());
        this.mPhotoId = parcel.readString();
        this.groupInfo = (GroupInfo) parcel.readParcelable(GroupInfo.class.getClassLoader());
        this.mComment = parcel.readString();
        this.uploadTarget = parcel.readInt();
        this.mimeType = parcel.readString();
    }

    static {
        CREATOR = new C03691();
    }
}
