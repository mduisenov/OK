package ru.ok.android.ui.fragments.messages.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.MusicService.InformationState;
import ru.ok.android.ui.fragments.messages.view.AudioPlayerWaveView.TouchEventCallback;
import ru.ok.android.utils.AudioPlaybackController.PlaybackState;
import ru.ok.android.utils.AudioPlaybackController.PlaybackStatus;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.utils.bus.BusProtocol;

public class AudioMsgPlayer extends RelativeLayout {
    private boolean alwaysActive;
    private ValueAnimator animator;
    private AudioPlayerWaveView audioWave;
    private ImageView button;
    private InputCallback controllCallback;
    private Long duration;
    private byte[] finalData;
    private boolean noTimerBg;
    private float playbackProgress;
    private View recordBg;
    private View recordView;
    private boolean right;
    private View spinner;
    private State state;
    private TextView timer;
    private PlaybackStatus uiState;

    public interface InputCallback {
        void onPlayPauseClick(View view);

        boolean onSeekStarted(View view, long j);

        boolean onSeekStopped(View view, long j);

        boolean onSeeking(View view, long j);
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.1 */
    class C08901 implements AnimatorUpdateListener {
        final /* synthetic */ float val$startDisplayShift;

        C08901(float f) {
            this.val$startDisplayShift = f;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            AudioMsgPlayer.this.audioWave.setRollingMode(false);
            AudioMsgPlayer.this.audioWave.setDisplayShift((1.0f - animation.getAnimatedFraction()) * this.val$startDisplayShift);
            AudioMsgPlayer.this.audioWave.invalidate();
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.2 */
    class C08912 implements AnimatorUpdateListener {
        final /* synthetic */ int val$startPeaksCount;
        final /* synthetic */ int val$totalPeaksCount;

        C08912(int i, int i2) {
            this.val$totalPeaksCount = i;
            this.val$startPeaksCount = i2;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            AudioMsgPlayer.this.audioWave.setSkipPeaks(Math.max(0, (int) (((float) (this.val$totalPeaksCount - this.val$startPeaksCount)) * (1.0f - animation.getAnimatedFraction()))));
            AudioMsgPlayer.this.audioWave.setAltProportion(animation.getAnimatedFraction());
            AudioMsgPlayer.this.audioWave.invalidate();
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.3 */
    class C08923 extends SimpleAnimatorListener {
        C08923() {
        }

        public void onAnimationEnd(Animator animation) {
            AudioMsgPlayer.this.finalizeAnimation();
            super.onAnimationEnd(animation);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.4 */
    class C08934 implements OnClickListener {
        final /* synthetic */ InputCallback val$inputCallback;

        C08934(InputCallback inputCallback) {
            this.val$inputCallback = inputCallback;
        }

        public void onClick(View view) {
            AudioMsgPlayer.this.sendStopMusicService();
            this.val$inputCallback.onPlayPauseClick(AudioMsgPlayer.this);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.5 */
    class C08945 implements OnClickListener {
        final /* synthetic */ InputCallback val$inputCallback;

        C08945(InputCallback inputCallback) {
            this.val$inputCallback = inputCallback;
        }

        public void onClick(View view) {
            AudioMsgPlayer.this.sendStopMusicService();
            this.val$inputCallback.onPlayPauseClick(AudioMsgPlayer.this);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.6 */
    class C08956 implements OnClickListener {
        final /* synthetic */ InputCallback val$inputCallback;

        C08956(InputCallback inputCallback) {
            this.val$inputCallback = inputCallback;
        }

        public void onClick(View view) {
            AudioMsgPlayer.this.sendStopMusicService();
            this.val$inputCallback.onPlayPauseClick(AudioMsgPlayer.this);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.7 */
    class C08967 implements TouchEventCallback {
        final /* synthetic */ InputCallback val$inputCallback;

        C08967(InputCallback inputCallback) {
            this.val$inputCallback = inputCallback;
        }

        public boolean onEvent(View view, MotionEvent motionEvent) {
            int action = MotionEventCompat.getActionMasked(motionEvent);
            if (AudioMsgPlayer.this.duration == null || 0 == AudioMsgPlayer.this.duration.longValue()) {
                return false;
            }
            long pos = (long) ((((double) motionEvent.getX()) / ((double) view.getWidth())) * ((double) AudioMsgPlayer.this.duration.longValue()));
            switch (action) {
                case RECEIVED_VALUE:
                    return this.val$inputCallback.onSeekStarted(AudioMsgPlayer.this, pos);
                case Message.TEXT_FIELD_NUMBER /*1*/:
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    return this.val$inputCallback.onSeekStopped(AudioMsgPlayer.this, pos);
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    return this.val$inputCallback.onSeeking(AudioMsgPlayer.this, pos);
                default:
                    return false;
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.8 */
    static /* synthetic */ class C08978 {
        static final /* synthetic */ int[] f107x61d8c5d3;

        static {
            f107x61d8c5d3 = new int[PlaybackStatus.values().length];
            try {
                f107x61d8c5d3[PlaybackStatus.STATUS_STOPPED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f107x61d8c5d3[PlaybackStatus.STATUS_BUFFERING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f107x61d8c5d3[PlaybackStatus.STATUS_PLAYING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f107x61d8c5d3[PlaybackStatus.STATUS_PAUSED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f107x61d8c5d3[PlaybackStatus.STATUS_ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public enum State {
        PLAYER,
        RECORDER
    }

    protected void onDetachedFromWindow() {
        GlobalBus.unregister(this);
        super.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        GlobalBus.register(this);
        super.onAttachedToWindow();
    }

    @Subscribe(on = 2131623946, to = 2131624252)
    public void onStreamMediaStatus(BusEvent event) {
        if (this.uiState == PlaybackStatus.STATUS_PLAYING || this.state == State.RECORDER) {
            InformationState state = (InformationState) event.bundleOutput.getSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE);
            if (this.controllCallback != null && state == InformationState.PLAY) {
                this.controllCallback.onPlayPauseClick(this);
            }
        }
    }

    public AudioMsgPlayer(Context context) {
        super(context);
        this.uiState = PlaybackStatus.STATUS_STOPPED;
        this.noTimerBg = false;
        this.state = State.PLAYER;
        this.alwaysActive = false;
    }

    public AudioMsgPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.uiState = PlaybackStatus.STATUS_STOPPED;
        this.noTimerBg = false;
        this.state = State.PLAYER;
        this.alwaysActive = false;
    }

    public AudioMsgPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.uiState = PlaybackStatus.STATUS_STOPPED;
        this.noTimerBg = false;
        this.state = State.PLAYER;
        this.alwaysActive = false;
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        this.button = (ImageView) findViewById(2131624627);
        this.audioWave = (AudioPlayerWaveView) findViewById(2131624632);
        this.timer = (TextView) findViewById(2131624633);
        this.spinner = findViewById(2131624628);
        this.recordView = findViewById(2131624630);
        this.recordBg = findViewById(2131624631);
        this.spinner.setVisibility(8);
        updateUIState();
    }

    public void setIsRight() {
        this.right = true;
        this.audioWave.setIsRight();
        updateUIState();
    }

    public void setIsLeft() {
        this.right = false;
        this.audioWave.setIsLeft();
        updateUIState();
    }

    public void setRollingMode(boolean rolling) {
        cancelAnimation();
        this.audioWave.setRollingMode(rolling);
    }

    private void cancelAnimation() {
        if (this.animator != null) {
            this.animator.cancel();
            finalizeAnimation();
            this.animator = null;
        }
    }

    public void animateTransition(byte[] finalData) {
        cancelAnimation();
        this.finalData = finalData;
        int displayWidth = this.audioWave.getWaveTotalDisplayWidth();
        this.animator = ValueAnimator.ofInt(new int[]{100});
        if (displayWidth < this.audioWave.getWidth()) {
            this.audioWave.setWaveInfo(finalData);
            float startDisplayShift = 1.0f - (((float) displayWidth) / ((float) this.audioWave.getWidth()));
            this.animator.setInterpolator(new DecelerateInterpolator());
            this.animator.addUpdateListener(new C08901(startDisplayShift));
        } else {
            int totalPeaksCount = this.audioWave.getPeaksCount();
            int startPeaksCount = (this.audioWave.getWidth() * totalPeaksCount) / displayWidth;
            this.audioWave.setRollingMode(false);
            this.audioWave.setSkipPeaks(totalPeaksCount - startPeaksCount);
            this.animator.setInterpolator(new AccelerateDecelerateInterpolator());
            this.audioWave.setAlternativePeaks(finalData);
            this.audioWave.setAltProportion(0.0f);
            this.animator.addUpdateListener(new C08912(totalPeaksCount, startPeaksCount));
        }
        this.animator.addListener(new C08923());
        this.animator.setDuration(200);
        this.animator.start();
    }

    private void finalizeAnimation() {
        this.audioWave.setSkipPeaks(0);
        this.audioWave.setDisplayShift(0.0f);
        this.audioWave.setWaveInfo(this.finalData);
        this.audioWave.setAlternativePeaks(null);
        this.audioWave.setAltProportion(0.0f);
        this.audioWave.invalidate();
    }

    public void setAlwaysActive(boolean alwaysActive) {
        this.alwaysActive = alwaysActive;
    }

    public void setEnableButtons(boolean value) {
        if (this.button != null) {
            this.button.setEnabled(value);
        }
        if (this.spinner != null) {
            this.spinner.setEnabled(value);
        }
    }

    public void setWaveInfo(String waveInfo) {
        this.audioWave.setWaveInfo(waveInfo);
        this.audioWave.invalidate();
    }

    public void setWaveInfo(byte[] waveInfo) {
        this.audioWave.setWaveInfo(waveInfo);
        this.audioWave.invalidate();
    }

    public void setDuration(Long mediaDuration) {
        this.duration = mediaDuration;
        updateTimer();
    }

    private void updateTimer() {
        long durationSeconds = 0;
        if (this.duration != null && this.duration.longValue() > 0) {
            durationSeconds = this.duration.longValue() / 1000;
        }
        long secondsLeft = (long) ((1.0d - ((double) this.playbackProgress)) * ((double) ((float) durationSeconds)));
        if (secondsLeft > 3599) {
            secondsLeft = 3599;
        }
        this.timer.setText(String.format("%02d:%02d", new Object[]{Long.valueOf(secondsLeft / 60), Long.valueOf(secondsLeft % 60)}));
    }

    public void setPosition(long timeMs) {
        if (this.duration == null || this.duration.longValue() <= 0) {
            this.playbackProgress = 0.0f;
            this.audioWave.setProgress(0.0f);
            return;
        }
        if (timeMs > this.duration.longValue()) {
            timeMs = this.duration.longValue();
        }
        this.playbackProgress = ((float) timeMs) / ((float) this.duration.longValue());
        if (this.playbackProgress < 0.0f) {
            this.playbackProgress = 0.0f;
        } else if (this.playbackProgress > 1.0f) {
            this.playbackProgress = 1.0f;
        }
        updateTimer();
        this.audioWave.setProgress(this.playbackProgress);
    }

    public void resetState() {
        this.right = false;
        onStopped();
        setPosition(0);
    }

    public void setPlaybackState(PlaybackState state) {
        setPlaybackStatus(state.getStatus());
        setPosition((long) state.getPositionMs());
    }

    public void setRecorderState() {
        setState(State.RECORDER);
    }

    public void setPlayerState() {
        setState(State.PLAYER);
    }

    private void setState(State state) {
        this.state = state;
        updateUIState();
    }

    private void setPlaybackStatus(PlaybackStatus status) {
        this.uiState = status;
        updateUIState();
    }

    private void updateUIState() {
        int i = 2130838749;
        int i2 = 2130838745;
        boolean active = this.alwaysActive;
        boolean spinnerVisible = false;
        if (this.state == State.PLAYER) {
            switch (C08978.f107x61d8c5d3[this.uiState.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    if (this.right) {
                        if (!active) {
                            this.button.setImageDrawable(opacityDown(2130838278));
                            break;
                        } else {
                            this.button.setImageResource(2130838278);
                            break;
                        }
                    }
                    this.button.setImageResource(2130838277);
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    spinnerVisible = true;
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    break;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    active = true;
                    this.button.setImageResource(2130838278);
                    break;
                case Message.UUID_FIELD_NUMBER /*5*/:
                    this.button.setImageResource(2130838275);
                    break;
            }
            active = true;
            this.button.setImageResource(2130838276);
            if (spinnerVisible) {
                ViewUtil.setBackgroundCompat(this.button, null);
                this.spinner.setVisibility(0);
            } else {
                this.spinner.setVisibility(8);
                if (false) {
                    this.button.setBackgroundResource(2130838746);
                } else if (active) {
                    this.button.setBackgroundResource(2130838745);
                } else {
                    View view = this.button;
                    if (!this.right) {
                        i2 = 2130838744;
                    }
                    ViewUtil.setBackgroundCompat(view, opacityDown(i2));
                }
            }
            this.button.setVisibility(0);
            this.recordView.clearAnimation();
            this.recordView.setVisibility(8);
            this.recordBg.setVisibility(8);
        } else {
            active = true;
            this.spinner.setVisibility(8);
            this.button.setVisibility(8);
            this.recordView.setVisibility(0);
            this.recordBg.setVisibility(0);
            Animation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(700);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(-1);
            animation.setRepeatMode(2);
            this.recordView.startAnimation(animation);
        }
        if (active) {
            TextView textView = this.timer;
            if (this.noTimerBg) {
                i2 = 0;
            } else {
                i2 = 2130838749;
            }
            textView.setBackgroundResource(i2);
            this.timer.setTextColor(getResources().getColor(2131492898));
            return;
        }
        Drawable drawable;
        View view2 = this.timer;
        if (this.noTimerBg) {
            drawable = null;
        } else {
            if (!this.right) {
                i = 2130838748;
            }
            drawable = opacityDown(i);
        }
        ViewUtil.setBackgroundCompat(view2, drawable);
        this.timer.setTextColor(colorOpacityDown(getResources().getColor(this.right ? 2131492898 : 2131492897)));
    }

    public void onError() {
        setPlaybackStatus(PlaybackStatus.STATUS_ERROR);
    }

    public void onBuffering() {
        setPlaybackStatus(PlaybackStatus.STATUS_BUFFERING);
    }

    public void onPlaying() {
        setPlaybackStatus(PlaybackStatus.STATUS_PLAYING);
    }

    public void onPaused() {
        setPlaybackStatus(PlaybackStatus.STATUS_PAUSED);
    }

    public void onStopped() {
        setPlaybackStatus(PlaybackStatus.STATUS_STOPPED);
    }

    private Drawable opacityDown(int resId) {
        Drawable result = getResources().getDrawable(resId);
        result.mutate();
        result.setAlpha(NotificationCompat.FLAG_HIGH_PRIORITY);
        return result;
    }

    private int colorOpacityDown(int fgColor) {
        return Color.argb(NotificationCompat.FLAG_HIGH_PRIORITY, Color.red(fgColor), Color.green(fgColor), Color.blue(fgColor));
    }

    public void setEventsListener(InputCallback inputCallback) {
        this.controllCallback = inputCallback;
        this.button.setOnClickListener(new C08934(inputCallback));
        setOnClickListener(new C08945(inputCallback));
        this.spinner.setOnClickListener(new C08956(inputCallback));
        this.audioWave.setTouchCallback(new C08967(inputCallback));
    }

    private void sendStopMusicService() {
        GlobalBus.send(2131624086, new BusEvent());
    }
}
