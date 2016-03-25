package ru.ok.android.ui.fragments.messages.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.custom.NotificationsView;
import ru.ok.android.ui.custom.imageview.MultiUserAvatar;
import ru.ok.android.ui.fragments.messages.helpers.ConversationParticipantsUtils;
import ru.ok.android.ui.fragments.messages.view.ParticipantsPreviewView;
import ru.ok.android.ui.messaging.data.ConversationsData;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserOnlineType;

public final class ConversationsAdapter extends Adapter<ViewHolder> implements OnClickListener, IChatStateHandler {
    private volatile IChatStateProvider chatStateProvider;
    private String composingMultipleText;
    private String composingText;
    private final Context context;
    private final Map<String, List<UserInfo>> conversation2Users;
    @Nullable
    private ConversationsData conversations;
    private final boolean disableConversations;
    @NonNull
    private final ConversationsAdapterListener listener;
    private final String myUserId;
    @Nullable
    private String selectionConversationId;
    @Nullable
    private String selectionUserId;

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.ConversationsAdapter.1 */
    class C08611 implements Runnable {
        final /* synthetic */ int val$index;

        C08611(int i) {
            this.val$index = i;
        }

        public void run() {
            ConversationsAdapter.this.notifyItemChanged(this.val$index);
        }
    }

    public interface ConversationsAdapterListener {
        void onConversationAvatarClicked(Conversation conversation);

        void onConversationContextMenuButtonClicked(Conversation conversation, View view);

        void onConversationSelected(Conversation conversation, int i);
    }

    final class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        MultiUserAvatar avatar;
        View contextMenuButton;
        NotificationsView imageNewMessage;
        TextView lastMessageMeText;
        ParticipantsPreviewView lastMessageUserAvatars;
        View online;
        TextView text;
        TextView textTime;
        TextView textUserName;

        public ViewHolder(View convertView) {
            super(convertView);
            this.text = (TextView) convertView.findViewById(C0263R.id.text);
            this.textTime = (TextView) convertView.findViewById(2131624541);
            this.textUserName = (TextView) convertView.findViewById(C0263R.id.name);
            this.avatar = (MultiUserAvatar) convertView.findViewById(2131624969);
            this.avatar.setOnClickListener(ConversationsAdapter.this);
            this.online = convertView.findViewById(2131624634);
            this.lastMessageUserAvatars = (ParticipantsPreviewView) convertView.findViewById(2131624970);
            this.lastMessageMeText = (TextView) convertView.findViewById(2131624971);
            this.contextMenuButton = convertView.findViewById(2131624874);
            this.contextMenuButton.setOnClickListener(ConversationsAdapter.this);
            this.imageNewMessage = (NotificationsView) convertView.findViewById(2131624798);
            convertView.setOnClickListener(ConversationsAdapter.this);
        }
    }

    public ConversationsAdapter(@NonNull Context context, boolean disableConversations, @NonNull ConversationsAdapterListener listener) {
        this.conversation2Users = new HashMap();
        this.myUserId = JsonSessionTransportProvider.getInstance().getStateHolder().getUserId();
        this.context = context;
        this.disableConversations = disableConversations;
        this.listener = listener;
        setHasStableIds(true);
    }

    public void clearSelectedIdIfNotExist() {
        if (!TextUtils.isEmpty(this.selectionConversationId)) {
            boolean found = false;
            if (!(this.conversations == null || this.conversations.conversations == null)) {
                for (Conversation conversation : this.conversations.conversations) {
                    if (TextUtils.equals(conversation.getId(), this.selectionConversationId)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                this.selectionConversationId = null;
            }
        }
    }

    public void updateData(ConversationsData data) {
        this.conversations = data;
        this.conversation2Users.clear();
        clearSelectedIdIfNotExist();
        notifyDataSetChanged();
    }

    public String getSelectionConversationId() {
        return this.selectionConversationId;
    }

    public String getSelectionUserId() {
        return this.selectionUserId;
    }

    public void setSelectionConversationId(String conversationId) {
        this.selectionConversationId = conversationId;
        this.selectionUserId = null;
        notifyDataSetChanged();
    }

    public void setSelectionUserId(String userId) {
        this.selectionUserId = userId;
        this.selectionConversationId = null;
        notifyDataSetChanged();
    }

    private void updateForEnableDisable(Conversation conversation, ViewHolder holder) {
        if (this.disableConversations) {
            float alpha;
            if (conversation.getCapabilities().getCanPost()) {
                alpha = 1.0f;
            } else {
                alpha = 0.35f;
            }
            holder.avatar.setAlpha(alpha);
            holder.textUserName.setAlpha(alpha);
            holder.textTime.setAlpha(alpha);
            holder.text.setAlpha(alpha);
        }
    }

    private boolean isConversationSelected(Conversation conversation) {
        if (TextUtils.equals(this.selectionConversationId, conversation.getId())) {
            return true;
        }
        if (TextUtils.isEmpty(this.selectionUserId) || conversation.getType() != Type.PRIVATE) {
            return false;
        }
        return TextUtils.equals(ConversationParticipantsUtils.findNonCurrentUserIdProto(conversation.getParticipantsList()), this.selectionUserId);
    }

    private void updateNewMessages(ViewHolder holder, Conversation conversation) {
        int newMessagesCount = conversation.getNewMessagesCount();
        if (newMessagesCount > 0) {
            holder.imageNewMessage.setVisibility(0);
            holder.imageNewMessage.setValue(newMessagesCount);
            return;
        }
        holder.imageNewMessage.setVisibility(8);
    }

    private void updateForSelectedConversation(ViewHolder holder) {
        holder.itemView.setSelected(true);
        holder.itemView.setBackgroundColor(this.context.getResources().getColor(2131493155));
    }

    private void updateForNonSelectedConversation(ViewHolder holder) {
        holder.itemView.setSelected(false);
        holder.itemView.setBackgroundDrawable(null);
    }

    private void updateLastMessage(ViewHolder holder, Conversation conversation) {
        List<UserInfo> allUsersInConversation = getUsers4Conversation(conversation);
        if (this.chatStateProvider != null) {
            List<Long> composingUsersIds = this.chatStateProvider.getServerState(conversation.getId());
            if (composingUsersIds != null && composingUsersIds.size() > 0) {
                CharSequence charSequence;
                if (this.composingText == null || this.composingMultipleText == null) {
                    this.composingText = LocalizationManager.getString(this.context, 2131165567);
                    this.composingMultipleText = LocalizationManager.getString(this.context, 2131165568);
                }
                List<UserInfo> composingUsers = holder.lastMessageUserAvatars.getParticipants();
                composingUsers.clear();
                if (conversation.getType() == Type.PRIVATE) {
                    holder.lastMessageUserAvatars.setVisibility(8);
                } else {
                    for (UserInfo particularUser : allUsersInConversation) {
                        if (composingUsersIds.contains(Long.valueOf(particularUser.getId()))) {
                            composingUsers.add(particularUser);
                        }
                    }
                    holder.lastMessageUserAvatars.setParticipants(composingUsers);
                    holder.lastMessageUserAvatars.setVisibility(0);
                }
                holder.text.setIncludeFontPadding(false);
                holder.text.setTypeface(null, 2);
                holder.lastMessageMeText.setVisibility(8);
                TextView textView = holder.text;
                if (composingUsers == null || composingUsers.size() <= 1) {
                    charSequence = this.composingText;
                } else {
                    charSequence = this.composingMultipleText;
                }
                textView.setText(charSequence);
                return;
            }
        }
        holder.text.setTypeface(null, 0);
        holder.text.setText(conversation.getLastMessage());
        String lastMessageAuthorId = conversation.getLastMessageAuthorId();
        if (lastMessageAuthorId.equals(this.myUserId)) {
            holder.lastMessageMeText.setVisibility(0);
            holder.lastMessageUserAvatars.setVisibility(8);
        } else if (TextUtils.isEmpty(lastMessageAuthorId) || conversation.getType() != Type.CHAT) {
            holder.lastMessageMeText.setVisibility(8);
            holder.lastMessageUserAvatars.setVisibility(8);
        } else {
            holder.lastMessageMeText.setVisibility(8);
            boolean lastMessageUserAvatarsSet = false;
            for (UserInfo user : allUsersInConversation) {
                if (TextUtils.equals(user.getId(), lastMessageAuthorId)) {
                    List<UserInfo> lastAuthorList = holder.lastMessageUserAvatars.getParticipants();
                    lastAuthorList.clear();
                    lastAuthorList.add(user);
                    holder.lastMessageUserAvatars.setParticipants(lastAuthorList);
                    lastMessageUserAvatarsSet = true;
                    break;
                }
            }
            holder.lastMessageUserAvatars.setVisibility(lastMessageUserAvatarsSet ? 0 : 8);
        }
        for (UserInfo user2 : allUsersInConversation) {
            if (TextUtils.equals(user2.getId(), lastMessageAuthorId)) {
                lastAuthorList = holder.lastMessageUserAvatars.getParticipants();
                lastAuthorList.clear();
                lastAuthorList.add(user2);
                holder.lastMessageUserAvatars.setParticipants(lastAuthorList);
                return;
            }
        }
    }

    private void updateDate(ViewHolder holder, Conversation conversation) {
        holder.textTime.setText(DateFormatter.formatDeltaTimePast(this.context, conversation.getLastMsgTime(), false, true));
    }

    private void updateAuthorName(ViewHolder holder, Conversation conversation) {
        CharSequence topic = conversation.getBuiltTopic();
        boolean empty = TextUtils.isEmpty(topic);
        if (empty) {
            topic = LocalizationManager.getString(this.context, 2131166264);
        }
        holder.textUserName.setTextAppearance(this.context, empty ? 2131296306 : 2131296305);
        holder.textUserName.setText(topic);
    }

    private void updateAvatars(ViewHolder holder, Conversation conversation) {
        String str;
        UserOnlineType userOnlineType = null;
        List<UserInfo> users = getUsers4Conversation(conversation);
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
        boolean isPrivate = conversation.getType() == Type.PRIVATE;
        MultiUserAvatar multiUserAvatar = holder.avatar;
        if (isPrivate || conversation.getParticipantsCount() <= 1) {
            str = currentUserId;
        } else {
            str = null;
        }
        multiUserAvatar.setUsers(users, str, conversation.getId());
        holder.avatar.setTag(conversation);
        holder.contextMenuButton.setTag(conversation);
        holder.itemView.setTag(conversation);
        GeneralUserInfo generalUserInfo = null;
        if (isPrivate) {
            for (GeneralUserInfo info : users) {
                if (info.getObjectType() == 0 && !TextUtils.equals(info.getId(), currentUserId)) {
                    generalUserInfo = info;
                    break;
                }
            }
        }
        View view = holder.online;
        if (generalUserInfo != null) {
            userOnlineType = Utils.onlineStatus((UserInfo) generalUserInfo);
        }
        Utils.updateOnlineView(view, userOnlineType);
    }

    public List<UserInfo> getUsers4Conversation(Conversation conversation) {
        String conversationId = conversation.getId();
        List<UserInfo> users = (List) this.conversation2Users.get(conversationId);
        if (users != null) {
            return users;
        }
        users = ProtoProxy.proto2ApiP(conversation.getParticipantsList());
        this.conversation2Users.put(conversationId, users);
        return users;
    }

    public void onClick(View view) {
        Conversation conversation = (Conversation) view.getTag();
        if (view.getId() == 2131624969) {
            this.listener.onConversationAvatarClicked(conversation);
        } else if (view.getId() == 2131624874) {
            this.listener.onConversationContextMenuButtonClicked(conversation, view);
        } else {
            this.listener.onConversationSelected(conversation, indexOf(this.conversations.conversations, conversation));
        }
    }

    private static int indexOf(List<Conversation> conversations, Conversation conversation) {
        String id = conversation.getId();
        int i = 0;
        while (i < conversations.size()) {
            Conversation c = (Conversation) conversations.get(i);
            if (c == conversation || TextUtils.equals(id, c.getId())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public Conversation getItem(int position) {
        return (Conversation) this.conversations.conversations.get(position);
    }

    public long getItemId(int position) {
        return (long) ((Conversation) this.conversations.conversations.get(position)).getId().hashCode();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LocalizationManager.inflate(this.context, 2130903252, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Conversation conversation = getItem(position);
        if (isConversationSelected(conversation)) {
            updateForSelectedConversation(holder);
        } else {
            updateForNonSelectedConversation(holder);
        }
        updateNewMessages(holder, conversation);
        updateLastMessage(holder, conversation);
        updateDate(holder, conversation);
        updateAuthorName(holder, conversation);
        updateAvatars(holder, conversation);
        updateForEnableDisable(conversation, holder);
    }

    public int getItemCount() {
        return this.conversations != null ? this.conversations.conversations.size() : 0;
    }

    public void notifyComposing(long decodedChatId, long encodedUserId) {
        notifyComposingStatusChanged(decodedChatId);
    }

    public void notifyPaused(long decodedChatId, long encodedUserId) {
        notifyComposingStatusChanged(decodedChatId);
    }

    public void setChatStateProvider(IChatStateProvider chatStateProvider) {
        this.chatStateProvider = chatStateProvider;
    }

    private void notifyComposingStatusChanged(long decodedChatId) {
        for (int i = 0; i < getItemCount(); i++) {
            if (OdklMessagingEventsService.decodeChatId(getItem(i).getId()).chatId == decodedChatId) {
                ThreadUtil.executeOnMain(new C08611(i));
                return;
            }
        }
    }
}
