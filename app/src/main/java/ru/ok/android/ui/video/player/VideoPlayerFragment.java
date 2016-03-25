package ru.ok.android.ui.video.player;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.exoplayer.ExoPlaybackException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.video.OneLogVideo;
import ru.ok.android.ui.video.player.ExoHandlePlayer.Listener;
import ru.ok.android.ui.video.player.ExoHandlePlayer.RendererBuilder;
import ru.ok.android.ui.video.player.render.DashRendererBuilder;
import ru.ok.android.ui.video.player.render.DefaultRendererBuilder;
import ru.ok.android.ui.video.player.render.HlsRendererBuilder;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.StringUtils;
import ru.ok.java.api.response.video.VideoGetResponse;

public abstract class VideoPlayerFragment extends AbstractVideoFragment {
    private boolean audioFocusGranted;
    protected EventLogger eventLogger;
    private final Listener exoPlayerListener;
    protected boolean networkErrorShow;
    private OnAudioFocusChangeListener onAudioFocusChangeListener;
    protected ExoHandlePlayer player;
    protected boolean playerNeedsPrepare;
    private final Callback surfaceListener;
    protected VideoSurfaceView surfaceView;

    /* renamed from: ru.ok.android.ui.video.player.VideoPlayerFragment.1 */
    class C13871 implements OnTouchListener {
        C13871() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            VideoPlayerFragment.this.gradientView.onTouchEvent(motionEvent);
            return false;
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.VideoPlayerFragment.2 */
    class C13882 implements Callback {
        C13882() {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            if (VideoPlayerFragment.this.player != null) {
                VideoPlayerFragment.this.player.setSurface(holder.getSurface());
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (VideoPlayerFragment.this.player != null) {
                VideoPlayerFragment.this.player.blockingClearSurface();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.VideoPlayerFragment.3 */
    class C13893 implements Listener {
        C13893() {
        }

        public void onStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    Logger.m172d("IDLE");
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    VideoPlayerFragment.this.playerNeedsPrepare = false;
                    VideoPlayerFragment.this.displayVideoPreparing();
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    VideoPlayerFragment.this.networkErrorShow = false;
                    VideoPlayerFragment.this.displayVideoBuffering();
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    if (playWhenReady) {
                        VideoPlayerFragment.this.displayVideoPlaying();
                    } else {
                        VideoPlayerFragment.this.displayVideoPaused();
                    }
                case Message.UUID_FIELD_NUMBER /*5*/:
                    VideoPlayerFragment.this.displayVideoEnded();
                default:
            }
        }

        public void onError(Exception e) {
            Logger.m178e((Throwable) e);
            VideoGetResponse video = VideoPlayerFragment.this.getVideo();
            if (video != null) {
                try {
                    OneLogVideo.logError(Long.parseLong(video.id), Log.getStackTraceString(e));
                } catch (Exception e2) {
                }
            }
            if (e instanceof UnsupportedDrmException) {
                Toast.makeText(VideoPlayerFragment.this.getActivity(), VideoPlayerFragment.this.getStringLocalized(2131165791), 1).show();
            } else if ((e instanceof ConnectException) || (e instanceof UnknownHostException) || ((e instanceof ExoPlaybackException) && e.getMessage().contains("Unable to connect to"))) {
                if (VideoPlayerFragment.this.networkErrorShow) {
                    TimeToast.show(VideoPlayerFragment.this.getActivity(), VideoPlayerFragment.this.getStringLocalized(2131166272), 1);
                } else {
                    VideoPlayerFragment.this.networkErrorShow = true;
                }
                VideoPlayerFragment.this.showOnlyPlaybackControl(VideoPlayerFragment.this.mediaController.getPauseButton());
                VideoPlayerFragment.this.showError(2131165842);
            } else if (!VideoPlayerFragment.this.recoverFromError()) {
                VideoPlayerFragment.this.showError(2131166747);
            }
            VideoPlayerFragment.this.playerNeedsPrepare = true;
        }

        public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
            VideoPlayerFragment.this.surfaceView.setVideoWidthHeightRatio(height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height));
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.VideoPlayerFragment.4 */
    static /* synthetic */ class C13904 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$ui$video$player$Quality;

        static {
            $SwitchMap$ru$ok$android$ui$video$player$Quality = new int[Quality.values().length];
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality.Auto.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality.Live_Hls.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$video$player$Quality[Quality.Hls.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    static class AudioFocusHandler implements OnAudioFocusChangeListener {
        AudioFocusHandler() {
        }

        public void onAudioFocusChange(int focusChange) {
            Logger.m172d("on Audio focus change");
        }
    }

    public VideoPlayerFragment() {
        this.onAudioFocusChangeListener = new AudioFocusHandler();
        this.audioFocusGranted = false;
        this.surfaceListener = new C13882();
        this.exoPlayerListener = new C13893();
    }

    protected int getLayoutId() {
        return 2130903205;
    }

    public void hidePlayer() {
        this.surfaceView.setVisibility(8);
    }

    public void showPlayer() {
        this.surfaceView.setVisibility(0);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = super.onCreateView(inflater, container, savedInstanceState);
        this.surfaceView = (VideoSurfaceView) viewRoot.findViewById(2131624863);
        this.surfaceView.getHolder().addCallback(this.surfaceListener);
        this.surfaceView.setOnTouchListener(new C13871());
        return viewRoot;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.surfaceView.getHolder().removeCallback(this.surfaceListener);
        this.surfaceView.setOnTouchListener(null);
    }

    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        requestAudioFocus();
    }

    public void onPause() {
        super.onPause();
        abandonAudioFocus();
    }

    protected void onTouchSurfaceView(MotionEvent motionEvent) {
        super.onTouchSurfaceView(motionEvent);
        if (motionEvent.getAction() == 1) {
            this.surfaceView.performClick();
        }
    }

    private RendererBuilder getRendererBuilder(Context context, Quality videoQuality, Uri contentUri) {
        String userAgent = PlayerUtil.getUserAgent(context);
        String contentId = contentUri.toString().toLowerCase(Locale.US).replaceAll("\\s", "");
        switch (C13904.$SwitchMap$ru$ok$android$ui$video$player$Quality[videoQuality.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return new DashRendererBuilder(userAgent, contentUri.toString(), contentId, new WidevineTestMediaDrmCallback(contentId));
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return new HlsRendererBuilder(userAgent, contentUri.toString(), contentId);
            default:
                return new DefaultRendererBuilder(context, contentUri, videoQuality);
        }
    }

    protected ExoHandlePlayer createPlayer(RendererBuilder rendererBuilder) {
        ExoHandlePlayer player = new ExoHandlePlayer(rendererBuilder);
        player.setBackgrounded(false);
        player.addListener(this.exoPlayerListener);
        player.seekTo(this.playerPosition);
        this.playerNeedsPrepare = true;
        this.networkErrorShow = false;
        this.mediaController.setMediaPlayer(player.getPlayerControl());
        this.mediaController.setEnabled(true);
        this.eventLogger = new EventLogger();
        this.eventLogger.startSession();
        player.addListener(this.eventLogger);
        player.setInfoListener(this.eventLogger);
        player.setInternalErrorListener(this.eventLogger);
        return player;
    }

    protected void preparePlayer(Quality quality, Uri contentUri) {
        Activity activity = getActivity();
        if (activity != null) {
            releasePlayer();
            this.player = createPlayer(getRendererBuilder(activity, quality, contentUri));
            this.player.setSurface(this.surfaceView.getHolder().getSurface());
            this.player.setPlayWhenReady(true);
            if (this.playerNeedsPrepare) {
                Logger.m173d("VideoPlayerFragment", "prepare player");
                this.player.prepare();
            }
        }
    }

    protected void releasePlayer() {
        if (this.player != null) {
            Logger.m172d("");
            long playerPosition = this.player.getCurrentPosition();
            this.player.removeListener(this.exoPlayerListener);
            this.player.release();
            this.player = null;
            onPlayerReleased(playerPosition);
            this.eventLogger.endSession();
            this.eventLogger = null;
        }
    }

    protected void onPlayerReleased(long position) {
        this.playerPosition = position;
        Logger.m172d("player release");
    }

    protected boolean recoverFromError() {
        return false;
    }

    protected ArrayList<Quality> getQualities() {
        ArrayList<Quality> arrayList = null;
        if (this.player != null) {
            String[] tracks = this.player.getTracks(0);
            if (tracks != null) {
                arrayList = new ArrayList();
                for (String track : tracks) {
                    if (!StringUtils.isEmpty(track)) {
                        Quality quality = PlayerUtil.getDashQualityFromString(track);
                        if (quality != null) {
                            arrayList.add(quality);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    protected Quality getCurrentQuality() {
        if (this.player == null) {
            return null;
        }
        String[] tracks = this.player.getTracks(0);
        if (tracks == null || tracks.length == 0) {
            return null;
        }
        int selected = this.player.getSelectedTrackIndex(0);
        if (selected >= tracks.length) {
            selected = tracks.length - 1;
        }
        return PlayerUtil.getDashQualityFromString(tracks[selected]);
    }

    private void requestAudioFocus() {
        if (!this.audioFocusGranted) {
            AudioManager am = (AudioManager) getActivity().getSystemService("audio");
            if (this.onAudioFocusChangeListener == null) {
                this.onAudioFocusChangeListener = new AudioFocusHandler();
            }
            if (am.requestAudioFocus(this.onAudioFocusChangeListener, 3, 1) == 1) {
                this.audioFocusGranted = true;
            } else {
                Logger.m184w("FAILED TO GET AUDIO FOCUS");
            }
        }
    }

    private void abandonAudioFocus() {
        if (((AudioManager) getActivity().getSystemService("audio")).abandonAudioFocus(this.onAudioFocusChangeListener) == 1) {
            this.audioFocusGranted = false;
        } else {
            Logger.m172d("FAILED TO ABANDON AUDIO FOCUS");
        }
        this.onAudioFocusChangeListener = null;
    }

    public ExoHandlePlayer getPlayer() {
        return this.player;
    }
}
