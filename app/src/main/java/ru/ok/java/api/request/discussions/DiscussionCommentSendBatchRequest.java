package ru.ok.java.api.request.discussions;

import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.request.batch.BatchRequest;
import ru.ok.java.api.request.batch.BatchRequests;
import ru.ok.java.api.request.mediatopic.MediatopicByIdsRequest;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasFormat = true, httpType = HttpMethodType.POST)
public final class DiscussionCommentSendBatchRequest extends BatchRequest {
    public final DiscussionSendCommentRequest sendRequest;

    public DiscussionCommentSendBatchRequest(DiscussionSendCommentRequest sendRequest, DiscussionCommentRequest commentRequest, DiscussionInfoRequest infoRequest, MediatopicByIdsRequest mediaTopicRequest) {
        super(new BatchRequests().addRequest(sendRequest).addRequest(commentRequest).addRequest(infoRequest).addRequest(mediaTopicRequest));
        this.sendRequest = sendRequest;
    }
}
