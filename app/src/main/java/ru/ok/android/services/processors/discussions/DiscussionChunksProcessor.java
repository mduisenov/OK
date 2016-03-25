package ru.ok.android.services.processors.discussions;

import android.database.Cursor;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.DiscussionsStorageFacade;
import ru.ok.android.db.access.fillers.UserInfoValuesFiller;
import ru.ok.android.db.base.OfflineTable.Status;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.discussions.JsonDiscussionCommentsBatchParser;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.batch.SupplierRequest;
import ru.ok.java.api.request.discussions.DiscussionCommentsRequest;
import ru.ok.java.api.request.discussions.DiscussionInfoRequest;
import ru.ok.java.api.request.discussions.DiscussionLikesRequest;
import ru.ok.java.api.request.mediatopic.MediatopicByIdsRequest;
import ru.ok.java.api.request.messaging.AttachmentRequest;
import ru.ok.java.api.request.paging.PagingAnchor;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.java.api.request.param.RequestJSONParam;
import ru.ok.java.api.request.users.UserInfoRequest;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.response.discussion.DiscussionCommentsBatchResponse;
import ru.ok.java.api.response.discussion.UsersLikesResponse;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.Discussion;
import ru.ok.model.UserInfo;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageComment;

public final class DiscussionChunksProcessor {
    @Subscribe(on = 2131623944, to = 2131623967)
    public void loadFirstCommentsPortion(BusEvent event) {
        try {
            Discussion discussion = (Discussion) event.bundleInput.getParcelable("DISCUSSION");
            PagingAnchor initialAnchor = PagingAnchor.valueOf(event.bundleInput.getString("ANCHOR"));
            Logger.m173d("discussion: %s, anchor: %s, direction: %s", discussion, initialAnchor, initialAnchor == PagingAnchor.LAST ? PagingDirection.BACKWARD : PagingDirection.FORWARD);
            DiscussionCommentsBatchResponse response = performCommentsChunkRequest(discussion, initialAnchor.name(), direction);
            Bundle output = createOutputBundle(response.info, response.comments.list, queryDiscussionOfflineComments(discussion), response.users, response.likes);
            boolean hasMore = initialAnchor != PagingAnchor.LAST && response.comments.hasMore;
            boolean hasPrev = initialAnchor != PagingAnchor.LAST ? !response.comments.isFirst : response.comments.hasMore;
            output.putBoolean("HAS_MORE_NEXT", hasMore);
            output.putBoolean("HAS_MORE_PREVIOUS", hasPrev);
            GlobalBus.send(2131624146, new BusEvent(event.bundleInput, output, -1));
        } catch (Throwable e) {
            Logger.m178e(e);
            GlobalBus.send(2131624146, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    private List<OfflineMessage<MessageComment>> queryDiscussionOfflineComments(Discussion discussion) {
        Cursor cursor = DiscussionsStorageFacade.queryComments(discussion);
        List<OfflineMessage<MessageComment>> result = new ArrayList();
        List<Integer> comments2Delete = new ArrayList();
        while (cursor.moveToNext()) {
            OfflineMessage<MessageComment> comment = DiscussionsStorageFacade.cursor2Comment(cursor);
            if (comment.offlineData.status == Status.SENT || comment.offlineData.status == Status.RECEIVED) {
                comments2Delete.add(Integer.valueOf(comment.offlineData.databaseId));
            } else {
                try {
                    result.add(comment);
                } finally {
                    cursor.close();
                }
            }
        }
        DiscussionsStorageFacade.deleteComments(comments2Delete);
        return result;
    }

    @Subscribe(on = 2131623944, to = 2131623969)
    public void loadPreviousCommentsPortion(BusEvent event) {
        try {
            Logger.m173d("discussion: %s, anchor: %s", (Discussion) event.bundleInput.getParcelable("DISCUSSION"), event.bundleInput.getString("ANCHOR"));
            DiscussionCommentsBatchResponse response = performCommentsChunkRequest(discussion, anchor, PagingDirection.BACKWARD);
            Bundle output = createOutputBundle(response.info, response.comments.list, null, response.users, response.likes);
            output.putBoolean("HAS_MORE_PREVIOUS", response.comments.hasMore);
            GlobalBus.send(2131624148, new BusEvent(event.bundleInput, output, -1));
        } catch (Throwable e) {
            Logger.m178e(e);
            GlobalBus.send(2131624148, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623968)
    public void loadNextCommentsPortion(BusEvent event) {
        try {
            Logger.m173d("discussion: %s, anchor: %s", (Discussion) event.bundleInput.getParcelable("DISCUSSION"), event.bundleInput.getString("ANCHOR"));
            DiscussionCommentsBatchResponse response = performCommentsChunkRequest(discussion, anchor, PagingDirection.FORWARD);
            Bundle output = createOutputBundle(response.info, response.comments.list, null, response.users, response.likes);
            output.putBoolean("HAS_MORE_NEXT", response.comments.hasMore);
            GlobalBus.send(2131624147, new BusEvent(event.bundleInput, output, -1));
        } catch (Throwable e) {
            Logger.m178e(e);
            GlobalBus.send(2131624147, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    private static DiscussionCommentsBatchResponse performCommentsChunkRequest(Discussion discussion, String anchor, PagingDirection direction) throws Exception {
        DiscussionCommentsBatchResponse result = (DiscussionCommentsBatchResponse) new JsonDiscussionCommentsBatchParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(createRequests(discussion, anchor, direction))).parse();
        Collections.sort(result.comments.list, MessageBase.COMPARATOR_DATE);
        return result;
    }

    private static BatchRequest createRequests(Discussion discussion, String anchor, PagingDirection direction) {
        DiscussionCommentsRequest commentsRequest = new DiscussionCommentsRequest(discussion.id, discussion.type, anchor, direction, 50);
        UserInfoRequest usersInfoRequest = new UserInfoRequest(new RequestJSONParam(new SupplierRequest("discussions.getComments.user_ids")), UserInfoValuesFiller.DISCUSSIONS.getRequestFields(), false);
        DiscussionInfoRequest discussionInfoRequest = new DiscussionInfoRequest(discussion.id, discussion.type);
        DiscussionLikesRequest likesRequest = new DiscussionLikesRequest(discussion.id, discussion.type, null, PagingDirection.FORWARD, 20, new RequestFieldsBuilder().withPrefix("user.").addField(FIELDS.GENDER).addField(DeviceUtils.getUserAvatarPicFieldName()).build());
        MediatopicByIdsRequest mediaTopicRequest = new MediatopicByIdsRequest(new RequestJSONParam(new SupplierRequest(discussionInfoRequest.getMediaTopicIdsSupplier())));
        BatchRequests requests = new BatchRequests();
        requests.addRequest(discussionInfoRequest).addRequest(commentsRequest).addRequest(usersInfoRequest).addRequest(likesRequest, true).addRequest(mediaTopicRequest).addRequest(new AttachmentRequest(new RequestJSONParam(new SupplierRequest("discussions.getComments.attachment_ids"))));
        return new BatchRequest(requests);
    }

    private static Bundle createOutputBundle(DiscussionInfoResponse discussionInfo, List<MessageComment> messages, List<OfflineMessage<MessageComment>> additionalMessages, ArrayList<UserInfo> users, UsersLikesResponse likes) {
        Bundle output = new Bundle();
        ArrayList<OfflineMessage<MessageComment>> offlineMessages = new ArrayList();
        for (MessageComment message : messages) {
            offlineMessages.add(new OfflineMessage(message, null));
        }
        if (additionalMessages != null) {
            offlineMessages.addAll(additionalMessages);
        }
        output.putParcelableArrayList("MESSAGES", offlineMessages);
        output.putParcelable("GENERAL_INFO", discussionInfo);
        output.putParcelableArrayList("USERS", users);
        output.putParcelableArrayList("LIKE_USERS", likes.getUsers());
        return output;
    }
}
