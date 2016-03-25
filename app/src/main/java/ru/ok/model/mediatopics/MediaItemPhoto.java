package ru.ok.model.mediatopics;

import java.util.Collections;
import java.util.List;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;
import ru.ok.model.stream.entities.BaseEntity;

public final class MediaItemPhoto extends MediaReshareItem {
    private final List<AbsFeedPhotoEntity> photos;

    public MediaItemPhoto(List<AbsFeedPhotoEntity> photos, List<BaseEntity> reshareOwners, boolean isReshare) {
        super(MediaItemType.PHOTO, reshareOwners, isReshare);
        this.photos = Collections.unmodifiableList(photos);
    }

    public List<AbsFeedPhotoEntity> getPhotos() {
        return this.photos;
    }
}
