package ru.ok.android.ui.fragments.messages.loaders.data;

import java.util.List;
import java.util.Set;
import ru.ok.model.UserInfo;
import ru.ok.model.messages.MessageBase;

public final class MessagesBundle<M extends MessageBase, G> {
    public final G generalInfo;
    public boolean hasMoreNext;
    public boolean hasMorePrev;
    public final long initialAccessDate;
    public final List<OfflineMessage<M>> messages;
    public final Set<UserInfo> users;

    public MessagesBundle(G generalInfo, List<OfflineMessage<M>> messages, Set<UserInfo> users, long initialAccessDate) {
        this.hasMorePrev = true;
        this.hasMoreNext = true;
        this.generalInfo = generalInfo;
        this.messages = messages;
        this.users = users;
        this.initialAccessDate = initialAccessDate;
    }
}
