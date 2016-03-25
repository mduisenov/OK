package ru.ok.model.photo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import java.util.List;

public class PhotosInfo implements Parcelable {
    public static final Creator<PhotosInfo> CREATOR;
    protected boolean hasMore;
    protected String pagingAnchor;
    protected List<PhotoInfo> photos;
    protected int totalCount;

    /* renamed from: ru.ok.model.photo.PhotosInfo.1 */
    static class C15631 implements Creator<PhotosInfo> {
        C15631() {
        }

        public PhotosInfo createFromParcel(Parcel source) {
            PhotosInfo photoInfos = new PhotosInfo();
            photoInfos.readFromParcel(source);
            return photoInfos;
        }

        public PhotosInfo[] newArray(int size) {
            return new PhotosInfo[size];
        }
    }

    @Nullable
    public List<PhotoInfo> getPhotos() {
        return this.photos;
    }

    public void setPhotos(List<PhotoInfo> photos) {
        this.photos = photos;
    }

    public boolean hasPhotos() {
        return (this.photos == null || this.photos.isEmpty()) ? false : true;
    }

    public boolean hasMore() {
        return this.hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getPagingAnchor() {
        return this.pagingAnchor;
    }

    public void setPagingAnchor(String pagingAnchor) {
        this.pagingAnchor = pagingAnchor;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String toString() {
        return "PhotosInfo[size=" + (this.photos == null ? "null" : Integer.toString(this.photos.size())) + " hasMore=" + this.hasMore + " pagingAnchor=" + this.pagingAnchor + " totalCount=" + this.totalCount + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.photos);
        dest.writeString(this.pagingAnchor);
        dest.writeInt(this.totalCount);
        dest.writeByte((byte) (this.hasMore ? 1 : 0));
    }

    public final void readFromParcel(Parcel parcel) {
        boolean z = true;
        this.photos = parcel.readArrayList(PhotoInfo.class.getClassLoader());
        this.pagingAnchor = parcel.readString();
        this.totalCount = parcel.readInt();
        if (parcel.readByte() != (byte) 1) {
            z = false;
        }
        this.hasMore = z;
    }

    static {
        CREATOR = new C15631();
    }
}
