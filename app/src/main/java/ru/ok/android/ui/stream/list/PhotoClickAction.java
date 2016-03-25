package ru.ok.android.ui.stream.list;

import android.support.annotation.Nullable;
import android.view.View;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public class PhotoClickAction implements ClickAction {
    protected final FeedWithState feed;
    protected final MediaItemPhoto mediaItemPhoto;
    protected final AbsFeedPhotoEntity photoEntity;

    public PhotoClickAction(FeedWithState feed, AbsFeedPhotoEntity photoEntity, @Nullable MediaItemPhoto mediaItemPhoto) {
        this.feed = feed;
        this.photoEntity = photoEntity;
        this.mediaItemPhoto = mediaItemPhoto;
    }

    public void setClickListener(View view, StreamItemViewController streamItemViewController) {
        view.setOnClickListener(streamItemViewController.getPhotoClickListener());
    }

    public void setTags(View view) {
        view.setTag(2131624322, this.feed);
        view.setTag(2131624320, this.photoEntity);
        view.setTag(2131624330, this.mediaItemPhoto);
        view.setTag(2131624342, this.feed.feed);
    }
}
