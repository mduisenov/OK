package ru.ok.model.stream.message;

public class FeedEntitySpan extends FeedMessageSpan {
    String entityId;
    int entityType;
    String ref;

    FeedEntitySpan() {
    }

    public FeedEntitySpan(int entityType, String entityId, String ref) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.ref = ref;
    }

    public int getEntityType() {
        return this.entityType;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public String getRef() {
        return this.ref;
    }

    public String toString() {
        return "FeedEntitySpan[type=" + this.entityType + " id=" + this.entityId + " ref=" + this.ref + "]";
    }
}
