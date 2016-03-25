package ru.ok.android.ui.fragments.messages.adapter;

import ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer;
import ru.ok.android.utils.AudioPlaybackController;
import ru.ok.android.utils.AudioPlaybackController.PlaybackEventsListener;
import ru.ok.model.messages.Attachment;

final class AttachPlaybackListener implements PlaybackEventsListener {
    private final AudioMsgPlayer audioPlayer;

    AttachPlaybackListener(AudioMsgPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    public void onError() {
        if (messageIsForCurrentPlayer()) {
            this.audioPlayer.onError();
        }
    }

    public void onDismissed() {
        if (messageIsForCurrentPlayer()) {
            this.audioPlayer.setPosition(0);
            this.audioPlayer.onStopped();
        }
    }

    public void onBuffering() {
        if (messageIsForCurrentPlayer()) {
            this.audioPlayer.onBuffering();
        }
    }

    public void onPlaying() {
        if (messageIsForCurrentPlayer()) {
            this.audioPlayer.onPlaying();
        }
    }

    public void onEnd() {
        if (messageIsForCurrentPlayer()) {
            this.audioPlayer.setPosition(0);
            this.audioPlayer.onStopped();
        }
    }

    public void onPosition(long positionMilliseconds) {
        if (messageIsForCurrentPlayer()) {
            this.audioPlayer.setPosition(positionMilliseconds);
        }
    }

    private boolean messageIsForCurrentPlayer() {
        return AudioPlaybackController.isPlaying((Attachment) this.audioPlayer.getTag());
    }
}
