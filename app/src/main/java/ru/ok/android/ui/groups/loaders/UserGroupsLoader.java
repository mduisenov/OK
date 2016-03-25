package ru.ok.android.ui.groups.loaders;

import android.content.Context;
import ru.ok.android.services.processors.groups.GroupsProcessor;
import ru.ok.android.services.processors.groups.GroupsProcessor.UserGroupsInfoProcessorResult;
import ru.ok.java.api.request.paging.PagingDirection;

public class UserGroupsLoader extends BaseGroupsPageLoader {
    private String uid;

    public UserGroupsLoader(Context context, String uid, int count) {
        this(context, uid, null, PagingDirection.FORWARD, count);
    }

    public UserGroupsLoader(Context context, String uid, String anchor, PagingDirection direction, int count) {
        super(context, anchor, direction, count);
        this.uid = uid;
    }

    public GroupsLoaderResult loadInBackground() {
        UserGroupsInfoProcessorResult result = GroupsProcessor.getUserGroupsInfo(this.uid, this.anchor, this.direction.getValue(), this.count);
        return new GroupsLoaderResult(new GroupsLoaderLoadParams(this.anchor, this.direction), result.isSuccess, result.errorType, result.groupsInfos, null, result.anchor, result.hasMore);
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void setDirection(PagingDirection direction) {
        this.direction = direction;
    }
}
