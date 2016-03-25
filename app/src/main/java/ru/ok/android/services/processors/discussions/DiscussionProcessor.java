package ru.ok.android.services.processors.discussions;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.ServiceHelper;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.DiscussionsStorageFacade;
import ru.ok.android.db.base.OfflineTable.Status;
import ru.ok.android.offline.OfflineAlarmHelper;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.messaging.MessagesProcessor;
import ru.ok.android.services.processors.messaging.MessagesProcessor.DeleteCallback;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.JsonLikeInfoParser;
import ru.ok.java.api.json.discussions.JsonDiscussionCommentParser;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.discussions.DiscussionCommentRequest;
import ru.ok.java.api.request.discussions.DiscussionDeleteCommentRequest;
import ru.ok.java.api.request.discussions.DiscussionSpamCommentRequest;
import ru.ok.java.api.request.like.LikeRequest;
import ru.ok.java.api.request.like.UnLikeRequest;
import ru.ok.java.api.request.param.RequestCollectionParam;
import ru.ok.model.Discussion;
import ru.ok.model.messages.MessageComment;
import ru.ok.model.stream.LikeInfo;

public final class DiscussionProcessor {

    private static abstract class DeleteCallbackAdapter implements DeleteCallback {
        private DeleteCallbackAdapter() {
        }

        public void deleteDatabaseMessages(Collection<Integer> ids) {
            DiscussionsStorageFacade.deleteComments(ids);
        }

        public void unscheduleUndeliveredNotification(Bundle data, Integer id) {
            Discussion discussion = (Discussion) data.getParcelable("DISCUSSION");
            OfflineAlarmHelper.unScheduleDiscussionUndeliveredNotification(OdnoklassnikiApplication.getContext(), discussion.id, discussion.type, id.intValue());
        }

        public void onPostDelete(Bundle data) throws Exception {
        }
    }

    /* renamed from: ru.ok.android.services.processors.discussions.DiscussionProcessor.1 */
    class C04511 extends DeleteCallbackAdapter {
        C04511() {
            super();
        }

        public BaseRequest createDeleteRequest(Bundle data, Set<String> serverIds) {
            Discussion discussion = (Discussion) data.getParcelable("DISCUSSION");
            return new DiscussionDeleteCommentRequest(discussion.id, discussion.type, serverIds, data.getBoolean("BLOCK"));
        }
    }

    /* renamed from: ru.ok.android.services.processors.discussions.DiscussionProcessor.2 */
    class C04522 extends DeleteCallbackAdapter {
        C04522() {
            super();
        }

        public BaseRequest createDeleteRequest(Bundle data, Set<String> serverIds) {
            Discussion discussion = (Discussion) data.getParcelable("DISCUSSION");
            return new DiscussionSpamCommentRequest(discussion.id, discussion.type, serverIds);
        }
    }

    @Subscribe(on = 2131623944, to = 2131623964)
    public void deleteComments(BusEvent event) {
        MessagesProcessor.deleteGeneral(2131624143, event, new C04511());
    }

    @Subscribe(on = 2131623944, to = 2131623972)
    public void spamComments(BusEvent event) {
        MessagesProcessor.deleteGeneral(2131624151, event, new C04522());
    }

    @Subscribe(on = 2131623944, to = 2131623966)
    public void addCommentLike(BusEvent event) {
        try {
            BaseRequest likeRequest;
            String commentId = event.bundleInput.getString("MESSAGE_ID");
            LikeInfo likeInfo = (LikeInfo) event.bundleInput.getParcelable("LIKE_INFO");
            String logContext = event.bundleInput.getString("LOG_CONTEXT");
            if (likeInfo.self) {
                likeRequest = new UnLikeRequest(likeInfo.likeId, logContext);
            } else {
                likeRequest = new LikeRequest(likeInfo.likeId, logContext);
            }
            LikeInfo resultLikeInfo = new JsonLikeInfoParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(likeRequest).getResultAsObject().optJSONObject("summary")).parse();
            Bundle output = new Bundle();
            output.putString("MESSAGE_ID", commentId);
            output.putParcelable("LIKE_INFO", resultLikeInfo);
            GlobalBus.send(2131624145, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624145, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623965)
    public void addDiscussionLike(BusEvent event) {
        try {
            BaseRequest likeRequest;
            LikeInfo likeInfo = (LikeInfo) event.bundleInput.getParcelable("LIKE_INFO");
            String logContext = event.bundleInput.getString("LOG_CONTEXT");
            if (likeInfo.self) {
                likeRequest = new UnLikeRequest(likeInfo.likeId, logContext);
            } else {
                likeRequest = new LikeRequest(likeInfo.likeId, logContext);
            }
            LikeInfo resultLikeInfo = new JsonLikeInfoParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(likeRequest).getResultAsObject().optJSONObject("summary")).parse();
            Bundle output = new Bundle();
            output.putParcelable("LIKE_INFO", resultLikeInfo);
            GlobalBus.send(2131624144, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624144, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623975)
    public void loadOneComment(BusEvent event) {
        try {
            Discussion discussion = (Discussion) event.bundleInput.getParcelable("DISCUSSION");
            String messageId = event.bundleInput.getString("MESSAGE_ID");
            Bundle output = new Bundle();
            output.putParcelable("MESSAGE", new OfflineMessage((MessageComment) new JsonDiscussionCommentParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new DiscussionCommentRequest(discussion.id, discussion.type, new RequestCollectionParam(Arrays.asList(new String[]{messageId}))))).parse(), null));
            GlobalBus.send(2131624155, new BusEvent(event.bundleInput, output, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624155, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623973)
    public void editComment(BusEvent e) {
        try {
            Discussion discussion = (Discussion) e.bundleInput.getParcelable("DISCUSSION");
            OfflineMessage<MessageComment> comment = (OfflineMessage) e.bundleInput.getParcelable("COMMENT");
            String newText = e.bundleInput.getString("TEXT");
            Logger.m173d("comment: %s, newText: %s, discussion: %s", comment, newText, discussion);
            if (comment.offlineData != null) {
                ContentValues cv = new ContentValues();
                if (((MessageComment) comment.message).hasServerId()) {
                    cv.put("message_edited", newText);
                } else {
                    cv.put(Message.ELEMENT, newText);
                }
                cv.put(NotificationCompat.CATEGORY_STATUS, Status.WAITING.name());
                int databaseId = comment.offlineData.databaseId;
                DiscussionsStorageFacade.updateComment(OdnoklassnikiApplication.getContext(), databaseId, cv);
                ServiceHelper.from().sendDiscussionComment(databaseId);
            } else {
                Logger.m184w("We do not support editing of online comments yet");
            }
            GlobalBus.send(2131624152, new BusEvent(e.bundleInput, null, -1));
        } catch (Throwable ex) {
            Logger.m178e(ex);
            GlobalBus.send(2131624152, new BusEvent(e.bundleInput, null, -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131623974)
    public void editCommentUndo(BusEvent e) {
        Logger.m173d("Undo edit for comment: %s", (OfflineMessage) e.bundleInput.getParcelable("COMMENT"));
        try {
            ContentValues cv = new ContentValues();
            cv.put("message_edited", (String) null);
            cv.put("failure_reason", (String) null);
            cv.put(NotificationCompat.CATEGORY_STATUS, Status.RECEIVED.name());
            DiscussionsStorageFacade.updateComment(OdnoklassnikiApplication.getContext(), comment.offlineData.databaseId, cv);
        } catch (Throwable ex) {
            Logger.m178e(ex);
            GlobalBus.send(2131624153, new BusEvent(e.bundleInput, CommandProcessor.createErrorBundle(ex), -2));
        }
    }
}
