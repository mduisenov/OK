package ru.ok.model.stream.entities;

import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.LikeInfoContext;

public final class FeedAlbumEntity extends BaseEntity {
    private final PhotoAlbumInfo albumInfo;
    private final String id;

    public FeedAlbumEntity(PhotoAlbumInfo albumInfo, LikeInfoContext likeInfo, DiscussionSummary discussionSummary) throws FeedObjectException {
        super(8, likeInfo, discussionSummary);
        String albumId = albumInfo.getId();
        if (albumId == null) {
            throw new FeedObjectException("album ID is null");
        }
        this.id = albumId;
        this.albumInfo = albumInfo;
    }

    public String getId() {
        return this.id;
    }
}
