package ru.ok.android.ui.groups.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import java.util.List;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.groups.GroupsProcessor;
import ru.ok.android.services.processors.groups.GroupsProcessor.GroupTopCategoriesProcessorResult;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.groups.GroupsTopCategoryItem;

public class GroupsTopCategoriesLoader extends AsyncTaskLoader<Result> {
    private String anchor;
    private int count;
    private PagingDirection direction;

    public static class Result {
        public final List<GroupsTopCategoryItem> categories;
        public final ErrorType errorType;
        public final boolean isSuccess;

        public Result(boolean isSuccess, List<GroupsTopCategoryItem> categories, ErrorType errorType) {
            this.isSuccess = isSuccess;
            this.categories = categories;
            this.errorType = errorType;
        }
    }

    public GroupsTopCategoriesLoader(Context context, int count) {
        super(context);
        this.direction = PagingDirection.FORWARD;
        this.count = count;
    }

    public Result loadInBackground() {
        GroupTopCategoriesProcessorResult result = GroupsProcessor.getGroupsTopCategories(this.anchor, this.direction.getValue(), this.count);
        return new Result(result.isSuccess, result.groupCategoryItems, result.errorType);
    }
}
