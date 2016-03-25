package ru.ok.model.stream.entities;

import java.io.Serializable;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;

public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1;
    private final DiscussionSummary discussionSummary;
    private final LikeInfoContext likeInfo;
    private final int type;

    public abstract String getId();

    protected BaseEntity(int type, LikeInfoContext likeInfo, DiscussionSummary discussionSummary) {
        this.type = type;
        this.likeInfo = likeInfo;
        this.discussionSummary = discussionSummary;
    }

    public int getType() {
        return this.type;
    }

    public LikeInfoContext getLikeInfo() {
        return this.likeInfo;
    }

    public DiscussionSummary getDiscussionSummary() {
        return this.discussionSummary;
    }
}
