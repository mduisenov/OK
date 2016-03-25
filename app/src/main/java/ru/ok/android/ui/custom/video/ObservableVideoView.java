package ru.ok.android.ui.custom.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.AttributeSet;
import android.widget.VideoView;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObservableVideoView extends VideoView implements OnCompletionListener {
    private final CopyOnWriteArrayList<OnCompletionListener> completionListeners;
    private OnCompletionListener defaultCompletionListener;
    private boolean isPlaying;
    private final CopyOnWriteArrayList<VideoPlayingStateListener> playingStateListeners;

    public ObservableVideoView(Context context) {
        super(context);
        this.playingStateListeners = new CopyOnWriteArrayList();
        this.completionListeners = new CopyOnWriteArrayList();
        this.isPlaying = false;
        init();
    }

    public ObservableVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.playingStateListeners = new CopyOnWriteArrayList();
        this.completionListeners = new CopyOnWriteArrayList();
        this.isPlaying = false;
        init();
    }

    public ObservableVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.playingStateListeners = new CopyOnWriteArrayList();
        this.completionListeners = new CopyOnWriteArrayList();
        this.isPlaying = false;
        init();
    }

    @TargetApi(21)
    public ObservableVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.playingStateListeners = new CopyOnWriteArrayList();
        this.completionListeners = new CopyOnWriteArrayList();
        this.isPlaying = false;
        init();
    }

    private void init() {
        super.setOnCompletionListener(this);
    }

    public void addVideoPlayingStateListener(VideoPlayingStateListener listener) {
        if (listener != null) {
            synchronized (this.playingStateListeners) {
                this.playingStateListeners.addIfAbsent(listener);
            }
        }
    }

    public void removeVideoPlayingStateListener(VideoPlayingStateListener listener) {
        if (listener != null) {
            synchronized (this.playingStateListeners) {
                this.playingStateListeners.remove(listener);
            }
        }
    }

    public void start() {
        super.start();
        if (!this.isPlaying) {
            this.isPlaying = true;
            notifyIsPlaying(true, false);
        }
    }

    public void stopPlayback() {
        super.stopPlayback();
        if (this.isPlaying) {
            this.isPlaying = false;
            notifyIsPlaying(false, false);
        }
    }

    public void resume() {
        super.resume();
        if (!this.isPlaying) {
            this.isPlaying = true;
            notifyIsPlaying(true, false);
        }
    }

    public void pause() {
        super.pause();
        if (this.isPlaying) {
            this.isPlaying = false;
            notifyIsPlaying(false, false);
        }
    }

    private void notifyIsPlaying(boolean isPlaying, boolean isEnd) {
        synchronized (this.playingStateListeners) {
            Iterator i$ = this.playingStateListeners.iterator();
            while (i$.hasNext()) {
                ((VideoPlayingStateListener) i$.next()).onVideoIsPlayingChanged(isPlaying, isEnd);
            }
        }
    }

    public void addOnCompletionListener(OnCompletionListener l) {
        if (l != null) {
            synchronized (this.completionListeners) {
                this.completionListeners.add(l);
            }
        }
    }

    public void removeOnCompletionListener(OnCompletionListener l) {
        if (l != null) {
            synchronized (this.completionListeners) {
                this.completionListeners.remove(l);
            }
        }
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        synchronized (this.completionListeners) {
            if (this.defaultCompletionListener != null) {
                removeOnCompletionListener(this.defaultCompletionListener);
            }
            this.defaultCompletionListener = l;
            if (l != null) {
                addOnCompletionListener(l);
            }
        }
    }

    public void onCompletion(MediaPlayer mp) {
        if (this.isPlaying) {
            this.isPlaying = false;
            notifyIsPlaying(false, true);
        }
        synchronized (this.completionListeners) {
            Iterator i$ = this.completionListeners.iterator();
            while (i$.hasNext()) {
                ((OnCompletionListener) i$.next()).onCompletion(mp);
            }
        }
    }
}
