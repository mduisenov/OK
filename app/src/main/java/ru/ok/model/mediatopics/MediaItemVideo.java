package ru.ok.model.mediatopics;

import java.util.Collections;
import java.util.List;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedVideoEntity;

public final class MediaItemVideo extends MediaReshareItem {
    private final List<FeedVideoEntity> videos;

    public MediaItemVideo(List<FeedVideoEntity> videos, List<BaseEntity> reshareOwners, boolean isReshare) {
        super(MediaItemType.VIDEO, reshareOwners, isReshare);
        this.videos = Collections.unmodifiableList(videos);
    }

    public List<FeedVideoEntity> getVideos() {
        return this.videos;
    }
}
