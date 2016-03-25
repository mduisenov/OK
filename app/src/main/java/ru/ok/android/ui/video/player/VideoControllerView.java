package ru.ok.android.ui.video.player;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.google.android.gms.location.LocationStatusCodes;
import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;
import ru.mail.libverify.C0176R;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.utils.localization.LocalizationManager;

public class VideoControllerView extends FrameLayout {
    private ViewGroup anchor;
    private ControlInterface controlInterface;
    private TextView currentTime;
    private boolean dragging;
    private TextView endTime;
    StringBuilder formatBuilder;
    Formatter formatter;
    private ImageButton fullscreenButton;
    private OnClickListener fullscreenListener;
    private final Handler handler;
    private MediaPlayerControl mPlayer;
    private OnSeekBarChangeListener mSeekListener;
    private View mainView;
    private OnHidedListener onHidedListener;
    private ImageButton pauseButton;
    private SeekBar progressView;
    private boolean showing;

    public interface ControlInterface {
        boolean isFullScreen();

        void onQualityClick();

        void onShowingChanged(boolean z);

        void seek(long j);

        void toggleFullScreen();

        void togglePlayPause(boolean z);
    }

    /* renamed from: ru.ok.android.ui.video.player.VideoControllerView.1 */
    class C13831 implements OnClickListener {
        C13831() {
        }

        public void onClick(View v) {
            VideoControllerView.this.onTooglePlayPauseClick();
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.VideoControllerView.2 */
    class C13842 implements OnClickListener {
        C13842() {
        }

        public void onClick(View v) {
            if (VideoControllerView.this.controlInterface != null) {
                VideoControllerView.this.controlInterface.onQualityClick();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.VideoControllerView.3 */
    class C13853 implements OnClickListener {
        C13853() {
        }

        public void onClick(View v) {
            VideoControllerView.this.doToggleFullscreen();
            VideoControllerView.this.show(3000);
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.VideoControllerView.4 */
    class C13864 implements OnSeekBarChangeListener {
        C13864() {
        }

        public void onStartTrackingTouch(SeekBar bar) {
            VideoControllerView.this.dragging = true;
            VideoControllerView.this.handler.removeMessages(2);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (VideoControllerView.this.mPlayer != null && fromuser) {
                long newPosition = (((long) progress) * ((long) VideoControllerView.this.mPlayer.getDuration())) / 1000;
                VideoControllerView.this.mPlayer.seekTo((int) newPosition);
                if (VideoControllerView.this.currentTime != null) {
                    VideoControllerView.this.currentTime.setText(VideoControllerView.this.stringForTime((int) newPosition));
                }
                VideoControllerView.this.show();
            }
        }

        public void onStopTrackingTouch(SeekBar bar) {
            VideoControllerView.this.dragging = false;
            long pos = (long) VideoControllerView.this.setProgress();
            VideoControllerView.this.updatePausePlay();
            VideoControllerView.this.show(3000);
            VideoControllerView.this.controlInterface.seek(pos);
            VideoControllerView.this.handler.sendEmptyMessage(2);
        }
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<VideoControllerView> mView;

        MessageHandler(VideoControllerView view) {
            this.mView = new WeakReference(view);
        }

        public void handleMessage(Message msg) {
            VideoControllerView view = (VideoControllerView) this.mView.get();
            if (view != null && view.mPlayer != null) {
                switch (msg.what) {
                    case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                        view.hide();
                    case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                        int pos = view.setProgress();
                        if (!view.dragging && view.showing) {
                            sendMessageDelayed(obtainMessage(2), (long) (1000 - (pos % LocationStatusCodes.GEOFENCE_NOT_AVAILABLE)));
                        }
                    case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                        sendMessageDelayed(obtainMessage(2), (long) (1000 - (view.setProgress() % LocationStatusCodes.GEOFENCE_NOT_AVAILABLE)));
                    default:
                }
            }
        }
    }

    public interface OnHidedListener {
        void onViewHided();
    }

    public void toggle() {
        if (this.showing) {
            hide();
        } else {
            show();
        }
    }

    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.handler = new MessageHandler(this);
        this.fullscreenListener = new C13853();
        this.mSeekListener = new C13864();
        this.mainView = null;
    }

    public VideoControllerView(Context context) {
        super(context);
        this.handler = new MessageHandler(this);
        this.fullscreenListener = new C13853();
        this.mSeekListener = new C13864();
    }

    public void setControlInterface(ControlInterface controlInterface) {
        this.controlInterface = controlInterface;
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        if (this.mainView != null) {
            initControllerView(this.mainView);
        }
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        this.mPlayer = player;
        updatePausePlay();
        updateFullScreen();
    }

    public void setAnchorView(ViewGroup view) {
        this.anchor = view;
        LayoutParams frameParams = new LayoutParams(-1, -1);
        removeAllViews();
        addView(makeControllerView(), frameParams);
    }

    public void setOnHidedListener(OnHidedListener onHidedListener) {
        this.onHidedListener = onHidedListener;
    }

    public View getPauseButton() {
        return this.pauseButton;
    }

    protected View makeControllerView() {
        this.mainView = LocalizationManager.inflate(getContext(), getLayoutId(), null, false);
        initControllerView(this.mainView);
        return this.mainView;
    }

    protected int getLayoutId() {
        return 2130903298;
    }

    private void initControllerView(View v) {
        this.fullscreenButton = (ImageButton) v.findViewById(2131624437);
        if (this.fullscreenButton != null) {
            this.fullscreenButton.requestFocus();
            this.fullscreenButton.setOnClickListener(this.fullscreenListener);
        }
        this.pauseButton = (ImageButton) v.findViewById(2131624941);
        this.pauseButton.setOnClickListener(new C13831());
        v.findViewById(2131625010).setOnClickListener(new C13842());
        this.progressView = (SeekBar) v.findViewById(2131625057);
        if (this.progressView != null) {
            if (this.progressView instanceof SeekBar) {
                this.progressView.setOnSeekBarChangeListener(this.mSeekListener);
            }
            this.progressView.setMax(LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
        }
        this.endTime = (TextView) v.findViewById(C0176R.id.time);
        this.currentTime = (TextView) v.findViewById(2131625056);
        this.formatBuilder = new StringBuilder();
        this.formatter = new Formatter(this.formatBuilder, Locale.getDefault());
    }

    public void show() {
        if (getDuration() < 10000) {
            show(LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
        } else {
            show(3000);
        }
    }

    private int getDuration() {
        if (this.mPlayer != null) {
            return this.mPlayer.getDuration();
        }
        return 0;
    }

    private void disableUnsupportedButtons() {
        if (this.mPlayer != null) {
            try {
                if (this.pauseButton != null && !this.mPlayer.canPause()) {
                    this.pauseButton.setEnabled(false);
                }
            } catch (IncompatibleClassChangeError e) {
            }
        }
    }

    public void show(int timeout) {
        if (!(this.showing || this.anchor == null)) {
            setProgress();
            if (this.pauseButton != null) {
                this.pauseButton.requestFocus();
            }
            disableUnsupportedButtons();
            this.anchor.addView(this, new LayoutParams(-1, -2, 80));
            updatePausePlay();
            updateFullScreen();
            setShowing(true);
        }
        this.handler.sendEmptyMessage(2);
        Message msg = this.handler.obtainMessage(1);
        if (timeout != 0) {
            this.handler.removeMessages(1);
            this.handler.sendMessageDelayed(msg, (long) timeout);
        }
    }

    public boolean isShowing() {
        return this.showing;
    }

    private void setShowing(boolean value) {
        if (this.showing != value) {
            this.showing = value;
            if (this.controlInterface != null) {
                this.controlInterface.onShowingChanged(this.showing);
            }
        }
    }

    public void hide() {
        if (this.anchor != null && this.mPlayer != null && this.mPlayer.isPlaying()) {
            try {
                this.anchor.removeView(this);
                this.handler.removeMessages(2);
            } catch (IllegalArgumentException e) {
                Log.w("MediaController", "already removed");
            }
            setShowing(false);
            if (this.onHidedListener != null) {
                this.onHidedListener.onViewHided();
            }
        }
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / LocationStatusCodes.GEOFENCE_NOT_AVAILABLE;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        this.formatBuilder.setLength(0);
        if (hours > 0) {
            return this.formatter.format("%d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)}).toString();
        }
        return this.formatter.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)}).toString();
    }

    private int setProgress() {
        if (this.mPlayer == null || this.dragging) {
            return 0;
        }
        int position = this.mPlayer.getCurrentPosition();
        setProgress(position, this.mPlayer.getDuration());
        return position;
    }

    public void updateProgressToFinish() {
        if (this.mPlayer != null) {
            this.handler.removeMessages(2);
            int duration = this.mPlayer.getDuration();
            setProgress(duration, duration);
        }
    }

    private void setProgress(int position, int duration) {
        if (this.progressView != null) {
            if (duration > 0) {
                this.progressView.setProgress((int) ((1000 * ((long) position)) / ((long) duration)));
            }
            this.progressView.setSecondaryProgress(this.mPlayer.getBufferPercentage() * 10);
        }
        if (this.endTime != null) {
            this.endTime.setText(stringForTime(duration));
        }
        if (this.currentTime != null) {
            this.currentTime.setText(stringForTime(position));
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        show(3000);
        return super.onTouchEvent(event);
    }

    public boolean onTrackballEvent(MotionEvent ev) {
        show(3000);
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int i = 3000;
        if (this.mPlayer == null) {
            return true;
        }
        boolean uniqueDown;
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0 && event.getAction() == 0) {
            uniqueDown = true;
        } else {
            uniqueDown = false;
        }
        if (keyCode == 79 || keyCode == 85 || keyCode == 62) {
            if (uniqueDown) {
                doPauseResume();
                if (!this.mPlayer.isPlaying()) {
                    i = 0;
                }
                show(i);
                if (this.pauseButton != null) {
                    this.pauseButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == 126) {
            if (uniqueDown && !this.mPlayer.isPlaying()) {
                this.mPlayer.start();
                updatePausePlay();
                show(3000);
            }
            return true;
        } else if (keyCode == 86 || keyCode == 127) {
            if (uniqueDown && this.mPlayer.isPlaying()) {
                this.mPlayer.pause();
                updatePausePlay();
                show(0);
            }
            return true;
        } else if (keyCode == 25 || keyCode == 24 || keyCode == 164) {
            return super.dispatchKeyEvent(event);
        } else {
            if (keyCode == 4 || keyCode == 82) {
                if (uniqueDown) {
                    hide();
                }
                return true;
            }
            show(3000);
            return super.dispatchKeyEvent(event);
        }
    }

    public void onTooglePlayPauseClick() {
        if (this.mPlayer != null) {
            if (this.controlInterface != null) {
                this.controlInterface.togglePlayPause(this.mPlayer.isPlaying());
            }
            doPauseResume();
            show(this.mPlayer.isPlaying() ? 3000 : 0);
        }
    }

    public void onRepeatClick() {
        if (this.mPlayer != null) {
            this.mPlayer.seekTo(0);
            this.mPlayer.start();
        }
    }

    public void pause() {
        if (this.mPlayer != null) {
            if (this.mPlayer.isPlaying()) {
                this.mPlayer.pause();
            }
            updatePausePlay();
        }
    }

    public void updatePausePlay() {
        if (this.mainView != null && this.pauseButton != null && this.mPlayer != null) {
            this.pauseButton.setImageResource(this.mPlayer.isPlaying() ? 2130838190 : 2130838191);
        }
    }

    public void updateFullScreen() {
        if (this.mainView != null && this.fullscreenButton != null && this.mPlayer != null) {
            if (this.controlInterface.isFullScreen()) {
                this.fullscreenButton.setImageResource(2130838085);
            } else {
                this.fullscreenButton.setImageResource(2130838076);
            }
        }
    }

    private void doPauseResume() {
        if (this.mPlayer != null) {
            if (this.mPlayer.isPlaying()) {
                this.mPlayer.pause();
            } else {
                this.mPlayer.start();
            }
            updatePausePlay();
        }
    }

    private void doToggleFullscreen() {
        if (this.controlInterface != null) {
            this.controlInterface.toggleFullScreen();
        }
    }

    public void setEnabled(boolean enabled) {
        if (this.pauseButton != null) {
            this.pauseButton.setEnabled(enabled);
        }
        if (this.progressView != null) {
            this.progressView.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    protected void onDetachedFromWindow() {
        this.handler.removeMessages(2);
        this.handler.removeMessages(1);
        super.onDetachedFromWindow();
    }
}
