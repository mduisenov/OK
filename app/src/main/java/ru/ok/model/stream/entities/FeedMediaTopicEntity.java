package ru.ok.model.stream.entities;

import java.util.List;
import ru.ok.model.mediatopics.MediaItem;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;

public final class FeedMediaTopicEntity extends BaseEntity implements TimestampedEntity {
    private BaseEntity author;
    private final long createdDate;
    private final String deleteId;
    private boolean editable;
    private final boolean hasMore;
    private final String id;
    private final boolean isPromo;
    private final boolean isSticky;
    private final boolean isUnmodifiable;
    private final String markAsSpamId;
    final List<MediaItem> mediaItems;
    private BaseEntity owner;
    private List<BaseEntity> places;
    private List<BaseEntity> withFriends;

    public FeedMediaTopicEntity(String id, long createdDate, boolean hasMore, LikeInfoContext likeSummary, DiscussionSummary discussionSummary, String markAsSpamId, String deleteId, boolean isSticky, boolean isUnmodifiable, boolean isPromo, List<MediaItem> mediaItems) {
        super(9, likeSummary, discussionSummary);
        this.id = id;
        this.createdDate = createdDate;
        this.hasMore = hasMore;
        this.isSticky = isSticky;
        this.isUnmodifiable = isUnmodifiable;
        this.markAsSpamId = markAsSpamId;
        this.deleteId = deleteId;
        this.isPromo = isPromo;
        this.mediaItems = mediaItems;
    }

    public String getId() {
        return this.id;
    }

    public BaseEntity getAuthor() {
        return this.author;
    }

    public List<BaseEntity> getWithFriends() {
        return this.withFriends;
    }

    public List<BaseEntity> getPlaces() {
        return this.places;
    }

    public boolean isHasMore() {
        return this.hasMore;
    }

    public int getMediaItemsCount() {
        return this.mediaItems.size();
    }

    public MediaItem getMediaItem(int index) {
        return (MediaItem) this.mediaItems.get(index);
    }

    public void setAuthor(BaseEntity entity) {
        this.author = entity;
    }

    public String getMarkAsSpamId() {
        return this.markAsSpamId;
    }

    public String getDeleteId() {
        return this.deleteId;
    }

    public void setWithFriends(List<BaseEntity> withFriends) {
        this.withFriends = withFriends;
    }

    public void setPlaces(List<BaseEntity> places) {
        this.places = places;
    }

    public long getCreationTime() {
        return this.createdDate;
    }

    public BaseEntity getOwner() {
        return this.owner;
    }

    public void setOwner(BaseEntity owner) {
        this.owner = owner;
    }

    public boolean isSticky() {
        return this.isSticky;
    }

    public boolean isUnmodifiable() {
        return this.isUnmodifiable;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isPromo() {
        return this.isPromo;
    }
}
