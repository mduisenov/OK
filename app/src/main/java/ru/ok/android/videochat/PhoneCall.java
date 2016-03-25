package ru.ok.android.videochat;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioTrack;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import com.google.android.gms.location.LocationStatusCodes;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;
import ru.ok.android.ui.custom.OnSizeChangedListener;
import ru.ok.android.utils.Logger;

public class PhoneCall {
    private boolean audioDucked;
    private long callStartTime;
    private CallThread callThread;
    private Camera camera;
    private Method cameraAddBufferMethod;
    private boolean cameraAddBufferSupported;
    private byte[] cameraFrameBuffer;
    private volatile boolean cameraFrameReady;
    private int cameraOrientation;
    private boolean cameraOrientationSet;
    private SurfaceHolder cameraPreview;
    private Method cameraSetDisplayOrientationMethod;
    private int captureHeight;
    private int captureWidth;
    private String cid;
    private boolean frontCamera;
    private boolean haveAudioFocus;
    private H264Decoder hwVideoDecoder;
    private boolean incoming;
    private RelativeLayoutWithSizeListener inputVideoView;
    private long lastCameraParamsUpdate;
    private Handler mainHandler;
    private volatile boolean muted;
    private long nativePtr;
    private boolean previewCallbackAttached;
    private int previewFormat;
    private byte[] previewFrameData;
    private boolean remoteVideoOn;
    private float remoteVideoRotation;
    private boolean remoteVideoViewVisible;
    private SoundPlaybackThread soundPlaybackThread;
    private TextureView textureView;
    private boolean videoOn;

    /* renamed from: ru.ok.android.videochat.PhoneCall.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ boolean val$newVideoOn;

        AnonymousClass10(boolean z) {
            this.val$newVideoOn = z;
        }

        public void run() {
            PhoneCall.this.nSwitchVideo(PhoneCall.this.nativePtr, this.val$newVideoOn ? 1 : 0);
        }
    }

    /* renamed from: ru.ok.android.videochat.PhoneCall.1 */
    class C14771 implements Runnable {
        final /* synthetic */ View val$vv;

        C14771(View view) {
            this.val$vv = view;
        }

        public void run() {
            this.val$vv.setVisibility(4);
        }
    }

    /* renamed from: ru.ok.android.videochat.PhoneCall.2 */
    class C14782 implements OnSizeChangedListener {
        C14782() {
        }

        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            PhoneCall.this.adjustIncomingVideoWindow();
        }
    }

    /* renamed from: ru.ok.android.videochat.PhoneCall.3 */
    class C14793 implements Runnable {
        C14793() {
        }

        public void run() {
            PhoneCall.this.doCreateTextureView();
        }
    }

    /* renamed from: ru.ok.android.videochat.PhoneCall.4 */
    class C14804 implements Runnable {
        C14804() {
        }

        public void run() {
            H264Decoder decoder = PhoneCall.this.hwVideoDecoder;
            if (decoder != null) {
                decoder.setView(null);
            }
            if (PhoneCall.this.textureView != null) {
                if (PhoneCall.this.inputVideoView != null) {
                    PhoneCall.this.inputVideoView.removeView(PhoneCall.this.textureView);
                }
                PhoneCall.this.textureView.setSurfaceTextureListener(null);
                PhoneCall.this.textureView = null;
            }
        }
    }

    /* renamed from: ru.ok.android.videochat.PhoneCall.6 */
    class C14816 implements Runnable {
        final /* synthetic */ String val$description;

        C14816(String str) {
            this.val$description = str;
        }

        public void run() {
            PhoneCall.this.close(this.val$description);
        }
    }

    /* renamed from: ru.ok.android.videochat.PhoneCall.7 */
    class C14827 implements Runnable {
        C14827() {
        }

        public void run() {
            if (PhoneCall.this.inputVideoView != null && PhoneCall.this.textureView != null && PhoneCall.this.hwVideoDecoder != null) {
                int width = PhoneCall.this.hwVideoDecoder.getWidth();
                int height = PhoneCall.this.hwVideoDecoder.getHeight();
                if (width != 0 && height != 0) {
                    int width2;
                    int rot = (int) PhoneCall.this.remoteVideoRotation;
                    if (VERSION.SDK_INT >= 11) {
                        rot %= 360;
                        if (rot < 0) {
                            rot += 360;
                        }
                        rot = (((rot + 45) / 90) * 90) % 360;
                        if (((float) rot) != PhoneCall.this.textureView.getRotation()) {
                            PhoneCall.this.textureView.setRotation((float) rot);
                        }
                    } else {
                        rot = 0;
                    }
                    LayoutParams lp = (LayoutParams) PhoneCall.this.textureView.getLayoutParams();
                    int containerWidth = PhoneCall.this.inputVideoView.getWidth();
                    int containerHeight = PhoneCall.this.inputVideoView.getHeight();
                    if (rot == 90 || rot == 270) {
                        int tmp = containerWidth;
                        containerWidth = containerHeight;
                        containerHeight = tmp;
                    }
                    if (PhoneCall.this.hwVideoDecoder != null && width > 0 && height > 0) {
                        float ratio = ((float) width) / ((float) height);
                        if (ratio > ((float) containerWidth) / ((float) containerHeight)) {
                            lp.width = containerWidth;
                            lp.height = Math.round(((float) lp.width) / ratio);
                            lp.height = Math.min(lp.height, containerHeight);
                        } else {
                            lp.height = containerHeight;
                            lp.width = Math.round(((float) lp.height) * ratio);
                            lp.width = Math.min(lp.width, containerWidth);
                        }
                    }
                    if (lp.width > PhoneCall.this.inputVideoView.getWidth()) {
                        width2 = (PhoneCall.this.inputVideoView.getWidth() - lp.width) / 2;
                        lp.rightMargin = width2;
                        lp.leftMargin = width2;
                    } else {
                        lp.rightMargin = 0;
                        lp.leftMargin = 0;
                    }
                    if (lp.height > PhoneCall.this.inputVideoView.getHeight()) {
                        width2 = (PhoneCall.this.inputVideoView.getHeight() - lp.height) / 2;
                        lp.bottomMargin = width2;
                        lp.topMargin = width2;
                    } else {
                        lp.bottomMargin = 0;
                        lp.topMargin = 0;
                    }
                    lp.addRule(13);
                    PhoneCall.this.textureView.setLayoutParams(lp);
                    PhoneCall.this.textureView.getParent().requestLayout();
                }
            }
        }
    }

    /* renamed from: ru.ok.android.videochat.PhoneCall.8 */
    class C14838 implements Runnable {
        final /* synthetic */ boolean val$show;

        C14838(boolean z) {
            this.val$show = z;
        }

        public void run() {
            if (PhoneCall.this.inputVideoView != null) {
                PhoneCall.this.doCreateTextureView();
                PhoneCall.this.adjustIncomingVideoWindow();
            } else {
                PhoneCall.this.doCreateTextureView();
                PhoneCall.this.adjustIncomingVideoWindow();
            }
        }
    }

    /* renamed from: ru.ok.android.videochat.PhoneCall.9 */
    class C14849 implements PreviewCallback {
        C14849() {
        }

        public void onPreviewFrame(byte[] data, Camera camera) {
            if (PhoneCall.this.videoOn) {
                PhoneCall.this.previewFrameData = data;
                PhoneCall.this.cameraFrameReady = true;
                return;
            }
            PhoneCall.this.cameraAddCallbackBuffer();
        }
    }

    private static class SoundPlaybackThread extends Thread {
        private PhoneCall call;
        public int sampleRate;
        public Boolean terminating;

        public SoundPlaybackThread(PhoneCall call) {
            super("SoundPlaybackThread");
            this.terminating = Boolean.valueOf(false);
            this.call = call;
        }

        public void run() {
            int bufferSize = AudioTrack.getMinBufferSize(this.sampleRate, 2, 2);
            short[] data = new short[160];
            Logger.m173d("Playback samplerate: %d", Integer.valueOf(this.sampleRate));
            try {
                AudioTrack audioTrack = new AudioTrack(0, this.sampleRate, 2, 2, bufferSize, 1);
                audioTrack.play();
                boolean playbackStopped = false;
                while (!this.terminating.booleanValue()) {
                    if (this.call.haveAudioFocus) {
                        if (playbackStopped) {
                            audioTrack.play();
                            playbackStopped = false;
                        }
                        this.call.nGetAudioData(this.call.nativePtr, data, data.length, false);
                        audioTrack.write(data, 0, data.length);
                    } else {
                        if (!playbackStopped) {
                            audioTrack.stop();
                            playbackStopped = true;
                        }
                        sleep(200);
                    }
                }
                audioTrack.stop();
                audioTrack.release();
            } catch (Throwable t) {
                Logger.m176e("Audio playback failed: " + t);
            }
            this.call = null;
        }
    }

    private static native void initVideoRenderer(long j);

    private native void nAnswer(long j);

    private native void nCloseCall(long j, String str);

    private native int nGetAudioData(long j, short[] sArr, int i, boolean z);

    private native boolean nGetH264ConfigData(long j, ByteBuffer byteBuffer);

    private native boolean nGetH264FrameData(long j, ByteBuffer byteBuffer);

    private native int nGetJitter(long j);

    private native void nInitCameraPreview(long j, int i, int i2, boolean z);

    private native void nInitIncomingVideo(long j, int i, int i2, boolean z);

    private native long nMakeCall(String str, String str2, String str3, String str4, String str5);

    private native int nProcessMessages(long j, int i);

    private native void nPushCameraFrame(long j, byte[] bArr, int i, int i2, int i3, int i4);

    private native long nReceiveCall(String str);

    private native void nRenderCameraFrame(long j);

    private native void nRenderIncomingVideo(long j);

    private native void nSetH264DecodingEnabled(long j, boolean z);

    private native void nSetMute(long j, boolean z);

    private native void nSetSpeakerSamplerate(long j, int i);

    private native void nSuspendVideo(long j, boolean z);

    private native void nSwitchVideo(long j, int i);

    public PhoneCall() {
        this.incoming = false;
        this.previewCallbackAttached = false;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.remoteVideoRotation = 0.0f;
        this.nativePtr = 0;
        this.videoOn = false;
        this.cameraFrameReady = false;
        this.frontCamera = false;
        this.cameraOrientation = 0;
        this.muted = false;
        this.haveAudioFocus = true;
        this.audioDucked = false;
        this.callStartTime = -1;
        this.cameraAddBufferSupported = true;
        this.cameraOrientationSet = false;
    }

    static {
        System.loadLibrary("odnoklassniki-android");
        initVideoRenderer(new NativeVideoRenderer().getVideoRendererFactory());
    }

    private void reinitCall() {
        close(null);
    }

    public Boolean makeCall(String addr, String from, String to, String security, String signature) {
        close(null);
        this.incoming = false;
        this.nativePtr = nMakeCall(addr, from, to, security, signature);
        if (this.nativePtr == 0) {
            return Boolean.valueOf(false);
        }
        commonCallInit();
        this.remoteVideoOn = false;
        VideochatController.instance().playSound(2131099651, -1, false);
        return Boolean.valueOf(true);
    }

    public Boolean receiveCall(String params) {
        this.incoming = true;
        close(null);
        this.nativePtr = nReceiveCall(params);
        if (this.nativePtr == 0) {
            return Boolean.valueOf(false);
        }
        commonCallInit();
        this.remoteVideoOn = false;
        return Boolean.valueOf(true);
    }

    private void commonCallInit() {
        nSetH264DecodingEnabled(this.nativePtr, false);
    }

    public Boolean processMessages(int timeoutMS) {
        boolean z = false;
        if (0 == this.nativePtr) {
            try {
                Thread.sleep((long) timeoutMS);
                return Boolean.valueOf(false);
            } catch (InterruptedException e) {
                return Boolean.valueOf(false);
            }
        }
        if (nProcessMessages(this.nativePtr, timeoutMS) != 0) {
            z = true;
        }
        Boolean result = Boolean.valueOf(z);
        pushCameraFrame();
        return result;
    }

    private void pushCameraFrame() {
        if (this.cameraFrameReady) {
            nPushCameraFrame(this.nativePtr, this.previewFrameData, this.captureWidth, this.captureHeight, getVideoRotation(), this.previewFormat == 17 ? 0 : 1);
            VideochatController.instance().onCameraFrame();
            this.cameraFrameReady = false;
            if (this.camera != null) {
                cameraAddCallbackBuffer();
                updateCameraParams();
            }
        }
    }

    private int getVideoRotation() {
        int uiRotation = VideochatController.instance().getUIOrientation();
        if (this.frontCamera) {
            return (360 - ((this.cameraOrientation + uiRotation) % 360)) % 360;
        }
        return ((uiRotation - this.cameraOrientation) + 360) % 360;
    }

    private void updateCameraParams() {
        long uptime = SystemClock.uptimeMillis();
        if (this.lastCameraParamsUpdate > uptime) {
            this.lastCameraParamsUpdate = uptime;
        }
        if (uptime - this.lastCameraParamsUpdate > 5000) {
            int jitter = getJitter();
            int targetFps = 15;
            if (jitter > LocationStatusCodes.GEOFENCE_NOT_AVAILABLE) {
                targetFps = 1;
            } else if (jitter > 500) {
                targetFps = 3;
            } else if (jitter > 300) {
                targetFps = 8;
            }
            Parameters params = this.camera.getParameters();
            if (targetFps != params.getPreviewFrameRate()) {
                Logger.m173d("jitter=%d; setting fps to %d", Integer.valueOf(jitter), Integer.valueOf(targetFps));
                params.setPreviewFrameRate(targetFps);
                try {
                    this.camera.setParameters(params);
                } catch (Throwable th) {
                    Logger.m184w("Failed to set camera framerate");
                }
            }
            this.lastCameraParamsUpdate = uptime;
        }
    }

    public void close(String reason) {
        closeHWVideoDecoder();
        closeTextureView();
        VideochatController.instance().stopSound();
        VideochatController.instance().getVibrator().cancel();
        View vv = this.inputVideoView;
        if (vv != null) {
            this.mainHandler.post(new C14771(vv));
        }
        if (this.camera != null) {
            this.camera.setPreviewCallback(null);
            this.camera.stopPreview();
            Logger.m173d("releasing camera: %s", this.camera.toString());
            this.camera.release();
            this.previewCallbackAttached = false;
            this.camera = null;
        }
        if (this.soundPlaybackThread != null) {
            this.soundPlaybackThread.terminating = Boolean.valueOf(true);
            this.soundPlaybackThread = null;
        }
        if (this.nativePtr != 0) {
            nCloseCall(this.nativePtr, reason);
            this.nativePtr = 0;
        }
    }

    public void renderInputVideoFrame() {
        if (0 != this.nativePtr && VideochatController.instance().videoSupported()) {
            nRenderIncomingVideo(this.nativePtr);
        }
    }

    public void initVideoInput(int width, int height) {
        if (0 != this.nativePtr) {
            if (VideochatController.instance().videoSupported()) {
                nInitIncomingVideo(this.nativePtr, width, height, VideochatController.instance().isGL20Supported());
                return;
            }
            suspendVideo();
        }
    }

    public void renderCameraFrame() {
        if (0 != this.nativePtr) {
            nRenderCameraFrame(this.nativePtr);
        }
    }

    public void initCameraPreview(int width, int height) {
        if (0 != this.nativePtr) {
            nInitCameraPreview(this.nativePtr, width, height, VideochatController.instance().isGL20Supported());
        }
    }

    public void setInputVideoView(RelativeLayoutWithSizeListener inputVideoView) {
        if (this.inputVideoView != inputVideoView) {
            if (this.inputVideoView != null) {
                this.inputVideoView.setSizeChangeListener(null);
            }
            this.inputVideoView = inputVideoView;
            inputVideoView.setSizeChangeListener(new C14782());
            createTextureView();
        }
    }

    private void createTextureView() {
        this.mainHandler.post(new C14793());
    }

    private void doCreateTextureView() {
        if (this.inputVideoView != null && this.textureView != null) {
        }
    }

    private void closeTextureView() {
        this.mainHandler.post(new C14804());
    }

    private void startSoundPlayback() {
        int selectedRate = 0;
        int[] sampleRates = new int[]{16000, 22050, 11025, 8000, 44100};
        for (int i = 0; i < sampleRates.length; i++) {
            if (AudioTrack.getMinBufferSize(sampleRates[i], 2, 2) > 0) {
                selectedRate = sampleRates[i];
                break;
            }
        }
        if (selectedRate == 0) {
            throw new IllegalStateException("Failed to initialize audio track");
        }
        if (this.soundPlaybackThread != null) {
            this.soundPlaybackThread.terminating = Boolean.valueOf(true);
            this.soundPlaybackThread = null;
        }
        nSetSpeakerSamplerate(this.nativePtr, selectedRate);
        this.soundPlaybackThread = new SoundPlaybackThread(this);
        this.soundPlaybackThread.sampleRate = selectedRate;
        this.soundPlaybackThread.start();
    }

    public void onIncomingFrame() {
        VideochatController.instance().onIncomingFrame();
    }

    public void onH264ConfigChanged(int size, int width, int height) {
        if (this.hwVideoDecoder != null) {
            this.hwVideoDecoder.close();
        }
        Log.w(getClass().getName(), "Received H.264 video on device that doesn't support it!");
    }

    public void onH264FrameReceived(int size, boolean key) {
        if (this.hwVideoDecoder != null) {
            Log.w(getClass().getName(), "Received H.264 video on device that doesn't support it!");
        }
    }

    public void onCallEvent(long event, String description) {
        Logger.m172d("call event: " + event + "; descr: " + description);
        VideochatController.instance().onCallEvent(event, description);
        if (event == 2) {
            if (this.incoming) {
                VideochatController.instance().playSound(2131099655, -1, true);
                vibrateOnIncoming();
                return;
            }
            VideochatController.instance().playSound(2131099648, -1, false);
        } else if (event == 3) {
            startActiveCall();
            VideochatController.instance().updateUI();
        } else if (event == 4) {
            if (this.nativePtr != 0) {
                this.callThread.execute(new C14816(description));
            }
            if (description.equals("Busy")) {
                VideochatController.instance().playSound(2131099650, 0, false);
            } else {
                VideochatController.instance().playSound(2131099652, 0, false);
            }
            vibrateOnDrop();
        }
    }

    public void onVideoRotation(float rotation) {
        this.remoteVideoRotation = rotation;
        adjustIncomingVideoWindow();
    }

    private void adjustIncomingVideoWindow() {
        if (this.inputVideoView != null && this.textureView != null && this.hwVideoDecoder != null && this.remoteVideoOn) {
            this.mainHandler.post(new C14827());
        }
    }

    private void vibrateOnDrop() {
    }

    private void vibrateOnIncoming() {
        Vibrator v = VideochatController.instance().getVibrator();
        if (VideochatController.instance().getAudioManager().shouldVibrate(0)) {
            v.vibrate(new long[]{0, 500, 2000}, 0);
        }
    }

    public void onVideoEvent(long event) {
        Logger.m172d("Video event: " + event);
        if (event == 1) {
            this.remoteVideoOn = true;
        } else if (event == 2) {
            this.remoteVideoOn = false;
        } else {
            return;
        }
        if (!this.remoteVideoOn) {
            closeTextureView();
            showRemoteVideoView(false);
            closeHWVideoDecoder();
        }
        VideochatController.instance().onRemoteVideoSwitch(this.remoteVideoOn);
    }

    private void showRemoteVideoView(boolean show) {
        this.remoteVideoViewVisible = show;
        if (this.inputVideoView != null) {
            this.mainHandler.post(new C14838(show));
        }
    }

    private void initCamera() {
        CameraSetupInterface camSetupHelper;
        Parameters params;
        if (this.camera == null) {
            try {
                Camera.class.getMethod("getNumberOfCameras", (Class[]) null);
                CameraSetupInterface camSetupHelper2 = new CameraSetupHelper();
                try {
                    camSetupHelper2.initCamera();
                    camSetupHelper = camSetupHelper2;
                } catch (Exception e) {
                    camSetupHelper = camSetupHelper2;
                    camSetupHelper = new CameraSetupHelperCompatible();
                    camSetupHelper.initCamera();
                    this.camera = camSetupHelper.getCamera();
                    this.cameraOrientation = camSetupHelper.getCameraOrientation();
                    this.frontCamera = camSetupHelper.isFrontCamera();
                    if (this.camera == null) {
                        this.cameraOrientationSet = false;
                        Logger.m173d("acquired camera: %s", this.camera.toString());
                        params = this.camera.getParameters();
                        selectCameraResolution();
                        params.setPreviewSize(this.captureWidth, this.captureHeight);
                        params.setPreviewFormat(17);
                        params.setPreviewFrameRate(15);
                        this.camera.setParameters(params);
                        this.previewFormat = this.camera.getParameters().getPreviewFormat();
                        attachCameraPreview();
                    }
                }
            } catch (Exception e2) {
                camSetupHelper = new CameraSetupHelperCompatible();
                camSetupHelper.initCamera();
                this.camera = camSetupHelper.getCamera();
                this.cameraOrientation = camSetupHelper.getCameraOrientation();
                this.frontCamera = camSetupHelper.isFrontCamera();
                if (this.camera == null) {
                    this.cameraOrientationSet = false;
                    Logger.m173d("acquired camera: %s", this.camera.toString());
                    params = this.camera.getParameters();
                    selectCameraResolution();
                    params.setPreviewSize(this.captureWidth, this.captureHeight);
                    params.setPreviewFormat(17);
                    params.setPreviewFrameRate(15);
                    this.camera.setParameters(params);
                    this.previewFormat = this.camera.getParameters().getPreviewFormat();
                    attachCameraPreview();
                }
            }
            this.camera = camSetupHelper.getCamera();
            this.cameraOrientation = camSetupHelper.getCameraOrientation();
            this.frontCamera = camSetupHelper.isFrontCamera();
            if (this.camera == null) {
                this.cameraOrientationSet = false;
                Logger.m173d("acquired camera: %s", this.camera.toString());
                params = this.camera.getParameters();
                selectCameraResolution();
                params.setPreviewSize(this.captureWidth, this.captureHeight);
                params.setPreviewFormat(17);
                params.setPreviewFrameRate(15);
                this.camera.setParameters(params);
                this.previewFormat = this.camera.getParameters().getPreviewFormat();
                attachCameraPreview();
            }
        }
    }

    private void attachCameraPreview() {
        try {
            if (this.camera != null) {
                if (this.cameraPreview != null) {
                    if (this.cameraSetDisplayOrientationMethod == null) {
                        this.cameraSetDisplayOrientationMethod = Camera.class.getMethod("setDisplayOrientation", new Class[]{Integer.TYPE});
                    }
                    if (this.cameraPreview.getSurface() != null) {
                        this.camera.stopPreview();
                        if (!this.cameraOrientationSet) {
                            this.cameraOrientationSet = true;
                            this.cameraSetDisplayOrientationMethod.invoke(this.camera, new Object[]{Integer.valueOf(getVideoRotation())});
                        }
                        this.camera.setPreviewDisplay(this.cameraPreview);
                        setPreviewCallback(true);
                        this.camera.startPreview();
                        return;
                    }
                }
                setPreviewCallback(false);
                this.camera.setPreviewDisplay(null);
                this.cameraOrientationSet = false;
            }
        } catch (Exception ex) {
            Log.e(getClass().getName(), "Failed to attach camera preview", ex);
            if (this.camera != null) {
                this.camera.release();
                this.previewCallbackAttached = false;
                this.camera = null;
            }
        }
    }

    private void selectCameraResolution() {
        Object[] objArr;
        Parameters cameraParams = this.camera.getParameters();
        int maxRes = Math.max(320, 224);
        try {
            Method m = Parameters.class.getMethod("getSupportedPreviewSizes", (Class[]) null);
            objArr = new Object[0];
            long minimumPixelCount = 0;
            int optimalWidth = 0;
            int optimalHeight = 0;
            for (Size s : (List) m.invoke(this.camera.getParameters(), objArr)) {
                int i = s.width;
                if (r0 >= maxRes) {
                    i = s.height;
                    if (r0 >= maxRes) {
                        long pixelCount = (long) (s.height * s.width);
                        if (optimalWidth == 0 || optimalHeight == 0 || pixelCount < minimumPixelCount) {
                            minimumPixelCount = pixelCount;
                            optimalHeight = s.height;
                            optimalWidth = s.width;
                        }
                    }
                }
            }
            this.captureHeight = optimalHeight;
            this.captureWidth = optimalWidth;
        } catch (Exception e) {
            cameraParams.setPreviewSize(maxRes, maxRes);
            this.camera.setParameters(cameraParams);
            Size size = this.camera.getParameters().getPreviewSize();
            this.captureHeight = size.height;
            this.captureWidth = size.width;
        }
        objArr = new Object[2];
        objArr[0] = Integer.valueOf(this.captureWidth);
        objArr[1] = Integer.valueOf(this.captureHeight);
        Logger.m173d("Camera capture resolution: %dx%d", objArr);
    }

    private Boolean startVideoCapture(boolean customRender) {
        initCamera();
        if (this.camera == null) {
            return Boolean.valueOf(false);
        }
        return Boolean.valueOf(true);
    }

    private void setPreviewCallback(boolean attach) {
        if (this.previewCallbackAttached != attach) {
            this.previewCallbackAttached = attach;
            if (attach) {
                PreviewCallback callback = new C14849();
                try {
                    Camera.class.getMethod("setPreviewCallbackWithBuffer", new Class[]{PreviewCallback.class}).invoke(this.camera, new Object[]{callback});
                } catch (Exception e) {
                    this.cameraAddBufferSupported = false;
                    this.camera.setPreviewCallback(callback);
                }
                cameraAddCallbackBuffer();
                return;
            }
            this.camera.setPreviewCallback(null);
        }
    }

    private void closeHWVideoDecoder() {
        if (this.hwVideoDecoder != null) {
            this.hwVideoDecoder.close();
            this.hwVideoDecoder = null;
        }
    }

    private void cameraAddCallbackBuffer() {
        if (this.cameraAddBufferSupported) {
            if (this.cameraAddBufferMethod == null) {
                try {
                    this.cameraAddBufferMethod = Camera.class.getMethod("addCallbackBuffer", new Class[]{byte[].class});
                } catch (Exception ex) {
                    Logger.m172d("Failed to add callback buffer: " + ex);
                    this.cameraAddBufferSupported = false;
                    return;
                }
            }
            if (this.camera != null && this.cameraAddBufferMethod != null) {
                if (this.cameraFrameBuffer == null) {
                    if (17 == this.previewFormat) {
                        this.cameraFrameBuffer = new byte[(((this.captureWidth * this.captureHeight) * 3) / 2)];
                    } else {
                        this.cameraFrameBuffer = new byte[((this.captureWidth * this.captureHeight) * 2)];
                    }
                }
                try {
                    this.cameraAddBufferMethod.invoke(this.camera, new Object[]{this.cameraFrameBuffer});
                } catch (Exception ex2) {
                    Logger.m177e("Failed to add callback buffer: ", ex2);
                }
            }
        }
    }

    public void switchVideo(boolean customRender) {
        boolean newVideoOn;
        if (this.videoOn) {
            newVideoOn = false;
        } else {
            newVideoOn = true;
        }
        if (0 != this.nativePtr) {
            this.callThread.execute(new AnonymousClass10(newVideoOn));
        }
        if (newVideoOn) {
            if (this.camera == null) {
                startVideoCapture(customRender);
            }
            if (this.camera == null) {
                this.videoOn = false;
                throw new RuntimeException("Failed to start video");
            } else if (customRender) {
                this.camera.startPreview();
                setPreviewCallback(true);
            } else {
                attachCameraPreview();
            }
        } else if (this.camera != null) {
            this.camera.stopPreview();
        }
        this.videoOn = newVideoOn;
    }

    public void updateVideo(boolean customRender) {
        if (this.videoOn && this.camera != null && !customRender) {
            this.cameraOrientationSet = false;
            attachCameraPreview();
        }
    }

    public void switchMute() {
        this.muted = !this.muted;
        nSetMute(this.nativePtr, this.muted);
    }

    public boolean isMuted() {
        return this.muted;
    }

    public Boolean isVideoOn() {
        return Boolean.valueOf(this.videoOn);
    }

    public void setCallThread(CallThread thread) {
        this.callThread = thread;
    }

    public boolean isRemoteVideoOn() {
        return this.remoteVideoOn;
    }

    public void suspendVideo() {
        if (this.nativePtr != 0 && this.callThread != null) {
            this.callThread.execute(new Runnable() {
                public void run() {
                    PhoneCall.this.nSuspendVideo(PhoneCall.this.nativePtr, true);
                }
            });
        }
    }

    public void resumeVideo() {
        if (this.nativePtr != 0 && this.callThread != null) {
            this.callThread.execute(new Runnable() {
                public void run() {
                    PhoneCall.this.nSuspendVideo(PhoneCall.this.nativePtr, false);
                }
            });
        }
    }

    public void notifyAudioFocusGain() {
        this.haveAudioFocus = true;
    }

    public void notifyAudioFocusLoss() {
    }

    public void duckAudio() {
        this.audioDucked = true;
    }

    public long getCallStartTime() {
        return this.callStartTime;
    }

    public int getJitter() {
        return nGetJitter(this.nativePtr);
    }

    public void answer() {
        if (this.nativePtr != 0) {
            this.callThread.execute(new Runnable() {
                public void run() {
                    PhoneCall.this.nAnswer(PhoneCall.this.nativePtr);
                }
            });
        }
    }

    private void startActiveCall() {
        VideochatController.instance().stopSound();
        VideochatController.instance().getVibrator().cancel();
        VideochatController.instance().notifyAudioStarted();
        this.callStartTime = SystemClock.elapsedRealtime();
        startSoundPlayback();
    }

    public void notifySpeakerPhoneOn(boolean on) {
    }

    public void setCameraPreview(SurfaceHolder surface) {
        this.cameraPreview = surface;
        attachCameraPreview();
    }

    public int getCaptureWidth() {
        if (this.cameraOrientation == 90 || this.cameraOrientation == 270) {
            return this.captureHeight;
        }
        return this.captureWidth;
    }

    public int getCaptureHeight() {
        if (this.cameraOrientation == 90 || this.cameraOrientation == 270) {
            return this.captureWidth;
        }
        return this.captureHeight;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCid() {
        return this.cid;
    }
}
