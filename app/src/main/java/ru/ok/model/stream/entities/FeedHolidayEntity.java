package ru.ok.model.stream.entities;

public class FeedHolidayEntity extends BaseEntity {
    public final String id;

    public FeedHolidayEntity(String id) {
        super(23, null, null);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
