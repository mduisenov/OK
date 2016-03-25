package ru.ok.android.services.processors.offline.discussions;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import java.util.Map;
import java.util.Set;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.offline.NotificationUtils;
import ru.ok.android.offline.OfflineAlarmHelper;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.offline.OfflineBaseSendProcessor;
import ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.MessageProcessStrategy;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.discussions.DiscussionCommentSendBatchRequest;
import ru.ok.java.api.request.discussions.DiscussionEditCommentRequest;

abstract class DiscussionCommentsSendBaseProcessor extends OfflineBaseSendProcessor<Object, BaseRequest> {
    public DiscussionCommentsSendBaseProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    protected final Uri itemUri(Map<String, String> ids) {
        return OdklProvider.commentUri(Long.parseLong((String) ids.get("_id")));
    }

    protected final void idColumns(Set<String> columns) {
        columns.add("_id");
        columns.add("discussion_id");
        columns.add("discussion_type");
        columns.add("server_id");
    }

    protected final void onItemOverdue(Context context, Map<String, String> ids, BaseRequest request) {
        String discussionId = (String) ids.get("discussion_id");
        String discussionType = (String) ids.get("discussion_type");
        int commentId = Integer.parseInt((String) ids.get("_id"));
        String message = extractRequestMessage(request);
        OfflineAlarmHelper.unScheduleDiscussionUndeliveredNotification(context, discussionId, discussionType, commentId);
        NotificationUtils.sendDiscussionCommentFailedBroadcast(context, discussionId, discussionType, message);
    }

    private String extractRequestMessage(BaseRequest request) {
        return request instanceof DiscussionCommentSendBatchRequest ? ((DiscussionCommentSendBatchRequest) request).sendRequest.getComment() : ((DiscussionEditCommentRequest) request).getComment();
    }

    protected final void onItemFailed(Context context, Map<String, String> map) {
        OfflineAlarmHelper.scheduleNextAttempt(context);
    }

    protected final void onItemFailedServer(Context context, Map<String, String> ids, BaseRequest request, ErrorType errorType) {
        String discussionId = (String) ids.get("discussion_id");
        String discussionType = (String) ids.get("discussion_type");
        OfflineAlarmHelper.unScheduleDiscussionUndeliveredNotification(context, discussionId, discussionType, Integer.parseInt((String) ids.get("_id")));
        int errorMessage = errorType.getDefaultErrorMessage();
        if (errorType == ErrorType.RESTRICTED_ACCESS_SECTION_FOR_FRIENDS) {
            errorMessage = 2131165801;
        } else if (errorType == ErrorType.RESTRICTED_ACCESS_FOR_NON_MEMBERS) {
            errorMessage = 2131165608;
        } else if (errorType == ErrorType.RESTRICTED_ACCESS_ACTION_BLOCKED) {
            errorMessage = 2131165606;
        }
        NotificationUtils.sendDiscussionCommentFailedServerBroadcast(context, discussionId, discussionType, LocalizationManager.getString(context, errorMessage), extractRequestMessage(request));
    }

    protected final void onItemSuccess(Context context, Map<String, String> ids) {
        OfflineAlarmHelper.unScheduleDiscussionUndeliveredNotification(context, (String) ids.get("discussion_id"), (String) ids.get("discussion_type"), Integer.parseInt((String) ids.get("_id")));
    }

    protected final String[] projection() {
        return new String[]{"_id", "server_id", "discussion_id", "discussion_type", Message.ELEMENT, "message_edited", "reply_to_comment_id", "author_type", "_date", "server_id"};
    }

    protected MessageProcessStrategy<? extends BaseRequest, ?> createStrategy(Map<String, String> ids) {
        return !TextUtils.isEmpty((String) ids.get("server_id")) ? new DiscussionCommentEditStrategy() : new DiscussionCommentSendStrategy();
    }
}
