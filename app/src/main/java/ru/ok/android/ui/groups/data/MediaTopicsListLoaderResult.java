package ru.ok.android.ui.groups.data;

import android.support.annotation.Nullable;
import java.util.List;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.mediatopic.MediaTopicsResponse;
import ru.ok.android.ui.stream.list.StreamItem;
import ru.ok.java.api.request.paging.PagingDirection;

public class MediaTopicsListLoaderResult {
    public ErrorType errorType;
    public boolean isSuccess;
    @Nullable
    public List<StreamItem> mediaTopicStreamItems;
    public MediaTopicsResponse mediaTopicsResponse;
    public String requestAnchor;
    public PagingDirection requestDirection;
    public Long tagId;
}
