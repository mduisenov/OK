package ru.ok.android.ui.groups.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ok.java.api.request.paging.PagingDirection;

public abstract class BaseGroupsPageLoader extends AsyncTaskLoader<GroupsLoaderResult> {
    protected String anchor;
    protected int count;
    protected PagingDirection direction;

    public BaseGroupsPageLoader(Context context, String anchor, PagingDirection direction, int count) {
        super(context);
        this.anchor = anchor;
        this.direction = direction;
        this.count = count;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void setDirection(PagingDirection direction) {
        this.direction = direction;
    }
}
