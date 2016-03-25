package ru.ok.android.ui.custom;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smack.util.StringUtils;
import ru.ok.android.ui.custom.video.ObservableVideoView;
import ru.ok.android.utils.Base64;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.ConfigurationPreferences.Type;
import ru.ok.android.utils.Logger;

public class VideoPlaybackView extends RelativeLayout {
    private MediaController mMediaController;
    private ProgressBar mSpinner;
    private ObservableVideoView mVideoView;
    private OnMediaControlListener mediaControlListener;
    private int position;
    boolean wasPlaying;

    /* renamed from: ru.ok.android.ui.custom.VideoPlaybackView.1 */
    class C06311 implements OnPreparedListener {
        C06311() {
        }

        public void onPrepared(MediaPlayer mp) {
            VideoPlaybackView.this.mSpinner.setVisibility(8);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.VideoPlaybackView.2 */
    class C06322 implements OnTouchListener {
        C06322() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                VideoPlaybackView.this.toggleControls();
            }
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.custom.VideoPlaybackView.3 */
    class C06333 implements OnSystemUiVisibilityChangeListener {
        C06333() {
        }

        public void onSystemUiVisibilityChange(int visibility) {
            if (VideoPlaybackView.this.mediaControlListener == null) {
                return;
            }
            if (visibility == 0) {
                VideoPlaybackView.this.mediaControlListener.onHideMediaControl();
            } else {
                VideoPlaybackView.this.mediaControlListener.onShowMediaControl();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.VideoPlaybackView.4 */
    class C06344 implements OnCompletionListener {
        final /* synthetic */ VideoEventListener val$cb;

        C06344(VideoEventListener videoEventListener) {
            this.val$cb = videoEventListener;
        }

        public void onCompletion(MediaPlayer mp) {
            this.val$cb.onFinished();
            VideoPlaybackView.this.hideController();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.VideoPlaybackView.5 */
    class C06355 implements OnErrorListener {
        final /* synthetic */ VideoEventListener val$cb;

        C06355(VideoEventListener videoEventListener) {
            this.val$cb = videoEventListener;
        }

        public boolean onError(MediaPlayer mp, int what, int extra) {
            this.val$cb.onError();
            VideoPlaybackView.this.hideController();
            return true;
        }
    }

    public interface OnMediaControlListener {
        void onHideMediaControl();

        void onShowMediaControl();
    }

    public interface VideoEventListener {
        void onError();

        void onFinished();
    }

    public VideoPlaybackView(Context context) {
        super(context);
        init();
    }

    public VideoPlaybackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoPlaybackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(2130903570, this, true);
        this.mVideoView = (ObservableVideoView) findViewById(2131625424);
        this.mSpinner = (ProgressBar) findViewById(2131624536);
        this.mVideoView.setOnPreparedListener(new C06311());
        OnTouchListener onTouchListener = new C06322();
        this.mVideoView.setOnTouchListener(onTouchListener);
        setOnTouchListener(onTouchListener);
    }

    public void hideProgress() {
        this.mSpinner.setVisibility(8);
    }

    private void toggleControls() {
        if (this.mSpinner != null && this.mSpinner.isShown()) {
            return;
        }
        if (this.mMediaController.isShowing()) {
            hideController();
        } else {
            showController();
        }
    }

    private void showController() {
        this.mMediaController.show();
        if (this.mediaControlListener != null) {
            this.mediaControlListener.onShowMediaControl();
        }
    }

    private void hideController() {
        this.mMediaController.hide();
        if (this.mediaControlListener != null) {
            this.mediaControlListener.onHideMediaControl();
        }
    }

    public void setMediaController(MediaController controller) {
        ObservableVideoView observableVideoView = this.mVideoView;
        this.mMediaController = controller;
        observableVideoView.setMediaController(controller);
        this.mMediaController.setOnSystemUiVisibilityChangeListener(new C06333());
    }

    public void setVideoCallback(VideoEventListener cb) {
        this.mVideoView.setOnCompletionListener(new C06344(cb));
        this.mVideoView.setOnErrorListener(new C06355(cb));
    }

    public void setMediaControlListener(OnMediaControlListener mediaControlListener) {
        this.mediaControlListener = mediaControlListener;
    }

    public void startUrlPlayback(Uri uri) {
        boolean uriSet = false;
        ConfigurationPreferences cp = ConfigurationPreferences.getInstance();
        if (cp.getEnvironment() == Type.Test && uri.getHost().equalsIgnoreCase(Uri.parse(cp.getWebServer()).getHost())) {
            Method setVideoURIMethod = null;
            try {
                setVideoURIMethod = this.mVideoView.getClass().getMethod("setVideoURI", new Class[]{Uri.class, Map.class});
            } catch (Exception e) {
            }
            if (setVideoURIMethod != null) {
                try {
                    String cred = "dev:OdklDev1";
                    new HashMap(1).put("Authorization", "Basic " + Base64.encodeBytes("dev:OdklDev1".getBytes(StringUtils.UTF8)));
                    setVideoURIMethod.invoke(this.mVideoView, new Object[]{uri, params});
                    uriSet = true;
                } catch (Exception e2) {
                }
            }
        }
        if (!uriSet) {
            try {
                this.mVideoView.setVideoURI(uri);
            } catch (Exception e3) {
                Logger.m176e("fix NPE into com.mediatek.common.media.IOmaSettingHelper.setSettingHeader");
            }
        }
        this.mVideoView.start();
        this.mSpinner.setVisibility(0);
        this.mVideoView.requestFocus();
    }

    public void pause() {
        this.wasPlaying = this.mVideoView.isPlaying();
        this.position = this.mVideoView.getCurrentPosition();
        this.mVideoView.pause();
    }

    public void resume() {
        this.mVideoView.seekTo(this.position);
        if (this.wasPlaying) {
            this.mVideoView.start();
        }
    }

    public void play() {
        this.mVideoView.start();
    }

    public ObservableVideoView getVideoView() {
        return this.mVideoView;
    }
}
