package ru.ok.model.photo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.List;

public class PhotoAlbumsInfo implements Parcelable {
    public static final Creator<PhotoAlbumsInfo> CREATOR;
    protected List<PhotoAlbumInfo> albums;
    protected boolean hasMore;
    protected String pagingAnchor;
    protected int totalCount;

    /* renamed from: ru.ok.model.photo.PhotoAlbumsInfo.1 */
    static class C15581 implements Creator<PhotoAlbumsInfo> {
        C15581() {
        }

        public PhotoAlbumsInfo createFromParcel(Parcel source) {
            PhotoAlbumsInfo albumInfos = new PhotoAlbumsInfo();
            albumInfos.readFromParcel(source);
            return albumInfos;
        }

        public PhotoAlbumsInfo[] newArray(int size) {
            return new PhotoAlbumsInfo[size];
        }
    }

    public List<PhotoAlbumInfo> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<PhotoAlbumInfo> albums) {
        this.albums = albums;
    }

    public boolean isHasMore() {
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

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.albums);
        dest.writeString(this.pagingAnchor);
        dest.writeInt(this.totalCount);
        dest.writeByte((byte) (this.hasMore ? 1 : 0));
    }

    public final void readFromParcel(Parcel parcel) {
        boolean z = true;
        this.albums = parcel.readArrayList(PhotoAlbumInfo.class.getClassLoader());
        this.pagingAnchor = parcel.readString();
        this.totalCount = parcel.readInt();
        if (parcel.readByte() != (byte) 1) {
            z = false;
        }
        this.hasMore = z;
    }

    static {
        CREATOR = new C15581();
    }
}
