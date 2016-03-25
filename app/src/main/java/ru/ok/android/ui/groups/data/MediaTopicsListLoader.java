package ru.ok.android.ui.groups.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.processors.mediatopic.MediaTopicGetTopicsProcessor;
import ru.ok.android.services.processors.mediatopic.MediaTopicGetTopicsProcessor.Result;
import ru.ok.android.services.processors.mediatopic.MediaTopicsResponse;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.android.ui.stream.list.Feed2StreamItemBinder;
import ru.ok.android.ui.stream.list.FeedDisplayParams;
import ru.ok.android.ui.stream.list.StreamItem;
import ru.ok.android.ui.stream.view.FeedMediaTopicStyle;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.stream.JsonGetStreamParser;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.mediatopics.MediatopicWithEntityBuilders;
import ru.ok.model.stream.EntityReferenceResolver;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedMediaTopicEntity;
import ru.ok.model.stream.entities.FeedMediaTopicEntityBuilder;

public class MediaTopicsListLoader extends AsyncTaskLoader<MediaTopicsListLoaderResult> {
    private String anchor;
    private PagingDirection direction;
    private final String filter;
    private final String groupId;
    private final Integer pageCount;
    private Long tagId;
    private final String topicId;
    private final String userId;

    public MediaTopicsListLoader(Context context, String groupId, String userId, String filter, Long tagId, Integer pageCount) {
        super(context);
        this.direction = PagingDirection.FORWARD;
        this.groupId = groupId;
        this.userId = userId;
        this.filter = filter;
        this.tagId = tagId;
        this.pageCount = pageCount;
        this.topicId = null;
    }

    public MediaTopicsListLoader(Context context, String groupId, String topicId) {
        super(context);
        this.direction = PagingDirection.FORWARD;
        this.groupId = groupId;
        this.topicId = topicId;
        this.userId = null;
        this.filter = null;
        this.pageCount = null;
    }

    public MediaTopicsListLoaderResult loadInBackground() {
        Result result;
        MediaTopicsListLoaderResult ret = new MediaTopicsListLoaderResult();
        ret.tagId = this.tagId;
        ret.requestAnchor = this.anchor;
        ret.requestDirection = this.direction;
        if (this.topicId != null) {
            result = MediaTopicGetTopicsProcessor.getRejectedTopic(this.groupId, this.topicId);
        } else {
            result = MediaTopicGetTopicsProcessor.getTopics(this.filter, this.userId, this.groupId, this.tagId == null ? null : Long.toString(this.tagId.longValue()), null, this.anchor, this.direction.getValue(), this.pageCount.intValue());
        }
        ret.isSuccess = result.isSuccess;
        if (result.isSuccess) {
            MediaTopicsResponse mediaTopicsResponse = result.mediaTopicsResponse;
            ret.mediaTopicsResponse = mediaTopicsResponse;
            ret.mediaTopicStreamItems = processLoaderResultResponse(mediaTopicsResponse);
        } else {
            ret.errorType = result.errorType;
        }
        return ret;
    }

    private List<StreamItem> processLoaderResultResponse(MediaTopicsResponse mediaTopicsResponse) {
        try {
            Pair<List<FeedMediaTopicEntity>, Map<String, BaseEntity>> mediaTopicsWithEntitiesPair = buildMediaTopics(mediaTopicsResponse.mediaTopicWithEntityBuilders);
            List<FeedMediaTopicEntity> feedMediaTopicEntities = mediaTopicsWithEntitiesPair.first;
            Map<String, BaseEntity> entities = mediaTopicsWithEntitiesPair.second;
            Feed2StreamItemBinder feed2StreamItemBinder = new Feed2StreamItemBinder(getContext(), FeedDisplayParams.fromStreamContext(getStreamContext()), new FeedMediaTopicStyle(getContext(), null, 0, 2131296527));
            List<StreamItem> arrayList = new ArrayList(feedMediaTopicEntities.size());
            for (int i = 0; i < feedMediaTopicEntities.size(); i++) {
                FeedMediaTopicEntity feedMediaTopicEntity = (FeedMediaTopicEntity) feedMediaTopicEntities.get(i);
                feed2StreamItemBinder.bindIndividualMediaTopic(new FeedWithState(new MediaTopicFeed(getDummyFeedId(feedMediaTopicEntity), 5, feedMediaTopicEntity.getCreationTime(), null, null, feedMediaTopicEntity.getLikeInfo(), feedMediaTopicEntity.getDiscussionSummary(), isMarkAsSpamEnabled(feedMediaTopicEntity) ? feedMediaTopicEntity.getMarkAsSpamId() : null, feedMediaTopicEntity.getDeleteId(), 0, entities, feedMediaTopicEntity.isSticky(), canSetToStatus(feedMediaTopicEntity))), 0, feedMediaTopicEntity, arrayList);
            }
            return arrayList;
        } catch (Throwable e) {
            Logger.m180e(e, "Error build media topics from response for groupId %d", this.groupId);
            return null;
        }
    }

    private boolean canSetToStatus(FeedMediaTopicEntity feedMediaTopicEntity) {
        return entityIsCurrentUser(feedMediaTopicEntity.getAuthor()) || entityIsCurrentUser(feedMediaTopicEntity.getOwner());
    }

    private boolean isMarkAsSpamEnabled(FeedMediaTopicEntity feedMediaTopicEntity) {
        if (entityIsCurrentUser(feedMediaTopicEntity.getAuthor()) || entityIsCurrentUser(feedMediaTopicEntity.getOwner())) {
            return false;
        }
        return true;
    }

    public static boolean entityIsCurrentUser(BaseEntity entity) {
        return entity != null && entity.getType() == 7 && entity.getId().equals(OdnoklassnikiApplication.getCurrentUser().getId());
    }

    private StreamContext getStreamContext() {
        if (this.groupId != null) {
            return StreamContext.groupProfile(this.groupId);
        }
        if (this.userId != null) {
            return StreamContext.userProfile(this.userId);
        }
        return StreamContext.stream();
    }

    private Pair<List<FeedMediaTopicEntity>, Map<String, BaseEntity>> buildMediaTopics(MediatopicWithEntityBuilders mediatopicWithEntityBuilders) throws FeedObjectException {
        List<FeedMediaTopicEntity> mediaTopicsList = new ArrayList();
        Map<String, BaseEntity> resolvedEntities = EntityReferenceResolver.resolveEntityRefs(mediatopicWithEntityBuilders.entities);
        for (int i = 0; i < mediatopicWithEntityBuilders.mediatopics.size(); i++) {
            try {
                FeedMediaTopicEntityBuilder entityBuilder = (FeedMediaTopicEntityBuilder) mediatopicWithEntityBuilders.mediatopics.get(i);
                resolvedEntities.put(JsonGetStreamParser.toString(entityBuilder.getType(), entityBuilder.getId()), entityBuilder.preBuild());
            } catch (FeedObjectException e) {
                Logger.m187w(e, "Failed to build entity object: %s", e);
            }
        }
        for (FeedMediaTopicEntityBuilder mediaTopicEntityBuilder : mediatopicWithEntityBuilders.mediatopics) {
            try {
                mediaTopicsList.add((FeedMediaTopicEntity) mediaTopicEntityBuilder.build(resolvedEntities));
            } catch (Throwable ex) {
                Logger.m179e(ex, "Can't resolve reference!");
            }
        }
        return new Pair(mediaTopicsList, resolvedEntities);
    }

    private long getDummyFeedId(FeedMediaTopicEntity feedMediaTopicEntity) {
        long ret = 0;
        try {
            ret = Long.parseLong(feedMediaTopicEntity.getId());
        } catch (Throwable e) {
            Logger.m179e(e, "Error assume mediaTopic.id as long");
        }
        return ret;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void setDirection(PagingDirection direction) {
        this.direction = direction;
    }
}
