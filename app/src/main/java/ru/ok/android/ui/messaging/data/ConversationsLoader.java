package ru.ok.android.ui.messaging.data;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.support.v4.content.TwoSourcesDataLoader;
import java.util.List;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.services.processors.messaging.ConversationsProcessor;
import ru.ok.android.utils.Logger;

public final class ConversationsLoader extends TwoSourcesDataLoader<ConversationsData> {
    private final ContentObserver observer;

    /* renamed from: ru.ok.android.ui.messaging.data.ConversationsLoader.1 */
    class C10431 extends ContentObserver {
        C10431(Handler x0) {
            super(x0);
        }

        public boolean deliverSelfNotifications() {
            return false;
        }

        public void onChange(boolean selfChange) {
            ConversationsLoader.this.onContentChanged();
        }
    }

    public ConversationsLoader(Context context, boolean performWebLoading) {
        super(context, performWebLoading);
        this.observer = new C10431(new Handler());
    }

    protected ConversationsData doLoadDatabase() {
        long t = System.currentTimeMillis();
        List<Conversation> conversations = ConversationsCache.getInstance().getAllConversations();
        int totalUnreadCount = 0;
        for (Conversation conversation : conversations) {
            if (conversation.getNewMessagesCount() > 0) {
                totalUnreadCount++;
            }
        }
        Logger.m173d("Query conversations for %02f seconds", Float.valueOf(((float) (System.currentTimeMillis() - t)) / 1000.0f));
        return new ConversationsData(conversations, totalUnreadCount);
    }

    protected void doLoadWeb() throws Exception {
        ConversationsProcessor.loadAllConversations(true);
    }

    protected void onStartLoading() {
        super.onStartLoading();
        getContext().getContentResolver().registerContentObserver(OdklProvider.conversationsUri(), true, this.observer);
    }

    protected void onReset() {
        super.onReset();
        getContext().getContentResolver().unregisterContentObserver(this.observer);
    }
}
