package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.ui.stream.music.PlayerStateHolder;
import ru.ok.android.ui.stream.music.PlayerStateHolder.PlayerStateHolderListener;

public abstract class BasePresentTrackView extends ImageButton implements OnClickListener, PlayerStateHolderListener {
    protected PlayerStateHolder playerStateHolder;
    private Boolean prevCurrent;

    abstract long getTrackId();

    abstract boolean isMusicPresent();

    abstract void onStartPlayMusic();

    public BasePresentTrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public void setPlayerStateHolder(PlayerStateHolder playerStateHolder) {
        this.playerStateHolder = playerStateHolder;
        this.playerStateHolder.addStateChangeListener(this);
    }

    public void onClick(View v) {
        if (this.playerStateHolder != null) {
            if (this.playerStateHolder.isSongPlaying(getTrackId())) {
                v.getContext().startService(MusicService.getTogglePlayIntent(v.getContext()));
            } else {
                onStartPlayMusic();
            }
        }
    }

    protected void updateIsPlayingState() {
        boolean isPlaying = this.playerStateHolder.isSongPlaying(getTrackId());
        boolean isBuffering = this.playerStateHolder.isSongBuffering(getTrackId());
        boolean isError = this.playerStateHolder.isSongError(getTrackId());
        int i = (isPlaying || isBuffering) ? 2130838142 : 2130838144;
        setImageResource(i);
    }

    public void onMusicStateChanged() {
        if (isMusicPresent()) {
            boolean isSongCurrent = this.playerStateHolder.isSongCurrent(getTrackId());
            if (isSongCurrent || this.prevCurrent == null || this.prevCurrent.booleanValue() != isSongCurrent) {
                updateIsPlayingState();
            }
            this.prevCurrent = Boolean.valueOf(isSongCurrent);
        }
    }
}
