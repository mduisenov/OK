package ru.ok.android.ui.custom;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewAnimator;
import com.google.android.gms.location.LocationStatusCodes;
import java.io.File;
import ru.ok.android.C0206R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.audio.AudioRecorder;
import ru.ok.android.services.processors.audio.AudioRecorder.RecordingCallback;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer;
import ru.ok.android.ui.fragments.messages.view.AudioMsgPlayer.InputCallback;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.QuickActionList;
import ru.ok.android.ui.quickactions.QuickActionList.OnActionItemClickListener;
import ru.ok.android.utils.AudioPlaybackController;
import ru.ok.android.utils.AudioPlaybackController.PlaybackEventsListener;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.PermissionUtils.Requester;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.ServicesSettingsHelper;

public final class CreateMessageView extends LinearLayout implements TextWatcher, GestureHandlerCallback {
    private ViewAnimator actionsAnimator;
    private CheckBox asAdminCheckbox;
    private ImageFadeButton attachAudioButton;
    private OnAudioAttachListener attachAudioListener;
    private ImageFadeButton attachPhotoButton;
    private OnPhotoAttachClickListener attachPhotoListener;
    private ImageFadeButton attachVideoButton;
    private OnVideoAttachClickListener attachVideoListener;
    private View audioButtonsContainer;
    private OkViewStub audioButtonsContainerStub;
    private AudioMsgPlayer audioPlayer;
    private float audioPlayerRightMarginDP;
    private ViewStub audioPlayerStub;
    private View audioRecordingButton;
    private boolean audioRecordingEnabled;
    private int audioRecordingErrorTextId;
    private byte[] audioWave;
    private ImageFadeButton cancelButton;
    private ValueAnimator dismissButtonAnimator;
    private GestureHandler gestureHandler;
    private Handler handler;
    private OnSendMessageClickListener listener;
    private EditText messageEditText;
    private int mode;
    private MediaPlayer mp;
    private View ownerView;
    private Paint paint;
    private Requester permissionRequester;
    private String recordedAudioFile;
    private View sendAudioButton;
    private ImageFadeButton sendMessageButton;
    private CheckBox smileCheckBox;
    private UIState state;
    private View stickerCircle;
    private Runnable updateAudioRecordingUI;
    private int[] viewLocation;

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.10 */
    class AnonymousClass10 extends SimpleAnimatorListener {
        final /* synthetic */ View val$view;

        AnonymousClass10(View view) {
            this.val$view = view;
        }

        public void onAnimationStart(Animator animation) {
            this.val$view.setVisibility(AudioRecorder.instance().isRecording() ? 8 : 0);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.11 */
    class AnonymousClass11 extends SimpleAnimatorListener {
        final /* synthetic */ View val$view;

        AnonymousClass11(View view) {
            this.val$view = view;
        }

        public void onAnimationEnd(Animator animation) {
            this.val$view.setVisibility(8);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.12 */
    class AnonymousClass12 implements OnCompletionListener {
        final /* synthetic */ BeepCallBack val$callBack;

        AnonymousClass12(BeepCallBack beepCallBack) {
            this.val$callBack = beepCallBack;
        }

        public void onCompletion(MediaPlayer mp) {
            if (this.val$callBack != null) {
                this.val$callBack.onBeepFinishPlay();
            }
            mp.release();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.13 */
    class AnonymousClass13 implements OnErrorListener {
        final /* synthetic */ BeepCallBack val$callBack;

        AnonymousClass13(BeepCallBack beepCallBack) {
            this.val$callBack = beepCallBack;
        }

        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (this.val$callBack != null) {
                this.val$callBack.onBeepFinishPlay();
            }
            mp.release();
            return false;
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.14 */
    class AnonymousClass14 implements OnPreparedListener {
        final /* synthetic */ BeepCallBack val$callBack;

        AnonymousClass14(BeepCallBack beepCallBack) {
            this.val$callBack = beepCallBack;
        }

        public void onPrepared(MediaPlayer mp) {
            if (this.val$callBack != null) {
                this.val$callBack.onBeepStartPlay();
            }
            mp.start();
        }
    }

    private interface BeepCallBack {
        void onBeepFinishPlay();

        void onBeepStartPlay();
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.15 */
    class AnonymousClass15 implements BeepCallBack {
        final /* synthetic */ AudioRecorder val$recorder;

        AnonymousClass15(AudioRecorder audioRecorder) {
            this.val$recorder = audioRecorder;
        }

        public void onBeepFinishPlay() {
            CreateMessageView.this.startAudioRecordingNow(this.val$recorder);
        }

        public void onBeepStartPlay() {
            CreateMessageView.this.showSendButton();
            CreateMessageView.this.showPlayer();
            CreateMessageView.this.state = UIState.RECORDING;
            CreateMessageView.this.updateRecordingUI();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.19 */
    class AnonymousClass19 implements AnimatorUpdateListener {
        final /* synthetic */ float val$startMargin;
        final /* synthetic */ float val$startWidth;

        AnonymousClass19(float f, float f2) {
            this.val$startWidth = f;
            this.val$startMargin = f2;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            int size = ((int) CreateMessageView.this.dpToPixels(((1.0f - animation.getAnimatedFraction()) * (this.val$startWidth - 20.0f)) + 20.0f)) & -2;
            LayoutParams lp = CreateMessageView.this.audioRecordingButton.getLayoutParams();
            if (lp instanceof MarginLayoutParams) {
                MarginLayoutParams rlp = (MarginLayoutParams) lp;
                rlp.height = size;
                rlp.width = size;
                rlp.bottomMargin = (CreateMessageView.this.attachAudioButton.getHeight() / 2) - (lp.height / 2);
                CreateMessageView.this.audioRecordingButton.setLayoutParams(rlp);
            }
            if (CreateMessageView.this.audioPlayer != null) {
                float fraction = animation.getAnimatedFraction();
                CreateMessageView.this.setAudioPlayerRightMarginDP((17.0f * fraction) + ((1.0f - fraction) * this.val$startMargin));
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.1 */
    class C06021 implements Runnable {
        C06021() {
        }

        public void run() {
            if (CreateMessageView.this.audioPlayer != null) {
                CreateMessageView.this.audioPlayer.setClickable(!AudioRecorder.instance().isRecording());
            }
            if (AudioRecorder.instance().isRecording()) {
                CreateMessageView.this.audioPlayer.setDuration(Long.valueOf(AudioRecorder.instance().getRecordingDuration()));
                if (AudioRecorder.instance().isAudioWaveChanged()) {
                    CreateMessageView.this.audioPlayer.setWaveInfo(AudioRecorder.instance().getAudioWaveDisplayed());
                }
                CreateMessageView.this.handler.postDelayed(CreateMessageView.this.updateAudioRecordingUI, 100);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.21 */
    class AnonymousClass21 implements AnimatorUpdateListener {
        final /* synthetic */ float val$radius;

        AnonymousClass21(float f) {
            this.val$radius = f;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            int size = ((int) (CreateMessageView.this.dpToPixels(20.0f) + (animation.getAnimatedFraction() * ((float) ((int) CreateMessageView.this.dpToPixels(this.val$radius)))))) & -2;
            LayoutParams lp = CreateMessageView.this.audioRecordingButton.getLayoutParams();
            if (lp instanceof MarginLayoutParams) {
                MarginLayoutParams rlp = (MarginLayoutParams) lp;
                rlp.height = size;
                rlp.width = size;
                rlp.bottomMargin = (CreateMessageView.this.attachAudioButton.getHeight() / 2) - (lp.height / 2);
                CreateMessageView.this.audioRecordingButton.setLayoutParams(rlp);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.24 */
    static /* synthetic */ class AnonymousClass24 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$ui$custom$CreateMessageView$UIState;

        static {
            $SwitchMap$ru$ok$android$ui$custom$CreateMessageView$UIState = new int[UIState.values().length];
            try {
                $SwitchMap$ru$ok$android$ui$custom$CreateMessageView$UIState[UIState.PLAYING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$CreateMessageView$UIState[UIState.PAUSED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$CreateMessageView$UIState[UIState.RECORDING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$custom$CreateMessageView$UIState[UIState.DEFAULT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.2 */
    class C06032 implements OnClickListener {
        C06032() {
        }

        public void onClick(View view) {
            if (CreateMessageView.this.listener != null && view.isEnabled()) {
                CreateMessageView.this.listener.onSendMessageClick(view);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.3 */
    class C06043 implements OnClickListener {
        C06043() {
        }

        public void onClick(View view) {
            CreateMessageView.this.toggleAudioRecording();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.4 */
    class C06054 implements OnClickListener {
        C06054() {
        }

        public void onClick(View v) {
            StatisticManager.getInstance().addStatisticEvent("attach-photo-clicked", new Pair[0]);
            if (CreateMessageView.this.attachPhotoListener != null) {
                CreateMessageView.this.attachPhotoListener.onPhotoSelectClick(CreateMessageView.this.attachPhotoButton);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.5 */
    class C06065 implements OnLongClickListener {
        C06065() {
        }

        public boolean onLongClick(View v) {
            StatisticManager.getInstance().addStatisticEvent("attach-photo-long-clicked", new Pair[0]);
            if (CreateMessageView.this.attachPhotoListener != null) {
                CreateMessageView.this.attachPhotoListener.onCameraClick(CreateMessageView.this.attachPhotoButton);
            }
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.6 */
    class C06086 implements OnClickListener {

        /* renamed from: ru.ok.android.ui.custom.CreateMessageView.6.1 */
        class C06071 implements OnActionItemClickListener {
            C06071() {
            }

            public void onItemClick(QuickActionList source, int pos, int actionId) {
                boolean recordNew;
                if (actionId == 0) {
                    recordNew = true;
                } else {
                    recordNew = false;
                }
                CreateMessageView.this.attachVideoListener.onAttachVideoClick(recordNew);
                StatisticManager.getInstance().addStatisticEvent("attach-video-clicked", new Pair("recordNew", String.valueOf(recordNew)));
            }
        }

        C06086() {
        }

        public void onClick(View v) {
            StatisticManager.getInstance().addStatisticEvent("attach-clip-clicked", new Pair[0]);
            if (CreateMessageView.this.attachVideoListener != null) {
                QuickActionList action = new QuickActionList(v.getContext());
                action.addActionItem(new ActionItem(0, 2131166757, 2130838202));
                action.addActionItem(new ActionItem(1, 2131166758, 2130838212));
                action.setOnActionItemClickListener(new C06071());
                action.show(v);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.7 */
    class C06097 implements OnClickListener {
        C06097() {
        }

        public void onClick(View v) {
            CreateMessageView.this.pauseRecordingPlayback();
            CreateMessageView.this.cancelAudioRecording();
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.8 */
    class C06108 implements OnCheckedChangeListener {
        C06108() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            CreateMessageView.this.listener.onAdminStateChanged(isChecked);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.CreateMessageView.9 */
    class C06119 implements InputCallback {
        C06119() {
        }

        public void onPlayPauseClick(View view) {
            CreateMessageView.this.togglePlayback();
        }

        public boolean onSeekStarted(View view, long timeMS) {
            if (CreateMessageView.this.recordedAudioFile == null) {
                return false;
            }
            if (!AudioPlaybackController.isPlaying(CreateMessageView.this.recordedAudioFile)) {
                CreateMessageView.this.startRecordingPlayback();
                CreateMessageView.this.pauseRecordingPlayback();
            }
            AudioPlaybackController.startSeek(timeMS);
            return true;
        }

        public boolean onSeeking(View view, long timeMS) {
            if (CreateMessageView.this.recordedAudioFile == null || !AudioPlaybackController.isPlaying(CreateMessageView.this.recordedAudioFile)) {
                return false;
            }
            AudioPlaybackController.handleSeeking(timeMS);
            return true;
        }

        public boolean onSeekStopped(View view, long timeMS) {
            if (CreateMessageView.this.recordedAudioFile == null || !AudioPlaybackController.isPlaying(CreateMessageView.this.recordedAudioFile)) {
                return false;
            }
            AudioPlaybackController.stopSeek(timeMS);
            return true;
        }
    }

    public interface OnAudioAttachListener {
        void onAudioAttachRecording(boolean z);

        void onAudioAttachRequested(String str, byte[] bArr);
    }

    public interface OnPhotoAttachClickListener {
        void onCameraClick(View view);

        void onPhotoSelectClick(View view);
    }

    public interface OnSendMessageClickListener {
        void onAdminStateChanged(boolean z);

        void onSendMessageClick(View view);
    }

    public interface OnVideoAttachClickListener {
        void onAttachVideoClick(boolean z);
    }

    private enum UIState {
        DEFAULT,
        RECORDING,
        PLAYING,
        PAUSED
    }

    public void addTextWatcher(TextWatcher textWatcher) {
        this.messageEditText.addTextChangedListener(textWatcher);
    }

    public void removeTextWatcher(TextWatcher textWatcher) {
        this.messageEditText.removeTextChangedListener(textWatcher);
    }

    public CreateMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.audioRecordingEnabled = false;
        this.viewLocation = new int[2];
        this.mode = 1;
        this.audioPlayerRightMarginDP = 0.0f;
        this.state = UIState.DEFAULT;
        this.handler = new Handler();
        this.updateAudioRecordingUI = new C06021();
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setClickable(true);
        setOrientation(1);
        int padding = getResources().getDimensionPixelSize(2131230919);
        setBackgroundColor(-1);
        setPadding(padding, 0, 0, 0);
        setPadding(0, 0, 0, 0);
        LocalizationManager.inflate(context, 2130903138, (ViewGroup) this, true);
        this.messageEditText = (EditText) findViewById(2131624738);
        this.messageEditText.addTextChangedListener(this);
        this.messageEditText.setSelected(false);
        this.messageEditText.setEnabled(false);
        this.smileCheckBox = (CheckBox) findViewById(2131624539);
        this.actionsAnimator = (ViewAnimator) findViewById(2131624733);
        if (VERSION.SDK_INT < 11) {
            this.actionsAnimator.setInAnimation(null);
            this.actionsAnimator.setOutAnimation(null);
        }
        this.sendMessageButton = (ImageFadeButton) findViewById(2131624543);
        this.sendMessageButton.setOnClickListener(new C06032());
        this.attachAudioButton = (ImageFadeButton) findViewById(2131624734);
        this.attachAudioButton.setOnClickListener(new C06043());
        this.attachAudioButton.setEnabled(false);
        this.attachPhotoButton = (ImageFadeButton) findViewById(2131624737);
        this.attachPhotoButton.setOnClickListener(new C06054());
        this.attachPhotoButton.setOnLongClickListener(new C06065());
        this.attachPhotoButton.setVisibility(0);
        this.attachPhotoButton.setEnabled(false);
        this.attachVideoButton = (ImageFadeButton) findViewById(2131624736);
        this.attachVideoButton.setOnClickListener(new C06086());
        this.attachVideoButton.setVisibility(0);
        this.attachVideoButton.setEnabled(false);
        this.cancelButton = (ImageFadeButton) findViewById(2131624735);
        this.cancelButton.setOnClickListener(new C06097());
        this.sendMessageButton.setEnabled(false);
        this.audioPlayerStub = (ViewStub) findViewById(2131624706);
        this.asAdminCheckbox = (CheckBox) findViewById(2131624732);
        this.asAdminCheckbox.setOnCheckedChangeListener(new C06108());
        setGestureHandler(new GestureHandler(this));
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.CreateMessage);
        if (a.hasValue(0)) {
            this.messageEditText.setHint(LocalizationManager.getString(context, a.getResourceId(0, 0)));
        }
        a.recycle();
        this.paint = new Paint();
        this.paint.setColor(getResources().getColor(2131492960));
        this.paint.setStrokeWidth(dpToPixels(0.5f));
        this.stickerCircle = findViewById(C0263R.id.circle);
    }

    private void positionAudioButtons() {
        makeAudioContainerVisible();
        MarginLayoutParams lp = (MarginLayoutParams) this.audioButtonsContainer.getLayoutParams();
        int[] ownerLocation = new int[2];
        int[] menuLocation = new int[2];
        this.attachAudioButton.getLocationOnScreen(menuLocation);
        this.ownerView.getLocationOnScreen(ownerLocation);
        lp.rightMargin = ((this.attachAudioButton.getWidth() / 2) + (((ownerLocation[0] + this.ownerView.getWidth()) - menuLocation[0]) - this.attachAudioButton.getWidth())) - (lp.width / 2);
        this.audioButtonsContainer.setLayoutParams(lp);
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawLine(0.0f, 0.0f, (float) getWidth(), 0.0f, this.paint);
    }

    private void inflateAudioPlayerView() {
        this.audioPlayerStub.setVisibility(0);
        this.audioPlayer = (AudioMsgPlayer) findViewById(2131624624);
        if (this.audioPlayerRightMarginDP > 0.1f) {
            setAudioPlayerRightMarginDP(this.audioPlayerRightMarginDP);
        }
        this.audioPlayer.setAlwaysActive(true);
        this.audioPlayer.requestLayout();
        this.audioPlayerStub = null;
        this.audioPlayer.setVisibility(0);
        this.audioPlayer.setIsRight();
        this.audioPlayer.setEventsListener(new C06119());
    }

    private void togglePlayback() {
        if (AudioPlaybackController.isPlaying() || AudioPlaybackController.isBuffering()) {
            this.audioPlayer.onPaused();
            pauseRecordingPlayback();
            return;
        }
        this.audioPlayer.onPlaying();
        startRecordingPlayback();
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean enableSend;
        boolean sendAlreadyShown;
        if (this.state != UIState.DEFAULT) {
            this.messageEditText.getEditableText().clear();
        }
        if (TextUtils.getTrimmedLength(s) <= 0 || !this.attachPhotoButton.isEnabled()) {
            enableSend = false;
        } else {
            enableSend = true;
        }
        this.sendMessageButton.setEnabled(enableSend);
        if (this.sendMessageButton.getVisibility() == 0) {
            sendAlreadyShown = true;
        } else {
            sendAlreadyShown = false;
        }
        if (enableSend != sendAlreadyShown && this.mode != 1) {
            if (enableSend) {
                hideButton(this.attachPhotoButton);
                hideButton(this.attachVideoButton);
                this.actionsAnimator.setDisplayedChild(2);
                return;
            }
            showButton(this.attachPhotoButton);
            showButton(this.attachVideoButton);
            this.actionsAnimator.setDisplayedChild(0);
        }
    }

    private void showButton(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", new float[]{(float) getMeasuredHeight(), 0.0f});
        animator.setDuration(175);
        animator.addListener(new AnonymousClass10(view));
        animator.start();
    }

    private void hideButton(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", new float[]{0.0f, (float) getMeasuredHeight()});
        animator.setDuration(175);
        animator.addListener(new AnonymousClass11(view));
        animator.start();
    }

    public void afterTextChanged(Editable editable) {
    }

    public void setChecked(boolean value) {
        this.smileCheckBox.setChecked(value);
    }

    public Editable getText() {
        return this.messageEditText.getText();
    }

    public EditText getEditText() {
        return this.messageEditText;
    }

    public void setText(CharSequence text) {
        boolean z = false;
        this.messageEditText.setText(text);
        int len = text == null ? 0 : TextUtils.getTrimmedLength(text);
        ImageFadeButton imageFadeButton = this.sendMessageButton;
        if (len > 0) {
            z = true;
        }
        imageFadeButton.setEnabled(z);
        if (len > 0) {
            this.messageEditText.setSelection(len);
        }
    }

    public void setSelection(int index) {
        this.messageEditText.setSelection(index);
    }

    public void setOnSendMessageClickListener(OnSendMessageClickListener listener) {
        this.listener = listener;
    }

    public void setOnPhotoAttachListener(OnPhotoAttachClickListener listener) {
        this.attachPhotoListener = listener;
    }

    public void setOnVideoAttachListener(OnVideoAttachClickListener listener) {
        this.attachVideoListener = listener;
    }

    public void setAttachAudioListener(OnAudioAttachListener attachAudioListener) {
        this.attachAudioListener = attachAudioListener;
    }

    public boolean handleBackPress() {
        if (this.state == UIState.DEFAULT) {
            return false;
        }
        cancelAudioRecording();
        return true;
    }

    public void startEditing() {
        KeyBoardUtils.showKeyBoard(getContext(), this.messageEditText);
    }

    public void setAdminEnabled(boolean adminEnabled) {
        this.asAdminCheckbox.setVisibility(adminEnabled ? 0 : 8);
    }

    public boolean isAdminSelected() {
        return this.asAdminCheckbox.isChecked();
    }

    public void setAdminSelected(boolean isSelected) {
        this.asAdminCheckbox.setChecked(isSelected);
    }

    public View getAdminView() {
        return this.asAdminCheckbox;
    }

    public void setHintId(int textId) {
        this.messageEditText.setHint(textId != 0 ? LocalizationManager.from(getContext()).getString(textId) : null);
    }

    public void setMaxTextLength(int maxLength) {
        if (maxLength != 0) {
            this.messageEditText.setFilters(new InputFilter[]{new LengthFilter(maxLength)});
        }
    }

    public void setSendMode(int mode) {
        int visibility = 8;
        Logger.m173d("set mode: %d", Integer.valueOf(mode));
        this.mode = mode;
        if (mode != 0) {
            updateRecordingUI();
        } else if (TextUtils.getTrimmedLength(this.messageEditText.getText()) == 0) {
            setViewVisibility(this.sendMessageButton, 8);
            setViewVisibility(this.attachAudioButton, 0);
            if (this.state != UIState.RECORDING) {
                visibility = 0;
            }
            setViewVisibility(this.attachPhotoButton, visibility);
            setViewVisibility(this.attachVideoButton, visibility);
        } else {
            setViewVisibility(this.attachAudioButton, 8);
            setViewVisibility(this.sendMessageButton, 0);
            setViewVisibility(this.attachPhotoButton, 8);
            setViewVisibility(this.attachVideoButton, 8);
        }
    }

    private void playBeep(Context context, BeepCallBack callBack) {
        try {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(2131099649);
            if (afd != null) {
                this.mp = new MediaPlayer();
                this.mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                this.mp.setAudioStreamType(4);
                this.mp.prepareAsync();
                this.mp.setOnCompletionListener(new AnonymousClass12(callBack));
                this.mp.setOnErrorListener(new AnonymousClass13(callBack));
                this.mp.setOnPreparedListener(new AnonymousClass14(callBack));
            }
        } catch (Throwable e) {
            Logger.m179e(e, "Error play beep");
        }
    }

    private void startAudioRecording() {
        startAudioRecording(false);
    }

    private void startAudioRecording(boolean isNotifyBeep) {
        Logger.m172d("Start audio recording in messages");
        AudioRecorder recorder = AudioRecorder.instance();
        if (!recorder.isRecording()) {
            setKeepScreenOn(true);
            if (isNotifyBeep) {
                playBeep(getContext(), new AnonymousClass15(recorder));
                return;
            }
            showSendButton();
            showPlayer();
            this.state = UIState.RECORDING;
            updateRecordingUI();
            startAudioRecordingNow(recorder);
        }
    }

    private void startAudioRecordingNow(AudioRecorder recorder) {
        Logger.m172d("OkRecorder : Start Recorder " + SystemClock.currentThreadTimeMillis());
        if (recorder.startRecording(getContext(), ServicesSettingsHelper.getServicesSettings().getAudioAttachRecordingMaxDuration() * LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, new RecordingCallback() {
            public void onError() {
                Logger.m176e("Audio recording error");
                Toast.makeText(CreateMessageView.this.getContext(), LocalizationManager.getString(CreateMessageView.this.getContext(), 2131165799), 1).show();
                CreateMessageView.this.cancelAudioRecording();
            }

            public void onDone() {
                CreateMessageView.this.stopAudioRecording();
            }
        })) {
            this.smileCheckBox.setChecked(false);
            this.audioRecordingButton.requestFocus();
            this.audioRecordingButton.bringToFront();
            this.handler.postDelayed(this.updateAudioRecordingUI, 100);
            return;
        }
        Logger.m176e("Failed to start audio recording ");
        Toast.makeText(getContext(), LocalizationManager.getString(getContext(), 2131165799), 1).show();
        cancelAudioRecording();
    }

    private void showPlayer() {
        if (this.audioPlayer == null) {
            inflateAudioPlayerView();
            this.audioPlayer.setPosition(0);
            this.audioPlayer.setDuration(Long.valueOf(0));
            this.audioPlayer.setWaveInfo((byte[]) null);
        }
    }

    private void showSendButton() {
        positionAudioButtons();
        this.sendAudioButton.setVisibility(0);
        showButtonUseAnimator(dpToPixels(20.0f), dpToPixels(80.0f), 200);
    }

    private float dpToPixels(float value) {
        return TypedValue.applyDimension(1, value, getResources().getDisplayMetrics());
    }

    private float pixelsToDp(float value) {
        return value / dpToPixels(1.0f);
    }

    private void showButtonUseAnimator(float valueX, float valueY, long duration) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this.sendAudioButton, "translationX", new float[]{0.0f, -valueX});
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this.sendAudioButton, "translationY", new float[]{0.0f, -valueY});
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(new Animator[]{animatorX, animatorY});
        animator.setDuration(duration);
        animator.addListener(new SimpleAnimatorListener() {
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                CreateMessageView.this.sendAudioButton.setEnabled(false);
            }

            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                CreateMessageView.this.sendAudioButton.setEnabled(true);
            }
        });
        animator.start();
    }

    private void stopAudioRecording() {
        Logger.m172d("OkRecorder : Stop Recorder");
        AudioRecorder recorder = AudioRecorder.instance();
        if (recorder.isRecording()) {
            if (recorder.stopRecording()) {
                this.state = UIState.PAUSED;
                this.audioWave = recorder.getAudioWave();
                this.recordedAudioFile = recorder.getRecorderFilePath();
                if (!TextUtils.isEmpty(this.recordedAudioFile)) {
                    long duration = (long) AudioPlaybackController.getMediaDuration(this.recordedAudioFile);
                    if (duration > 0 && this.audioPlayer != null) {
                        this.audioPlayer.setDuration(Long.valueOf(duration));
                        this.audioPlayer.animateTransition(this.audioWave);
                    }
                }
                setKeepScreenOn(false);
            } else {
                this.state = UIState.DEFAULT;
            }
            dismissRecordingButton();
            updateRecordingUI();
        }
    }

    private void dismissRecordingButton() {
        if (this.audioRecordingButton.getVisibility() == 0) {
            this.audioRecordingButton.setEnabled(false);
            if (this.dismissButtonAnimator != null) {
                this.dismissButtonAnimator.cancel();
            }
            this.dismissButtonAnimator = ValueAnimator.ofInt(new int[]{100});
            this.dismissButtonAnimator.setDuration(200);
            float startWidth = pixelsToDp((float) this.audioRecordingButton.getWidth());
            LayoutParams playerLp = this.audioPlayer.getLayoutParams();
            float startMargin = playerLp instanceof MarginLayoutParams ? pixelsToDp((float) ((MarginLayoutParams) playerLp).rightMargin) : -1.0f;
            this.dismissButtonAnimator.addListener(new AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    CreateMessageView.this.audioRecordingButton.setEnabled(true);
                    CreateMessageView.this.audioRecordingButton.setVisibility(8);
                    CreateMessageView.this.setAudioPlayerRightMarginDP(17.0f);
                    CreateMessageView.this.dismissButtonAnimator = null;
                    CreateMessageView.this.updateRecordingUI();
                }

                public void onAnimationCancel(Animator animation) {
                    onAnimationEnd(animation);
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
            this.dismissButtonAnimator.addUpdateListener(new AnonymousClass19(startWidth, startMargin));
            this.dismissButtonAnimator.start();
        }
    }

    private void sendAudioRecording() {
        if (!(TextUtils.isEmpty(this.recordedAudioFile) || this.attachAudioListener == null)) {
            this.attachAudioListener.onAudioAttachRequested(this.recordedAudioFile, this.audioWave);
        }
        if (AudioPlaybackController.isPlaying(this.recordedAudioFile)) {
            AudioPlaybackController.dismissPlayer();
        }
        this.recordedAudioFile = null;
        this.audioWave = null;
        if (this.state != UIState.DEFAULT) {
            this.state = UIState.DEFAULT;
            updateRecordingUI();
            this.messageEditText.requestFocus();
        }
    }

    private void cancelAudioRecording() {
        AudioRecorder recorder = AudioRecorder.instance();
        if (recorder.isRecording() && recorder.stopRecording()) {
            this.recordedAudioFile = recorder.getRecorderFilePath();
            if (AudioPlaybackController.isPlaying(this.recordedAudioFile)) {
                AudioPlaybackController.dismissPlayer();
            }
            if (!TextUtils.isEmpty(this.recordedAudioFile)) {
                new File(this.recordedAudioFile).delete();
            }
        }
        this.recordedAudioFile = null;
        this.audioWave = null;
        this.handler.removeCallbacks(this.updateAudioRecordingUI);
        if (this.state != UIState.DEFAULT) {
            this.state = UIState.DEFAULT;
            updateRecordingUI();
            this.messageEditText.requestFocus();
        }
        setKeepScreenOn(false);
    }

    private void updateRecordingUI() {
        int i = 0;
        switch (AnonymousClass24.$SwitchMap$ru$ok$android$ui$custom$CreateMessageView$UIState[this.state.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                setButtonPressed(this.attachAudioButton, false);
                this.attachAudioButton.setVisibility(8);
                this.audioPlayer.setEnableButtons(true);
                this.cancelButton.setVisibility(0);
                setViewVisibility(this.attachPhotoButton, 8);
                setViewVisibility(this.attachVideoButton, 8);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.attachAudioListener != null) {
                    this.attachAudioListener.onAudioAttachRecording(true);
                }
                this.messageEditText.setVisibility(4);
                this.smileCheckBox.setVisibility(4);
                makeAudioContainerVisible();
                setViewVisibility(this.audioPlayer, 0);
                setViewVisibility(this.sendAudioButton, 0);
                setViewVisibility(this.attachPhotoButton, 8);
                setViewVisibility(this.attachVideoButton, 8);
                this.audioPlayer.setPlaybackState(AudioPlaybackController.getState());
                this.audioPlayer.setDuration(Long.valueOf(0));
                this.audioPlayer.setPosition(0);
                this.audioPlayer.setRollingMode(true);
                this.audioPlayer.setWaveInfo((byte[]) null);
                this.audioPlayer.setEnableButtons(false);
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (this.attachAudioListener != null) {
                    this.attachAudioListener.onAudioAttachRecording(false);
                }
                this.messageEditText.setVisibility(0);
                this.smileCheckBox.setVisibility(0);
                if (this.attachAudioButton.getVisibility() != 0) {
                    setButtonPressed(this.attachAudioButton, false);
                }
                if (TextUtils.getTrimmedLength(this.messageEditText.getText()) != 0 || this.mode == 1) {
                    Logger.m173d("mode value: %d", Integer.valueOf(this.mode));
                    this.sendMessageButton.setVisibility(0);
                    this.attachAudioButton.setVisibility(8);
                    this.attachPhotoButton.setVisibility(8);
                    this.attachVideoButton.setVisibility(8);
                } else {
                    Logger.m173d("mode value: %d", Integer.valueOf(this.mode));
                    this.attachAudioButton.setVisibility(0);
                    this.sendMessageButton.setVisibility(8);
                    int visibility = AudioRecorder.instance().isRecording() ? 8 : 0;
                    this.attachPhotoButton.setVisibility(visibility);
                    this.attachVideoButton.setVisibility(visibility);
                }
                setViewVisibility(this.audioPlayer, 8);
                if (this.audioButtonsContainer != null) {
                    setViewVisibility(this.sendAudioButton, 8);
                    setViewVisibility(this.audioButtonsContainer, 8);
                    break;
                }
                break;
        }
        if (this.state != UIState.RECORDING) {
            if (this.dismissButtonAnimator == null) {
                setViewVisibility(this.audioRecordingButton, 8);
            }
            if (this.audioPlayer != null) {
                this.audioPlayer.setPlayerState();
            }
        } else if (this.audioPlayer != null) {
            this.audioPlayer.setRecorderState();
        }
        if (this.state == UIState.PAUSED) {
            ImageFadeButton imageFadeButton = this.cancelButton;
            if (this.dismissButtonAnimator != null) {
                i = 4;
            }
            imageFadeButton.setVisibility(i);
            return;
        }
        this.cancelButton.setVisibility(8);
    }

    private void setViewVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void toggleAudioRecording() {
        if (AudioRecorder.instance().isRecording()) {
            stopAudioRecording();
        } else {
            startAudioRecording();
        }
    }

    void startRecordingPlayback() {
        if (!TextUtils.isEmpty(this.recordedAudioFile)) {
            if (AudioPlaybackController.isPlaying(this.recordedAudioFile)) {
                AudioPlaybackController.resumePlayback();
                return;
            }
            PlaybackEventsListener playbackEventsListener = new PlaybackEventsListener() {
                public void onError() {
                    if (messageIsForCurrentPlayer()) {
                        CreateMessageView.this.audioPlayer.onError();
                        CreateMessageView.this.state = UIState.PAUSED;
                        CreateMessageView.this.updateRecordingUI();
                    }
                }

                public void onDismissed() {
                    if (messageIsForCurrentPlayer()) {
                        CreateMessageView.this.audioPlayer.setPosition(0);
                        CreateMessageView.this.audioPlayer.onStopped();
                        CreateMessageView.this.state = UIState.PAUSED;
                        CreateMessageView.this.updateRecordingUI();
                    }
                }

                public void onBuffering() {
                    if (messageIsForCurrentPlayer()) {
                        CreateMessageView.this.audioPlayer.onBuffering();
                    }
                }

                public void onPlaying() {
                    if (messageIsForCurrentPlayer()) {
                        CreateMessageView.this.audioPlayer.onPlaying();
                    }
                }

                public void onEnd() {
                    if (messageIsForCurrentPlayer()) {
                        CreateMessageView.this.audioPlayer.setPosition(0);
                        CreateMessageView.this.audioPlayer.onStopped();
                        CreateMessageView.this.state = UIState.PAUSED;
                        CreateMessageView.this.updateRecordingUI();
                    }
                }

                public void onPosition(long positionMilliseconds) {
                    if (messageIsForCurrentPlayer()) {
                        CreateMessageView.this.audioPlayer.setPosition(positionMilliseconds);
                    }
                }

                private boolean messageIsForCurrentPlayer() {
                    return AudioPlaybackController.isPlaying(CreateMessageView.this.recordedAudioFile);
                }
            };
            this.audioPlayer.setIsRight();
            AudioPlaybackController.startPlayback(getContext(), this.recordedAudioFile, playbackEventsListener, 33);
            StatisticManager.getInstance().addStatisticEvent("attach-audio-pre-send-play", new Pair[0]);
        }
    }

    void pauseRecordingPlayback() {
        if (!TextUtils.isEmpty(this.recordedAudioFile) && AudioPlaybackController.isPlaying(this.recordedAudioFile)) {
            AudioPlaybackController.pausePlayback();
            this.audioPlayer.setIsLeft();
        }
    }

    public int getItemAtPoint(float x, float y) {
        if (isPointInsideView(x, y, this.audioRecordingButton) || isPointInsideView(x, y, this.attachAudioButton)) {
            return 2131624734;
        }
        if (isPointInsideView(x, y, this.sendAudioButton)) {
            return 2131625104;
        }
        return -1;
    }

    public void onTouchDown(int itemId) {
        switch (itemId) {
            case 2131624734:
                if (this.audioRecordingEnabled && AudioRecorder.instance().isRecording()) {
                    stopAudioRecording();
                }
            default:
        }
    }

    public void onTouchUp(int itemId, int originItemId) {
        switch (itemId) {
            case 2131624734:
                if (this.state != UIState.RECORDING && originItemId != 2131624734) {
                    if (this.audioRecordingEnabled) {
                        startAudioRecording();
                        break;
                    }
                } else if (AudioRecorder.instance().getRecordingDuration() <= 1000) {
                    cancelAudioRecording();
                    break;
                } else {
                    stopAudioRecording();
                    break;
                }
                break;
            case 2131625104:
                stopAudioRecording();
                sendAudioRecording();
                break;
            default:
                stopAudioRecording();
                break;
        }
        resetPressed();
    }

    public void onItemHeld(int itemId) {
        switch (itemId) {
            case 2131624734:
                initRecording(100.0f, false);
            default:
        }
    }

    private void initRecording(float radius, boolean smallGap) {
        if (PermissionUtils.checkSelfPermission(getContext(), "android.permission.RECORD_AUDIO") != -1) {
            makeAudioContainerVisible();
            if (this.audioRecordingEnabled) {
                if (!(this.audioRecordingButton == null || this.audioRecordingButton.getVisibility() == 0)) {
                    this.audioRecordingButton.setVisibility(0);
                    setButtonPressed(this.attachAudioButton, false);
                    this.attachAudioButton.setVisibility(4);
                    this.audioRecordingButton.requestFocus();
                    this.audioRecordingButton.bringToFront();
                    positionAudioButtons();
                    setAudioPlayerRightMarginDP(smallGap ? 17.0f : 42.0f);
                    ValueAnimator animator = ValueAnimator.ofInt(new int[]{100});
                    animator.setDuration(200);
                    animator.addUpdateListener(new AnonymousClass21(radius));
                    animator.start();
                }
                startAudioRecording();
            } else if (this.audioRecordingErrorTextId != 0) {
                Toast.makeText(getContext(), LocalizationManager.getString(getContext(), this.audioRecordingErrorTextId), 1).show();
            }
        } else if (this.permissionRequester != null) {
            this.permissionRequester.requestPermissions("android.permission.RECORD_AUDIO");
        }
    }

    private void setAudioPlayerRightMarginDP(float marginDP) {
        this.audioPlayerRightMarginDP = marginDP;
        if (this.audioPlayer != null) {
            int margin = (int) dpToPixels(marginDP);
            LayoutParams playerLp = this.audioPlayer.getLayoutParams();
            if (playerLp instanceof MarginLayoutParams) {
                MarginLayoutParams playerLpMargin = (MarginLayoutParams) playerLp;
                if (playerLpMargin.rightMargin != margin) {
                    playerLpMargin.rightMargin = margin;
                    this.audioPlayer.setLayoutParams(playerLp);
                    this.audioPlayer.getParent().requestLayout();
                }
            }
        }
    }

    public void onItemTapped(int itemId) {
        switch (itemId) {
            case 2131624734:
                if (!this.audioRecordingEnabled) {
                    if (this.audioRecordingErrorTextId != 0) {
                        Toast.makeText(getContext(), LocalizationManager.getString(getContext(), this.audioRecordingErrorTextId), 1).show();
                        break;
                    }
                }
                initRecording(50.0f, true);
                break;
                break;
            case 2131624735:
                cancelAudioRecording();
                break;
            case 2131625104:
                stopAudioRecording();
                sendAudioRecording();
                break;
        }
        resetPressed();
    }

    public void onItemEntered(int itemId) {
        switch (itemId) {
            case 2131624734:
                if (this.audioRecordingEnabled) {
                    setButtonPressed(this.attachAudioButton, true);
                }
            case 2131624735:
                setButtonPressed(this.cancelButton, false);
            case 2131625104:
                this.sendAudioButton.setFocusableInTouchMode(true);
                this.sendAudioButton.requestFocus();
            default:
        }
    }

    public void onOutside(int itemId) {
        switch (itemId) {
            case 2131624734:
                setButtonPressed(this.attachAudioButton, false);
            case 2131624735:
                setButtonPressed(this.cancelButton, false);
            case 2131625104:
                this.sendAudioButton.setFocusableInTouchMode(false);
                this.sendAudioButton.setFocusable(false);
            default:
        }
    }

    public void onCancelled(int itemId) {
        getParent().requestDisallowInterceptTouchEvent(false);
        resetPressed();
    }

    private void resetPressed() {
        setButtonPressed(this.attachAudioButton, false);
    }

    private boolean isPointInsideView(float x, float y, View view) {
        if (view == null || view.getVisibility() != 0) {
            return false;
        }
        view.getLocationOnScreen(this.viewLocation);
        int viewX = this.viewLocation[0];
        int viewY = this.viewLocation[1];
        if (x <= ((float) viewX) || x >= ((float) (view.getWidth() + viewX)) || y <= ((float) viewY) || y >= ((float) (view.getHeight() + viewY))) {
            return false;
        }
        return true;
    }

    private void setButtonPressed(ImageFadeButton button, boolean pressed) {
        if (!pressed || button.isEnabled()) {
            button.setPressed(pressed);
            button.showPressed(pressed);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureHandler == null || !this.gestureHandler.handleEvent(event)) {
            return false;
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 || getItemAtPoint(ev.getRawX(), ev.getRawY()) == -1) {
            return false;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    public void setGestureHandler(GestureHandler gestureHandler) {
        this.gestureHandler = gestureHandler;
    }

    public void setAudioRecordingControlsStub(View ownerView, OkViewStub stub) {
        this.audioButtonsContainerStub = stub;
        this.ownerView = ownerView;
    }

    private void makeAudioContainerVisible() {
        if (this.audioButtonsContainer != null) {
            this.audioButtonsContainer.setVisibility(0);
        } else if (this.audioButtonsContainerStub == null) {
            Logger.m184w("audioButtonsContainerStub is null!!!");
        } else {
            this.audioButtonsContainer = this.audioButtonsContainerStub.inflate();
            this.audioButtonsContainerStub = null;
            this.audioRecordingButton = this.audioButtonsContainer.findViewById(2131625105);
            this.sendAudioButton = this.audioButtonsContainer.findViewById(2131625104);
            this.audioRecordingButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    CreateMessageView.this.stopAudioRecording();
                }
            });
            this.audioRecordingButton.setSoundEffectsEnabled(false);
            this.sendAudioButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    CreateMessageView.this.stopAudioRecording();
                    CreateMessageView.this.sendAudioRecording();
                }
            });
        }
    }

    public void handleStop() {
        cancelAudioRecording();
    }

    public void handleDestroyView() {
        cancelAudioRecording();
    }

    public void setError(int errorId) {
        this.audioRecordingErrorTextId = errorId;
        updateRecordingUI();
    }

    public void setPermissionRequester(Requester permissionRequester) {
        this.permissionRequester = permissionRequester;
    }

    public void setEnabledStates(boolean sendMessageAllowed, boolean audioRecordingAllowed, boolean videoAllowed, boolean updateEditTextState) {
        this.audioRecordingEnabled = audioRecordingAllowed & sendMessageAllowed;
        ImageFadeButton imageFadeButton = this.sendMessageButton;
        boolean z = TextUtils.getTrimmedLength(getText()) > 0 && sendMessageAllowed;
        imageFadeButton.setEnabled(z);
        this.attachPhotoButton.setEnabled(sendMessageAllowed);
        this.attachVideoButton.setEnabled(sendMessageAllowed & videoAllowed);
        this.attachAudioButton.setEnabled(this.audioRecordingEnabled);
        this.smileCheckBox.setEnabled(sendMessageAllowed);
        if (updateEditTextState) {
            this.messageEditText.setEnabled(sendMessageAllowed);
        }
    }

    public CheckBox getSmileCheckBox() {
        return this.smileCheckBox;
    }

    public void setCircleVisible(boolean visible) {
        this.stickerCircle.setVisibility(visible ? 0 : 8);
    }
}
