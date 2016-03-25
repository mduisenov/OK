package ru.ok.android.ui.fragments.messages.adapter;

public interface IChatStateHandler {
    void notifyComposing(long j, long j2);

    void notifyPaused(long j, long j2);

    void setChatStateProvider(IChatStateProvider iChatStateProvider);
}
