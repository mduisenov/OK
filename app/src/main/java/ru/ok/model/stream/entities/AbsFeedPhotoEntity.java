package ru.ok.model.stream.entities;

import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.LikeInfoContext;

public abstract class AbsFeedPhotoEntity extends BaseEntity implements TimestampedEntity {
    private final PhotoInfo photoInfo;

    AbsFeedPhotoEntity(int entityType, PhotoInfo photoInfo) {
        super(entityType, new LikeInfoContext(photoInfo.getLikeInfo(), entityType, photoInfo.getId()), photoInfo.getDiscussionSummary());
        this.photoInfo = photoInfo;
    }

    public String getId() {
        return this.photoInfo.getId();
    }

    public PhotoInfo getPhotoInfo() {
        return this.photoInfo;
    }

    public long getCreationTime() {
        return this.photoInfo.getCreatedMs();
    }
}
