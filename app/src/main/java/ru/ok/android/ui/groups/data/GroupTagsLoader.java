package ru.ok.android.ui.groups.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ok.android.services.processors.mediatopic.MediaTopicGetGroupTagsProcessor;
import ru.ok.android.services.processors.mediatopic.MediaTopicGetGroupTagsProcessor.Result;
import ru.ok.android.ui.groups.fragments.GroupTopicsFragment.GroupTagsLoaderResult;

public class GroupTagsLoader extends AsyncTaskLoader<GroupTagsLoaderResult> {
    private final String groupId;

    public GroupTagsLoader(Context context, String groupId) {
        super(context);
        this.groupId = groupId;
    }

    public GroupTagsLoaderResult loadInBackground() {
        Result result = MediaTopicGetGroupTagsProcessor.getGroupTags(this.groupId, null, null);
        return new GroupTagsLoaderResult(result.isSuccess, result.tags);
    }
}
