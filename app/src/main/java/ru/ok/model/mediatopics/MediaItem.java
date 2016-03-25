package ru.ok.model.mediatopics;

import java.io.Serializable;

public abstract class MediaItem implements Serializable {
    private static final long serialVersionUID = 1;
    private final MediaItemType type;

    protected MediaItem(MediaItemType type) {
        this.type = type;
    }

    public MediaItemType getType() {
        return this.type;
    }
}
