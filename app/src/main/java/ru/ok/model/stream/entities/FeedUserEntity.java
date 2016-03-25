package ru.ok.model.stream.entities;

import ru.ok.model.UserInfo;
import ru.ok.model.stream.LikeInfoContext;

public class FeedUserEntity extends BaseEntity {
    private final UserInfo userInfo;

    public FeedUserEntity(UserInfo userInfo, LikeInfoContext likeInfo) {
        super(7, likeInfo, null);
        this.userInfo = userInfo;
    }

    public String getId() {
        return this.userInfo.uid;
    }

    public UserInfo getUserInfo() {
        return this.userInfo;
    }

    public String toString() {
        return "FeedUserEntity{" + this.userInfo + '}';
    }
}
