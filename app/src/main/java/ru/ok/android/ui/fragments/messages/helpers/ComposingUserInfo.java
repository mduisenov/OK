package ru.ok.android.ui.fragments.messages.helpers;

public class ComposingUserInfo {
    public final long chatId;
    public final long composingUserId;

    public ComposingUserInfo(long chatId, long composingUserId) {
        this.chatId = chatId;
        this.composingUserId = composingUserId;
    }

    public boolean equals(Object o) {
        if (this == null || o == null || !(o instanceof ComposingUserInfo)) {
            return false;
        }
        ComposingUserInfo other = (ComposingUserInfo) o;
        if (this.chatId == other.chatId && this.composingUserId == other.composingUserId) {
            return true;
        }
        return false;
    }
}
