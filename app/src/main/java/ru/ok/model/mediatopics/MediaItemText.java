package ru.ok.model.mediatopics;

import ru.ok.model.stream.message.FeedMessage;

public final class MediaItemText extends MediaItem {
    private final FeedMessage text;

    public MediaItemText(FeedMessage text) {
        super(MediaItemType.TEXT);
        this.text = text;
    }

    public FeedMessage getText() {
        return this.text;
    }
}
