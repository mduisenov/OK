package ru.ok.android.ui.groups.loaders;

import java.util.List;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.groups.GroupsProcessor.GroupAdditionalInfo;
import ru.ok.model.GroupInfo;

public class GroupsLoaderResult {
    public final String anchor;
    public final ErrorType errorType;
    public final List<GroupInfo> groupInfos;
    public final List<GroupAdditionalInfo> groupsAdditionalInfos;
    public final boolean hasMore;
    public final boolean isSuccess;
    public final GroupsLoaderLoadParams loadParams;

    public GroupsLoaderResult(GroupsLoaderLoadParams loadParams, boolean isSuccess, ErrorType errorType) {
        this(loadParams, isSuccess, errorType, null, null, null, false);
    }

    public GroupsLoaderResult(GroupsLoaderLoadParams loadParams, boolean isSuccess, ErrorType errorType, List<GroupInfo> groupInfos, List<GroupAdditionalInfo> groupsAdditionalInfos, String anchor, boolean hasMore) {
        this.loadParams = loadParams;
        this.isSuccess = isSuccess;
        this.errorType = errorType;
        this.groupInfos = groupInfos;
        this.groupsAdditionalInfos = groupsAdditionalInfos;
        this.anchor = anchor;
        this.hasMore = hasMore;
    }
}
