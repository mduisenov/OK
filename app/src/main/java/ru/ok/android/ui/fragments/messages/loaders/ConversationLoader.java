package ru.ok.android.ui.fragments.messages.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.GeneralDataLoader;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.model.cache.ram.UsersCache;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Participant;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.model.UserInfo;

public final class ConversationLoader extends GeneralDataLoader<Pair<Conversation, List<UserInfo>>> {
    private final String conversationId;

    public ConversationLoader(Context context, String conversationId) {
        super(context);
        this.conversationId = conversationId;
    }

    @Nullable
    protected Pair<Conversation, List<UserInfo>> loadData() {
        Conversation conversation = ConversationsCache.getInstance().getConversation(this.conversationId);
        if (conversation == null) {
            return null;
        }
        List<UserInfo> users = new ArrayList();
        for (Participant p : conversation.getParticipantsList()) {
            UserInfo user = UsersCache.getInstance().getUser(p.getId());
            if (user != null) {
                users.add(user);
            } else {
                users.add(ProtoProxy.proto2Api(p));
            }
        }
        return new Pair(conversation, users);
    }

    protected List<Uri> observableUris(Pair<Conversation, List<UserInfo>> pair) {
        return Arrays.asList(new Uri[]{OdklProvider.conversationUri(this.conversationId)});
    }
}
