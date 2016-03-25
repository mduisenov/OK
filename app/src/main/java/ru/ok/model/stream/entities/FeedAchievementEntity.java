package ru.ok.model.stream.entities;

public class FeedAchievementEntity extends BaseEntity {
    private FeedAchievementTypeEntity achievementType;
    private final String id;
    private BaseEntity receiver;

    protected FeedAchievementEntity(String id, BaseEntity receiver, FeedAchievementTypeEntity achievementType) {
        super(22, null, null);
        this.id = id;
        this.receiver = receiver;
        this.achievementType = achievementType;
    }

    public String getId() {
        return this.id;
    }

    public FeedAchievementTypeEntity getAchievementType() {
        return this.achievementType;
    }

    void setReceiver(BaseEntity receiver) {
        this.receiver = receiver;
    }

    void setAchievementType(FeedAchievementTypeEntity AchievementType) {
        this.achievementType = AchievementType;
    }
}
