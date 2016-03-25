package ru.ok.model.stream.entities;

import ru.ok.model.photo.PhotoInfo;

public class FeedGroupPhotoEntity extends AbsFeedPhotoEntity {
    FeedGroupPhotoEntity(PhotoInfo photoInfo) {
        super(12, photoInfo);
    }
}
