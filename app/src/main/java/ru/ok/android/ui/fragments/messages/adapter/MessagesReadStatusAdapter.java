package ru.ok.android.ui.fragments.messages.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.proto.ConversationProto.Participant;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.messaging.views.ReadStatusView;
import ru.ok.android.utils.Utils;
import ru.ok.model.UserInfo;
import ru.ok.model.messages.MessageBase;

public class MessagesReadStatusAdapter extends BaseAdapter implements IPresentationDetailsProvider {
    private static final Comparator<Participant> conversationParticipantComparatorByLastViewTimeAsc;
    private final Context context;
    private Conversation conversation;
    String currentUserId;
    private final DataSetObserver dataSetObserver;
    private boolean finalInitCalled;
    private HandleBlocker handleBlocker;
    private final LinkedHashMap<Integer, List<String>> indexedParticipants;
    private final MessagesBaseAdapter listAdapter;
    private ArrayList<Participant> participantsLeft;
    private Set<UserInfo> userInfos;

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesReadStatusAdapter.1 */
    class C08761 extends DataSetObserver {
        C08761() {
        }

        public void onChanged() {
            super.onChanged();
            MessagesReadStatusAdapter.this.updateIndexedParticipants();
        }

        public void onInvalidated() {
            super.onInvalidated();
            MessagesReadStatusAdapter.this.updateIndexedParticipants();
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.MessagesReadStatusAdapter.2 */
    static class C08772 implements Comparator<Participant> {
        C08772() {
        }

        public int compare(Participant left, Participant right) {
            if (left.getLastViewTime() == right.getLastViewTime()) {
                if (Long.valueOf(left.getId()).longValue() > Long.valueOf(right.getId()).longValue()) {
                    return 1;
                }
                return -1;
            } else if (left.getLastViewTime() <= right.getLastViewTime()) {
                return left.getLastViewTime() < right.getLastViewTime() ? -1 : 0;
            } else {
                return 1;
            }
        }
    }

    public MessagesReadStatusAdapter(@NonNull Context context, @NonNull MessagesBaseAdapter listAdapter) {
        this.indexedParticipants = new LinkedHashMap();
        this.userInfos = new HashSet();
        this.dataSetObserver = new C08761();
        this.participantsLeft = new ArrayList();
        this.context = context;
        this.listAdapter = listAdapter;
        listAdapter.setPresentationDetailsProvider(this);
        updateIndexedParticipants();
    }

    public void finalInit() {
        this.finalInitCalled = true;
        this.currentUserId = OdnoklassnikiApplication.getCurrentUser().getId();
        this.listAdapter.registerDataSetObserver(this.dataSetObserver);
    }

    public int getCount() {
        return this.listAdapter.getCount() + getReadStatusRowsCount();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (this.finalInitCalled) {
            View view = convertView;
            List<String> ids = (List) this.indexedParticipants.get(Integer.valueOf(position));
            switch (getItemViewType(position)) {
                case RECEIVED_VALUE:
                    View readStatusView = view == null ? new ReadStatusView(this.context, this.handleBlocker) : (ReadStatusView) convertView;
                    readStatusView.setUsers(ids, this.userInfos, this.conversation.getType() == Type.CHAT, getLastViewTimeForDialog(ids));
                    return readStatusView;
                default:
                    return this.listAdapter.getView(getDataIndexForPosition(position), convertView, parent);
            }
        }
        throw new IllegalStateException("You must call finalInit method");
    }

    private long getLastViewTimeForDialog(List<String> ids) {
        if (this.conversation.getType() != Type.CHAT && ids.size() > 0 && this.conversation.getParticipantsCount() > 0) {
            for (Participant conversationParticipant : this.conversation.getParticipantsList()) {
                if (TextUtils.equals(conversationParticipant.getId(), (CharSequence) ids.get(0))) {
                    return conversationParticipant.getLastViewTime();
                }
            }
        }
        return 0;
    }

    public boolean areAllItemsEnabled() {
        return this.listAdapter.areAllItemsEnabled() && this.indexedParticipants.size() == 0;
    }

    public int getItemViewType(int position) {
        return this.indexedParticipants.containsKey(Integer.valueOf(position)) ? 0 : this.listAdapter.getItemViewType(getDataIndexForPosition(position)) + 1;
    }

    public int getViewTypeCount() {
        return this.listAdapter.getViewTypeCount() + 1;
    }

    public boolean isEnabled(int position) {
        return this.indexedParticipants.containsKey(Integer.valueOf(position)) ? false : this.listAdapter.isEnabled(getDataIndexForPosition(position));
    }

    public Object getItem(int position) {
        if (this.indexedParticipants.containsKey(Integer.valueOf(position))) {
            return null;
        }
        return this.listAdapter.getItem(getDataIndexForPosition(position));
    }

    public long getItemId(int position) {
        List<String> ids = (List) this.indexedParticipants.get(Integer.valueOf(position));
        return ids != null ? (long) Utils.getHashcode(ids) : this.listAdapter.getItemId(getDataIndexForPosition(position));
    }

    public void notifyDataSetChanged() {
        this.listAdapter.notifyDataSetChanged();
        updateIndexedParticipants();
        super.notifyDataSetChanged();
    }

    public int getDataIndexForPosition(int position) {
        int statusReadRows = 0;
        for (Integer index : this.indexedParticipants.keySet()) {
            if (index.intValue() < position) {
                statusReadRows++;
            }
        }
        return position - statusReadRows;
    }

    private int getReadStatusRowsCount() {
        return this.indexedParticipants.size();
    }

    public boolean hasStableIds() {
        return this.listAdapter.hasStableIds();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.listAdapter.registerDataSetObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.listAdapter.unregisterDataSetObserver(observer);
    }

    static {
        conversationParticipantComparatorByLastViewTimeAsc = new C08772();
    }

    private void updateIndexedParticipants() {
        MessagesBundle<MessageBase, ru.ok.model.Conversation> messagesBundle = this.listAdapter.getData();
        this.indexedParticipants.clear();
        if (messagesBundle != null && messagesBundle.messages != null && messagesBundle.messages.size() != 0 && this.conversation != null && this.conversation.getParticipantsCount() > 0) {
            Iterator i$;
            Participant conversationParticipant;
            String currentUserId = OdnoklassnikiApplication.getCurrentUser().getId();
            this.participantsLeft.clear();
            for (Participant conversationParticipant2 : this.conversation.getParticipantsList()) {
                if (!TextUtils.equals(conversationParticipant2.getId(), currentUserId)) {
                    this.participantsLeft.add(conversationParticipant2);
                }
            }
            Collections.sort(this.participantsLeft, conversationParticipantComparatorByLastViewTimeAsc);
            List<OfflineMessage<MessageBase>> messages = messagesBundle.messages;
            List<String> participantIdsToInsertInSameRow = new ArrayList();
            int readStatusBlocksInsertedCount = 0;
            int lastMessageWithServerIdIndex = -1;
            int currentMessageIndex = 0;
            while (currentMessageIndex < messages.size() && this.participantsLeft.size() != 0) {
                MessageBase currentMessage = ((OfflineMessage) messages.get(currentMessageIndex)).message;
                if (currentMessage.hasServerId()) {
                    lastMessageWithServerIdIndex = currentMessageIndex;
                    while (this.participantsLeft.size() > 0 && currentMessage.date > ((Participant) this.participantsLeft.get(0)).getLastViewTime()) {
                        conversationParticipant2 = (Participant) this.participantsLeft.get(0);
                        if (currentMessageIndex > 0 && !isDialogMessageFromUser(this.conversation, (OfflineMessage) messages.get(currentMessageIndex - 1), conversationParticipant2.getId())) {
                            participantIdsToInsertInSameRow.add(conversationParticipant2.getId());
                        }
                        this.participantsLeft.remove(conversationParticipant2);
                    }
                    if (participantIdsToInsertInSameRow.size() > 0) {
                        this.indexedParticipants.put(Integer.valueOf(currentMessageIndex + readStatusBlocksInsertedCount), participantIdsToInsertInSameRow);
                        readStatusBlocksInsertedCount++;
                        participantIdsToInsertInSameRow = new ArrayList();
                    }
                }
                currentMessageIndex++;
            }
            if (this.participantsLeft.size() > 0 && lastMessageWithServerIdIndex >= 0 && !isDialogMessageFromUser(this.conversation, (OfflineMessage) messages.get(lastMessageWithServerIdIndex), ((Participant) this.participantsLeft.get(0)).getId())) {
                i$ = this.participantsLeft.iterator();
                while (i$.hasNext()) {
                    participantIdsToInsertInSameRow.add(((Participant) i$.next()).getId());
                }
                this.indexedParticipants.put(Integer.valueOf((lastMessageWithServerIdIndex + 1) + readStatusBlocksInsertedCount), participantIdsToInsertInSameRow);
            }
        }
    }

    private static boolean isDialogMessageFromUser(Conversation conversation, OfflineMessage<MessageBase> message, String userId) {
        return conversation.getType() == Type.PRIVATE && TextUtils.equals(message.message.authorId, userId);
    }

    public void setHandleBlocker(HandleBlocker handleBlocker) {
        this.handleBlocker = handleBlocker;
    }

    public void setConversationInfo(Conversation conversation, Collection<UserInfo> users) {
        this.conversation = conversation;
        this.userInfos.clear();
        for (UserInfo userInfo : users) {
            this.userInfos.add(userInfo);
        }
        notifyDataSetChanged();
    }

    public int getEmbeddedItemsCountBeforeViewPosition(int position) {
        int itemsCount = 0;
        for (Integer index : this.indexedParticipants.keySet()) {
            int dataRowsCount = index.intValue() - itemsCount;
            itemsCount++;
            if (dataRowsCount > position) {
                return itemsCount - 1;
            }
        }
        return itemsCount;
    }

    public boolean shouldHavePaddingBefore(int position) {
        if (this.indexedParticipants != null) {
            return this.indexedParticipants.containsKey(Integer.valueOf((getEmbeddedItemsCountBeforeViewPosition(position) + position) - 1));
        }
        return false;
    }
}
