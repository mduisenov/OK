package ru.ok.model.stream.entities;

public final class FeedPlaceEntity extends BaseEntity {
    private final String id;
    private final double latitude;
    private final double longitude;
    private final String name;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    protected FeedPlaceEntity(String id, String name, double latitude, double longitude) {
        super(17, null, null);
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
