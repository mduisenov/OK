package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.List;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedAlbumEntityBuilder extends BaseEntityBuilder<FeedAlbumEntityBuilder, FeedAlbumEntity> {
    public static final Creator<FeedAlbumEntityBuilder> CREATOR;
    PhotoAlbumInfo albumInfo;

    /* renamed from: ru.ok.model.stream.entities.FeedAlbumEntityBuilder.1 */
    static class C16141 implements Creator<FeedAlbumEntityBuilder> {
        C16141() {
        }

        public FeedAlbumEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedAlbumEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedAlbumEntityBuilder[] newArray(int size) {
            return new FeedAlbumEntityBuilder[size];
        }
    }

    public FeedAlbumEntityBuilder() {
        super(8);
        this.albumInfo = new PhotoAlbumInfo();
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            this.albumInfo.setTitle(title);
        }
    }

    public void setAlbumId(String id) {
        if (!TextUtils.isEmpty(id)) {
            this.albumInfo.setId(id);
            super.withId(id);
        }
    }

    public FeedAlbumEntityBuilder withId(String id) {
        setAlbumId(id);
        return this;
    }

    public void setCreated(String created) {
        if (!TextUtils.isEmpty(created)) {
            this.albumInfo.setCreated(created);
        }
    }

    public void setType(AccessType type) {
        if (type != null) {
            this.albumInfo.setType(type);
        }
    }

    public void setPhotoCount(int photoCount) {
        this.albumInfo.setPhotoCount(photoCount);
    }

    public void setCommentsCount(int commentsCount) {
        this.albumInfo.setCommentsCount(commentsCount);
    }

    public FeedAlbumEntityBuilder withLikeInfo(LikeInfoContext likeInfo) {
        super.withLikeInfo(likeInfo);
        this.albumInfo.setLikesCount(likeInfo.count);
        return this;
    }

    public void setUserId(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            this.albumInfo.setUserId(userId);
        }
    }

    protected FeedAlbumEntity doPreBuild() throws FeedObjectException {
        return new FeedAlbumEntity(this.albumInfo, getLikeInfo(), getDiscussionSummary());
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.albumInfo, flags);
    }

    public void getRefs(List<String> list) {
    }

    protected FeedAlbumEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        PhotoAlbumInfo albumInfo;
        try {
            super.readFromParcel(src);
            albumInfo = (PhotoAlbumInfo) src.readParcelable(FeedAlbumEntityBuilder.class.getClassLoader());
            if (albumInfo == null) {
                throw new RecoverableUnParcelException("Unparceled album info is null.");
            }
            this.albumInfo = albumInfo;
            return this;
        } catch (Throwable th) {
            albumInfo = (PhotoAlbumInfo) src.readParcelable(FeedAlbumEntityBuilder.class.getClassLoader());
            if (albumInfo == null) {
                RecoverableUnParcelException recoverableUnParcelException = new RecoverableUnParcelException("Unparceled album info is null.");
            } else {
                this.albumInfo = albumInfo;
            }
        }
    }

    static {
        CREATOR = new C16141();
    }
}
