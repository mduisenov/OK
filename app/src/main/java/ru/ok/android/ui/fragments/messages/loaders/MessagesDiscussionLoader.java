package ru.ok.android.ui.fragments.messages.loaders;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.ServiceHelper;
import ru.ok.android.bus.Bus;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.DBStatementsFactory;
import ru.ok.android.db.access.DiscussionsStorageFacade;
import ru.ok.android.db.access.QueriesComments.UpdateStatusAndDate;
import ru.ok.android.db.base.OfflineTable.Status;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.offline.OfflineAlarmHelper;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.bus.BusDiscussionsHelper;
import ru.ok.java.api.request.paging.PagingAnchor;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.model.Discussion;
import ru.ok.model.UserInfo;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase.MessageBaseBuilder;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageComment;
import ru.ok.model.messages.MessageComment.MessageCommentBuilder;
import ru.ok.model.stream.LikeInfo;
import ru.ok.model.stream.LikeInfoContext;

public final class MessagesDiscussionLoader extends MessagesBaseLoader<MessageComment, DiscussionInfoResponse> {
    private final Discussion discussionInfo;
    private final PagingAnchor initialAnchor;
    private List<UserInfo> likedUsers;

    /* renamed from: ru.ok.android.ui.fragments.messages.loaders.MessagesDiscussionLoader.1 */
    class C08851 implements Runnable {
        final /* synthetic */ OfflineMessage val$message;

        C08851(OfflineMessage offlineMessage) {
            this.val$message = offlineMessage;
        }

        public void run() {
            int databaseId = this.val$message.offlineData.databaseId;
            SQLiteStatement statement = DBStatementsFactory.getStatement(OdnoklassnikiApplication.getDatabase(MessagesDiscussionLoader.this.getContext()), UpdateStatusAndDate.QUERY);
            statement.bindString(1, Status.WAITING.name());
            statement.bindLong(2, System.currentTimeMillis());
            statement.bindLong(3, (long) databaseId);
            statement.execute();
            MessagesDiscussionLoader.this.getContext().getContentResolver().notifyChange(OdklProvider.commentUri((long) databaseId), null);
            OfflineAlarmHelper.scheduleDiscussionUndeliveredNotification(OdnoklassnikiApplication.getContext(), MessagesDiscussionLoader.this.discussionInfo, databaseId);
            ServiceHelper.from().sendDiscussionComment(databaseId);
        }
    }

    public MessagesDiscussionLoader(Context context, Discussion discussionInfo, PagingAnchor initialAnchor) {
        super(context, 2131165600);
        this.discussionInfo = discussionInfo;
        this.initialAnchor = initialAnchor;
    }

    protected void loadFirst() {
        BusDiscussionsHelper.loadFirstCommentsPortion(this.discussionInfo, this.initialAnchor);
    }

    public void loadPrevious() {
        String anchor;
        String commentId = findFirstServerCommentId();
        if (TextUtils.isEmpty(commentId)) {
            anchor = PagingAnchor.LAST.name();
        } else {
            anchor = PagingAnchor.buildAnchor(commentId);
        }
        BusDiscussionsHelper.loadPreviousCommentsPortion(this.discussionInfo, anchor);
    }

    public void loadNew(boolean markAsRead) {
        String anchor;
        String commentId = findLastServerNotSendCommentId();
        if (TextUtils.isEmpty(commentId)) {
            anchor = PagingAnchor.FIRST.name();
        } else {
            anchor = PagingAnchor.buildAnchor(commentId);
        }
        BusDiscussionsHelper.loadNextCommentsPortion(this.discussionInfo, anchor, true);
    }

    public void loadNext() {
        if (!this.messages.isEmpty()) {
            String commentId = findLastServerNotSendCommentId();
            if (!TextUtils.isEmpty(commentId)) {
                BusDiscussionsHelper.loadNextCommentsPortion(this.discussionInfo, PagingAnchor.buildAnchor(commentId), false);
            }
        }
    }

    public List<UserInfo> getLikedUsers() {
        return this.likedUsers;
    }

    private String findLastServerNotSendCommentId() {
        for (int i = this.messages.size() - 1; i >= 0; i--) {
            OfflineMessage<MessageComment> message = (OfflineMessage) this.messages.get(i);
            if (((MessageComment) message.message).hasServerId() && message.offlineData == null) {
                return ((MessageComment) message.message).id;
            }
        }
        return null;
    }

    private String findFirstServerCommentId() {
        if (0 >= this.messages.size()) {
            return null;
        }
        MessageComment message = ((OfflineMessage) this.messages.get(0)).message;
        if (message.hasServerId()) {
        }
        return message.id;
    }

    public void likeMessage(MessageComment message) {
        BusDiscussionsHelper.likeComment(this.discussionInfo, message, null);
    }

    public void addMessage(String text, RepliedTo repliedTo, MessageAuthor messageAuthor) {
        BusDiscussionsHelper.addComment(this.discussionInfo, text, repliedTo, messageAuthor);
    }

    protected void loadOneMessage(String messageId, String reasonMessageId) {
        BusDiscussionsHelper.loadOneComment(this.discussionInfo, messageId, reasonMessageId);
    }

    public void deleteMessages(ArrayList<OfflineMessage<MessageComment>> offlineMessages, boolean block) {
        BusDiscussionsHelper.deleteComments(this.discussionInfo, offlineMessages, block);
    }

    public void spamMessages(ArrayList<OfflineMessage<MessageComment>> messages) {
        BusDiscussionsHelper.spamComments(this.discussionInfo, messages);
    }

    public void resendMessage(OfflineMessage<MessageComment> message) {
        ThreadUtil.execute(new C08851(message));
    }

    public void undoMessageEdit(OfflineMessage<MessageComment> message) {
        BusDiscussionsHelper.undoCommentEdit(message);
    }

    protected boolean isForCurrentLoader(BusEvent event) {
        return this.discussionInfo.equals((Discussion) event.bundleInput.getParcelable("DISCUSSION"));
    }

    protected Uri getUriForMessage(OfflineMessage<MessageComment> message) {
        return OdklProvider.commentUri((long) message.offlineData.databaseId);
    }

    public long extractInitialAccessDate(DiscussionInfoResponse generalInfo) {
        return generalInfo.generalInfo.lastUserAccessDate;
    }

    protected OfflineMessage<MessageComment> convertCursor2OfflineMessage(Cursor cursor) {
        return DiscussionsStorageFacade.cursor2Comment(cursor);
    }

    protected MessageBaseBuilder<MessageComment> createMessageBuilder() {
        return new MessageCommentBuilder();
    }

    protected void fillBuilder(MessageComment message, MessageBaseBuilder<MessageComment> messageBaseBuilder) {
    }

    protected int getFirstPortionEventKind() {
        return 2131624146;
    }

    protected int getPreviousPortionEventKind() {
        return 2131624148;
    }

    protected int getNextPortionEventKind() {
        return 2131624147;
    }

    protected int getMessageAddEventKind() {
        return 2131624142;
    }

    protected int getMessageDeleteEventKind() {
        return 2131624143;
    }

    protected int getMessageSpamEventKind() {
        return 2131624151;
    }

    protected int getMessageLikeEventKind() {
        return 2131624145;
    }

    protected int getMessageLoadOneEventKind() {
        return 2131624155;
    }

    public void editMessage(OfflineMessage<MessageComment> message, String newText) {
        BusDiscussionsHelper.editComment(this.discussionInfo, message, newText);
    }

    protected int getSingleMessageLoadErrorId() {
        return 2131165707;
    }

    protected void registerBus(@NonNull Bus bus) {
        super.registerBus(bus);
        bus.subscribe(2131624144, this, 2131623946);
        bus.subscribe(2131624154, this, 2131623946);
    }

    public void unregisterBus(@NonNull Bus bus) {
        bus.unsubscribe(2131624154, this);
        bus.unsubscribe(2131624144, this);
        super.unregisterBus(bus);
    }

    public void consume(@AnyRes int kind, @NonNull BusEvent event) {
        switch (kind) {
            case 2131624144:
                onLike(event);
                return;
            case 2131624146:
                if (event.resultCode == -1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("DISCUSSION_ID", this.discussionInfo.id);
                    GlobalBus.send(2131624150, new BusEvent(bundle, null, -1));
                    break;
                }
                break;
            case 2131624147:
            case 2131624148:
                break;
            case 2131624154:
                onLoadInfo(event);
                return;
        }
        tryToExtractLikes(event);
        super.consume(kind, event);
    }

    private void onLike(BusEvent event) {
        if (!isForCurrentLoader(event)) {
            return;
        }
        if (event.resultCode == -1) {
            onLikeInfo((LikeInfo) event.bundleOutput.getParcelable("LIKE_INFO"));
        } else {
            TimeToast.show(getContext(), 2131165712, 1);
        }
    }

    public void onLikeInfo(LikeInfo likeInfo) {
        if (this.generalInfo != null) {
            DiscussionGeneralInfo info = ((DiscussionInfoResponse) this.generalInfo).generalInfo;
            info.setLikeInfo(new LikeInfoContext(likeInfo, info.getLikeInfo().entityType, info.getLikeInfo().entityId));
            UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
            if (currentUser != null) {
                if (this.likedUsers == null) {
                    this.likedUsers = new ArrayList();
                }
                do {
                } while (this.likedUsers.remove(currentUser));
                if (likeInfo.self) {
                    this.likedUsers.add(0, currentUser);
                }
            }
            recreateAndDeliverResult(true);
        }
    }

    private void onLoadInfo(BusEvent event) {
        if (isForCurrentLoader(event) && event.resultCode == -1) {
            this.generalInfo = (DiscussionInfoResponse) event.bundleOutput.getParcelable("DISCUSSION");
            recreateAndDeliverResult(true);
        }
    }

    private void tryToExtractLikes(BusEvent event) {
        if (isForCurrentLoader(event)) {
            Bundle bundle = event.bundleOutput;
            if (bundle.containsKey("LIKE_USERS")) {
                ArrayList<UserInfo> likedUsers = bundle.getParcelableArrayList("LIKE_USERS");
                DiscussionInfoResponse generalInfo = (DiscussionInfoResponse) bundle.getParcelable("GENERAL_INFO");
                if (generalInfo != null && generalInfo.generalInfo.getLikeInfo().self) {
                    UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
                    if (currentUser != null) {
                        do {
                        } while (likedUsers.remove(currentUser));
                        likedUsers.add(0, currentUser);
                    }
                }
                this.likedUsers = likedUsers;
                return;
            }
            this.likedUsers = null;
        }
    }
}
