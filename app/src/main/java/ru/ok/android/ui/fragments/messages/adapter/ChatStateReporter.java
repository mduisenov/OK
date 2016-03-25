package ru.ok.android.ui.fragments.messages.adapter;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import org.jivesoftware.smackx.chatstates.ChatState;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService.OdklMessagingEventsServiceBinder;
import ru.ok.android.services.processors.xmpp.XmppSettingsContainer;
import ru.ok.android.ui.fragments.messages.helpers.DecodedChatId;

public class ChatStateReporter implements TextWatcher {
    private final int SendPaused;
    private boolean alreadyComposing;
    private DecodedChatId chatWithOwnerId;
    private int delayBetweenComposingAndComposing;
    private int delayBetweenComposingAndPaused;
    private OdklMessagingEventsServiceBinder stateServiceBinder;
    Handler stoppedTypingHandler;
    private Type type;

    /* renamed from: ru.ok.android.ui.fragments.messages.adapter.ChatStateReporter.1 */
    class C08601 implements Callback {
        C08601() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVED_VALUE:
                    ChatStateReporter.this.alreadyComposing = false;
                    break;
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    ChatStateReporter.this.reportStateToServer(ChatState.paused);
                    break;
            }
            return true;
        }
    }

    public ChatStateReporter(XmppSettingsContainer xmppSettingsContainer) {
        this.SendPaused = 1;
        this.stoppedTypingHandler = new Handler(new C08601());
        this.alreadyComposing = false;
        this.delayBetweenComposingAndComposing = xmppSettingsContainer.delayBetweenComposingAndComposing;
        this.delayBetweenComposingAndPaused = xmppSettingsContainer.delayBetweenComposingAndPaused;
    }

    public void reportChatPaused() {
        reportStateToServer(ChatState.paused);
    }

    public void reportChatInactive() {
        reportStateToServer(ChatState.inactive);
    }

    public void initializeActiveChat(Conversation conversation) {
        if (conversation == null) {
            reportStateToServer(ChatState.inactive);
        } else if (this.chatWithOwnerId == null) {
            this.chatWithOwnerId = OdklMessagingEventsService.decodeChatId(conversation.getId());
            this.type = conversation.getType();
            reportStateToServer(ChatState.active);
        }
    }

    public void setTargetService(OdklMessagingEventsServiceBinder stateServiceBinder) {
        this.stateServiceBinder = stateServiceBinder;
    }

    private void reportStateToServer(ChatState state) {
        if (state == ChatState.inactive) {
            this.stoppedTypingHandler.removeCallbacksAndMessages(null);
        }
        if (this.stateServiceBinder != null && this.chatWithOwnerId != null) {
            if (state != ChatState.composing) {
                this.alreadyComposing = false;
            }
            this.stateServiceBinder.reportStateToServer(this.chatWithOwnerId.chatId, this.type, state);
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count == 0 && before > 0) {
            reportStateToServer(ChatState.paused);
        } else if (!this.alreadyComposing && count > 0) {
            this.alreadyComposing = true;
            reportStateToServer(ChatState.composing);
            this.stoppedTypingHandler.removeCallbacksAndMessages(null);
            this.stoppedTypingHandler.sendEmptyMessageDelayed(0, (long) this.delayBetweenComposingAndComposing);
            this.stoppedTypingHandler.sendEmptyMessageDelayed(1, (long) this.delayBetweenComposingAndPaused);
        }
    }

    public void afterTextChanged(Editable s) {
    }
}
