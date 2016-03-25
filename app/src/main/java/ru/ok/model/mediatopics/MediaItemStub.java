package ru.ok.model.mediatopics;

public class MediaItemStub extends MediaItem {
    private final String text;

    public MediaItemStub(String text) {
        super(MediaItemType.STUB);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
