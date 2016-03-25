package ru.ok.model.mediatopics;

import java.util.List;
import ru.ok.model.stream.entities.BaseEntity;

public abstract class MediaReshareItem extends MediaItem {
    private final boolean isReshare;
    private final List<BaseEntity> reshareOwners;

    protected MediaReshareItem(MediaItemType type, List<BaseEntity> reshareOwners, boolean isReshare) {
        super(type);
        this.reshareOwners = reshareOwners;
        this.isReshare = isReshare;
    }

    public List<BaseEntity> getReshareOwners() {
        return this.reshareOwners;
    }

    public boolean isReshare() {
        return this.isReshare;
    }
}
