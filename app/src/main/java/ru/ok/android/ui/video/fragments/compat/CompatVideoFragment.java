package ru.ok.android.ui.video.fragments.compat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.receivers.ConnectivityReceiver;
import ru.ok.android.ui.custom.VideoPlaybackView;
import ru.ok.android.ui.custom.VideoPlaybackView.OnMediaControlListener;
import ru.ok.android.ui.custom.VideoPlaybackView.VideoEventListener;
import ru.ok.android.ui.custom.video.VideoBannerStatEventHandler;
import ru.ok.android.ui.custom.video.VideoPlayHeadListener;
import ru.ok.android.ui.custom.video.VideoStatEventProcessor;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.video.activity.VideoActivity;
import ru.ok.android.ui.video.activity.VideoPlayBack;
import ru.ok.android.ui.video.fragments.FORMAT;
import ru.ok.android.ui.video.fragments.VideoCompatUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusVideoHelper;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.java.api.response.video.VideoGetResponse.VideoStatus;
import ru.ok.model.stream.banner.VideoData;

public final class CompatVideoFragment extends BaseFragment implements OnMediaControlListener, VideoEventListener, VideoPlayHeadListener {
    private ViewStub errorStub;
    private MediaController mediaController;
    private FORMAT previousFormat;
    private VideoGetResponse videoInfo;
    private VideoStatEventProcessor videoStatEventProcessor;
    private VideoPlaybackView videoView;

    /* renamed from: ru.ok.android.ui.video.fragments.compat.CompatVideoFragment.1 */
    class C13651 implements OnAttachStateChangeListener {
        C13651() {
        }

        public void onViewAttachedToWindow(View v) {
        }

        public void onViewDetachedFromWindow(View v) {
            CompatVideoFragment.this.onHideMediaControl();
        }
    }

    /* renamed from: ru.ok.android.ui.video.fragments.compat.CompatVideoFragment.2 */
    class C13662 implements Comparator<Pair<FORMAT, String>> {
        final /* synthetic */ FORMAT val$previousFormat;

        C13662(FORMAT format) {
            this.val$previousFormat = format;
        }

        public int compare(Pair<FORMAT, String> a, Pair<FORMAT, String> b) {
            int aPrior = ((FORMAT) a.first).getNoWifiPrior();
            int bPrior = ((FORMAT) b.first).getNoWifiPrior();
            if (this.val$previousFormat != null) {
                boolean skipA = ((FORMAT) a.first).getSize() >= this.val$previousFormat.getSize();
                boolean skipB;
                if (((FORMAT) b.first).getSize() >= this.val$previousFormat.getSize()) {
                    skipB = true;
                } else {
                    skipB = false;
                }
                if (skipA && !skipB) {
                    return -1;
                }
                if (!skipA && skipB) {
                    return 1;
                }
                if (skipA && skipB) {
                    return 0;
                }
            }
            if (aPrior <= bPrior) {
                return aPrior < bPrior ? -1 : 0;
            } else {
                return 1;
            }
        }
    }

    public static CompatVideoFragment newInstance(VideoGetResponse video, String videoUrl, VideoData statData) {
        Bundle args = new Bundle();
        args.putParcelable("video", video);
        args.putString("video_url", videoUrl);
        args.putParcelable("video_stat_data", statData);
        CompatVideoFragment result = new CompatVideoFragment();
        result.setArguments(args);
        return result;
    }

    private String getVideoId() {
        VideoGetResponse video = getVideo();
        if (video == null) {
            return null;
        }
        return video.id;
    }

    private VideoGetResponse getVideo() {
        return (VideoGetResponse) getArguments().getParcelable("video");
    }

    private String getVideoUrl() {
        return getArguments().getString("video_url");
    }

    private VideoData getVideoStatData() {
        return (VideoData) getArguments().getParcelable("video_stat_data");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View result = LayoutInflater.from(getActivity()).inflate(2130903568, container, false);
        this.mediaController = new MediaController(getActivity());
        this.mediaController.addOnAttachStateChangeListener(new C13651());
        this.videoView = (VideoPlaybackView) result.findViewById(2131625422);
        this.videoView.setVideoCallback(this);
        this.videoView.setMediaController(this.mediaController);
        this.videoView.setMediaControlListener(this);
        this.errorStub = (ViewStub) result.findViewById(2131624589);
        VideoData videoData = getVideoStatData();
        if (videoData != null) {
            this.videoStatEventProcessor = new VideoStatEventProcessor(this.videoView.getVideoView());
            this.videoStatEventProcessor.addVideoStatEventHandler(new VideoBannerStatEventHandler(getContext(), videoData));
        }
        return result;
    }

    public void hidePlayer() {
        this.videoView.setVisibility(8);
    }

    public void showPlayer() {
        this.videoView.setVisibility(0);
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        if (playWhenReady) {
            this.videoView.play();
        } else {
            this.videoView.pause();
        }
    }

    protected CharSequence getTitle() {
        VideoGetResponse video = getVideo();
        if (video != null) {
            return video.title;
        }
        return "";
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.videoStatEventProcessor != null) {
            this.videoStatEventProcessor.dispose();
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GlobalBus.send(2131624086, new BusEvent());
        String videoId = getVideoId();
        if (TextUtils.isEmpty(videoId)) {
            openVideoByUrl();
            return;
        }
        this.videoInfo = getVideo();
        if (!findUrlAndPlay()) {
            BusVideoHelper.getVideoInfo(videoId);
        }
    }

    private void openVideoByUrl() {
        String videoUrl = getVideoUrl();
        if (TextUtils.isEmpty(videoUrl)) {
            closeWithFailure();
            return;
        }
        Logger.m173d("open video url: %s", videoUrl);
        this.videoView.startUrlPlayback(Uri.parse(videoUrl));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RECEIVED_VALUE:
                if (resultCode == -1) {
                    String url = data.getAction();
                    if (!TextUtils.isEmpty(url)) {
                        playUrl(url);
                        return;
                    }
                }
                NavigationHelper.finishActivity(getActivity());
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void playUrl(String url) {
        if (getActivity() != null) {
            Logger.m173d("url=%s", url);
            this.videoView.startUrlPlayback(Uri.parse(url));
        }
    }

    @Subscribe(on = 2131623946, to = 2131624267)
    public void onVideoInfoFetched(BusEvent event) {
        if (getActivity() != null) {
            String videoId = getVideoId();
            if (!event.bundleInput.getStringArrayList("VIDEO_IDS").contains(videoId)) {
                return;
            }
            if (event.resultCode == -2) {
                closeWithFailure();
                return;
            }
            Iterator i$ = event.bundleOutput.getParcelableArrayList("VIDEO_INFOS").iterator();
            while (i$.hasNext()) {
                VideoGetResponse info = (VideoGetResponse) i$.next();
                if (TextUtils.equals(info.id, videoId)) {
                    if (info.status == VideoStatus.OK) {
                        this.videoInfo = info;
                        if (!findUrlAndPlay()) {
                            openVideoByUrl();
                            return;
                        }
                        return;
                    }
                    onVideoStatusNoPlay(info.status);
                    this.videoView.hideProgress();
                    return;
                }
            }
            closeWithFailure();
        }
    }

    private boolean findUrlAndPlay() {
        if (this.videoInfo == null) {
            return false;
        }
        String url;
        FORMAT format;
        Pair<FORMAT, String> pair = getPreferredQualityUrl(this.videoInfo, this.previousFormat);
        if (pair != null) {
            url = (String) pair.second;
        } else {
            url = null;
        }
        if (pair != null) {
            format = (FORMAT) pair.first;
        } else {
            format = null;
        }
        this.previousFormat = format;
        Logger.m173d("Preferred url=%s", url);
        if (!TextUtils.isEmpty(url)) {
            if (this.videoInfo.isContentTypeVideo()) {
                playUrl(url);
            } else {
                openUrl(url);
            }
            return true;
        } else if (!TextUtils.isEmpty(getVideoUrl())) {
            return false;
        } else {
            onVideoStatusNoPlay(this.videoInfo.status);
            this.videoView.hideProgress();
            return true;
        }
    }

    private void onVideoStatusNoPlay(VideoStatus status) {
        if (getActivity() != null) {
            ((TextView) this.errorStub.inflate()).setText(VideoCompatUtils.getErrorStatusTextResValue(status));
        }
    }

    private void openUrl(String url) {
        Activity activity = getActivity();
        if (activity != null) {
            try {
                Logger.m173d("Show external video url: %s", url);
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                activity.finish();
            } catch (Throwable e) {
                Logger.m178e(e);
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

    public void onPause() {
        super.onPause();
        this.videoView.pause();
    }

    public void onResume() {
        super.onResume();
        this.videoView.resume();
    }

    public void onFinished() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (activity instanceof VideoPlayBack) {
            ((VideoPlayBack) activity).onVideoFinish();
        } else {
            NavigationHelper.finishActivity(activity);
        }
    }

    public void onError() {
        if (this.previousFormat == null || !findUrlAndPlay()) {
            closeWithFailure();
        }
    }

    protected int getLayoutId() {
        return 2130903568;
    }

    @Nullable
    protected Pair<FORMAT, String> getPreferredQualityUrl(VideoGetResponse response, FORMAT previousFormat) {
        List<Pair<FORMAT, String>> returnList = VideoCompatUtils.getNotEmptyFormats(response);
        if (returnList.isEmpty()) {
            return null;
        }
        if (ConnectivityReceiver.isWifi) {
            return getOptimalDisplayFormat(returnList, previousFormat);
        }
        return getOptimalDisplayFormatNoWiFi(returnList, previousFormat);
    }

    @Nullable
    private Pair<FORMAT, String> getOptimalDisplayFormat(List<Pair<FORMAT, String>> formatsList, FORMAT previousFormat) {
        Pair<FORMAT, String> optimalFormat = (Pair) formatsList.get(0);
        int maxSize = Math.min(this.videoView.getWidth(), this.videoView.getHeight());
        if (previousFormat != null) {
            maxSize = previousFormat.getSize() - 1;
        }
        for (Pair<FORMAT, String> format : formatsList) {
            int formatSize = ((FORMAT) format.first).getSize();
            if (formatSize <= maxSize && formatSize > ((FORMAT) optimalFormat.first).getSize()) {
                optimalFormat = format;
            }
        }
        if (previousFormat == null || previousFormat.getSize() > ((FORMAT) optimalFormat.first).getSize()) {
            return optimalFormat;
        }
        return null;
    }

    private Pair<FORMAT, String> getOptimalDisplayFormatNoWiFi(List<Pair<FORMAT, String>> formatsList, FORMAT previousFormat) {
        return (Pair) Collections.max(formatsList, new C13662(previousFormat));
    }

    public void onVideoPlayHeadPosition(int positionMs) {
        Logger.m173d("positionMs=%d", Integer.valueOf(positionMs));
    }

    public void onShowMediaControl() {
        Activity activity = getActivity();
        if (activity != null && (activity instanceof VideoActivity)) {
            ((VideoActivity) activity).showToolbar();
        }
    }

    public void onHideMediaControl() {
        Activity activity = getActivity();
        if (activity != null && (activity instanceof VideoActivity)) {
            ((VideoActivity) activity).hideToolBar();
        }
    }
}
