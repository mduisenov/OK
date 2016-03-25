package ru.ok.android.ui.custom.video;

public interface VideoStatEventHandler {
    void playHeadReachedPosition(int i);

    void playbackCompleted();

    void playbackPaused();

    void playbackResumed();

    void playbackStarted();
}
