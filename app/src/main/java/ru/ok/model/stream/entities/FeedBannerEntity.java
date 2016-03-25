package ru.ok.model.stream.entities;

import ru.ok.model.stream.banner.Banner;

public class FeedBannerEntity extends BaseEntity {
    final Banner banner;

    public String getId() {
        return null;
    }

    protected FeedBannerEntity(Banner banner) {
        super(14, null, null);
        this.banner = banner;
    }

    public Banner getBanner() {
        return this.banner;
    }

    public String toString() {
        return "FeedBannerEntity[" + this.banner + "]";
    }
}
