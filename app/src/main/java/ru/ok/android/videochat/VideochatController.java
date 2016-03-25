package ru.ok.android.videochat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.widget.RemoteViews;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.http.HttpHost;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.client.HttpClient;
import ru.ok.android.http.client.HttpResponseException;
import ru.ok.android.http.client.methods.HttpGet;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.http.support.v1.SupportHttpClients;
import ru.ok.android.http.util.EntityUtils;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.activity.PhoneCallActivity;
import ru.ok.android.ui.activity.RequestPermissionsActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.java.api.ApiHttpRequestBuilder;
import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.java.api.json.JsonResultGetPictureParser;
import ru.ok.java.api.request.GetUserPictureRequest;
import ru.ok.java.api.request.TouchSessionRequest;
import ru.ok.java.api.utils.Constants.Api;

public class VideochatController {
    private static String disconnectReason;
    private static VideochatController instance;
    Object audioFocusHelper;
    private AudioManager audioManager;
    private final int audioManagerMode;
    private PhoneCallActivity callActivity;
    private CallThread callThread;
    private Context context;
    PhoneCall currentCall;
    private Boolean gl20Supported;
    private BroadcastReceiver headsetPlugEventReceiver;
    private boolean incoming;
    int loop;
    private NotificationManager notificationManager;
    boolean pendingRingSound;
    private VideoChatStateListener phoneStateListener;
    private String remoteUid;
    private Bitmap remoteUserImage;
    int soundId;
    private SoundPool soundPool;
    private SoundPool soundPoolRing;
    private boolean speakerphoneOn;
    int streamId;
    private int uiStatus;
    private String userName;
    private String userPicUrl;
    private Boolean videoSupported;

    public interface VideoChatStateListener {
        void onCallStateChanged(int i);
    }

    /* renamed from: ru.ok.android.videochat.VideochatController.1 */
    class C14851 extends BroadcastReceiver {
        C14851() {
        }

        public void onReceive(Context context, Intent intent) {
            String name = intent.getExtras().getString("name");
            Integer state = Integer.valueOf(intent.getExtras().getInt("state"));
            if (name != null && state != null) {
                boolean z;
                String str = "Headset plug event: state=%d(%s) name=\"%s\"";
                Object[] objArr = new Object[3];
                objArr[0] = state;
                objArr[1] = state.intValue() == 0 ? "unplugged" : "plugged";
                objArr[2] = name;
                Logger.m173d(str, objArr);
                VideochatController videochatController = VideochatController.this;
                if (state.intValue() != 0) {
                    z = true;
                } else {
                    z = false;
                }
                videochatController.updateAudioRoute(Boolean.valueOf(z));
            }
        }
    }

    /* renamed from: ru.ok.android.videochat.VideochatController.2 */
    class C14862 implements OnLoadCompleteListener {
        C14862() {
        }

        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            Logger.m173d("SoundPool: load complete: sampleId=%d status=%d soundId=%d", Integer.valueOf(sampleId), Integer.valueOf(status), Integer.valueOf(VideochatController.this.soundId));
            if (status == 0 && !VideochatController.this.pendingRingSound && VideochatController.this.soundId == sampleId) {
                VideochatController.this.streamId = soundPool.play(VideochatController.this.soundId, 1.0f, 1.0f, 0, VideochatController.this.loop, 1.0f);
            }
        }
    }

    /* renamed from: ru.ok.android.videochat.VideochatController.3 */
    class C14873 implements OnLoadCompleteListener {
        C14873() {
        }

        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            Logger.m173d("SoundPoolRing: load complete: sampleId=%d status=%d soundId=%d", Integer.valueOf(sampleId), Integer.valueOf(status), Integer.valueOf(VideochatController.this.soundId));
            if (status == 0 && VideochatController.this.pendingRingSound && VideochatController.this.soundId == sampleId) {
                VideochatController.this.streamId = VideochatController.this.soundPoolRing.play(VideochatController.this.soundId, 1.0f, 1.0f, 0, VideochatController.this.loop, 1.0f);
            }
        }
    }

    /* renamed from: ru.ok.android.videochat.VideochatController.4 */
    class C14884 extends AsyncTask<String, Void, Void> {
        C14884() {
        }

        protected Void doInBackground(String... params) {
            try {
                String srv = params[0];
                JSONObject p = new JSONObject(params[1]);
                Logger.m173d("Busy url: %s", String.format("http://%s/api-call-signal?pid=%s&sid=%s&cid=%s&cid_sig=%s&type=busy", new Object[]{srv, p.getString("called_id"), p.getString("sid"), p.getString("cid"), p.getString("cid_sig")}));
                HttpClient client = SupportHttpClients.createMinimal();
                HttpGet request = new HttpGet(busyUrl);
                HttpHost host = VideochatController.getHostForRequest(request);
                if (host != null) {
                    client.execute(host, request);
                } else {
                    client.execute(request);
                }
            } catch (Exception e) {
                Logger.m184w("Failed to send busy signal: " + e + ": " + Log.getStackTraceString(e));
            }
            return null;
        }
    }

    /* renamed from: ru.ok.android.videochat.VideochatController.5 */
    class C14895 extends AsyncTask<String, Void, String> {
        private String uid;

        C14895() {
        }

        protected String doInBackground(String... params) {
            this.uid = params[0];
            try {
                return (String) new JsonResultGetPictureParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GetUserPictureRequest(this.uid, false))).parse();
            } catch (Exception ex) {
                Logger.m177e("Failed to get user picture: %s", ex);
                return null;
            }
        }

        protected void onPostExecute(String url) {
            if (url != null && this.uid.equals(VideochatController.this.remoteUid)) {
                VideochatController.this.setUserPic(url);
            }
        }
    }

    /* renamed from: ru.ok.android.videochat.VideochatController.6 */
    class C14906 extends AsyncTask<String, Void, Bitmap> {
        private String url;

        C14906() {
        }

        protected Bitmap doInBackground(String... params) {
            this.url = params[0];
            return VideochatController.getImageBitmap(this.url);
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (this.url.equals(VideochatController.this.userPicUrl)) {
                VideochatController.this.remoteUserImage = bitmap;
                VideochatController.this.notifyUserImageUpdate();
            }
        }
    }

    /* renamed from: ru.ok.android.videochat.VideochatController.7 */
    class C14927 extends AsyncTask<String, Void, String> {
        String server;
        String userName;

        /* renamed from: ru.ok.android.videochat.VideochatController.7.1 */
        class C14911 implements Runnable {
            final /* synthetic */ String val$params;

            C14911(String str) {
                this.val$params = str;
            }

            public void run() {
                VideochatController.instance().receiveCall(C14927.this.server, C14927.this.userName, this.val$params);
            }
        }

        C14927() {
        }

        protected String doInBackground(String... params) {
            if (VideochatController.this.loginIfNeeded(false)) {
                this.server = params[0];
                String cid = params[1];
                this.userName = params[2];
                try {
                    HttpResponse response;
                    HttpClient client = SupportHttpClients.createMinimal();
                    ServiceStateHolder stateHolder = JsonSessionTransportProvider.getInstance().getStateHolder();
                    HttpUriRequest request = new ApiHttpRequestBuilder(stateHolder, HttpMethodType.GET).setTargetUrl(new URI("http://" + this.server + "/")).addRelativePath("api-get-signal").addSignedParam("uid", stateHolder.getUserId(), false).addSignedParam("cid", cid, false).addSignedParam("client", Api.CLIENT_NAME, false).build();
                    Logger.m172d("Get signal, url=" + request.getURI());
                    HttpHost host = VideochatController.getHostForRequest(request);
                    if (host != null) {
                        response = client.execute(host, request);
                    } else {
                        response = client.execute(request);
                    }
                    int code = response.getStatusLine().getStatusCode();
                    if (code != 200) {
                        Logger.m176e("Signal request failed: code " + code);
                        throw new HttpResponseException(code, response.getStatusLine().getReasonPhrase());
                    }
                    String responseString = EntityUtils.toString(response.getEntity());
                    Logger.m173d("Signal response: %s", response);
                    return responseString;
                } catch (Exception e) {
                    Logger.m176e("Failed to get signal: " + e + ":" + Log.getStackTraceString(e));
                    return null;
                }
            }
            Logger.m176e("Login failed - call will be rejected");
            return null;
        }

        protected void onPostExecute(String params) {
            if (params != null) {
                ThreadUtil.executeOnMain(new C14911(params));
            }
        }
    }

    /* renamed from: ru.ok.android.videochat.VideochatController.8 */
    class C14938 extends AsyncTask<Void, Void, Void> {
        C14938() {
        }

        protected Void doInBackground(Void... params) {
            VideochatController.this.loginIfNeeded(true);
            return null;
        }
    }

    protected static class CallThread extends Thread {
        private PhoneCall call;
        public String dropReason;
        LinkedList<Runnable> taskList;
        public Boolean terminating;

        public CallThread(PhoneCall call) {
            super("CallThread");
            this.terminating = Boolean.valueOf(false);
            this.taskList = new LinkedList();
            this.call = call;
        }

        public void execute(Runnable task) {
            synchronized (this.taskList) {
                this.taskList.add(task);
            }
        }

        public void run() {
            this.call.setCallThread(this);
            while (!this.terminating.booleanValue()) {
                if (!this.call.processMessages(50).booleanValue()) {
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
                LinkedList<Runnable> currentTasks = null;
                synchronized (this.taskList) {
                    if (!this.taskList.isEmpty()) {
                        currentTasks = (LinkedList) this.taskList.clone();
                        this.taskList.clear();
                    }
                }
                if (currentTasks != null) {
                    Iterator i$ = currentTasks.iterator();
                    while (i$.hasNext()) {
                        ((Runnable) i$.next()).run();
                    }
                }
            }
            this.call.close(this.dropReason);
            this.call = null;
        }
    }

    private static class CameraPreviewRenderer implements android.opengl.GLSurfaceView.Renderer {
        private PhoneCall call;

        public CameraPreviewRenderer(PhoneCall call) {
            this.call = call;
        }

        public void onDrawFrame(GL10 gl) {
            if (this.call != null) {
                this.call.renderCameraFrame();
            }
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            if (this.call != null) {
                this.call.initCameraPreview(width, height);
            }
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        }
    }

    private static final class MakeCallCallback implements OnRequestPermissionsResultCallback {
        private final String appId;
        private final String disp;
        private final String from;
        private final String signature;
        private final String to;
        private final String userName;
        private final String userPic;

        public MakeCallCallback(String from, String to, String appId, String disp, String userName, String userPic, String signature) {
            this.from = from;
            this.to = to;
            this.appId = appId;
            this.disp = disp;
            this.userName = userName;
            this.userPic = userPic;
            this.signature = signature;
        }

        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (PermissionUtils.getGrantResult(grantResults) == 0) {
                VideochatController.instance().makeCall(this.from, this.to, this.appId, this.disp, this.userName, this.userPic, this.signature);
            }
        }
    }

    private static class Renderer implements android.opengl.GLSurfaceView.Renderer {
        private PhoneCall call;

        public Renderer(PhoneCall call) {
            this.call = call;
        }

        public void onDrawFrame(GL10 gl) {
            if (this.call != null) {
                this.call.renderInputVideoFrame();
            }
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            if (this.call != null) {
                this.call.initVideoInput(width, height);
            }
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        }
    }

    private VideochatController() {
        int i = 3;
        this.soundPool = new SoundPool(1, 3, 0);
        this.soundPoolRing = new SoundPool(1, 2, 0);
        if (VERSION.SDK_INT < 11) {
            i = 0;
        }
        this.audioManagerMode = i;
        this.gl20Supported = null;
        this.headsetPlugEventReceiver = new C14851();
        setAppContext(OdnoklassnikiApplication.getContext());
    }

    private void checkInitSoundPool() {
        this.soundPool.setOnLoadCompleteListener(new C14862());
        this.soundPoolRing.setOnLoadCompleteListener(new C14873());
    }

    private void setAppContext(Context context) {
        if (context != this.context) {
            if (this.context != null) {
                this.context.unregisterReceiver(this.headsetPlugEventReceiver);
            }
            this.context = context;
            this.notificationManager = (NotificationManager) context.getSystemService("notification");
            this.audioManager = (AudioManager) context.getSystemService("audio");
            context.registerReceiver(this.headsetPlugEventReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        }
    }

    public static VideochatController instance() {
        if (instance == null) {
            instance = new VideochatController();
        }
        return instance;
    }

    public void processOutgoingCall(String from, String to, String appId, String disp, String userName, String userPic, String signature) {
        if (this.currentCall != null) {
            Logger.m184w("Attempt to make call while another call is active");
            startPhoneCallActivity(true);
            return;
        }
        if (PermissionUtils.checkSelfPermission(this.context, "android.permission.RECORD_AUDIO") == 0) {
            makeCall(from, to, appId, disp, userName, userPic, signature);
            return;
        }
        MakeCallCallback callback = new MakeCallCallback(from, to, appId, disp, userName, userPic, signature);
        this.context.startActivity(RequestPermissionsActivity.createRequestPermissionsIntent(this.context, new String[]{"android.permission.RECORD_AUDIO"}, 0, callback).addFlags(268435456));
    }

    public void makeCall(String from, String to, String appId, String disp, String userName, String userPic, String signature) {
        try {
            this.incoming = false;
            setUserName(userName);
            setUIState(1);
            startPhoneCallActivity(false);
            commonCallInit(null);
            this.currentCall.makeCall(disp, from, to, appId, signature);
            if (this.currentCall != null) {
                this.callThread.start();
                setUserPic(userPic);
            }
            initCallAudio();
        } catch (Exception e) {
            stopCall();
            Logger.m176e("Failed to create call: " + e + ": " + Log.getStackTraceString(e));
        }
    }

    public void receiveCall(String server, String userName, String params) {
        Exception ex;
        String callerUidField;
        JSONObject parsedParams = null;
        String cid = null;
        try {
            JSONObject parsedParams2 = new JSONObject(params);
            try {
                String cidField = "cid";
                if (parsedParams2.has("cid")) {
                    cid = parsedParams2.getString("cid");
                }
                parsedParams = parsedParams2;
            } catch (Exception e) {
                ex = e;
                parsedParams = parsedParams2;
                Logger.m177e("Failed to get cid: %s", ex);
                if (this.currentCall == null) {
                    if (cid != null) {
                    }
                    sendBusy(server, params);
                }
                this.incoming = true;
                setUserName(userName);
                setUIState(5);
                showCallNotification();
                startPhoneCallActivity(false);
                commonCallInit(cid);
                this.currentCall.receiveCall(params);
                if (this.currentCall != null) {
                    this.callThread.start();
                    if (parsedParams != null) {
                        try {
                            callerUidField = "caller_enc";
                            if (parsedParams.has("caller_enc")) {
                                getImageForUid(parsedParams.getString("caller_enc"));
                            }
                        } catch (Exception ex2) {
                            Logger.m177e("Failed to get user picture: %s", ex2);
                        }
                    }
                }
                initCallAudio();
            }
        } catch (Exception e2) {
            ex2 = e2;
            Logger.m177e("Failed to get cid: %s", ex2);
            if (this.currentCall == null) {
                this.incoming = true;
                setUserName(userName);
                setUIState(5);
                showCallNotification();
                startPhoneCallActivity(false);
                commonCallInit(cid);
                this.currentCall.receiveCall(params);
                if (this.currentCall != null) {
                    this.callThread.start();
                    if (parsedParams != null) {
                        callerUidField = "caller_enc";
                        if (parsedParams.has("caller_enc")) {
                            getImageForUid(parsedParams.getString("caller_enc"));
                        }
                    }
                }
                initCallAudio();
            }
            if (cid != null) {
            }
            sendBusy(server, params);
        }
        try {
            if (this.currentCall == null) {
                this.incoming = true;
                setUserName(userName);
                setUIState(5);
                showCallNotification();
                startPhoneCallActivity(false);
                commonCallInit(cid);
                this.currentCall.receiveCall(params);
                if (this.currentCall != null) {
                    this.callThread.start();
                    if (parsedParams != null) {
                        callerUidField = "caller_enc";
                        if (parsedParams.has("caller_enc")) {
                            getImageForUid(parsedParams.getString("caller_enc"));
                        }
                    }
                }
                initCallAudio();
            } else if (cid != null || !cid.equals(this.currentCall.getCid())) {
                sendBusy(server, params);
            }
        } catch (Exception e3) {
            stopCall();
            Logger.m176e("Failed to receive call: " + e3 + ": " + Log.getStackTraceString(e3));
            sendBusy(server, params);
        }
    }

    private void sendBusy(String server, String params) {
        new C14884().execute(new String[]{server, params});
    }

    private void getImageForUid(String uid) {
        this.remoteUid = uid;
        new C14895().execute(new String[]{uid});
    }

    private void initCallAudio() {
        this.speakerphoneOn = true;
        updateAudioRoute(null);
        requestAudioFocus();
        this.currentCall.notifyAudioFocusGain();
    }

    private void commonCallInit(String cid) {
        if (this.currentCall != null) {
            Logger.m184w("Attempt to make call while another call is active");
            throw new RuntimeException("Cannot init call - another call is active");
        }
        disconnectReason = null;
        if (this.audioFocusHelper == null) {
            this.audioFocusHelper = getAudioFocusChangeListener();
        }
        stopCall();
        this.currentCall = new PhoneCall();
        this.currentCall.setCid(cid);
        this.callThread = new CallThread(this.currentCall);
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    private void setUIState(int uiStatus) {
        this.uiStatus = uiStatus;
        if (this.callActivity != null) {
            this.callActivity.onUIStatusChanged();
        }
    }

    private void setUserPic(String userPic) {
        this.userPicUrl = userPic;
        if (this.userPicUrl.equalsIgnoreCase("m")) {
            this.remoteUserImage = BitmapFactory.decodeResource(this.context.getResources(), 2130838321);
            notifyUserImageUpdate();
        } else if (this.userPicUrl.equalsIgnoreCase("f")) {
            this.remoteUserImage = BitmapFactory.decodeResource(this.context.getResources(), 2130837927);
            notifyUserImageUpdate();
        } else {
            new C14906().execute(new String[]{this.userPicUrl});
        }
    }

    private void notifyUserImageUpdate() {
        if (this.callActivity != null) {
            this.callActivity.updateRemoteUserImage(this.remoteUserImage);
        }
    }

    private void startPhoneCallActivity(boolean showBusyMessage) {
        Intent intent = new Intent(this.context, PhoneCallActivity.class).addFlags(4);
        intent.addFlags(268435456);
        intent.putExtra("BUSY_MESSAGE", showBusyMessage);
        this.context.startActivity(intent);
    }

    public void stopCall() {
        this.context.sendBroadcast(new Intent("ru.odnoklassniki.android.videochat.STOP_CALL"));
        if (this.callThread != null) {
            this.callThread.terminating = Boolean.valueOf(true);
            this.callThread = null;
        }
        this.currentCall = null;
        this.userPicUrl = null;
        if (this.notificationManager != null) {
            this.notificationManager.cancel(2131165320);
        }
        abandonAudioFocus();
        this.audioManager.setMode(0);
        this.speakerphoneOn = false;
        stopSound();
        speakerSwitch(false);
        this.remoteUid = null;
        this.remoteUserImage = null;
        updateAudioRoute(null);
    }

    public void onUserClosed() {
        if (this.callThread != null) {
            this.callThread.dropReason = "hangup";
        }
        stopCall();
    }

    public void setActivity(PhoneCallActivity activity) {
        if (!(this.callActivity == null || this.callActivity == activity)) {
            this.callActivity.finish();
        }
        this.callActivity = activity;
    }

    public void listen(VideoChatStateListener phoneStateListener) {
        this.phoneStateListener = phoneStateListener;
    }

    public void onCallEvent(long event, String description) {
        if (event == 1) {
            if (this.incoming) {
                setUIState(5);
            } else {
                setUIState(1);
            }
            if (this.phoneStateListener != null) {
                this.phoneStateListener.onCallStateChanged(1);
            }
        } else if (event == 2) {
            if (this.incoming) {
                this.audioManager.setMode(1);
                setUIState(5);
            } else {
                setUIState(2);
            }
            if (this.phoneStateListener != null) {
                this.phoneStateListener.onCallStateChanged(2);
            }
        } else if (event == 3) {
            this.audioManager.setMode(this.audioManagerMode);
            setUIState(3);
            if (this.phoneStateListener != null) {
                this.phoneStateListener.onCallStateChanged(3);
            }
        } else if (event == 4) {
            if (disconnectReason == null && description != null) {
                disconnectReason = description;
            }
            stopCall();
            setUIState(4);
            if (this.phoneStateListener != null) {
                this.phoneStateListener.onCallStateChanged(4);
            }
        }
        if (this.callActivity != null) {
            this.callActivity.handleCallEvent(event, description);
        }
    }

    public Renderer getInputVideoRenderer() {
        if (this.currentCall != null) {
            return new Renderer(this.currentCall);
        }
        return null;
    }

    public CameraPreviewRenderer getCameraPreviewRenderer() {
        if (this.currentCall != null) {
            return new CameraPreviewRenderer(this.currentCall);
        }
        return null;
    }

    public void onIncomingFrame() {
        if (videoSupported() && this.callActivity != null) {
            this.callActivity.onIncomingFrame();
        }
    }

    public void onCameraFrame() {
        if (this.callActivity != null && this.currentCall != null) {
            this.callActivity.onCameraFrame();
        }
    }

    public void switchVideo(boolean customRender) {
        if (this.currentCall != null) {
            this.currentCall.switchVideo(customRender);
        }
    }

    public void updateVideo(boolean customRender) {
        if (this.currentCall != null) {
            this.currentCall.updateVideo(customRender);
        }
    }

    public Boolean isVideoOn() {
        if (this.currentCall == null) {
            return Boolean.valueOf(false);
        }
        return this.currentCall.isVideoOn();
    }

    public int getUIOrientation() {
        if (this.callActivity != null) {
            try {
                switch (((Integer) Display.class.getMethod("getRotation", (Class[]) null).invoke(this.callActivity.getWindowManager().getDefaultDisplay(), (Object[]) null)).intValue()) {
                    case RECEIVED_VALUE:
                        return 0;
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        return 90;
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        return 180;
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        return 270;
                }
            } catch (Exception e) {
                Logger.m172d("Failed to get display orientation");
                return 0;
            }
        }
        return 0;
    }

    public void switchMute() {
        if (this.currentCall != null) {
            this.currentCall.switchMute();
        }
    }

    public boolean isMuted() {
        if (this.currentCall != null) {
            return this.currentCall.isMuted();
        }
        return false;
    }

    private static Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URLConnection conn = new URL(url).openConnection(NetUtils.getProxyForUrl(url));
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
            return bm;
        } catch (IOException e) {
            Logger.m177e("Error getting bitmap ", e);
            return bm;
        }
    }

    public Bitmap getRemoteUserImage() {
        return this.remoteUserImage;
    }

    public boolean isRemoteVideoOn() {
        if (this.currentCall == null) {
            return false;
        }
        return this.currentCall.isRemoteVideoOn();
    }

    public void onRemoteVideoSwitch(boolean remoteVideoOn) {
        if (this.callActivity != null) {
            this.callActivity.notifyRemoteVideo(remoteVideoOn);
        }
    }

    public void playSound(int resId, int loop, boolean ring) {
        checkInitSoundPool();
        this.loop = loop;
        stopSound();
        String str = "SoundPool: Playing sound: id=%d loop=%d, ring=%s";
        Object[] objArr = new Object[3];
        objArr[0] = Integer.valueOf(resId);
        objArr[1] = Integer.valueOf(loop);
        objArr[2] = ring ? "true" : "false";
        Logger.m173d(str, objArr);
        this.pendingRingSound = ring;
        if (ring) {
            this.soundId = this.soundPoolRing.load(this.context.getApplicationContext(), resId, 1);
        } else {
            this.soundId = this.soundPool.load(this.context.getApplicationContext(), resId, 1);
        }
    }

    public void stopSound() {
        checkInitSoundPool();
        Logger.m172d("SoundPool: Stop sound");
        if (this.streamId != 0) {
            this.soundPool.stop(this.streamId);
            this.soundPoolRing.stop(this.streamId);
        }
        if (this.soundId != 0) {
            this.soundPool.unload(this.soundId);
            this.soundPoolRing.unload(this.soundId);
        }
    }

    public Vibrator getVibrator() {
        return (Vibrator) this.context.getApplicationContext().getSystemService("vibrator");
    }

    public int getUIStatus() {
        return this.uiStatus;
    }

    public void suspendVideo() {
        if (this.currentCall != null && this.callThread != null) {
            this.currentCall.suspendVideo();
        }
    }

    public void resumeVideo() {
        if (this.currentCall != null && this.callThread != null && videoSupported()) {
            this.currentCall.resumeVideo();
        }
    }

    public void onCallActivityPaused() {
        showCallNotification();
    }

    private void showCallNotification() {
        if (this.currentCall != null) {
            CharSequence from = this.userName;
            CharSequence message = this.context.getText(2131165320);
            Intent intent = new Intent(this.context, PhoneCallActivity.class);
            AppLaunchLog.fillLocalVideoCall(intent);
            PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
            PendingIntent endCallIntent = PendingIntent.getBroadcast(this.context, 0, new Intent("ru.odnoklassniki.android.videochat.END_CALL"), 0);
            Notification notif = new Builder(this.context).setSmallIcon(2130838499).setContentText(this.userName).setWhen(System.currentTimeMillis()).setContentTitle(from).setContentText(message).setContentIntent(contentIntent).build();
            notif.flags = 50;
            RemoteViews notificationContentView = new RemoteViews(this.context.getPackageName(), 2130903075);
            notificationContentView.setTextViewText(2131624504, this.context.getText(2131165320));
            long callStartTime = this.currentCall.getCallStartTime();
            if (callStartTime > 0) {
                notificationContentView.setChronometer(2131624503, callStartTime, this.userName + " %s", true);
            } else {
                notificationContentView.setTextViewText(2131624503, this.userName);
            }
            notificationContentView.setOnClickPendingIntent(2131624505, endCallIntent);
            notificationContentView.setViewVisibility(2131624505, 8);
            notif.contentView = notificationContentView;
            if (this.notificationManager != null) {
                this.notificationManager.notify(2131165320, notif);
            }
        }
    }

    public long getCallStartTime() {
        if (this.currentCall == null) {
            return -1;
        }
        return this.currentCall.getCallStartTime();
    }

    public void onCallActivityResumed() {
        requestAudioFocus();
        if (this.currentCall != null) {
            this.currentCall.notifyAudioFocusGain();
        }
        if (this.notificationManager != null) {
            this.notificationManager.cancel(2131165320);
        }
    }

    private void requestAudioFocus() {
        if (this.audioFocusHelper != null) {
            try {
                this.audioFocusHelper.getClass().getMethod("requestAudioFocus", new Class[]{this.audioManager.getClass()}).invoke(this.audioFocusHelper, new Object[]{this.audioManager});
            } catch (Exception e) {
                Logger.m172d("Failed to request audio focus");
            }
        }
    }

    private void abandonAudioFocus() {
        if (this.audioFocusHelper != null) {
            try {
                this.audioFocusHelper.getClass().getMethod("abandonAudioFocus", new Class[]{this.audioManager.getClass()}).invoke(this.audioFocusHelper, new Object[]{this.audioManager});
            } catch (Exception e) {
                Logger.m172d("Failed to abandon audio focus");
            }
        }
    }

    public boolean callActive() {
        return this.currentCall != null;
    }

    public boolean isGL20Supported() {
        if (this.gl20Supported == null) {
            this.gl20Supported = Boolean.valueOf(checkGL20Support());
        }
        return this.gl20Supported.booleanValue();
    }

    public void forceGL10() {
        this.gl20Supported = Boolean.valueOf(false);
    }

    public boolean videoSupported() {
        if (this.videoSupported != null) {
            return this.videoSupported.booleanValue();
        }
        Boolean valueOf = Boolean.valueOf(glRequirementsMet());
        this.videoSupported = valueOf;
        return valueOf.booleanValue();
    }

    private static boolean checkGL20Support() {
        boolean supports20 = true;
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(display, new int[2]);
        int[] num_config = new int[1];
        egl.eglChooseConfig(display, new int[]{12324, 4, 12323, 4, 12322, 4, 12352, 4, 12344}, new EGLConfig[10], 10, num_config);
        egl.eglTerminate(display);
        while (egl.eglGetError() != 12288) {
            Logger.m176e(String.format("eglChooseConfig: EGL error: 0x%x", new Object[]{Integer.valueOf(egl.eglGetError())}));
        }
        if (num_config[0] <= 0) {
            supports20 = false;
        }
        Logger.m172d("OpenGL 2.0 support: " + supports20);
        return supports20;
    }

    private boolean glRequirementsMet() {
        if (isGL20Supported()) {
            return true;
        }
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(display, new int[2]);
        String vendor = egl.eglQueryString(display, 12371);
        Logger.m172d("OpenGL GL_VENDOR:" + vendor);
        if (vendor == null || vendor.equals("Android")) {
            return false;
        }
        return true;
    }

    public static String getDisconnectReason() {
        return disconnectReason;
    }

    public void speakerSwitch(boolean on) {
        if (this.speakerphoneOn != on) {
            this.speakerphoneOn = on;
            updateAudioRoute(null);
        }
    }

    @SuppressLint({"NewApi"})
    public void updateAudioRoute(Boolean headsetPlugged) {
        if (this.audioManager != null) {
            boolean headsetAttached = headsetPlugged != null ? headsetPlugged.booleanValue() : this.audioManager.isWiredHeadsetOn() || this.audioManager.isBluetoothA2dpOn();
            if (this.currentCall == null || !this.speakerphoneOn || headsetAttached) {
                setSpeakerphoneOn(false);
            } else {
                setSpeakerphoneOn(true);
            }
        }
    }

    private void setSpeakerphoneOn(boolean on) {
        Logger.m172d("Setting speakerphone " + (on ? "ON" : "OFF"));
        if (this.currentCall != null) {
            this.currentCall.notifySpeakerPhoneOn(on);
        }
        this.audioManager.setSpeakerphoneOn(on);
    }

    private Object getAudioFocusChangeListener() {
        if (this.audioFocusHelper == null) {
            try {
                this.audioFocusHelper = Class.forName("ru.ok.android.videochat.AudioFocusHelper").newInstance();
            } catch (Exception e) {
                return null;
            }
        }
        return this.audioFocusHelper;
    }

    private boolean loginIfNeeded(boolean goOnline) {
        Logger.m172d(">>> performing auth.touchSession request...");
        try {
            JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new TouchSessionRequest(goOnline));
            Logger.m172d("<<< auth.touchSession OK");
            return true;
        } catch (Throwable e) {
            Logger.m179e(e, "<<< auth.touchSession request failed");
            return false;
        }
    }

    public void processIncomingCall(String server, String cid, String userName) {
        if (MakeCallManager.isCallSupports()) {
            new C14927().execute(new String[]{server, cid, userName});
            return;
        }
        Logger.m176e("Failed to accept call - videochat not supported");
    }

    public void onUserAnswer() {
        new C14938().execute(new Void[0]);
        if (this.currentCall != null) {
            this.audioManager.setMode(this.audioManagerMode);
            this.currentCall.answer();
        }
    }

    public void updateUI() {
        setUIState(this.uiStatus);
    }

    public CharSequence getUserName() {
        return this.userName;
    }

    public void notifyAudioStarted() {
        this.audioManager.setMode(this.audioManagerMode);
    }

    public AudioManager getAudioManager() {
        return this.audioManager;
    }

    public void setCameraPreview(SurfaceHolder surface) {
        if (this.currentCall != null) {
            this.currentCall.setCameraPreview(surface);
        }
    }

    public void setInputVideoContainer(RelativeLayoutWithSizeListener inputVideoView) {
        if (this.currentCall != null) {
            this.currentCall.setInputVideoView(inputVideoView);
        }
    }

    public int getCaptureWidth() {
        return this.currentCall != null ? this.currentCall.getCaptureWidth() : 0;
    }

    public int getCaptureHeight() {
        return this.currentCall != null ? this.currentCall.getCaptureHeight() : 0;
    }

    public boolean getScreenOrientationLock() {
        return false;
    }

    private static HttpHost getHostForRequest(HttpUriRequest httpRequest) {
        HttpHost httpHost = null;
        Proxy proxy = NetUtils.getProxyForUrl(httpRequest.getURI());
        if (proxy == null || proxy == Proxy.NO_PROXY || proxy.type() == Type.DIRECT) {
            return null;
        }
        SocketAddress address = proxy.address();
        if (address instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) address;
            String str = "Using proxy %s for request %s %s";
            Object[] objArr = new Object[3];
            objArr[0] = proxy;
            objArr[1] = httpRequest.getMethod();
            if (Logger.isLoggingEnable()) {
                httpHost = httpRequest.getURI();
            }
            objArr[2] = httpHost;
            Logger.m173d(str, objArr);
            return new HttpHost(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        }
        Logger.m185w("Don't know how to configure proxy address: %s", address);
        return null;
    }
}
