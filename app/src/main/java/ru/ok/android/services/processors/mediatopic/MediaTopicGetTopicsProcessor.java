package ru.ok.android.services.processors.mediatopic;

import android.util.Log;
import java.lang.reflect.Field;
import org.json.JSONObject;
import ru.ok.android.services.processors.base.BaseProcessorResult;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.json.mediatopics.MediatopicByIdsParser;
import ru.ok.java.api.request.mediatopic.MediaTopicGetGroupTopicRequest;
import ru.ok.java.api.request.mediatopic.MediaTopicGetTopicsRequest;
import ru.ok.java.api.utils.JsonUtil;
import ru.ok.model.mediatopics.MediatopicWithEntityBuilders;
import ru.ok.model.stream.entities.BaseEntityBuilder;
import ru.ok.model.stream.entities.FeedMediaTopicEntityBuilder;

public class MediaTopicGetTopicsProcessor {

    public static class Result extends BaseProcessorResult {
        public final MediaTopicsResponse mediaTopicsResponse;

        public Result(boolean isSuccess, ErrorType errorType, MediaTopicsResponse mediaTopicsResponse) {
            super(isSuccess, errorType);
            this.mediaTopicsResponse = mediaTopicsResponse;
        }
    }

    public static Result getTopics(String filter, String uid, String gid, String tagId, String fields, String anchor, String direction, int count) {
        try {
            JSONObject jsonObject = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicGetTopicsRequest(filter, uid, gid, fields, tagId, anchor, direction, count)).getResultAsObject();
            MediatopicWithEntityBuilders mediaTopics = new MediatopicByIdsParser(jsonObject).parse();
            if ("GROUP_SUGGESTED".equals(filter)) {
                cleanLikeDiscussionForMediatopicEntities(mediaTopics);
            }
            return new Result(true, null, new MediaTopicsResponse(mediaTopics, filter, JsonUtil.optStringOrNull(jsonObject, "anchor"), JsonUtil.getBooleanSafely(jsonObject, "has_more"), JsonUtil.getBooleanSafely(jsonObject, "inconsistent")));
        } catch (Exception e) {
            return new Result(false, ErrorType.fromException(e), null);
        }
    }

    public static Result getRejectedTopic(String groupId, String topicId) {
        try {
            JSONObject jsonObject = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicGetGroupTopicRequest(groupId, topicId)).getResultAsObject();
            MediatopicWithEntityBuilders mediaTopics = new MediatopicByIdsParser(jsonObject).parse();
            cleanLikeDiscussionForMediatopicEntities(mediaTopics);
            return new Result(true, null, new MediaTopicsResponse(mediaTopics, null, JsonUtil.optStringOrNull(jsonObject, "anchor"), JsonUtil.getBooleanSafely(jsonObject, "has_more"), JsonUtil.getBooleanSafely(jsonObject, "inconsistent")));
        } catch (Exception e) {
            return new Result(false, ErrorType.fromException(e), null);
        }
    }

    private static void cleanLikeDiscussionForMediatopicEntities(MediatopicWithEntityBuilders mediaTopics) {
        for (FeedMediaTopicEntityBuilder feedMediaTopicEntityBuilder : mediaTopics.mediatopics) {
            try {
                Field likeInfo = BaseEntityBuilder.class.getDeclaredField("likeInfo");
                likeInfo.setAccessible(true);
                Field discussionSummary = BaseEntityBuilder.class.getDeclaredField("discussionSummary");
                discussionSummary.setAccessible(true);
                likeInfo.set(feedMediaTopicEntityBuilder, null);
                discussionSummary.set(feedMediaTopicEntityBuilder, null);
            } catch (Exception e) {
                Log.e("odkl-", "Error fix mediatopics discussionSummary, likeInfo", e);
            }
        }
    }
}
