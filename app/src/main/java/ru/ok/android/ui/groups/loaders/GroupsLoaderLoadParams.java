package ru.ok.android.ui.groups.loaders;

import ru.ok.java.api.request.paging.PagingDirection;

public class GroupsLoaderLoadParams {
    public final String anchor;
    public final String categoryId;
    public final PagingDirection direction;

    public GroupsLoaderLoadParams(String anchor, PagingDirection direction) {
        this(anchor, direction, null);
    }

    public GroupsLoaderLoadParams(String anchor, PagingDirection direction, String categoryId) {
        this.anchor = anchor;
        this.direction = direction;
        this.categoryId = categoryId;
    }
}
