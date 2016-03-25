package ru.ok.android.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import ru.mail.libverify.C0176R;
import ru.ok.android.C0206R;
import ru.ok.android.onelog.AppLaunchLogHelper;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.videochat.CameraPreviewView;
import ru.ok.android.videochat.CameraPreviewView.SurfaceListener;
import ru.ok.android.videochat.RelativeLayoutWithSizeListener;
import ru.ok.android.videochat.VideoRenderView;
import ru.ok.android.videochat.VideochatController;

public class PhoneCallActivity extends Activity implements SensorEventListener, OnRequestPermissionsResultCallback, SurfaceListener {
    private View cameraVideo;
    private CameraPreviewView cameraVideoNew;
    private VideoRenderView cameraVideoOld;
    private Display display;
    private Handler handler;
    private boolean haveValidFrame;
    private VideoRenderView inputVideo;
    private Sensor proximitySensor;
    private SensorManager sensorManager;
    private boolean stopping;
    private Toast toast;
    private ImageView userPicView;

    /* renamed from: ru.ok.android.ui.activity.PhoneCallActivity.1 */
    class C05501 extends Handler {
        C05501() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    if (msg.obj instanceof CallStatus) {
                        PhoneCallActivity.this.processCallStatus((CallStatus) msg.obj);
                    }
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.ui.activity.PhoneCallActivity.2 */
    class C05512 implements OnClickListener {
        C05512() {
        }

        @TargetApi(23)
        public void onClick(View v) {
            try {
                if (VideochatController.instance().videoSupported()) {
                    if (PermissionUtils.checkSelfPermission(PhoneCallActivity.this, "android.permission.CAMERA") != 0) {
                        ActivityCompat.requestPermissions(PhoneCallActivity.this, new String[]{"android.permission.CAMERA"}, C0206R.styleable.Theme_editTextStyle);
                        return;
                    }
                    PhoneCallActivity.this.switchVideo();
                    return;
                }
                Logger.m184w("Graphics library requirements not met - refusing to start camera capture");
                PhoneCallActivity.this.showDialog(3);
            } catch (Exception ex) {
                Logger.m176e("Failed to start video: " + ex);
                PhoneCallActivity.this.showDialog(1);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.activity.PhoneCallActivity.3 */
    class C05523 implements OnClickListener {
        C05523() {
        }

        public void onClick(View v) {
            VideochatController.instance().onUserClosed();
            PhoneCallActivity.this.stop();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.PhoneCallActivity.4 */
    class C05534 implements OnClickListener {
        C05534() {
        }

        public void onClick(View v) {
            VideochatController.instance().onUserClosed();
            PhoneCallActivity.this.stop();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.PhoneCallActivity.5 */
    class C05545 implements OnClickListener {
        C05545() {
        }

        @TargetApi(23)
        public void onClick(View v) {
            if (PermissionUtils.checkSelfPermission(PhoneCallActivity.this, "android.permission.RECORD_AUDIO") == 0) {
                VideochatController.instance().onUserAnswer();
                return;
            }
            ActivityCompat.requestPermissions(PhoneCallActivity.this, new String[]{"android.permission.RECORD_AUDIO"}, C0206R.styleable.Theme_checkedTextViewStyle);
        }
    }

    /* renamed from: ru.ok.android.ui.activity.PhoneCallActivity.6 */
    class C05556 implements OnClickListener {
        C05556() {
        }

        public void onClick(View v) {
            PhoneCallActivity.this.switchMute();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.PhoneCallActivity.7 */
    class C05567 implements DialogInterface.OnClickListener {
        C05567() {
        }

        public void onClick(DialogInterface dialog, int id) {
        }
    }

    /* renamed from: ru.ok.android.ui.activity.PhoneCallActivity.8 */
    class C05578 implements DialogInterface.OnClickListener {
        C05578() {
        }

        public void onClick(DialogInterface dialog, int id) {
        }
    }

    /* renamed from: ru.ok.android.ui.activity.PhoneCallActivity.9 */
    class C05589 implements DialogInterface.OnClickListener {
        C05589() {
        }

        public void onClick(DialogInterface dialog, int id) {
        }
    }

    class CallStatus {
        public String description;
        public long event;

        public CallStatus(long event, String description) {
            this.event = event;
            this.description = description;
        }
    }

    public PhoneCallActivity() {
        this.haveValidFrame = false;
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AppLaunchLogHelper.logIntent(intent);
    }

    @SuppressLint({"ShowToast"})
    public void onCreate(Bundle savedInstanceState) {
        RelativeLayoutWithSizeListener cameraLayout;
        AppLaunchLogHelper.logIntent(getIntent());
        if (VideochatController.instance().getScreenOrientationLock()) {
            setRequestedOrientation(0);
        }
        this.handler = new C05501();
        super.onCreate(savedInstanceState);
        setContentView(2130903372);
        if (VideochatController.instance().callActive()) {
            VideochatController.instance().onCallActivityResumed();
            ((ImageButton) findViewById(2131625181)).setOnClickListener(new C05512());
            ((Button) findViewById(2131624505)).setOnClickListener(new C05523());
            ((Button) findViewById(2131625184)).setOnClickListener(new C05534());
            ((Button) findViewById(2131625183)).setOnClickListener(new C05545());
            ((ImageButton) findViewById(2131625180)).setOnClickListener(new C05556());
            this.userPicView = (ImageView) findViewById(2131625172);
            cameraLayout = (RelativeLayoutWithSizeListener) findViewById(2131625173);
            VideochatController.instance().setActivity(this);
            setUIStatus(VideochatController.instance().getUIStatus());
            this.inputVideo = (VideoRenderView) findViewById(2131625171);
            this.inputVideo.setRenderer(VideochatController.instance().getInputVideoRenderer());
            this.inputVideo.setRenderMode(0);
            this.cameraVideo = findViewById(2131625175);
        } else {
            VideochatController.instance().onCallActivityResumed();
            ((ImageButton) findViewById(2131625181)).setOnClickListener(new C05512());
            ((Button) findViewById(2131624505)).setOnClickListener(new C05523());
            ((Button) findViewById(2131625184)).setOnClickListener(new C05534());
            ((Button) findViewById(2131625183)).setOnClickListener(new C05545());
            ((ImageButton) findViewById(2131625180)).setOnClickListener(new C05556());
            this.userPicView = (ImageView) findViewById(2131625172);
            cameraLayout = (RelativeLayoutWithSizeListener) findViewById(2131625173);
            VideochatController.instance().setActivity(this);
            setUIStatus(VideochatController.instance().getUIStatus());
            this.inputVideo = (VideoRenderView) findViewById(2131625171);
            this.inputVideo.setRenderer(VideochatController.instance().getInputVideoRenderer());
            this.inputVideo.setRenderMode(0);
            this.cameraVideo = findViewById(2131625175);
        }
        if (this.cameraVideo instanceof CameraPreviewView) {
            this.cameraVideoNew = (CameraPreviewView) this.cameraVideo;
        } else if (this.cameraVideo instanceof VideoRenderView) {
            this.cameraVideoOld = (VideoRenderView) this.cameraVideo;
        }
        try {
            this.cameraVideo.getClass().getMethod("setZOrderMediaOverlay", new Class[]{Boolean.TYPE}).invoke(this.cameraVideo, new Object[]{Boolean.valueOf(true)});
        } catch (Exception ex) {
            Logger.m185w("Failed to set overlay z-order; video may be displayed incorrectly: ", ex);
        }
        if (this.cameraVideoOld != null) {
            this.cameraVideoOld.setRenderer(VideochatController.instance().getCameraPreviewRenderer());
            this.cameraVideoOld.setRenderMode(0);
        } else if (cameraLayout != null) {
            VideochatController.instance().setInputVideoContainer(cameraLayout);
        }
        updateRemoteUserImage(VideochatController.instance().getRemoteUserImage());
        VideochatController.instance().resumeVideo();
        updateVideoUI();
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.toast = Toast.makeText(this, "", 0);
        getWindow().addFlags(524288);
        ((PowerManager) getSystemService("power")).newWakeLock(268435466, getClass().getName()).acquire(15000);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("BUSY_MESSAGE")) {
            showDialog(2);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case C0206R.styleable.Theme_checkedTextViewStyle /*102*/:
                if (PermissionUtils.getGrantResult(grantResults) == 0) {
                    VideochatController.instance().onUserAnswer();
                    return;
                }
                VideochatController.instance().onUserClosed();
                stop();
            case C0206R.styleable.Theme_editTextStyle /*103*/:
                if (PermissionUtils.getGrantResult(grantResults) == 0) {
                    switchVideo();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onStart() {
        super.onStart();
        StatisticManager.getInstance().startSession(this);
        if (this.proximitySensor == null) {
            this.proximitySensor = this.sensorManager.getDefaultSensor(8);
            this.sensorManager.registerListener(this, this.proximitySensor, 3);
        }
        VideochatController.instance().speakerSwitch(true);
    }

    protected Dialog onCreateDialog(int id) {
        Builder builder;
        switch (id) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                builder = new Builder(this);
                builder.setMessage(2131165473).setNeutralButton(2131165284, new C05567());
                return builder.create();
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                builder = new Builder(this);
                builder.setMessage(2131165782).setNeutralButton(2131165284, new C05589());
                return builder.create();
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                builder = new Builder(this);
                builder.setMessage(2131166810).setNeutralButton(2131165284, new C05578());
                return builder.create();
            default:
                return null;
        }
    }

    private void lockScreen() {
        this.haveValidFrame = false;
        findViewById(2131625188).setVisibility(0);
        VideochatController.instance().suspendVideo();
        updateVideoUI();
        if (this.toast != null) {
            this.toast.cancel();
        }
        getWindow().setFlags(1024, 1024);
    }

    private void unlockScreen() {
        VideochatController.instance().resumeVideo();
        findViewById(2131625188).setVisibility(4);
        updateVideoUI();
        getWindow().setFlags(0, 1024);
    }

    protected void onStop() {
        StatisticManager.getInstance().endSession(this);
        super.onStop();
        Chronometer chronometer = (Chronometer) findViewById(C0176R.id.chronometer);
        if (chronometer != null) {
            chronometer.stop();
        }
        if (this.proximitySensor != null) {
            this.sensorManager.unregisterListener(this);
            this.proximitySensor = null;
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateVideoUI();
        VideochatController.instance().updateVideo(this.cameraVideoOld != null);
    }

    private void setUIStatus(int uiStatus) {
        RelativeLayout statusLabelLayout = (RelativeLayout) findViewById(2131625176);
        View headerLayout = findViewById(2131625185);
        TextView headerStatusLabel = (TextView) findViewById(2131625187);
        Chronometer chronometer = (Chronometer) findViewById(C0176R.id.chronometer);
        ((TextView) findViewById(2131625186)).setText(VideochatController.instance().getUserName());
        long callStartTime = VideochatController.instance().getCallStartTime();
        if (callStartTime > 0) {
            chronometer.stop();
            chronometer.setBase(callStartTime);
            chronometer.start();
        }
        if (uiStatus == 1 || uiStatus == 2) {
            switchIncomingPanel(false);
            statusLabelLayout.setVisibility(4);
            headerStatusLabel.setText(2131165632);
            headerLayout.setVisibility(0);
            headerStatusLabel.setVisibility(0);
            chronometer.setVisibility(8);
        } else if (uiStatus == 4) {
            headerLayout.setVisibility(8);
            delayedStop();
        } else if (uiStatus == 5) {
            headerStatusLabel.setText(2131165999);
            headerLayout.setVisibility(0);
            headerStatusLabel.setVisibility(0);
            chronometer.setVisibility(8);
            statusLabelLayout.setVisibility(4);
            switchIncomingPanel(true);
        } else {
            headerStatusLabel.setVisibility(8);
            chronometer.setVisibility(0);
            switchIncomingPanel(false);
            statusLabelLayout.setVisibility(4);
        }
    }

    private void switchIncomingPanel(boolean on) {
        int i;
        int i2 = 0;
        View activeCallPanel = findViewById(2131625179);
        View incomingCallPanel = findViewById(2131625182);
        if (on) {
            i = 4;
        } else {
            i = 0;
        }
        activeCallPanel.setVisibility(i);
        if (!on) {
            i2 = 4;
        }
        incomingCallPanel.setVisibility(i2);
    }

    private int getReasonUserString(String reason) {
        if (reason.equals("Remote dropped")) {
            return 2131166455;
        }
        if (reason.equals("Call failed")) {
            return 2131165463;
        }
        if (reason.equals("Busy")) {
            return 2131165460;
        }
        if (reason.equals("Hangup")) {
            return 2131165970;
        }
        if (reason.equals("Rejected")) {
            return 2131166445;
        }
        if (reason.equals("Called offline")) {
            return 2131165469;
        }
        return 2131165461;
    }

    private void delayedStop() {
        if (!this.stopping) {
            this.stopping = true;
            String disconnectReason = VideochatController.getDisconnectReason();
            if (disconnectReason == null) {
                stop();
                return;
            }
            RelativeLayout statusLabelLayout = (RelativeLayout) findViewById(2131625176);
            ((TextView) findViewById(2131625177)).setText(getReasonUserString(disconnectReason));
            statusLabelLayout.setVisibility(0);
            this.handler.postAtTime(new Runnable() {
                public void run() {
                    PhoneCallActivity.this.stop();
                }
            }, SystemClock.uptimeMillis() + 3000);
        }
    }

    protected void switchMute() {
        VideochatController.instance().switchMute();
        boolean muted = VideochatController.instance().isMuted();
        ImageButton muteBtn = (ImageButton) findViewById(2131625180);
        if (muteBtn.isSelected() != muted) {
            this.toast.setText(muted ? 2131166205 : 2131166206);
            this.toast.show();
            muteBtn.setSelected(muted);
        }
    }

    private void switchVideo() {
        VideochatController.instance().switchVideo(this.cameraVideoOld != null);
        updateVideoUI();
    }

    private void updateVideoUI() {
        this.handler.post(new Runnable() {
            public void run() {
                int i = 4;
                if (PhoneCallActivity.this.cameraVideo != null && PhoneCallActivity.this.userPicView != null && PhoneCallActivity.this.inputVideo != null) {
                    int i2;
                    boolean z;
                    Boolean videoOn = VideochatController.instance().isVideoOn();
                    View access$300 = PhoneCallActivity.this.cameraVideo;
                    if (videoOn.booleanValue()) {
                        i2 = 0;
                    } else {
                        i2 = 4;
                    }
                    access$300.setVisibility(i2);
                    ImageButton cameraBtn = (ImageButton) PhoneCallActivity.this.findViewById(2131625181);
                    if (cameraBtn.isSelected() != videoOn.booleanValue()) {
                        PhoneCallActivity.this.toast.setText(videoOn.booleanValue() ? 2131165472 : 2131165471);
                        PhoneCallActivity.this.toast.show();
                        cameraBtn.setSelected(videoOn.booleanValue());
                    }
                    View cameraPreview = PhoneCallActivity.this.findViewById(2131625174);
                    LayoutParams lp = cameraPreview.getLayoutParams();
                    PhoneCallActivity.this.updateCameraLayoutParams(lp, videoOn);
                    cameraPreview.setLayoutParams(lp);
                    if (VideochatController.instance().isRemoteVideoOn() && PhoneCallActivity.this.haveValidFrame) {
                        z = true;
                    } else {
                        z = false;
                    }
                    Boolean remoteVideoOn = Boolean.valueOf(z);
                    ImageView access$400 = PhoneCallActivity.this.userPicView;
                    if (remoteVideoOn.booleanValue()) {
                        i2 = 4;
                    } else {
                        i2 = 0;
                    }
                    access$400.setVisibility(i2);
                    VideoRenderView access$500 = PhoneCallActivity.this.inputVideo;
                    if (remoteVideoOn.booleanValue()) {
                        i2 = 0;
                    } else {
                        i2 = 4;
                    }
                    access$500.setVisibility(i2);
                    View header = PhoneCallActivity.this.findViewById(2131625185);
                    if (!remoteVideoOn.booleanValue()) {
                        i = 0;
                    }
                    header.setVisibility(i);
                }
            }
        });
    }

    protected void updateCameraLayoutParams(LayoutParams lp, Boolean videoOn) {
        if (videoOn.booleanValue()) {
            boolean useDefValues = true;
            if (this.cameraVideoNew != null) {
                VideochatController vcc = VideochatController.instance();
                int rotation = VideochatController.instance().getUIOrientation();
                if (!(vcc.getCaptureWidth() == 0 || vcc.getCaptureHeight() == 0)) {
                    int captureWidth = vcc.getCaptureWidth();
                    int captureHeight = vcc.getCaptureHeight();
                    int max = Math.max(captureWidth, captureHeight);
                    int displayWidth = (captureWidth * 320) / max;
                    int displayHeight = (captureHeight * 320) / max;
                    if (rotation == 90 || rotation == 270) {
                        lp.width = displayHeight;
                        lp.height = displayWidth;
                    } else {
                        lp.width = displayWidth;
                        lp.height = displayHeight;
                    }
                    useDefValues = false;
                }
            }
            if (useDefValues) {
                lp.width = 240;
                lp.height = 168;
                return;
            }
            return;
        }
        lp.width = 0;
        lp.height = 0;
    }

    private void processCallStatus(CallStatus status) {
        if (status.event == 4) {
            delayedStop();
        }
    }

    private void stop() {
        finish();
    }

    public void handleCallEvent(long status, String description) {
        this.handler.sendMessage(Message.obtain(this.handler, 1, new CallStatus(status, description)));
    }

    public void onIncomingFrame() {
        if (!this.haveValidFrame) {
            this.haveValidFrame = true;
            updateVideoUI();
        }
        this.inputVideo.requestRender();
    }

    public void onCameraFrame() {
        if (this.cameraVideoOld != null) {
            this.cameraVideoOld.requestRender();
        }
    }

    public void updateRemoteUserImage(Bitmap remoteUserImage) {
        if (remoteUserImage == null) {
            this.userPicView.setImageDrawable(null);
        } else {
            this.userPicView.setImageBitmap(remoteUserImage);
        }
    }

    public void notifyRemoteVideo(boolean remoteVideoOn) {
        updateVideoUI();
    }

    public void onUIStatusChanged() {
        this.handler.post(new Runnable() {
            public void run() {
                PhoneCallActivity.this.setUIStatus(VideochatController.instance().getUIStatus());
            }
        });
    }

    protected void onPause() {
        VideochatController.instance().suspendVideo();
        VideochatController.instance().onCallActivityPaused();
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        if (this.cameraVideoNew != null) {
            this.cameraVideoNew.setSurfaceListener(this);
        }
        if (!VideochatController.instance().callActive()) {
            finish();
        }
        VideochatController.instance().resumeVideo();
        VideochatController.instance().onCallActivityResumed();
        setUIStatus(VideochatController.instance().getUIStatus());
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        if (this.display == null) {
            this.display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        }
        int rotation = VideochatController.instance().getUIOrientation();
        if (((double) distance) < 0.0d || distance >= 5.0f || distance >= event.sensor.getMaximumRange() || rotation != 0) {
            unlockScreen();
            VideochatController.instance().speakerSwitch(true);
            return;
        }
        lockScreen();
        VideochatController.instance().speakerSwitch(false);
    }

    public void onBackPressed() {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        VideochatController.instance().setCameraPreview(holder);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        VideochatController.instance().setCameraPreview(null);
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }
}
