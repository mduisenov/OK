package ru.ok.android.services.app.messaging;

import ru.ok.android.ui.fragments.messages.helpers.DecodedChatId;

public class DecodedChatAndSenderId extends DecodedChatId {
    public final long senderId;

    public DecodedChatAndSenderId(long chatId, long senderId, boolean isMultichat) {
        super(chatId, isMultichat);
        this.senderId = senderId;
    }
}
