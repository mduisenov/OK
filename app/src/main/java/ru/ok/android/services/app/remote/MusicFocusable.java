package ru.ok.android.services.app.remote;

public interface MusicFocusable {
    void onGainedAudioFocus();

    void onLostAudioFocus(boolean z);
}
