package ru.ok.android.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.text.TextUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jivesoftware.smack.util.StringUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.AttachmentUtils;
import ru.ok.model.ContentUrl;
import ru.ok.model.messages.Attachment;

public class AudioPlaybackController {
    private static AudioPlaybackController inst;
    private String attachId;
    private AttachLoader attachLoader;
    private String attachLocalId;
    private Handler handler;
    private boolean isPrepared;
    private Set<PlaybackEventsListener> listeners;
    private final OnCompletionListener onCompletionListener;
    private final OnErrorListener onErrorListener;
    private final OnInfoListener onInfoListener;
    private final OnPreparedListener onPreparedListener;
    private MediaPlayer player;
    private Runnable runnable;
    private long seekOffset;
    private boolean seeking;
    private String source;
    private PlaybackStatus status;
    private int timerInterval;

    public interface PlaybackEventsListener {
        void onBuffering();

        void onDismissed();

        void onEnd();

        void onError();

        void onPlaying();

        void onPosition(long j);
    }

    /* renamed from: ru.ok.android.utils.AudioPlaybackController.1 */
    class C14131 implements Runnable {
        C14131() {
        }

        public void run() {
            if (!AudioPlaybackController.this.listeners.isEmpty() && AudioPlaybackController.this.player != null) {
                if (!AudioPlaybackController.this.seeking) {
                    for (PlaybackEventsListener l : AudioPlaybackController.this.listeners) {
                        l.onPosition((long) AudioPlaybackController.this.player.getCurrentPosition());
                    }
                }
                AudioPlaybackController.this.startTimer();
            }
        }
    }

    /* renamed from: ru.ok.android.utils.AudioPlaybackController.2 */
    class C14142 implements OnInfoListener {
        C14142() {
        }

        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
            if (AudioPlaybackController.this.player != mediaPlayer) {
                return true;
            }
            if (i == 701) {
                AudioPlaybackController.this.stopTimer();
                AudioPlaybackController.this.status = PlaybackStatus.STATUS_BUFFERING;
                for (PlaybackEventsListener l : AudioPlaybackController.this.listeners) {
                    l.onBuffering();
                }
                return true;
            } else if (i != 702) {
                return false;
            } else {
                AudioPlaybackController.this.startTimer();
                AudioPlaybackController.this.status = PlaybackStatus.STATUS_PLAYING;
                for (PlaybackEventsListener l2 : AudioPlaybackController.this.listeners) {
                    l2.onPlaying();
                }
                return true;
            }
        }
    }

    /* renamed from: ru.ok.android.utils.AudioPlaybackController.3 */
    class C14153 implements OnPreparedListener {
        C14153() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            if (AudioPlaybackController.this.player == mediaPlayer) {
                AudioPlaybackController.this.isPrepared = true;
                if (AudioPlaybackController.this.seekOffset > 0) {
                    AudioPlaybackController.this.player.seekTo((int) AudioPlaybackController.this.seekOffset);
                }
                if (AudioPlaybackController.this.status != PlaybackStatus.STATUS_PAUSED) {
                    if (!AudioPlaybackController.this.seeking) {
                        AudioPlaybackController.this.player.start();
                    }
                    AudioPlaybackController.this.startTimer();
                    AudioPlaybackController.this.status = PlaybackStatus.STATUS_PLAYING;
                    for (PlaybackEventsListener l : AudioPlaybackController.this.listeners) {
                        l.onPlaying();
                    }
                }
            }
        }
    }

    /* renamed from: ru.ok.android.utils.AudioPlaybackController.4 */
    class C14164 implements OnCompletionListener {
        C14164() {
        }

        public void onCompletion(MediaPlayer mediaPlayer) {
            if (AudioPlaybackController.this.player == mediaPlayer) {
                AudioPlaybackController.this.stopTimer();
                AudioPlaybackController.this.status = PlaybackStatus.STATUS_STOPPED;
                for (PlaybackEventsListener l : AudioPlaybackController.this.listeners) {
                    l.onEnd();
                }
            }
        }
    }

    /* renamed from: ru.ok.android.utils.AudioPlaybackController.5 */
    class C14175 implements OnErrorListener {
        C14175() {
        }

        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            if (AudioPlaybackController.this.player == mediaPlayer) {
                AudioPlaybackController.this.handleError();
            }
            return true;
        }
    }

    private static class AttachLoader extends AsyncTask<Void, Void, Attachment> {
        private final String attachmentId;

        private AttachLoader(Attachment attachment) {
            this.attachmentId = attachment.id;
        }

        protected Attachment doInBackground(Void... voids) {
            Attachment attachment = null;
            try {
                if (!isCancelled()) {
                    attachment = AttachmentUtils.getAttachments(this.attachmentId);
                }
            } catch (Throwable e) {
                Logger.m178e(e);
            }
            return attachment;
        }

        protected void onPostExecute(Attachment res) {
            if (this == AudioPlaybackController.inst.attachLoader) {
                String audioUrl = null;
                if (!(res == null || res.mediaUrls == null || res.mediaUrls.isEmpty())) {
                    for (ContentUrl url : res.mediaUrls) {
                        audioUrl = url.getUri().toString();
                        if (url.getContentType().equals("audio/mpeg")) {
                            break;
                        }
                    }
                }
                if (audioUrl != null) {
                    AudioPlaybackController.inst.doStartPlayback(OdnoklassnikiApplication.getContext(), audioUrl, null, AudioPlaybackController.inst.timerInterval);
                } else {
                    AudioPlaybackController.inst.handleError();
                }
            }
        }
    }

    public static class PlaybackState {
        private int positionMs;
        private PlaybackStatus status;

        public PlaybackState(PlaybackStatus status, int positionMs) {
            this.status = status;
            this.positionMs = positionMs;
        }

        public PlaybackStatus getStatus() {
            return this.status;
        }

        public int getPositionMs() {
            return this.positionMs;
        }
    }

    public enum PlaybackStatus {
        STATUS_STOPPED,
        STATUS_BUFFERING,
        STATUS_PLAYING,
        STATUS_PAUSED,
        STATUS_ERROR
    }

    public AudioPlaybackController() {
        this.player = createMediaPlayer();
        this.isPrepared = false;
        this.listeners = new HashSet();
        this.handler = new Handler();
        this.status = PlaybackStatus.STATUS_STOPPED;
        this.runnable = new C14131();
        this.onInfoListener = new C14142();
        this.onPreparedListener = new C14153();
        this.onCompletionListener = new C14164();
        this.onErrorListener = new C14175();
    }

    static {
        inst = new AudioPlaybackController();
    }

    private MediaPlayer createMediaPlayer() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(0);
        return mediaPlayer;
    }

    public static void startPlayback(Context context, String source, PlaybackEventsListener listener, int reportInterval) {
        inst.doDismissPlayer();
        inst.doStartPlayback(context, source, listener, reportInterval);
    }

    public static void startPlayback(Context context, Attachment attachment, PlaybackEventsListener listener, int reportInterval) {
        inst.doDismissPlayer();
        inst.doStartPlayback(context, attachment, listener, reportInterval);
    }

    public static boolean isPlaying(String url) {
        return url != null && url.equals(inst.source);
    }

    public static boolean isPlaying(Attachment attachment) {
        return attachment != null && ((!TextUtils.isEmpty(attachment.localId) && attachment.localId.equals(inst.attachLocalId)) || (!TextUtils.isEmpty(attachment.id) && attachment.id.equals(inst.attachId)));
    }

    public static void addListener(PlaybackEventsListener audioPlayerListener) {
        inst.doAddListener(audioPlayerListener);
    }

    public static void removeListener(PlaybackEventsListener audioPlayerListener) {
        inst.doRemoveListener(audioPlayerListener);
    }

    public static void pausePlayback() {
        inst.doPausePlayback();
    }

    public static void resumePlayback() {
        inst.doResumePlayback();
    }

    public static boolean isPlaying() {
        return inst.doCheckPlaying();
    }

    public static boolean isBuffering() {
        return inst.doCheckBuffering();
    }

    public static void dismissPlayer() {
        inst.doDismissPlayer();
    }

    public static PlaybackState getState() {
        return inst.doGetState();
    }

    private void doPausePlayback() {
        this.status = PlaybackStatus.STATUS_PAUSED;
        stopTimer();
        if (doCheckPlaying()) {
            this.player.pause();
        }
    }

    private void doResumePlayback() {
        if (this.isPrepared) {
            this.status = PlaybackStatus.STATUS_PLAYING;
            startTimer();
            this.player.start();
            return;
        }
        this.status = PlaybackStatus.STATUS_BUFFERING;
        for (PlaybackEventsListener l : this.listeners) {
            l.onBuffering();
        }
    }

    private void doDismissPlayer() {
        stopTimer();
        for (PlaybackEventsListener l : this.listeners) {
            l.onDismissed();
        }
        this.listeners.clear();
        if (this.player != null) {
            this.player.release();
        }
        this.source = null;
        this.attachLocalId = null;
        this.attachId = null;
        this.status = PlaybackStatus.STATUS_STOPPED;
        this.timerInterval = 0;
    }

    private boolean doCheckPlaying() {
        if (!this.isPrepared) {
            return false;
        }
        try {
            if (this.player == null || !this.player.isPlaying()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean doCheckBuffering() {
        return this.status == PlaybackStatus.STATUS_BUFFERING;
    }

    private void startTimer() {
        stopTimer();
        if (this.timerInterval > 0) {
            this.handler.postDelayed(this.runnable, (long) this.timerInterval);
        }
    }

    private void stopTimer() {
        this.handler.removeCallbacks(this.runnable);
    }

    private void doStartPlayback(Context context, String source, PlaybackEventsListener newListener, int reportInterval) {
        if (this.attachLoader != null) {
            this.attachLoader.cancel(false);
            this.attachLoader = null;
        }
        this.status = PlaybackStatus.STATUS_STOPPED;
        this.timerInterval = reportInterval;
        this.player = new MediaPlayer();
        this.isPrepared = false;
        this.source = source;
        if (newListener != null) {
            this.listeners.add(newListener);
        }
        this.player.setAudioStreamType(3);
        try {
            this.status = PlaybackStatus.STATUS_BUFFERING;
            for (PlaybackEventsListener l : this.listeners) {
                l.onBuffering();
            }
            if (!source.startsWith("http://mtest.odnoklassniki.ru/") || VERSION.SDK_INT < 14) {
                this.player.setDataSource(source);
            } else {
                Map<String, String> params = new HashMap(2);
                String cred = "dev:OdklDev1";
                params.put("Authorization", "Basic " + Base64.encodeBytes("dev:OdklDev1".getBytes(StringUtils.UTF8)));
                this.player.setDataSource(context, Uri.parse(source), params);
            }
            this.player.setOnPreparedListener(this.onPreparedListener);
            this.player.setOnCompletionListener(this.onCompletionListener);
            this.player.setOnErrorListener(this.onErrorListener);
            this.player.setOnInfoListener(this.onInfoListener);
            this.seekOffset = 0;
            this.seeking = false;
            this.player.prepareAsync();
        } catch (IOException e) {
            handleError();
        }
    }

    private void doStartPlayback(Context context, Attachment attachment, PlaybackEventsListener listener, int reportInterval) {
        this.attachLocalId = attachment.localId;
        this.attachId = attachment.id;
        if (this.attachLoader != null) {
            this.attachLoader.cancel(false);
        }
        this.timerInterval = reportInterval;
        this.listeners.add(listener);
        if (!TextUtils.isEmpty(attachment.id)) {
            this.status = PlaybackStatus.STATUS_BUFFERING;
            if (listener != null) {
                listener.onBuffering();
            }
            this.attachLoader = new AttachLoader(null);
            this.attachLoader.execute(new Void[0]);
        } else if (attachment.path == null) {
            handleError();
        } else {
            doStartPlayback(context, attachment.path, listener, reportInterval);
        }
    }

    private void doAddListener(PlaybackEventsListener audioPlayerListener) {
        this.listeners.add(audioPlayerListener);
        if (doCheckPlaying()) {
            startTimer();
        }
    }

    private void doRemoveListener(PlaybackEventsListener audioPlayerListener) {
        this.listeners.remove(audioPlayerListener);
        if (this.listeners.isEmpty()) {
            stopTimer();
        }
    }

    private PlaybackState doGetState() {
        int position = 0;
        if (!(this.status == PlaybackStatus.STATUS_STOPPED || this.player == null)) {
            try {
                position = this.player == null ? 0 : this.player.getCurrentPosition();
            } catch (IllegalStateException e) {
                position = 0;
            }
        }
        return new PlaybackState(this.status, position);
    }

    private void handleError() {
        stopTimer();
        if (this.player != null) {
            this.player.release();
            this.player = null;
            this.isPrepared = false;
        }
        this.status = PlaybackStatus.STATUS_ERROR;
        for (PlaybackEventsListener l : this.listeners) {
            l.onError();
        }
        this.listeners.clear();
    }

    public static int getMediaDuration(String filePath) {
        MediaPlayer pl = new MediaPlayer();
        int result = 0;
        try {
            pl.setDataSource(filePath);
            pl.prepare();
            result = pl.getDuration();
            pl.stop();
            pl.reset();
            pl.release();
            return result;
        } catch (Throwable e) {
            Logger.m179e(e, "Error getting duration for file \"" + filePath + "\"");
            return result;
        }
    }

    public void doStartSeek(long timeMS) {
        this.seeking = true;
        if (this.player != null) {
            if (doCheckPlaying()) {
                this.player.pause();
            }
            doHandleSeeking(timeMS);
        }
    }

    public void doHandleSeeking(long timeMS) {
        if (this.seeking) {
            for (PlaybackEventsListener l : this.listeners) {
                l.onPosition(timeMS);
            }
        }
    }

    public void doStopSeek(long timeMS) {
        if (this.player != null) {
            if (this.isPrepared) {
                try {
                    this.player.seekTo((int) timeMS);
                    if (this.status == PlaybackStatus.STATUS_PLAYING) {
                        this.player.start();
                    }
                } catch (Throwable e) {
                    Logger.m178e(e);
                }
            } else {
                this.seekOffset = timeMS;
            }
            this.seeking = false;
        }
    }

    public static void startSeek(long timeMS) {
        inst.doStartSeek(timeMS);
    }

    public static void handleSeeking(long timeMS) {
        inst.doHandleSeeking(timeMS);
    }

    public static void stopSeek(long timeMS) {
        inst.doStopSeek(timeMS);
    }
}
