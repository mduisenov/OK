package ru.ok.model.mediatopics;

import java.util.List;
import ru.ok.model.stream.entities.FeedPollEntity;

public class MediaItemPoll extends MediaItem {
    final List<FeedPollEntity> polls;

    protected MediaItemPoll(List<FeedPollEntity> polls) {
        super(MediaItemType.POLL);
        this.polls = polls;
    }

    public List<FeedPollEntity> getPolls() {
        return this.polls;
    }
}
