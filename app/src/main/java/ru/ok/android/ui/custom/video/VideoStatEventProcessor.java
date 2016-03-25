package ru.ok.android.ui.custom.video;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.google.android.gms.location.LocationStatusCodes;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ru.ok.android.proto.MessagesProto;

public class VideoStatEventProcessor implements OnCompletionListener, VideoPlayHeadListener, VideoPlayingStateListener {
    private final EventHandler eventHandler;
    private final List<VideoStatEventHandler> handlers;
    private volatile boolean isDisposed;
    private boolean isPlaybackStarted;
    private final VideoPlayHeadObserver videoPlayHeadObserver;
    private final ObservableVideoView videoView;

    private class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    VideoStatEventProcessor.this.notifyPlaybackCompleted();
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    if (VideoStatEventProcessor.this.isPlaybackStarted) {
                        VideoStatEventProcessor.this.notifyResumed();
                        return;
                    }
                    VideoStatEventProcessor.this.isPlaybackStarted = true;
                    VideoStatEventProcessor.this.notifyPlaybackStarted();
                case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                    VideoStatEventProcessor.this.notifyPaused();
                default:
            }
        }
    }

    public VideoStatEventProcessor(ObservableVideoView videoView) {
        this.handlers = new CopyOnWriteArrayList();
        this.videoView = videoView;
        HandlerThread handlerThread = new HandlerThread("VideoStatEventProcessor");
        handlerThread.start();
        Looper eventThreadLooper = handlerThread.getLooper();
        this.eventHandler = new EventHandler(eventThreadLooper);
        this.videoPlayHeadObserver = new VideoPlayHeadObserver(videoView, 1000, this, eventThreadLooper);
        videoView.addOnCompletionListener(this);
        videoView.addVideoPlayingStateListener(this);
    }

    public void addVideoStatEventHandler(VideoStatEventHandler handler) {
        synchronized (this.handlers) {
            this.handlers.add(handler);
        }
    }

    public void dispose() {
        if (!this.isDisposed) {
            synchronized (this.eventHandler) {
                if (!this.isDisposed) {
                    this.videoPlayHeadObserver.dispose();
                    this.videoView.removeOnCompletionListener(this);
                    this.videoView.removeVideoPlayingStateListener(this);
                    this.eventHandler.getLooper().quit();
                    this.eventHandler.removeCallbacksAndMessages(null);
                    this.isDisposed = true;
                }
            }
        }
    }

    public void onCompletion(MediaPlayer mp) {
        this.eventHandler.sendEmptyMessage(1);
    }

    public void onVideoIsPlayingChanged(boolean isPlaying, boolean isEnd) {
        if (isPlaying) {
            this.eventHandler.sendEmptyMessage(2);
        } else if (!isEnd) {
            this.eventHandler.sendEmptyMessage(3);
        }
    }

    public void onVideoPlayHeadPosition(int positionMs) {
        notifyPlayHeadReachedPosition(positionMs);
    }

    private void notifyPlaybackStarted() {
        synchronized (this.handlers) {
            for (VideoStatEventHandler handler : this.handlers) {
                handler.playbackStarted();
            }
        }
    }

    private void notifyPlaybackCompleted() {
        synchronized (this.handlers) {
            for (VideoStatEventHandler handler : this.handlers) {
                handler.playbackCompleted();
            }
        }
    }

    private void notifyResumed() {
        synchronized (this.handlers) {
            for (VideoStatEventHandler handler : this.handlers) {
                handler.playbackResumed();
            }
        }
    }

    private void notifyPaused() {
        synchronized (this.handlers) {
            for (VideoStatEventHandler handler : this.handlers) {
                handler.playbackPaused();
            }
        }
    }

    private void notifyPlayHeadReachedPosition(int positionMs) {
        synchronized (this.handlers) {
            for (VideoStatEventHandler handler : this.handlers) {
                handler.playHeadReachedPosition(positionMs / LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
            }
        }
    }
}
