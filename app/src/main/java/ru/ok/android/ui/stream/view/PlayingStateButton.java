package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class PlayingStateButton extends ImageButton {
    private static final int[] STATE_BUFFERING;
    private static final int[] STATE_PLAYING;
    protected boolean isBuffering;
    protected boolean isPlaying;
    protected float progress;

    public PlayingStateButton(Context context) {
        this(context, null);
    }

    public PlayingStateButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public PlayingStateButton(Context context, AttributeSet attrs, int defThemeAttrId, int defStyleId) {
        super(context, attrs, defThemeAttrId);
    }

    public void setPlaying(boolean isPlaying) {
        boolean hasChanged = isPlaying != this.isPlaying;
        this.isPlaying = isPlaying;
        if (hasChanged) {
            refreshDrawableState();
            onPlayingStateChanged(isPlaying);
        }
    }

    protected void onPlayingStateChanged(boolean isPlaying) {
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void setBuffering(boolean isBuffering) {
        boolean hasChanged = isBuffering != this.isBuffering;
        this.isBuffering = isBuffering;
        if (hasChanged) {
            refreshDrawableState();
            onBufferingStateChanged(isBuffering);
        }
    }

    protected void onBufferingStateChanged(boolean isBuffering) {
    }

    public void setProgress(float progress) {
        this.progress = progress;
        onProgressChanged(progress);
    }

    protected void onProgressChanged(float progress) {
    }

    public float getProgress() {
        return this.progress;
    }

    public int[] onCreateDrawableState(int extraSpace) {
        int[] state = super.onCreateDrawableState(extraSpace + 2);
        if (this.isPlaying) {
            mergeDrawableStates(state, STATE_PLAYING);
        }
        if (this.isBuffering) {
            mergeDrawableStates(state, STATE_BUFFERING);
        }
        return state;
    }

    static {
        STATE_PLAYING = new int[]{2130772281};
        STATE_BUFFERING = new int[]{2130772282};
    }
}
