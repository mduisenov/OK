package ru.ok.android.ui.groups.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ok.android.services.processors.groups.GroupsProcessor;
import ru.ok.android.services.processors.groups.GroupsProcessor.CategoriesGroupsProcessorResult;
import ru.ok.android.services.processors.groups.GroupsProcessor.CategoryGroupPage;
import ru.ok.java.api.request.paging.PagingDirection;

public class CategoriesGroupsLoader extends AsyncTaskLoader<GroupsLoaderResult> {
    private String anchor;
    private String categoryId;
    private int count;
    private PagingDirection direction;
    private int friendMembersLimit;

    public CategoriesGroupsLoader(Context context, String categoryId, int count) {
        this(context, categoryId, null, PagingDirection.FORWARD, count);
    }

    public CategoriesGroupsLoader(Context context, String categoryId, String anchor, PagingDirection direction, int count) {
        super(context);
        this.friendMembersLimit = 5;
        this.categoryId = categoryId;
        this.anchor = anchor;
        this.direction = direction;
        this.count = count;
    }

    public GroupsLoaderResult loadInBackground() {
        CategoriesGroupsProcessorResult result = GroupsProcessor.getCategoriesGroups(this.categoryId, this.anchor, this.direction.getValue(), this.count, this.friendMembersLimit);
        if (!result.isSuccess || result.categories == null) {
            return new GroupsLoaderResult(new GroupsLoaderLoadParams(this.anchor, this.direction, this.categoryId), result.isSuccess, result.errorType);
        }
        CategoryGroupPage categoryGroupPage;
        if (result.categories == null) {
            categoryGroupPage = null;
        } else {
            categoryGroupPage = (CategoryGroupPage) result.categories.values().iterator().next();
        }
        return new GroupsLoaderResult(new GroupsLoaderLoadParams(this.anchor, this.direction, this.categoryId), result.isSuccess, result.errorType, categoryGroupPage.groups, categoryGroupPage.groupsAdditionalInfos, categoryGroupPage.anchor, categoryGroupPage.hasMore);
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void setDirection(PagingDirection direction) {
        this.direction = direction;
    }
}
