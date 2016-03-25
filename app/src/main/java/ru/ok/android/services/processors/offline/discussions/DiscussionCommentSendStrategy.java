package ru.ok.android.services.processors.offline.discussions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import java.util.Map;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.MessageProcessStrategy;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.json.JsonResultParser;
import ru.ok.java.api.json.discussions.DiscussionSendCommentBatchParser;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.discussions.DiscussionCommentRequest;
import ru.ok.java.api.request.discussions.DiscussionCommentSendBatchRequest;
import ru.ok.java.api.request.discussions.DiscussionInfoRequest;
import ru.ok.java.api.request.discussions.DiscussionSendCommentRequest;
import ru.ok.java.api.request.mediatopic.MediatopicByIdsRequest;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.response.discussion.DiscussionSendCommentBatchResponse;
import ru.ok.model.Discussion;

final class DiscussionCommentSendStrategy implements MessageProcessStrategy<DiscussionCommentSendBatchRequest, DiscussionSendCommentBatchResponse> {
    DiscussionCommentSendStrategy() {
    }

    public DiscussionCommentSendBatchRequest createRequest(Cursor cursor) {
        String discussionId = cursor.getString(cursor.getColumnIndex("discussion_id"));
        String discussionType = cursor.getString(cursor.getColumnIndex("discussion_type"));
        DiscussionSendCommentRequest sendRequest = new DiscussionSendCommentRequest(discussionId, discussionType, cursor.getString(cursor.getColumnIndex(Message.ELEMENT)), cursor.getString(cursor.getColumnIndex("reply_to_comment_id")), "GROUP".equals(cursor.getString(cursor.getColumnIndex("author_type"))));
        DiscussionCommentRequest commentRequest = new DiscussionCommentRequest(discussionId, discussionType, new RequestJSONParam(new SupplierRequest(sendRequest.getCommentIdSupplier())));
        DiscussionInfoRequest infoRequest = new DiscussionInfoRequest(discussionId, discussionType);
        return new DiscussionCommentSendBatchRequest(sendRequest, commentRequest, infoRequest, new MediatopicByIdsRequest(new RequestJSONParam(new SupplierRequest(infoRequest.getMediaTopicIdsSupplier()))));
    }

    public JsonResultParser<DiscussionSendCommentBatchResponse> createParser(JsonHttpResult result) {
        return new DiscussionSendCommentBatchParser(result);
    }

    public void onItemPostUpdate(Context context, Map<String, String> ids, DiscussionSendCommentBatchResponse response) {
        String discussionId = (String) ids.get("discussion_id");
        String discussionType = (String) ids.get("discussion_type");
        Bundle input = new Bundle();
        input.putParcelable("DISCUSSION", new Discussion(discussionId, discussionType));
        Bundle output = new Bundle();
        output.putParcelable("DISCUSSION", response.infoResponse);
        GlobalBus.send(2131624154, new BusEvent(input, output, -1));
    }

    public void fillValuesByResult(ContentValues cv, DiscussionSendCommentBatchResponse parsed) {
        cv.put("server_id", parsed.sendResponse.getId());
    }

    public void removeExistingDuplicates(Map<String, String> map, DiscussionSendCommentBatchResponse response) {
    }
}
