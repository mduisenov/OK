package ru.ok.model.mediatopics;

import java.util.List;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedMediaTopicEntity;

public class MediaItemTopic extends MediaReshareItem {
    private final List<FeedMediaTopicEntity> mediaTopics;

    public MediaItemTopic(List<BaseEntity> reshareOwners, List<FeedMediaTopicEntity> mediaTopics, boolean isReshare) {
        super(MediaItemType.TOPIC, reshareOwners, isReshare);
        this.mediaTopics = mediaTopics;
    }

    public List<FeedMediaTopicEntity> getMediaTopics() {
        return this.mediaTopics;
    }
}
