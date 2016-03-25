package ru.ok.model.stream.entities;

import ru.ok.model.stream.LikeInfoContext;

public class FeedAppEntity extends BaseEntity {
    private final int height;
    private final String iconUrl;
    private final String id;
    private final String name;
    private final String storeId;
    private final String tabStoreId;
    private final String url;
    private final int width;

    protected FeedAppEntity(LikeInfoContext likeInfo, String id, String iconUrl, int width, int height, String url, String name, String storeId, String tabStoreId) {
        super(1, likeInfo, null);
        this.id = id;
        this.iconUrl = iconUrl;
        this.width = width;
        this.height = height;
        this.url = url;
        this.name = name;
        this.storeId = storeId;
        this.tabStoreId = tabStoreId;
    }

    public String getId() {
        return this.id;
    }

    public String getStoreId() {
        return this.storeId == null ? this.tabStoreId : this.storeId;
    }

    public String getTabStoreId() {
        return this.tabStoreId == null ? this.storeId : this.tabStoreId;
    }
}
