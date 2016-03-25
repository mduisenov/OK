package ru.ok.model.stream.entities;

import android.os.Parcel;
import java.util.List;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;

public abstract class AbsFeedPhotoEntityBuilder extends BaseEntityBuilder<AbsFeedPhotoEntityBuilder, AbsFeedPhotoEntity> {
    PhotoInfo photoInfo;

    public AbsFeedPhotoEntityBuilder withPhotoInfo(PhotoInfo photoInfo) {
        this.photoInfo = photoInfo;
        withId(photoInfo.getId());
        return this;
    }

    public PhotoInfo getPhotoInfo() {
        return this.photoInfo;
    }

    protected AbsFeedPhotoEntity doPreBuild() throws FeedObjectException {
        if (getId() == null) {
            throw new FeedObjectException("Photo entity ID is null");
        } else if (this.photoInfo == null) {
            throw new FeedObjectException("PhotoInfo not set.");
        } else if (getType() == 12) {
            if (this.photoInfo.getOwnerType() == OwnerType.GROUP) {
                return new FeedGroupPhotoEntity(this.photoInfo);
            }
            throw new FeedObjectException("Unexpected owner type for group_photo entity: " + this.photoInfo.getOwnerType());
        } else if (getType() != 5) {
            throw new FeedObjectException("Don't know how to build entity type " + getType());
        } else if (this.photoInfo.getOwnerType() == OwnerType.USER) {
            return new FeedUserPhotoEntity(this.photoInfo);
        } else {
            throw new FeedObjectException("Unexpected owner type for photo entity: " + this.photoInfo.getOwnerType());
        }
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.photoInfo, flags);
    }

    public AbsFeedPhotoEntityBuilder(int entityType) {
        super(entityType);
    }

    protected AbsFeedPhotoEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            super.readFromParcel(src);
            return this;
        } finally {
            this.photoInfo = (PhotoInfo) src.readParcelable(AbsFeedPhotoEntityBuilder.class.getClassLoader());
        }
    }
}
