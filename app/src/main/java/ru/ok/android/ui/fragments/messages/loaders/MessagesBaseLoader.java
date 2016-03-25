package ru.ok.android.ui.fragments.messages.loaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.Bus;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.Subscriber;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.custom.animationlist.AnimateChangesListView;
import ru.ok.android.ui.custom.animationlist.AnimationChangeIdleListener;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesLoaderBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesLoaderBundle.ChangeReason;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo;
import ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo.Status;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageBase.MessageBaseBuilder;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.stream.LikeInfo;
import ru.ok.model.stream.LikeInfo.Builder;

public abstract class MessagesBaseLoader<M extends MessageBase, G extends Parcelable> extends Loader<MessagesLoaderBundle<M, G>> implements Subscriber<BusEvent>, AnimationChangeIdleListener {
    protected AnimateChangesListView<MessagesBundle<M, G>> animationList;
    private boolean busRegistered;
    protected final LinkedList<IdleOperation> eventsQueue;
    protected G generalInfo;
    private long initialAccessDate;
    private MessagesLoaderBundle<M, G> lastData;
    private final int messageDeleteFailedId;
    protected final List<OfflineMessage<M>> messages;
    private final List<ru.ok.android.ui.fragments.messages.loaders.MessagesBaseLoader$ru.ok.android.ui.fragments.messages.loaders.MessagesBaseLoader.MessageObserver> observers;
    private final Set<UserInfo> users;
    private boolean wasError;

    private interface IdleOperation {
        void run(boolean z);
    }

    private class BusResultRunnable implements IdleOperation {
        private final BusEvent event;
        @AnyRes
        private final int kind;

        public BusResultRunnable(int kind, @AnyRes BusEvent event) {
            this.kind = kind;
            this.event = event;
        }

        public void run(boolean skipAnimation) {
            MessagesBaseLoader.this.processEvent(this.kind, this.event, skipAnimation);
        }
    }

    private enum ChunkPosition {
        ENTIRELY {
            <M extends MessageBase> void addChunkToMessages(List<OfflineMessage<M>> messages, List<OfflineMessage<M>> chunk, boolean duplicatesAllowed) {
                boolean wasEmpty = messages.isEmpty();
                if (wasEmpty) {
                    messages.addAll(chunk);
                } else {
                    for (OfflineMessage<M> m : chunk) {
                        if (!messages.contains(m)) {
                            messages.add(m);
                        }
                    }
                }
                if (!wasEmpty) {
                    Collections.sort(messages, OfflineMessage.DATE_COMPARATOR);
                }
            }
        },
        TOP {
            <M extends MessageBase> void addChunkToMessages(List<OfflineMessage<M>> messages, List<OfflineMessage<M>> chunk, boolean duplicatesAllowed) {
                messages.addAll(0, chunk);
            }
        },
        BOTTOM {
            <M extends MessageBase> void addChunkToMessages(List<OfflineMessage<M>> messages, List<OfflineMessage<M>> chunk, boolean duplicatesAllowed) {
                if (duplicatesAllowed) {
                    messages.addAll(chunk);
                    return;
                }
                for (OfflineMessage<M> message : chunk) {
                    int index = messages.indexOf(message);
                    if (index < 0) {
                        messages.add(message);
                    } else {
                        messages.set(index, message);
                    }
                }
            }
        };

        abstract <M extends MessageBase> void addChunkToMessages(List<OfflineMessage<M>> list, List<OfflineMessage<M>> list2, boolean z);
    }

    private class MessageObserver extends ContentObserver {
        private final Uri uri;

        public MessageObserver(Uri uri) {
            super(new Handler());
            this.uri = uri;
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (!selfChange) {
                Logger.m173d("Message changed: %s", this.uri);
                new UpdateSingleMessageAsyncTask(null).execute(new Uri[]{this.uri});
            }
        }

        public boolean equals(Object o) {
            return o.getClass() == MessageObserver.class && ((MessageObserver) o).uri.equals(this.uri);
        }
    }

    protected class UpdateMessageRunnable implements IdleOperation {
        private final OfflineMessage<M> message;

        public UpdateMessageRunnable(OfflineMessage<M> updatedMessage) {
            this.message = updatedMessage;
        }

        public void run(boolean skipAnimation) {
            MessagesBaseLoader.this.updateSingleMessage(this.message, skipAnimation);
        }
    }

    private class UpdateSingleMessageAsyncTask extends AsyncTask<Uri, Void, OfflineMessage<M>> {
        private UpdateSingleMessageAsyncTask() {
        }

        protected OfflineMessage<M> doInBackground(Uri... params) {
            OfflineMessage<M> offlineMessage = null;
            Cursor cursor = MessagesBaseLoader.this.getContext().getContentResolver().query(params[0], null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        offlineMessage = MessagesBaseLoader.this.convertCursor2OfflineMessage(cursor);
                    } else {
                        cursor.close();
                    }
                } finally {
                    cursor.close();
                }
            }
            return offlineMessage;
        }

        protected void onPostExecute(OfflineMessage<M> updatedMessage) {
            if (!MessagesBaseLoader.this.isReset() && updatedMessage != null) {
                if (MessagesBaseLoader.this.animationList == null || !MessagesBaseLoader.this.animationList.isAnimating()) {
                    MessagesBaseLoader.this.updateSingleMessage(updatedMessage, false);
                    return;
                }
                Logger.m173d("List is animating: %s", updatedMessage);
                MessagesBaseLoader.this.eventsQueue.offer(new UpdateMessageRunnable(updatedMessage));
            }
        }
    }

    public abstract void addMessage(String str, RepliedTo repliedTo, MessageAuthor messageAuthor);

    protected abstract OfflineMessage<M> convertCursor2OfflineMessage(Cursor cursor);

    protected abstract MessageBaseBuilder<M> createMessageBuilder();

    public abstract void deleteMessages(ArrayList<OfflineMessage<M>> arrayList, boolean z);

    public abstract void editMessage(OfflineMessage<M> offlineMessage, String str);

    public abstract long extractInitialAccessDate(G g);

    protected abstract void fillBuilder(M m, MessageBaseBuilder<M> messageBaseBuilder);

    @AnyRes
    protected abstract int getFirstPortionEventKind();

    @AnyRes
    protected abstract int getMessageAddEventKind();

    @AnyRes
    protected abstract int getMessageDeleteEventKind();

    @AnyRes
    protected abstract int getMessageLikeEventKind();

    @AnyRes
    protected abstract int getMessageLoadOneEventKind();

    @AnyRes
    protected abstract int getMessageSpamEventKind();

    @AnyRes
    protected abstract int getNextPortionEventKind();

    @AnyRes
    protected abstract int getPreviousPortionEventKind();

    protected abstract int getSingleMessageLoadErrorId();

    protected abstract Uri getUriForMessage(OfflineMessage<M> offlineMessage);

    protected abstract boolean isForCurrentLoader(BusEvent busEvent);

    public abstract void likeMessage(M m);

    protected abstract void loadFirst();

    public abstract void loadNew(boolean z);

    public abstract void loadNext();

    protected abstract void loadOneMessage(String str, String str2);

    public abstract void loadPrevious();

    public abstract void resendMessage(OfflineMessage<M> offlineMessage);

    public abstract void spamMessages(ArrayList<OfflineMessage<M>> arrayList);

    public abstract void undoMessageEdit(OfflineMessage<M> offlineMessage);

    public MessagesBaseLoader(Context context, int messageDeleteFailedId) {
        super(context);
        this.messages = new ArrayList();
        this.users = new HashSet();
        this.eventsQueue = new LinkedList();
        this.observers = new ArrayList();
        this.messageDeleteFailedId = messageDeleteFailedId;
    }

    protected void onStartLoading() {
        super.onStartLoading();
        Logger.m172d("");
        registerBus(GlobalBus.getInstance());
        if (this.lastData == null) {
            loadFirst();
            return;
        }
        registerObserversForChunk(this.messages);
        deliverResult(this.lastData);
    }

    protected void onStopLoading() {
        super.onStopLoading();
        Logger.m172d("");
    }

    protected void onReset() {
        super.onReset();
        Logger.m172d("");
        unregisterBus(GlobalBus.getInstance());
        ContentResolver cr = getContext().getContentResolver();
        for (MessageObserver observer : this.observers) {
            cr.unregisterContentObserver(observer);
        }
        this.observers.clear();
    }

    public void loadRepliedToComment(OfflineMessage<M> message) {
        OfflineMessage<M> alreadyLoaded = findMessageByServerId(message.message.repliedToInfo.messageId);
        if (alreadyLoaded != null) {
            message.repliedToInfo = new RepliedToInfo(new OfflineMessage(alreadyLoaded.message, alreadyLoaded.offlineData), Status.EXPANDED);
            recreateAndDeliverResult(false);
            return;
        }
        loadOneMessage(message.message.repliedToInfo.messageId, message.message.id);
    }

    private OfflineMessage<M> findMessageByServerId(String messageId) {
        for (OfflineMessage<M> message : this.messages) {
            if (TextUtils.equals(messageId, message.message.id)) {
                return message;
            }
            if (message.repliedToInfo != null) {
                for (OfflineMessage<M> innerMessage = message.repliedToInfo.offlineMessage; innerMessage != null; innerMessage = innerMessage.repliedToInfo == null ? null : innerMessage.repliedToInfo.offlineMessage) {
                    if (TextUtils.equals(messageId, innerMessage.message.id)) {
                        return innerMessage;
                    }
                }
                continue;
            }
        }
        return null;
    }

    protected void registerBus(@NonNull Bus bus) {
        if (!this.busRegistered) {
            bus.subscribe(getFirstPortionEventKind(), this, 2131623946);
            bus.subscribe(getPreviousPortionEventKind(), this, 2131623946);
            bus.subscribe(getNextPortionEventKind(), this, 2131623946);
            bus.subscribe(getMessageAddEventKind(), this, 2131623946);
            bus.subscribe(getMessageDeleteEventKind(), this, 2131623946);
            bus.subscribe(getMessageSpamEventKind(), this, 2131623946);
            bus.subscribe(getMessageLikeEventKind(), this, 2131623946);
            bus.subscribe(getMessageLoadOneEventKind(), this, 2131623946);
            this.busRegistered = true;
        }
    }

    protected void unregisterBus(@NonNull Bus bus) {
        if (this.busRegistered) {
            bus.unsubscribe(getMessageLoadOneEventKind(), this);
            bus.unsubscribe(getMessageLikeEventKind(), this);
            bus.unsubscribe(getMessageSpamEventKind(), this);
            bus.unsubscribe(getMessageDeleteEventKind(), this);
            bus.unsubscribe(getMessageAddEventKind(), this);
            bus.unsubscribe(getNextPortionEventKind(), this);
            bus.unsubscribe(getPreviousPortionEventKind(), this);
            bus.unsubscribe(getFirstPortionEventKind(), this);
            this.busRegistered = false;
        }
    }

    public void consume(@AnyRes int kind, @NonNull BusEvent event) {
        if (!isForCurrentLoader(event)) {
            return;
        }
        if (this.animationList == null || !this.animationList.isAnimating()) {
            processEvent(kind, event, false);
            return;
        }
        Logger.m172d("List is animating, save event to queue");
        preprocessResult(kind, event);
        this.eventsQueue.offer(new BusResultRunnable(kind, event));
    }

    private void processEvent(@AnyRes int kind, BusEvent event, boolean skipAnimation) {
        if (kind == getFirstPortionEventKind()) {
            if (event.resultCode == -1) {
                Logger.m172d("Process FIRST_PORTION");
                boolean hasNext = event.bundleOutput.getBoolean("HAS_MORE_NEXT", false);
                boolean hasPrev = event.bundleOutput.getBoolean("HAS_MORE_PREVIOUS", true);
                updateInitialAccessDate(event);
                createAndDeliverBundle(event, ChangeReason.FIRST, ChunkPosition.ENTIRELY, Boolean.valueOf(hasNext), Boolean.valueOf(hasPrev), skipAnimation);
                return;
            }
            createAndDeliverErrorBundle(event, ChangeReason.FIRST);
        } else if (kind == getPreviousPortionEventKind()) {
            if (event.resultCode == -1) {
                Logger.m172d("Process PREVIOUS_PORTION");
                createAndDeliverBundle(event, ChangeReason.PREVIOUS, ChunkPosition.TOP, null, Boolean.valueOf(event.bundleOutput.getBoolean("HAS_MORE_PREVIOUS")), skipAnimation);
                return;
            }
            createAndDeliverErrorBundle(event, ChangeReason.PREVIOUS);
        } else if (kind == getNextPortionEventKind()) {
            ChangeReason changeReason = event.bundleInput.getBoolean("IS_NEW", true) ? ChangeReason.NEW : ChangeReason.NEXT;
            if (event.resultCode != -1) {
                createAndDeliverErrorBundle(event, changeReason);
            } else if (this.lastData != null) {
                Logger.m172d("Process NEXT_PORTION");
                boolean hasMoreNext = event.bundleOutput.getBoolean("HAS_MORE_NEXT");
                resetAccessDateIfNoOneNewMessage(event);
                updateInitialAccessDate(event);
                createAndDeliverBundle(event, changeReason, ChunkPosition.BOTTOM, Boolean.valueOf(hasMoreNext), null, skipAnimation);
            }
        } else if (kind == getMessageAddEventKind()) {
            if (event.resultCode == -1) {
                Logger.m172d("Process MESSAGE_ADDED");
                this.initialAccessDate = 0;
                preProcessAddMessageBundle(event.bundleOutput);
                createAndDeliverBundle(event, ChangeReason.ADDED, ChunkPosition.BOTTOM, null, null, false, false);
                return;
            }
            createAndDeliverErrorBundle(event, ChangeReason.ADDED);
        } else if (kind == getMessageDeleteEventKind() || kind == getMessageSpamEventKind()) {
            Logger.m172d("Process MESSAGES_DELETE");
            boolean isSpam = kind == getMessageSpamEventKind();
            if (event.bundleOutput == null || event.resultCode != -1) {
                TimeToast.show(getContext(), isSpam ? 2131166615 : this.messageDeleteFailedId, 0);
                return;
            }
            ArrayList<OfflineMessage<M>> deletedMessages = event.bundleOutput.getParcelableArrayList("MESSAGES");
            Iterator it;
            if (deletedMessages == null) {
                ArrayList<Integer> ids = event.bundleOutput.getIntegerArrayList("MESSAGE_IDS");
                if (ids == null) {
                    ArrayList<String> serverIds = event.bundleOutput.getStringArrayList("MESSAGE_SERVER_IDS");
                    if (serverIds != null) {
                        it = serverIds.iterator();
                        while (it.hasNext()) {
                            String id = (String) it.next();
                            for (OfflineMessage<M> message : this.messages) {
                                if (TextUtils.equals(message.message.id, id)) {
                                    removeObserverForMessage(message);
                                    this.messages.remove(message);
                                    break;
                                }
                            }
                        }
                    }
                }
                it = ids.iterator();
                while (it.hasNext()) {
                    Integer id2 = (Integer) it.next();
                    for (OfflineMessage<M> message2 : this.messages) {
                        if (message2.offlineData.databaseId == id2.intValue()) {
                            removeObserverForMessage(message2);
                            this.messages.remove(message2);
                            break;
                        }
                    }
                }
            }
            it = deletedMessages.iterator();
            while (it.hasNext()) {
                OfflineMessage<M> m = (OfflineMessage) it.next();
                removeObserverForMessage(m);
                this.messages.remove(m);
            }
            recreateAndDeliverResult(isSpam ? ChangeReason.SPAM : ChangeReason.UPDATED, skipAnimation);
        } else if (kind == getMessageLikeEventKind()) {
            Logger.m172d("Process MESSAGE_LIKE");
            if (event.resultCode == -1) {
                String likedMessageId = event.bundleOutput.getString("MESSAGE_ID");
                if (!TextUtils.isEmpty(likedMessageId)) {
                    setMessageLiked(likedMessageId, (LikeInfo) event.bundleOutput.getParcelable("LIKE_INFO"), skipAnimation);
                }
            }
        } else if (kind == getMessageLoadOneEventKind()) {
            Logger.m172d("Process LOAD_ONE_MESSAGE");
            message2 = findMessageByServerId(event.bundleInput.getString("REASON_MESSAGE_ID"));
            if (event.resultCode != -1 || message2 == null) {
                String errorText = LocalizationManager.from(getContext()).getString(getSingleMessageLoadErrorId());
                message2.repliedToInfo = new RepliedToInfo(null, Status.EXPANDED);
            } else {
                message2.repliedToInfo = new RepliedToInfo(extractSentMessage(event), Status.EXPANDED);
            }
            recreateAndDeliverResult(skipAnimation);
        }
    }

    protected void preProcessAddMessageBundle(Bundle bundle) {
    }

    private void resetAccessDateIfNoOneNewMessage(BusEvent event) {
        List<OfflineMessage<M>> messagesChunk = event.bundleOutput.getParcelableArrayList("MESSAGES");
        if (messagesChunk != null && !messagesChunk.isEmpty()) {
            if (!(((OfflineMessage) messagesChunk.get(messagesChunk.size() + -1)).message.date > extractInitialAccessDate(event.bundleOutput.getParcelable("GENERAL_INFO")))) {
                this.initialAccessDate = 0;
            }
        }
    }

    private void setMessageLiked(String likedMessageId, LikeInfo likeInfo, boolean skipAnimation) {
        OfflineMessage<M> offlineMessage = findMessageByServerId(likedMessageId);
        if (offlineMessage != null) {
            M message = offlineMessage.message;
            MessageBaseBuilder<M> builder = createMessageBuilder();
            builder.setId(message.id);
            builder.setText(message.text);
            builder.setTextEdited(message.textEdited);
            builder.setAuthorId(message.authorId);
            builder.setAuthorType(message.authorType);
            builder.setDate(message.date);
            builder.setDateEdited(message.dateEdited);
            builder.setAttachments(message.attachments);
            if (likeInfo == null) {
                likeInfo = new Builder(message.likeInfo).incrementCount().setSelf(true).build();
            }
            builder.setLikeInfo(likeInfo);
            builder.setFlags(message.flags);
            builder.setRepliedTo(message.repliedToInfo);
            M newMessage = builder.build();
            fillBuilder(newMessage, builder);
            int index = this.messages.indexOf(offlineMessage);
            if (index >= 0) {
                this.messages.set(index, new OfflineMessage(newMessage, null));
            }
            for (OfflineMessage<M> msg : this.messages) {
                if (msg.repliedToInfo != null) {
                    OfflineMessage<M> outerMessage = msg;
                    for (OfflineMessage<M> innerMessage = msg.repliedToInfo.offlineMessage; innerMessage != null; innerMessage = innerMessage.repliedToInfo == null ? null : innerMessage.repliedToInfo.offlineMessage) {
                        if (outerMessage.repliedToInfo.offlineMessage.message.id.equals(likedMessageId)) {
                            outerMessage.repliedToInfo.offlineMessage = new OfflineMessage(newMessage, null);
                        }
                        outerMessage = innerMessage;
                    }
                }
            }
            recreateAndDeliverResult(skipAnimation);
        }
    }

    private void updateInitialAccessDate(BusEvent event) {
        if (this.initialAccessDate == 0) {
            this.initialAccessDate = extractInitialAccessDate(event.bundleOutput.getParcelable("GENERAL_INFO"));
        }
    }

    private void createAndDeliverBundle(BusEvent event, ChangeReason reason, ChunkPosition chunkPosition, Boolean hasMoreNext, Boolean hasMorePrev, boolean skipAnimation) {
        createAndDeliverBundle(event, reason, chunkPosition, hasMoreNext, hasMorePrev, true, skipAnimation);
    }

    private void preprocessResult(@AnyRes int kind, BusEvent event) {
        registerObserversForChunk(event.bundleOutput.getParcelableArrayList("MESSAGES"));
    }

    private void createAndDeliverBundle(BusEvent event, ChangeReason reason, ChunkPosition chunkPosition, Boolean hasMoreNext, Boolean hasMorePrev, boolean rewriteGeneral, boolean skipAnimation) {
        Bundle bundleOutput = event.bundleOutput;
        List<OfflineMessage<M>> messagesChunk = bundleOutput.getParcelableArrayList("MESSAGES");
        List<UserInfo> usersChunk = bundleOutput.getParcelableArrayList("USERS");
        G generalInfoChunk = bundleOutput.getParcelable("GENERAL_INFO");
        boolean hasUnreadData = false;
        boolean hasNewData = (messagesChunk == null || messagesChunk.isEmpty()) ? false : true;
        if (!(this.lastData == null || this.lastData.processed)) {
            hasNewData |= this.lastData.hasNewData;
        }
        if (messagesChunk != null) {
            String currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
            for (int i = messagesChunk.size() - 1; i >= 0; i--) {
                if (!TextUtils.equals(((OfflineMessage) messagesChunk.get(i)).message.authorId, currentUserId)) {
                    hasUnreadData = ((OfflineMessage) messagesChunk.get(messagesChunk.size() + -1)).message.date > this.initialAccessDate;
                    if (hasUnreadData) {
                        break;
                    }
                }
            }
            chunkPosition.addChunkToMessages(this.messages, messagesChunk, false);
            registerObserversForChunk(messagesChunk);
        }
        if (usersChunk != null) {
            this.users.removeAll(usersChunk);
            this.users.addAll(usersChunk);
        }
        if (rewriteGeneral) {
            this.generalInfo = generalInfoChunk;
        }
        MessagesBundle<M, G> result = new MessagesBundle(generalInfoChunk != null ? generalInfoChunk : this.generalInfo, new ArrayList(this.messages), new HashSet(this.users), this.initialAccessDate);
        if (hasMoreNext != null) {
            result.hasMoreNext = hasMoreNext.booleanValue();
        } else if (this.lastData != null) {
            result.hasMoreNext = this.lastData.bundle.hasMoreNext;
        }
        if (hasMorePrev != null) {
            result.hasMorePrev = hasMorePrev.booleanValue();
        } else if (this.lastData != null) {
            result.hasMorePrev = this.lastData.bundle.hasMorePrev;
        }
        MessagesLoaderBundle messagesLoaderBundle = new MessagesLoaderBundle(result, reason, hasNewData, hasUnreadData, skipAnimation);
        this.lastData = messagesLoaderBundle;
        deliverResult(messagesLoaderBundle);
    }

    private OfflineMessage<M> extractSentMessage(BusEvent event) {
        return (OfflineMessage) event.bundleOutput.getParcelable("MESSAGE");
    }

    private void removeObserverForMessage(OfflineMessage<M> sentMessage) {
        if (sentMessage.offlineData != null) {
            Uri uri = getUriForMessage(sentMessage);
            if (uri != null) {
                this.observers.remove(new MessageObserver(uri));
            }
        }
    }

    private void createAndDeliverErrorBundle(BusEvent event, ChangeReason reason) {
        this.wasError = true;
        deliverResult(new MessagesLoaderBundle(new MessagesBundle(this.generalInfo, this.messages, this.users, this.initialAccessDate), reason, ErrorType.from(event.bundleOutput)));
    }

    private void registerObserversForChunk(List<OfflineMessage<M>> messagesChunk) {
        if (!isReset() && messagesChunk != null) {
            ContentResolver cr = getContext().getContentResolver();
            for (OfflineMessage<M> message : messagesChunk) {
                if (message.offlineData != null) {
                    Uri uri = getUriForMessage(message);
                    if (uri != null) {
                        MessageObserver observer = new MessageObserver(uri);
                        if (!this.observers.contains(observer)) {
                            cr.registerContentObserver(uri, false, observer);
                            this.observers.add(observer);
                        }
                    }
                }
            }
        }
    }

    protected void recreateAndDeliverResult(boolean skipAnimation) {
        recreateAndDeliverResult(ChangeReason.UPDATED, skipAnimation);
    }

    protected void recreateAndDeliverResult(ChangeReason reason, boolean skipAnimation) {
        this.wasError = false;
        MessagesBundle<M, G> result = new MessagesBundle(this.generalInfo, new ArrayList(this.messages), new HashSet(this.users), this.initialAccessDate);
        boolean hasNewData = false;
        if (this.lastData != null) {
            result.hasMoreNext = this.lastData.bundle.hasMoreNext;
            result.hasMorePrev = this.lastData.bundle.hasMorePrev;
            if (!this.lastData.processed) {
                int i;
                if (this.lastData.hasNewData || this.lastData.reason == ChangeReason.UPDATED) {
                    i = 1;
                } else {
                    i = 0;
                }
                hasNewData = false | i;
            }
        }
        MessagesLoaderBundle messagesLoaderBundle = new MessagesLoaderBundle(result, reason, hasNewData, false, skipAnimation);
        this.lastData = messagesLoaderBundle;
        deliverResult(messagesLoaderBundle);
    }

    public MessagesLoaderBundle<M, G> getLastData() {
        return this.lastData;
    }

    public MessagesBundle<M, G> getBundle() {
        return this.lastData != null ? this.lastData.bundle : null;
    }

    public boolean isDataPresents() {
        return this.lastData != null;
    }

    public void onListIdle() {
        Logger.m173d("Queue size: %d", Integer.valueOf(this.eventsQueue.size()));
        while (true) {
            IdleOperation runnable = (IdleOperation) this.eventsQueue.poll();
            if (runnable != null) {
                runnable.run(!this.eventsQueue.isEmpty());
            } else {
                return;
            }
        }
    }

    public void setAnimationChangeListView(AnimateChangesListView<MessagesBundle<M, G>> list) {
        this.animationList = list;
    }

    protected void updateSingleMessage(OfflineMessage<M> updatedMessage, boolean skipAnimation) {
        Logger.m173d("%s, skipAnimation: %s", updatedMessage, Boolean.valueOf(skipAnimation));
        for (int i = 0; i < this.messages.size(); i++) {
            OfflineMessage<M> message = (OfflineMessage) this.messages.get(i);
            if (message.offlineData != null && message.offlineData.databaseId == updatedMessage.offlineData.databaseId) {
                this.messages.set(i, updatedMessage);
                recreateAndDeliverResult(skipAnimation);
                return;
            }
        }
    }

    public boolean isWasError() {
        return this.wasError;
    }
}
