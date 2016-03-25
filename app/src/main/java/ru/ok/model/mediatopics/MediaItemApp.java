package ru.ok.model.mediatopics;

import ru.ok.model.stream.entities.FeedAppEntity;

public class MediaItemApp extends MediaItem {
    private final String actionMark;
    private final String actionText;
    private final FeedAppEntity app;
    private final String image;
    private final String imageMark;
    private final String imageTitle;
    private final String text;

    public MediaItemApp(FeedAppEntity app, String text, String image, String imageTitle, String imageMark, String actionText, String actionMark) {
        super(MediaItemType.APP);
        this.app = app;
        this.text = text;
        this.image = image;
        this.imageTitle = imageTitle;
        this.imageMark = imageMark;
        this.actionText = actionText;
        this.actionMark = actionMark;
    }

    public String getText() {
        return this.text;
    }

    public String getImage() {
        return this.image;
    }

    public String getImageTitle() {
        return this.imageTitle;
    }

    public FeedAppEntity getApp() {
        return this.app;
    }
}
