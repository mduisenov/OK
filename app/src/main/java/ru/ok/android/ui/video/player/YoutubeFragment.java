package ru.ok.android.ui.video.player;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController.MediaPlayerControl;
import com.google.android.gms.location.LocationStatusCodes;
import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import ru.ok.android.C0206R;
import ru.ok.android.fragments.web.WebExternalUrlManager;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.video.OneLogVideo;
import ru.ok.android.ui.video.activity.VideoPlayBack;
import ru.ok.android.ui.video.player.VideoControllerView.OnHidedListener;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.StringUtils;

public class YoutubeFragment extends AbstractVideoFragment {
    static final Map<String, Quality> stringsToQuality;
    private View cover;
    private int currentPosition;
    private int duration;
    private final OnClickListener onRetryLoadingClick;
    private final YoutubePlayerInterface playerInterface;
    private int seekValue;
    private String title;
    private WebInterface webInterface;
    private WebView webView;

    /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.1 */
    static class C13911 extends HashMap<String, Quality> {
        C13911() {
            put("auto", Quality.Auto);
            put("tiny", Quality._144p);
            put("small", Quality._240p);
            put("medium", Quality._360p);
            put("large", Quality._480p);
            put("hd720", Quality._720p);
            put("hd1080", Quality._1080p);
            put("highres", Quality._1080p);
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.2 */
    class C13922 extends WebChromeClient {
        C13922() {
        }

        public View getVideoLoadingProgressView() {
            Log.d("AbstractVideoFragment", "getVideoLoadingProgressView");
            return YoutubeFragment.this.spinnerView;
        }

        public Bitmap getDefaultVideoPoster() {
            return null;
        }

        public boolean onConsoleMessage(ConsoleMessage message) {
            Logger.m172d("WebChrome: " + message.message());
            if (message.messageLevel() == MessageLevel.ERROR && "Uncaught ReferenceError: JavascriptInterfaceName is not defined".equals(message.message())) {
                YoutubeFragment.this.webView.reload();
            }
            if (message.message().startsWith("Uncaught TypeError: Object #<P> has no method")) {
                Activity activity = YoutubeFragment.this.getActivity();
                if (activity != null) {
                    WebExternalUrlManager.onOutLinkOpenInBrowser(activity, YoutubeFragment.this.getVideoUri().toString());
                    activity.finish();
                }
            }
            return super.onConsoleMessage(message);
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.3 */
    class C13933 implements OnClickListener {
        C13933() {
        }

        public void onClick(View v) {
            if (YoutubeFragment.this.mediaController.isShowing()) {
                YoutubeFragment.this.mediaController.hide();
            } else {
                YoutubeFragment.this.mediaController.show();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.4 */
    class C13944 implements OnHidedListener {
        C13944() {
        }

        public void onViewHided() {
            YoutubeFragment.this.seekValue = -1;
        }
    }

    private interface OnLoadPageFinishCallBack {
        void onLoadFinish();
    }

    /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.5 */
    class C13955 implements OnLoadPageFinishCallBack {
        final /* synthetic */ int val$sec;
        final /* synthetic */ String val$videoId;

        C13955(String str, int i) {
            this.val$videoId = str;
            this.val$sec = i;
        }

        public void onLoadFinish() {
            YoutubeFragment.this.webView.loadUrl("javascript:player.loadVideoById('" + this.val$videoId + "'," + this.val$sec + ",'default');");
            YoutubeFragment.this.webView.loadUrl("javascript:player.playVideo();");
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.6 */
    class C13966 implements OnClickListener {
        C13966() {
        }

        public void onClick(View v) {
            YoutubeFragment.this.showVideo(YoutubeFragment.this.getYoutubeVideoId(), YoutubeFragment.this.currentPosition);
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.7 */
    static /* synthetic */ class C13977 {
        static final /* synthetic */ int[] f123xc97d60df;

        static {
            f123xc97d60df = new int[PlaybackState.values().length];
            try {
                f123xc97d60df[PlaybackState.Playing.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f123xc97d60df[PlaybackState.Paused.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f123xc97d60df[PlaybackState.Buffering.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f123xc97d60df[PlaybackState.VideoCued.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f123xc97d60df[PlaybackState.Unstarted.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f123xc97d60df[PlaybackState.Ended.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    private enum PlaybackState {
        Unstarted(-1),
        Ended(0),
        Playing(1),
        Paused(2),
        Buffering(3),
        VideoCued(5);
        
        final int id;

        private PlaybackState(int id) {
            this.id = id;
        }

        static PlaybackState byId(String s) {
            int i = Integer.valueOf(s).intValue();
            for (PlaybackState v : values()) {
                if (v.id == i) {
                    return v;
                }
            }
            return null;
        }
    }

    private class WebInterface extends WebViewClient {
        final long TIMEOUT;
        CountDownLatch countDown;
        final Handler handler;
        String jsResult;
        final Runnable loadTimeoutWatchdog;
        private Map<String, OnLoadPageFinishCallBack> mapCallbacks;

        /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.WebInterface.1 */
        class C13981 implements Runnable {
            C13981() {
            }

            public void run() {
                WebInterface.this.handler.removeCallbacks(WebInterface.this.loadTimeoutWatchdog);
                YoutubeFragment.this.mediaController.setMediaPlayer(YoutubeFragment.this.playerInterface);
                YoutubeFragment.this.webView.loadUrl("javascript:AndroidCallbacks.onGetTitle(player.getVideoData().title)");
                YoutubeFragment.this.webView.loadUrl("javascript:player.playVideo();");
                YoutubeFragment.this.spinnerView.setVisibility(8);
                YoutubeFragment.this.cover.setVisibility(4);
            }
        }

        /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.WebInterface.2 */
        class C13992 implements Runnable {
            final /* synthetic */ String val$eventData;

            C13992(String str) {
                this.val$eventData = str;
            }

            public void run() {
                PlaybackState state = PlaybackState.byId(this.val$eventData);
                YoutubeFragment.this.playerInterface.setState(state);
                switch (C13977.f123xc97d60df[state.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        YoutubeFragment.this.webView.loadUrl("javascript:AndroidCallbacks.onGetQualityLevels(player.getPlaybackQuality(),player.getAvailableQualityLevels().toString())");
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        YoutubeFragment.this.logWatchTime((long) YoutubeFragment.this.currentPosition);
                    default:
                }
            }
        }

        /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.WebInterface.3 */
        class C14003 implements Runnable {
            final /* synthetic */ String val$qualityString;

            C14003(String str) {
                this.val$qualityString = str;
            }

            public void run() {
                YoutubeFragment.this.playerInterface.currentQuality = this.val$qualityString;
            }
        }

        /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.WebInterface.4 */
        class C14014 implements Runnable {
            final /* synthetic */ int val$code;

            C14014(int i) {
                this.val$code = i;
            }

            public void run() {
                switch (this.val$code) {
                    case Message.UUID_FIELD_NUMBER /*5*/:
                        YoutubeFragment.this.showError(2131165842, YoutubeFragment.this.onRetryLoadingClick);
                    case C0206R.styleable.Theme_checkboxStyle /*101*/:
                    case 150:
                        YoutubeFragment.this.spinnerView.setVisibility(8);
                        YoutubeFragment.this.showError(2131165842, YoutubeFragment.this.onRetryLoadingClick);
                    default:
                        YoutubeFragment.this.showError(2131166747);
                }
            }
        }

        /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.WebInterface.5 */
        class C14025 implements Runnable {
            final /* synthetic */ String val$qualitiesString;

            C14025(String str) {
                this.val$qualitiesString = str;
            }

            public void run() {
                YoutubeFragment.this.playerInterface.qualities = this.val$qualitiesString;
            }
        }

        /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.WebInterface.6 */
        class C14036 implements Runnable {
            final /* synthetic */ String val$title;

            C14036(String str) {
                this.val$title = str;
            }

            public void run() {
                YoutubeFragment.this.title = this.val$title;
                YoutubeFragment.this.setTitle(this.val$title);
            }
        }

        /* renamed from: ru.ok.android.ui.video.player.YoutubeFragment.WebInterface.7 */
        class C14047 implements Runnable {
            C14047() {
            }

            public void run() {
                YoutubeFragment.this.showError(2131165842, YoutubeFragment.this.onRetryLoadingClick);
                Log.d("AbstractVideoFragment", "Show timeout error");
            }
        }

        private WebInterface() {
            this.TIMEOUT = 5000;
            this.handler = new Handler();
            this.mapCallbacks = new WeakHashMap();
            this.loadTimeoutWatchdog = new C14047();
        }

        public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String failUrl, OnLoadPageFinishCallBack callBack) {
            this.mapCallbacks.put(baseUrl, callBack);
            YoutubeFragment.this.webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, failUrl);
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            this.handler.removeCallbacks(this.loadTimeoutWatchdog);
            this.handler.postDelayed(this.loadTimeoutWatchdog, 5000);
            Logger.m172d("onPageStarted");
        }

        public void onPageFinished(WebView view, String url) {
            Logger.m172d("onPageFinished");
            if (this.mapCallbacks.containsKey(url)) {
                ((OnLoadPageFinishCallBack) this.mapCallbacks.get(url)).onLoadFinish();
                this.mapCallbacks.remove(url);
            }
        }

        @JavascriptInterface
        public void onReady(String eventData) {
            Logger.m172d("onReady");
            YoutubeFragment.this.webView.post(new C13981());
        }

        @JavascriptInterface
        public void onStateChange(String eventData) {
            YoutubeFragment.this.webView.post(new C13992(eventData));
        }

        @JavascriptInterface
        public void onPlaybackQualityChange(String qualityString) {
            Log.d("AbstractVideoFragment", "On playback quality changed: " + qualityString);
            if (qualityString != null) {
                YoutubeFragment.this.webView.post(new C14003(qualityString));
            }
        }

        @JavascriptInterface
        public void onError(String eventData) {
            Log.d("AbstractVideoFragment", "onError: " + eventData);
            YoutubeFragment.this.webView.post(new C14014(Integer.valueOf(eventData).intValue()));
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e("AbstractVideoFragment", "Loading page error: " + errorCode + " " + description);
        }

        @JavascriptInterface
        public void onGetQualityLevels(String currentQuality, String qualitiesString) {
            Log.d("AbstractVideoFragment", "onGetQualityLevels(" + currentQuality + ", {" + qualitiesString + "})");
            if (qualitiesString != null) {
                onPlaybackQualityChange(currentQuality);
                YoutubeFragment.this.webView.post(new C14025(qualitiesString));
            }
        }

        @JavascriptInterface
        public void onGetTitle(String title) {
            Log.d("AbstractVideoFragment", "onGetTitle(" + title + ")");
            YoutubeFragment.this.webView.post(new C14036(title));
        }

        private String callJsForResult(String code) {
            this.countDown = new CountDownLatch(1);
            YoutubeFragment.this.webView.loadUrl("javascript:AndroidCallbacks.onResult(" + code + ")");
            try {
                if (this.countDown.await(50, TimeUnit.MILLISECONDS)) {
                    return this.jsResult;
                }
                Log.e("AbstractVideoFragment", code + " timeout");
                return null;
            } catch (InterruptedException e) {
                OneLogVideo.logCrash(Long.valueOf(YoutubeFragment.this.getOkVideoId()).longValue(), Log.getStackTraceString(e));
                Thread.currentThread().interrupt();
            }
        }

        @JavascriptInterface
        public void onResult(String data) {
            this.jsResult = data;
            this.countDown.countDown();
        }
    }

    private class YoutubePlayerInterface implements MediaPlayerControl {
        public String currentQuality;
        public String qualities;
        private PlaybackState state;

        private YoutubePlayerInterface() {
        }

        public void start() {
            YoutubeFragment.this.webView.loadUrl("javascript:player.playVideo();");
        }

        public void pause() {
            YoutubeFragment.this.webView.loadUrl("javascript:player.pauseVideo();");
        }

        public int getDuration() {
            try {
                String s = YoutubeFragment.this.webInterface.callJsForResult("player.getDuration()");
                if (!TextUtils.isEmpty(s)) {
                    Logger.m172d("duration: " + s);
                    YoutubeFragment.this.duration = (int) (Float.valueOf(s).floatValue() * 1000.0f);
                }
            } catch (NumberFormatException ex) {
                Logger.m172d("error get duration");
                OneLogVideo.logError(Long.valueOf(YoutubeFragment.this.getOkVideoId()).longValue(), Log.getStackTraceString(ex));
            }
            return YoutubeFragment.this.duration;
        }

        public int getCurrentPosition() {
            try {
                String s = YoutubeFragment.this.webInterface.callJsForResult("player.getCurrentTime()");
                Logger.m172d("current position: " + s + " cur_pos:" + YoutubeFragment.this.currentPosition);
                if (!TextUtils.isEmpty(s)) {
                    int position = (int) (Float.valueOf(s).floatValue() * 1000.0f);
                    if (YoutubeFragment.this.seekValue == position / LocationStatusCodes.GEOFENCE_NOT_AVAILABLE) {
                        YoutubeFragment.this.seekValue = -1;
                    }
                    if (YoutubeFragment.this.seekValue == -1) {
                        YoutubeFragment.this.currentPosition = position;
                    }
                }
            } catch (NumberFormatException ex) {
                Logger.m172d("error get current position");
                OneLogVideo.logError(Long.valueOf(YoutubeFragment.this.getOkVideoId()).longValue(), Log.getStackTraceString(ex));
            }
            return YoutubeFragment.this.currentPosition;
        }

        public int getBufferPercentage() {
            try {
                String s = YoutubeFragment.this.webInterface.callJsForResult("player.getVideoLoadedFraction()");
                if (s != null) {
                    return (int) (Float.valueOf(s).floatValue() * 100.0f);
                }
                return 0;
            } catch (NumberFormatException ex) {
                OneLogVideo.logError(Long.valueOf(YoutubeFragment.this.getOkVideoId()).longValue(), Log.getStackTraceString(ex));
                return 0;
            }
        }

        public void seekTo(int pos) {
            YoutubeFragment.this.seekValue = pos / LocationStatusCodes.GEOFENCE_NOT_AVAILABLE;
            YoutubeFragment.this.currentPosition = pos;
            YoutubeFragment.this.webView.loadUrl("javascript:player.seekTo(" + YoutubeFragment.this.seekValue + ", true);");
        }

        public boolean isPlaying() {
            if (this.state == null) {
                String s = YoutubeFragment.this.webInterface.callJsForResult("player.getPlayerState()");
                if (!TextUtils.isEmpty(s)) {
                    this.state = PlaybackState.byId(s);
                }
                if (this.state == null) {
                    return false;
                }
            }
            switch (C13977.f123xc97d60df[this.state.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                case Message.TYPE_FIELD_NUMBER /*3*/:
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    return true;
                default:
                    return false;
            }
        }

        public boolean canPause() {
            return true;
        }

        public boolean canSeekBackward() {
            return true;
        }

        public boolean canSeekForward() {
            return true;
        }

        public int getAudioSessionId() {
            return 0;
        }

        private void setState(PlaybackState state) {
            Logger.m172d("onStateChange " + state);
            if (state != null) {
                this.state = state;
                displayCurrentState();
            }
        }

        private void displayCurrentState() {
            if (this.state != null) {
                YoutubeFragment.this.cover.setVisibility(this.state == PlaybackState.Unstarted ? 4 : 0);
                switch (C13977.f123xc97d60df[this.state.ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        YoutubeFragment.this.spinnerView.setVisibility(8);
                        YoutubeFragment.this.displayVideoPlaying();
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        YoutubeFragment.this.displayVideoPaused();
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                    case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                        YoutubeFragment.this.displayVideoBuffering();
                        YoutubeFragment.this.spinnerView.setVisibility(0);
                    case Message.UUID_FIELD_NUMBER /*5*/:
                    case Message.REPLYTO_FIELD_NUMBER /*6*/:
                        YoutubeFragment.this.displayVideoEnded();
                        FragmentActivity activity = YoutubeFragment.this.getActivity();
                        if (activity != null && (activity instanceof VideoPlayBack)) {
                            ((VideoPlayBack) activity).onVideoFinish();
                        }
                    default:
                }
            }
        }
    }

    public YoutubeFragment() {
        this.playerInterface = new YoutubePlayerInterface();
        this.currentPosition = 0;
        this.duration = -1;
        this.seekValue = -1;
        this.onRetryLoadingClick = new C13966();
    }

    static {
        stringsToQuality = new C13911();
    }

    public static YoutubeFragment forVideo(Uri videoUri, String okVideoId) throws MalformedURLException {
        String videoYoutubeId = parseVideoId(videoUri);
        Bundle args = new Bundle();
        args.putString("arg.youtube.videoId", videoYoutubeId);
        args.putString("arg.ok.videoId", okVideoId);
        args.putParcelable("arg.videoUri", videoUri);
        YoutubeFragment fragment = new YoutubeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String getYoutubeVideoId() {
        return getArguments().getString("arg.youtube.videoId");
    }

    private String getOkVideoId() {
        return getArguments().getString("arg.ok.videoId");
    }

    private Uri getVideoUri() {
        return (Uri) getArguments().getParcelable("arg.videoUri");
    }

    private void logWatchTime(long position) {
        if (position > 0) {
            long logPosition = position / 1000;
            String videoId = getOkVideoId();
            Quality currentQuality = getCurrentQuality();
            if (!TextUtils.isEmpty(videoId) && logPosition > 0) {
                OneLogVideo.logWatchTime(Long.valueOf(videoId).longValue(), currentQuality, logPosition);
            }
        }
    }

    private static String parseVideoId(Uri uri) throws MalformedURLException {
        if (uri.getHost() == null) {
            throw new MalformedURLException("Illegal uri");
        }
        String host = uri.getHost().toLowerCase();
        String path = uri.getPath().replaceFirst("^/", "");
        int i = -1;
        switch (host.hashCode()) {
            case -679381487:
                if (host.equals("youtu.be")) {
                    i = 3;
                    break;
                }
                break;
            case -351352779:
                if (host.equals("m.youtube.com")) {
                    i = 0;
                    break;
                }
                break;
            case -78033866:
                if (host.equals("youtube.com")) {
                    i = 2;
                    break;
                }
                break;
            case -12310945:
                if (host.equals("www.youtube.com")) {
                    i = 1;
                    break;
                }
                break;
        }
        switch (i) {
            case RECEIVED_VALUE:
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                String[] pathParts = path.split("/");
                if (pathParts.length == 1 && pathParts[0].equalsIgnoreCase("watch")) {
                    return uri.getQueryParameter(Logger.METHOD_V);
                }
                if (pathParts.length == 2 && pathParts[0].equalsIgnoreCase("embed")) {
                    return pathParts[1];
                }
                throw new MalformedURLException("Unknown url scheme");
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return path;
            default:
                throw new MalformedURLException("Host is not youtube");
        }
    }

    public static boolean isYouTubeUrl(String url) {
        String host = Uri.parse(url).getHost();
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        host = host.toLowerCase();
        boolean z = true;
        switch (host.hashCode()) {
            case -679381487:
                if (host.equals("youtu.be")) {
                    z = true;
                    break;
                }
                break;
            case -351352779:
                if (host.equals("m.youtube.com")) {
                    z = false;
                    break;
                }
                break;
            case -78033866:
                if (host.equals("youtube.com")) {
                    z = true;
                    break;
                }
                break;
            case -12310945:
                if (host.equals("www.youtube.com")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case RECEIVED_VALUE:
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return true;
            default:
                return false;
        }
    }

    protected int getLayoutId() {
        return 2130903207;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        this.webView = (WebView) root.findViewById(2131624869);
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        if (VERSION.SDK_INT >= 17) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        settings.setPluginState(PluginState.ON);
        this.webInterface = new WebInterface();
        this.webView.addJavascriptInterface(this.webInterface, "AndroidCallbacks");
        this.webView.setWebViewClient(this.webInterface);
        this.webView.setWebChromeClient(new C13922());
        this.webView.setBackgroundColor(0);
        if (VERSION.SDK_INT >= 19) {
            this.cover = root.findViewById(2131624870);
            this.gradientView.setOnTouchListener(null);
            this.cover.setOnClickListener(new C13933());
        } else {
            this.cover = root.findViewById(2131624870);
            this.gradientView.setOnTouchListener(null);
            this.cover.setOnClickListener(new C13933());
        }
        if (savedInstanceState != null) {
            this.currentPosition = savedInstanceState.getInt("position", 0);
        }
        showVideo(getYoutubeVideoId(), this.currentPosition);
        return root;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", this.currentPosition);
        super.onSaveInstanceState(outState);
    }

    protected VideoControllerView createController(Context context) {
        VideoControllerView controllerView = new VideoControllerView(context);
        controllerView.setOnHidedListener(new C13944());
        return controllerView;
    }

    public void onStart() {
        super.onStart();
        this.playerInterface.displayCurrentState();
    }

    public void onResume() {
        super.onResume();
        this.webView.resumeTimers();
        this.webView.onResume();
    }

    public void onPause() {
        this.webView.onPause();
        this.webView.pauseTimers();
        super.onPause();
    }

    public void onStop() {
        super.onStop();
        logWatchTime((long) this.currentPosition);
    }

    protected CharSequence getTitle() {
        if (StringUtils.isEmpty(this.title)) {
            return super.getTitle();
        }
        return this.title;
    }

    @Nullable
    protected ArrayList<Quality> getQualities() {
        ArrayList<Quality> result = new ArrayList();
        if (this.playerInterface.qualities != null) {
            for (String q : this.playerInterface.qualities.split(",")) {
                Quality quality = (Quality) stringsToQuality.get(q);
                if (!(quality == null || result.contains(quality))) {
                    result.add(0, quality);
                }
            }
        }
        return result;
    }

    @Nullable
    protected Quality getCurrentQuality() {
        return (Quality) stringsToQuality.get(this.playerInterface.currentQuality);
    }

    public void onQualitySelected(Quality quality, int index) {
        int value = 0;
        for (Entry<String, Quality> e : stringsToQuality.entrySet()) {
            if (e.getValue() == quality) {
                this.cover.setVisibility(0);
                String s = this.webInterface.callJsForResult("player.getCurrentTime()");
                if (s != null) {
                    value = (int) Float.valueOf(s).floatValue();
                }
                this.webView.loadUrl(createCueVideoByIdJs(value, getYoutubeVideoId(), (String) e.getKey()));
                this.webView.loadUrl("javascript:player.playVideo();");
                return;
            }
        }
    }

    private static String createCueVideoByIdJs(int value, String videoId, String quality) {
        return "javascript:player.cueVideoById('" + videoId + "'," + value + ",'" + quality + "');";
    }

    protected void showError(int resError, OnClickListener onErrorClick) {
        super.showError(resError, onErrorClick);
        this.errorStub.setVisibility(0);
        setVideoViews(4);
    }

    protected void hideError() {
        setVideoViews(0);
        this.cover.setVisibility(4);
        this.errorStub.setVisibility(8);
        super.hideError();
    }

    private void setVideoViews(int visibility) {
        this.webView.setVisibility(visibility);
        this.gradientView.setVisibility(visibility);
    }

    private void showYoutubePage(String videoId, int sec) throws IOException {
        Closeable is = null;
        try {
            is = getResources().getAssets().open("YTPlayerView-iframe-player.html");
            this.webInterface.loadDataWithBaseURL("first", IOUtils.inputStreamToString(is).replace("$(videoId)", videoId), "text/html", org.jivesoftware.smack.util.StringUtils.UTF8, "", new C13955(videoId, sec));
        } finally {
            IOUtils.closeSilently(is);
        }
    }

    private void showVideo(String videoId, int sec) {
        hideError();
        displayVideoBuffering();
        try {
            showYoutubePage(videoId, sec);
        } catch (IOException shouldNeverHappen) {
            Logger.m173d("failed to load page from assets", shouldNeverHappen);
            OneLogVideo.logCrash(Long.valueOf(getOkVideoId()).longValue(), Log.getStackTraceString(shouldNeverHappen));
        }
    }
}
