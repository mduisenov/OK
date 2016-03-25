package ru.ok.model.stream.entities;

import ru.ok.model.GroupInfo;
import ru.ok.model.stream.LikeInfoContext;

public class FeedGroupEntity extends BaseEntity {
    private final GroupInfo groupInfo;

    public String getId() {
        return this.groupInfo.getId();
    }

    public GroupInfo getGroupInfo() {
        return this.groupInfo;
    }

    protected FeedGroupEntity(GroupInfo groupInfo, LikeInfoContext likeInfo) {
        super(2, likeInfo, null);
        this.groupInfo = groupInfo;
    }

    public String toString() {
        return "FeedGroupEntity{groupInfo=" + this.groupInfo + '}';
    }
}
