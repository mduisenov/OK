package ru.ok.android.ui.fragments.messages.helpers;

public class DecodedChatId {
    public final long chatId;
    private final int hashCode;
    public final boolean isMultichat;

    public DecodedChatId(long chatId, boolean isMultichat) {
        this.chatId = chatId;
        this.isMultichat = isMultichat;
        this.hashCode = Long.valueOf(chatId).hashCode();
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof DecodedChatId)) {
            return false;
        }
        DecodedChatId that = (DecodedChatId) o;
        if (this.chatId == that.chatId && this.isMultichat == that.isMultichat) {
            return true;
        }
        return false;
    }
}
