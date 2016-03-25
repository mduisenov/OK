package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.util.AttributeSet;

public final class ProfilePresentTrackView extends BasePresentTrackView {
    private OnPlayTrackListener listener;
    private long trackId;

    public interface OnPlayTrackListener {
        void onPlayTrack(long j);
    }

    long getTrackId() {
        return this.trackId;
    }

    public void onStartPlayMusic() {
        if (this.listener != null && this.trackId > 0) {
            this.listener.onPlayTrack(this.trackId);
        }
    }

    public ProfilePresentTrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.trackId = 0;
    }

    public void setOnPlayTrackListener(OnPlayTrackListener listener) {
        this.listener = listener;
    }

    boolean isMusicPresent() {
        return this.trackId > 0;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
        updateIsPlayingState();
    }

    public void setPlayState() {
        setImageResource(2130838144);
    }
}
