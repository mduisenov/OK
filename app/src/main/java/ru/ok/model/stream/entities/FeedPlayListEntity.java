package ru.ok.model.stream.entities;

import java.util.Collections;
import java.util.List;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;

public class FeedPlayListEntity extends BaseEntity {
    private final String id;
    private final String imageUrl;
    private final String title;
    private List<FeedMusicTrackEntity> tracks;

    public FeedPlayListEntity(String id, String title, String imageUrl, LikeInfoContext likeInfo, DiscussionSummary discussionSummary) {
        super(18, likeInfo, discussionSummary);
        this.tracks = Collections.emptyList();
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return this.id;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public List<FeedMusicTrackEntity> getTracks() {
        return this.tracks;
    }

    void setTracks(List<FeedMusicTrackEntity> tracks) {
        this.tracks = tracks;
    }
}
