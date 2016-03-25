package ru.ok.model.stream.entities;

import java.util.List;
import ru.ok.model.stream.message.FeedMessage;

public class FeedPresentEntity extends BaseEntity {
    private final String id;
    private FeedPresentTypeEntity presentType;
    private BaseEntity receiver;
    private FeedMessage receiverLabel;
    private BaseEntity sender;
    private FeedMessage senderLabel;
    private List<FeedMusicTrackEntity> tracks;

    protected FeedPresentEntity(String id, BaseEntity sender, BaseEntity receiver, FeedPresentTypeEntity presentType, FeedMessage senderLabel, FeedMessage receiverLabel) {
        super(6, null, null);
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.presentType = presentType;
        this.senderLabel = senderLabel;
        this.receiverLabel = receiverLabel;
    }

    public String getId() {
        return this.id;
    }

    public BaseEntity getSender() {
        return this.sender;
    }

    public BaseEntity getReceiver() {
        return this.receiver;
    }

    public FeedPresentTypeEntity getPresentType() {
        return this.presentType;
    }

    void setSender(BaseEntity sender) {
        this.sender = sender;
    }

    void setReceiver(BaseEntity receiver) {
        this.receiver = receiver;
    }

    void setPresentType(FeedPresentTypeEntity presentType) {
        this.presentType = presentType;
    }

    public void setTracks(List<FeedMusicTrackEntity> tracks) {
        this.tracks = tracks;
    }

    public List<FeedMusicTrackEntity> getTracks() {
        return this.tracks;
    }

    public void setSenderLabel(FeedMessage senderLabel) {
        this.senderLabel = senderLabel;
    }

    public FeedMessage getSenderLabel() {
        return this.senderLabel;
    }

    public FeedMessage getReceiverLabel() {
        return this.receiverLabel;
    }

    public void setReceiverLabel(FeedMessage receiverLabel) {
        this.receiverLabel = receiverLabel;
    }
}
