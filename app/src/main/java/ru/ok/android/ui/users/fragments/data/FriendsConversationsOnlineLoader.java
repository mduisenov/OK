package ru.ok.android.ui.users.fragments.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader$android.support.v4.content.Loader.ForceLoadContentObserver;
import android.text.TextUtils;
import java.util.HashSet;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.proto.ConversationProto.Participant;

public final class FriendsConversationsOnlineLoader extends CursorLoader {
    private final ContentResolver cr;
    private final ForceLoadContentObserver observer;

    public FriendsConversationsOnlineLoader(Context context) {
        super(context, OdklProvider.friendsUri(), null, null, null, null);
        this.cr = context.getContentResolver();
        this.observer = new Loader.ForceLoadContentObserver(this);
    }

    public Cursor loadInBackground() {
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
        Set<String> allBuddies = new HashSet();
        for (Conversation c : ConversationsCache.getInstance().getAllConversations()) {
            if (c.getType() == Type.PRIVATE) {
                for (Participant p : c.getParticipantsList()) {
                    String id = p.getId();
                    if (!TextUtils.equals(id, currentUserId)) {
                        allBuddies.add(id);
                    }
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder("'");
        boolean addComma = false;
        for (String buddy : allBuddies) {
            if (addComma) {
                stringBuilder.append("','");
            } else {
                addComma = true;
            }
            stringBuilder.append(buddy);
        }
        stringBuilder.append("'");
        Cursor cursor = this.cr.query(Users.getContentUri().buildUpon().appendQueryParameter("join_conversations", "true").appendQueryParameter("uids", stringBuilder.toString()).build(), UsersStorageFacade.PROJECTION_FRIENDS, null, null, null);
        if (cursor != null) {
            cursor.getCount();
            cursor.registerContentObserver(this.observer);
        }
        return cursor;
    }
}
