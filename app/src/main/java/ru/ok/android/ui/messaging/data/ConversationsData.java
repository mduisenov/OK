package ru.ok.android.ui.messaging.data;

import android.support.annotation.NonNull;
import java.util.List;
import ru.ok.android.proto.ConversationProto.Conversation;

public final class ConversationsData {
    @NonNull
    public final List<Conversation> conversations;
    public final int totalUnreadCount;

    public ConversationsData(@NonNull List<Conversation> conversations, int totalUnreadCount) {
        this.conversations = conversations;
        this.totalUnreadCount = totalUnreadCount;
    }
}
