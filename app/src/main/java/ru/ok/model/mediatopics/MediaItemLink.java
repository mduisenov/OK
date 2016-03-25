package ru.ok.model.mediatopics;

import java.util.List;
import ru.ok.model.ImageUrl;
import ru.ok.model.stream.entities.BaseEntity;

public final class MediaItemLink extends MediaReshareItem {
    private String description;
    private List<ImageUrl> imageUrls;
    private String title;
    private String url;

    public MediaItemLink(String title, String description, String url, List<ImageUrl> imageUrls, List<BaseEntity> reshareOwners, boolean isReshare) {
        super(MediaItemType.LINK, reshareOwners, isReshare);
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrls = imageUrls;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public List<ImageUrl> getImageUrls() {
        return this.imageUrls;
    }
}
