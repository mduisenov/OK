package ru.ok.model.stream.entities;

import java.util.List;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;

public final class FeedVideoEntity extends BaseEntity {
    public final String description;
    public final long duration;
    public final String id;
    public final TreeSet<PhotoSize> thumbnailUrls;
    public final String title;

    public FeedVideoEntity(String id, String title, String description, List<PhotoSize> thumbnailUrls, long duration, LikeInfoContext likeSummary, DiscussionSummary discussionSummary) {
        super(13, likeSummary, discussionSummary);
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnailUrls = new TreeSet(thumbnailUrls);
        this.duration = duration;
    }

    public String getId() {
        return this.id;
    }
}
