package ru.ok.android.ui.custom.video;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.utils.Logger;

public class VideoPlayHeadObserver implements OnCompletionListener, VideoPlayingStateListener {
    private final EventHandler eventHandler;
    private final Object eventLock;
    private volatile boolean isDisposed;
    private final VideoPlayHeadListener listener;
    private final long timeGranularityMs;
    private final boolean usingOwnLooper;
    private final ObservableVideoView videoView;

    private class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    VideoPlayHeadObserver.this.notifyPlayHeadPosition();
                    sendEmptyMessageDelayed(1, VideoPlayHeadObserver.this.timeGranularityMs);
                default:
            }
        }
    }

    public VideoPlayHeadObserver(ObservableVideoView videoView, long timeGranularityMs, VideoPlayHeadListener listener, Looper looper) {
        this.eventLock = new Object();
        this.videoView = videoView;
        if (looper == null) {
            HandlerThread eventThread = new HandlerThread("VideoPlaybackObserver");
            eventThread.start();
            looper = eventThread.getLooper();
            this.usingOwnLooper = true;
        } else {
            this.usingOwnLooper = false;
        }
        this.eventHandler = new EventHandler(looper);
        this.timeGranularityMs = timeGranularityMs;
        this.listener = listener;
        startObserving();
    }

    private void startObserving() {
        this.videoView.addVideoPlayingStateListener(this);
        if (this.videoView.isPlaying()) {
            onVideoIsPlayingChanged(true, false);
        }
    }

    public void dispose() {
        if (!this.isDisposed) {
            synchronized (this.eventLock) {
                if (!this.isDisposed) {
                    this.videoView.removeVideoPlayingStateListener(this);
                    if (this.usingOwnLooper) {
                        this.eventHandler.getLooper().quit();
                    }
                    this.eventHandler.removeCallbacksAndMessages(null);
                    this.isDisposed = true;
                }
            }
        }
    }

    public void onVideoIsPlayingChanged(boolean isPlaying, boolean isEnd) {
        Logger.m173d("isPlaying=%s", Boolean.valueOf(isPlaying));
        this.eventHandler.removeMessages(1);
        if (isPlaying) {
            this.eventHandler.sendEmptyMessageDelayed(1, this.timeGranularityMs);
        }
        if (!isPlaying) {
            notifyPlayHeadPosition();
        }
    }

    public void onCompletion(MediaPlayer mp) {
        Logger.m172d("");
        this.eventHandler.removeMessages(1);
    }

    private void notifyPlayHeadPosition() {
        this.listener.onVideoPlayHeadPosition(this.videoView.getCurrentPosition());
    }
}
