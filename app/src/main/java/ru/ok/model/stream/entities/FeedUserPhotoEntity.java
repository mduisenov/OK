package ru.ok.model.stream.entities;

import ru.ok.model.photo.PhotoInfo;

public class FeedUserPhotoEntity extends AbsFeedPhotoEntity {
    FeedUserPhotoEntity(PhotoInfo photoInfo) {
        super(5, photoInfo);
    }
}
