package ru.ok.android.ui.video.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import java.util.Comparator;
import ru.ok.android.C0206R;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.receivers.ConnectivityReceiver;
import ru.ok.android.ui.custom.video.VideoPlayHeadListener;
import ru.ok.android.ui.custom.video.VideoStatEventProcessor;
import ru.ok.android.ui.video.OneLogVideo;
import ru.ok.android.ui.video.activity.VideoActivity;
import ru.ok.android.ui.video.activity.VideoPlayBack;
import ru.ok.android.ui.video.player.LiveVideoControllerView;
import ru.ok.android.ui.video.player.Quality;
import ru.ok.android.ui.video.player.VideoControllerView;
import ru.ok.android.ui.video.player.VideoPlayerFragment;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.indexing.Action;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.java.api.utils.Utils;
import ru.ok.model.stream.banner.VideoData;
import ru.ok.onelog.video.player.SimplePlayerOperation;

public final class OkVideoFragment extends VideoPlayerFragment implements VideoPlayHeadListener {
    private VideoCastConsumerImpl castConsumer;
    private VideoCastManager castManager;
    private Quality currentQuality;
    private VideoGetResponse currentResponse;
    private Quality previousQuality;
    private VideoStatEventProcessor videoStatEventProcessor;

    /* renamed from: ru.ok.android.ui.video.fragments.OkVideoFragment.1 */
    class C13591 implements OnClickListener {
        final /* synthetic */ VideoGetResponse val$response;

        C13591(VideoGetResponse videoGetResponse) {
            this.val$response = videoGetResponse;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (OkVideoFragment.this.getActivity() != null) {
                OkVideoFragment.this.playFromPlayer(this.val$response);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.video.fragments.OkVideoFragment.2 */
    class C13602 implements OnClickListener {
        final /* synthetic */ VideoGetResponse val$response;

        C13602(VideoGetResponse videoGetResponse) {
            this.val$response = videoGetResponse;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (OkVideoFragment.this.getActivity() != null) {
                OkVideoFragment.this.playFromCast(this.val$response);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.video.fragments.OkVideoFragment.3 */
    class C13613 extends VideoCastConsumerImpl {
        C13613() {
        }

        public void onApplicationConnected(ApplicationMetadata appMetadata, String sessionId, boolean wasLaunched) {
            Logger.m172d("onApplicationLaunched() is reached");
            OkVideoFragment.this.playFromCast(OkVideoFragment.this.currentResponse);
        }

        public void onApplicationDisconnected(int errorCode) {
            Logger.m172d("onApplicationDisconnected() is reached with errorCode: " + errorCode);
        }

        public void onDisconnected() {
            Logger.m172d("onDisconnected() is reached");
        }

        public void onFailed(int resourceId, int statusCode) {
            OkVideoFragment.this.showError(2131166817);
        }

        public void onApplicationConnectionFailed(int errorCode) {
            OkVideoFragment.this.showError(2131166816);
            switch (errorCode) {
                case C0206R.styleable.Toolbar_titleMarginTop /*15*/:
                    Logger.m172d("onApplicationConnectionFailed(): failed due to: ERROR_TIMEOUT");
                case 2004:
                    Logger.m172d("onApplicationConnectionFailed(): failed due to: ERROR_APPLICATION_NOT_FOUND");
                default:
                    Logger.m172d("onApplicationConnectionFailed(): failed due to: error code=" + errorCode);
            }
        }

        public void onConnectionFailed(ConnectionResult result) {
            OkVideoFragment.this.showToastIfVisible(2131165507, 0);
            OkVideoFragment.this.showOnlyPlaybackControl(OkVideoFragment.this.mediaController.getPauseButton());
        }

        public void onDeviceSelected(CastDevice device) {
            if (device != null) {
                OkVideoFragment.this.mediaController.pause();
                OkVideoFragment.this.showOnlyPlaybackControl(OkVideoFragment.this.spinnerView);
                Logger.m172d("onDeviceSelected");
                return;
            }
            Logger.m172d("onDeviceSelected null");
        }
    }

    public static OkVideoFragment newInstance(VideoGetResponse video, String videoUrl, VideoData statData, long position) {
        Bundle args = new Bundle();
        args.putParcelable("video", video);
        args.putString("video_url", videoUrl);
        args.putParcelable("video_stat_data", statData);
        args.putLong("video_position", position);
        OkVideoFragment result = new OkVideoFragment();
        result.setArguments(args);
        return result;
    }

    protected CharSequence getTitle() {
        VideoGetResponse video = getVideo();
        if (video != null) {
            return video.title;
        }
        return super.getTitle();
    }

    private String getVideoUrlArg() {
        return getArguments().getString("video_url");
    }

    private long getVideoPositionArg() {
        return getArguments().getLong("video_position", 0);
    }

    protected VideoControllerView createController(Context context) {
        VideoGetResponse video = getVideo();
        if (video == null || !video.isLiveStreamResponse()) {
            return super.createController(context);
        }
        return new LiveVideoControllerView(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.surfaceView.setVisibility(0);
        this.playerPosition = getVideoPositionArg();
        this.castManager = VideoCastManager.getInstance();
        setupCastListener();
        if (savedInstanceState != null) {
            setCurrentResponse((VideoGetResponse) savedInstanceState.getParcelable("ok-video-fragment.current-response"));
            this.currentQuality = (Quality) savedInstanceState.getSerializable("ok-video-fragment.current-qulity");
            this.previousQuality = (Quality) savedInstanceState.getSerializable("ok-video-fragment.prev-qulity");
        }
        GlobalBus.send(2131624086, new BusEvent());
        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.videoStatEventProcessor != null) {
            this.videoStatEventProcessor.dispose();
        }
    }

    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        this.currentResponse = getVideo();
        if (this.currentResponse == null) {
            tryOpenVideoDirectlyFromArgs();
        } else {
            startVideo(this.currentResponse);
        }
    }

    public void onResume() {
        super.onResume();
        this.surfaceView.setSystemUiVisibility(768);
        this.castManager.addVideoCastConsumer(this.castConsumer);
        this.castManager.incrementUiCounter();
    }

    public void onPause() {
        super.onPause();
        this.castManager.removeVideoCastConsumer(this.castConsumer);
        this.castManager.decrementUiCounter();
    }

    public void onDestroy() {
        super.onDestroy();
        this.castConsumer = null;
        this.castManager = null;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (inflateMenuLocalized(2131689476, menu)) {
            ((MediaRouteButton) MenuItemCompat.getActionView(menu.findItem(C0158R.id.media_route_menu_item))).setRouteSelector(this.castManager.getMediaRouteSelector());
        }
    }

    public void startVideo(VideoGetResponse response) {
        showOnlyPlaybackControl(this.spinnerView);
        setCurrentResponse(response);
        if (!response.isContentTypeVideo()) {
            if (TextUtils.isEmpty(response.urlExternal)) {
                showError(2131166747);
            }
            viewExternalUrl(response.urlExternal);
        } else if (!this.castManager.isConnected() || TextUtils.isEmpty(this.castManager.getDeviceName())) {
            playFromPlayer(response);
        } else {
            new Builder(getActivity()).setTitle(2131165488).setMessage(this.castManager.getDeviceName()).setPositiveButton(LocalizationManager.getString(getActivity(), 2131166881), new C13602(response)).setNegativeButton(LocalizationManager.getString(getActivity(), 2131166257), new C13591(response)).create().show();
        }
        if (this.surfaceView.getVisibility() != 0) {
            this.surfaceView.setVisibility(0);
        }
    }

    private boolean playFromPlayer(VideoGetResponse response) {
        Quality best = pickMostSuitableQuality(response, this.previousQuality);
        Logger.m173d("Quality chosen: %s", best);
        if (best == null) {
            showError(2131166747);
            return false;
        }
        Logger.m172d("start video url: " + best.getUrlFrom(response));
        preparePlayer(best, response);
        this.currentQuality = best;
        return true;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.currentResponse != null) {
            outState.putParcelable("ok-video-fragment.current-response", this.currentResponse);
        }
        if (this.currentQuality != null) {
            outState.putSerializable("ok-video-fragment.current-qulity", this.currentQuality);
        }
        if (this.previousQuality != null) {
            outState.putSerializable("ok-video-fragment.prev-qulity", this.previousQuality);
        }
    }

    public void onVideoEnd() {
        super.onVideoEnd();
        Activity activity = getActivity();
        if (activity != null && (activity instanceof VideoPlayBack)) {
            this.surfaceView.setVisibility(4);
            ((VideoPlayBack) activity).onVideoFinish();
        }
    }

    protected boolean isIndexingFragment() {
        return true;
    }

    protected Action createIndexingAction() {
        if (this.currentResponse == null || TextUtils.isEmpty(this.currentResponse.permalink)) {
            return super.createIndexingAction();
        }
        return Action.newAction("http://schema.org/ViewAction", this.currentResponse.title, Uri.parse(this.currentResponse.permalink), Uri.parse("android-app://ru.ok.android/odnoklassniki/ok.ru/video/" + Utils.getXoredIdSafe(this.currentResponse.id)));
    }

    private void tryOpenVideoDirectlyFromArgs() {
        showOnlyPlaybackControl(this.spinnerView);
        String videoUrl = getVideoUrlArg();
        if (TextUtils.isEmpty(videoUrl)) {
            showError(2131166747);
            return;
        }
        Logger.m173d("open video url: %s", videoUrl);
        preparePlayer(Quality._480p, Uri.parse(videoUrl));
    }

    protected void onTouchSurfaceView(MotionEvent motionEvent) {
        super.onTouchSurfaceView(motionEvent);
        if (motionEvent.getAction() != 0 || !this.playerNeedsPrepare) {
            return;
        }
        if (this.currentResponse == null || this.currentQuality == null) {
            showError(2131166747);
        } else {
            preparePlayer(this.currentQuality, this.currentResponse);
        }
    }

    protected void releasePlayer() {
        super.releasePlayer();
        VideoGetResponse video = getVideo();
        if (video != null) {
            String videoId = video.id;
            if (!TextUtils.isEmpty(videoId)) {
                OneLogVideo.log(Long.valueOf(videoId).longValue(), SimplePlayerOperation.stop, this.currentQuality);
            }
        }
    }

    protected void onPlayerReleased(long position) {
        super.onPlayerReleased(position);
        logWatchTime(position);
    }

    private void logWatchTime(long position) {
        VideoGetResponse video = getVideo();
        long logPosition = position / 1000;
        if (video != null && logPosition > 0) {
            String videoId = video.id;
            if (!TextUtils.isEmpty(videoId)) {
                OneLogVideo.logWatchTime(Long.valueOf(videoId).longValue(), this.currentQuality, logPosition);
            }
        }
    }

    private void logPause(long position) {
        VideoGetResponse video = getVideo();
        long logPosition = position / 1000;
        if (video != null && logPosition > 0) {
            String videoId = video.id;
            if (!TextUtils.isEmpty(videoId)) {
                OneLogVideo.logPause(Long.valueOf(videoId).longValue(), this.currentQuality, logPosition);
            }
        }
    }

    protected void preparePlayer(Quality quality, VideoGetResponse response) {
        String stringQuality = quality.getUrlFrom(response);
        if (!TextUtils.isEmpty(stringQuality)) {
            preparePlayer(quality, Uri.parse(stringQuality));
            this.previousQuality = quality;
            OneLogVideo.log(Long.valueOf(response.id).longValue(), SimplePlayerOperation.inited, quality);
            OneLogVideo.log(Long.valueOf(response.id).longValue(), SimplePlayerOperation.play, quality);
        }
    }

    protected boolean recoverFromError() {
        if (this.previousQuality == null) {
            return false;
        }
        return playFromPlayer(this.currentResponse);
    }

    private Quality pickMostSuitableQuality(VideoGetResponse response, Quality previousQuality) {
        if (previousQuality == null) {
            if (Quality.Live_Hls.isPresentIn(response)) {
                return Quality.Live_Hls;
            }
            if (Quality.Auto.isPresentIn(response)) {
                return Quality.Auto;
            }
            if (Quality.Hls.isPresentIn(response)) {
                return Quality.Hls;
            }
        }
        Comparator<Quality> comparator;
        if (ConnectivityReceiver.isWifi) {
            comparator = Quality.bestFits(Math.min(this.surfaceView.getWidth(), this.surfaceView.getHeight()));
        } else {
            comparator = Quality.higherPriorityForMobileData();
        }
        Quality best = null;
        for (Quality q : Quality.values()) {
            if (q != Quality.Live_Hls && q != Quality.Auto && q != Quality.Hls && q.isPresentIn(response) && comparator.compare(best, q) < 0 && (previousQuality == null || previousQuality.height == 0 || previousQuality.height > q.height)) {
                best = q;
            }
        }
        return best;
    }

    public void onQualitySelected(Quality quality, int index) {
        if (this.player != null) {
            this.player.selectTrack(0, index);
            VideoGetResponse video = getVideo();
            if (video != null) {
                String videoId = video.id;
                if (!TextUtils.isEmpty(videoId)) {
                    OneLogVideo.logQualityChange(Long.valueOf(videoId).longValue(), quality, this.currentQuality);
                }
            }
            this.currentQuality = quality;
        }
    }

    private void viewExternalUrl(String url) {
        Activity activity = getActivity();
        if (activity != null) {
            try {
                Logger.m173d("Show external video url: %s", url);
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                activity.finish();
            } catch (Throwable e) {
                Logger.m178e(e);
                OneLogVideo.logError(Long.parseLong(getVideo().id), Log.getStackTraceString(e));
                closeWithFailure();
            }
        }
    }

    protected void closeWithFailure() {
        Activity activity = getActivity();
        if (activity != null) {
            Toast.makeText(activity, getStringLocalized(2131166817), 0).show();
            activity.finish();
        }
    }

    public void onVideoPlayHeadPosition(int positionMs) {
        Logger.m173d("positionMs=%d", Integer.valueOf(positionMs));
    }

    private void setCurrentResponse(VideoGetResponse response) {
        if (this.currentResponse != response) {
            this.currentResponse = response;
            onCurrentResponseChanged(response);
        }
    }

    private void onCurrentResponseChanged(VideoGetResponse newResponse) {
        Activity activity = getActivity();
        if (activity != null && (activity instanceof VideoActivity)) {
            ((VideoActivity) activity).onCurrentResponseChanged(newResponse);
        }
    }

    protected void onClickPlayPause(boolean isPlaying) {
        super.onClickPlayPause(isPlaying);
        if (isPlaying) {
            logSimpleEvent(SimplePlayerOperation.pause);
            if (this.player != null) {
                logPause(this.player.getCurrentPosition());
            }
        }
    }

    private void logSimpleEvent(SimplePlayerOperation operation) {
        VideoGetResponse video = getVideo();
        if (video != null) {
            String videoId = video.id;
            if (!TextUtils.isEmpty(videoId)) {
                OneLogVideo.log(Long.valueOf(videoId).longValue(), operation, this.currentQuality);
            }
        }
    }

    private void playFromCast(VideoGetResponse response) {
        Activity activity = getActivity();
        if (activity == null || response == null || !(activity instanceof VideoActivity)) {
            Logger.m172d("error cast video");
        } else {
            ((VideoActivity) activity).showCastFragment(response, (int) (this.player == null ? 0 : this.player.getCurrentPosition()));
        }
    }

    private void setupCastListener() {
        this.castConsumer = new C13613();
    }
}
