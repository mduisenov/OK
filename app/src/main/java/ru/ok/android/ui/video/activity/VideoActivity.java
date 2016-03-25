package ru.ok.android.ui.video.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import java.net.MalformedURLException;
import java.util.Iterator;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.video.check.VideoPreferences;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.video.OneLogVideo;
import ru.ok.android.ui.video.fragments.OkVideoFragment;
import ru.ok.android.ui.video.fragments.VideoCompatUtils;
import ru.ok.android.ui.video.fragments.compat.CompatVideoFragment;
import ru.ok.android.ui.video.fragments.movies.MoviesFragment.OnSelectMovieCallback;
import ru.ok.android.ui.video.fragments.movies.SimilarMoviesFragment;
import ru.ok.android.ui.video.fragments.target.VideoTargetFragment;
import ru.ok.android.ui.video.fragments.target.VideoTargetFragment.TargetListener;
import ru.ok.android.ui.video.player.PlayerUtil;
import ru.ok.android.ui.video.player.Quality;
import ru.ok.android.ui.video.player.VideoPlayerFragment;
import ru.ok.android.ui.video.player.YoutubeFragment;
import ru.ok.android.ui.video.player.cast.VideoCastFragment;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.bus.BusVideoHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.java.api.response.video.VideoGetResponse.VideoStatus;
import ru.ok.model.stream.banner.VideoData;
import ru.ok.model.video.Advertisement;
import ru.ok.model.video.LiveStream;
import ru.ok.model.video.MovieInfo;
import ru.ok.onelog.video.player.SimplePlayerOperation;

public final class VideoActivity extends BaseActivity implements VideoPlayBack, OnSelectMovieCallback, TargetListener {
    private static final String TAG;
    private VideoCastManager castManager;
    private MenuItem castMenuItem;
    private VideoGetResponse currentResponse;
    private ViewStub errorStub;
    private TextView errorView;
    private MenuItem likeMenuItem;
    private boolean requestError;
    private View rootView;
    private MenuItem shareMenuItem;
    protected ProgressBar spinnerView;
    private MenuItem targetMenuItem;
    private Toolbar toolbar;
    private String videoId;
    private String videoUrl;

    /* renamed from: ru.ok.android.ui.video.activity.VideoActivity.1 */
    class C13541 implements OnClickListener {
        C13541() {
        }

        public void onClick(View v) {
            if (VideoActivity.this.requestError && !TextUtils.isEmpty(VideoActivity.this.videoId)) {
                VideoActivity.this.hideError();
                VideoActivity.this.spinnerView.setVisibility(0);
                VideoActivity.this.initVideo();
                VideoActivity.this.requestError = false;
            }
        }
    }

    /* renamed from: ru.ok.android.ui.video.activity.VideoActivity.2 */
    class C13552 implements OnClickListener {
        C13552() {
        }

        public void onClick(View v) {
            VideoActivity.this.hideError();
            VideoActivity.this.spinnerView.setVisibility(0);
            VideoActivity.this.initVideo();
        }
    }

    /* renamed from: ru.ok.android.ui.video.activity.VideoActivity.3 */
    static /* synthetic */ class C13563 {
        static final /* synthetic */ int[] f121x1ee153ec;

        static {
            f121x1ee153ec = new int[VideoStatus.values().length];
            try {
                f121x1ee153ec[VideoStatus.ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f121x1ee153ec[VideoStatus.UPLOADING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f121x1ee153ec[VideoStatus.PROCESSING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f121x1ee153ec[VideoStatus.ON_MODERATION.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f121x1ee153ec[VideoStatus.BLOCKED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f121x1ee153ec[VideoStatus.CENSORED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f121x1ee153ec[VideoStatus.COPYRIGHTS_RESTRICTED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f121x1ee153ec[VideoStatus.UNAVAILABLE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f121x1ee153ec[VideoStatus.LIMITED_ACCESS.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    private static class ParseVideoGetException extends Exception {
        private final int errorResId;

        ParseVideoGetException(String message) {
            super(message);
            this.errorResId = 2131165791;
        }

        ParseVideoGetException(String message, VideoStatus status) {
            super(message);
            this.errorResId = getErrorStatusTextResValue(status);
        }

        int getErrorResId() {
            return this.errorResId;
        }

        static int getErrorStatusTextResValue(VideoStatus status) {
            switch (C13563.f121x1ee153ec[status.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    return 2131165843;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    return 2131166781;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    return 2131166403;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    return 2131166313;
                case Message.UUID_FIELD_NUMBER /*5*/:
                    return 2131165448;
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    return 2131165560;
                case Message.ATTACHES_FIELD_NUMBER /*7*/:
                    return 2131165642;
                case Message.TASKID_FIELD_NUMBER /*8*/:
                    return 2131166742;
                case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                    return 2131166041;
                default:
                    return 2131166747;
            }
        }
    }

    static {
        TAG = VideoActivity.class.getSimpleName();
    }

    protected int getBaseCompatLayoutId() {
        return 2130903093;
    }

    protected void onCreateLocalized(Bundle savedState) {
        Window window = getWindow();
        window.addFlags(NotificationCompat.FLAG_HIGH_PRIORITY);
        if (VERSION.SDK_INT >= 19) {
            window.setFlags(67108864, 67108864);
        }
        super.onCreateLocalized(savedState);
        this.castManager = VideoCastManager.getInstance();
        setContentView(getBaseCompatLayoutId());
        this.rootView = findViewById(2131624586);
        this.rootView.setOnClickListener(new C13541());
        this.errorStub = (ViewStub) findViewById(2131624589);
        this.toolbar = (Toolbar) findViewById(C0158R.id.toolbar);
        if (this.toolbar != null) {
            setSupportActionBar(this.toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (VERSION.SDK_INT >= 19) {
                this.toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
            }
            this.toolbar.setFitsSystemWindows(true);
        }
        this.spinnerView = (ProgressBar) findViewById(2131624588);
        Intent intent = getIntent();
        if (savedState == null) {
            this.videoId = intent.getStringExtra("VIDEO_ID");
            this.videoUrl = intent.getStringExtra("VIDEO_URL");
        } else {
            this.videoId = savedState.getString("VIDEO_ID");
            this.videoUrl = savedState.getString("VIDEO_URL");
        }
        initVideo();
    }

    protected void onResume() {
        super.onResume();
        this.castManager.incrementUiCounter();
    }

    protected void onPause() {
        super.onPause();
        this.castManager.decrementUiCounter();
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void initVideo() {
        if (this.likeMenuItem != null) {
            this.likeMenuItem.setVisible(false);
        }
        if (this.shareMenuItem != null) {
            this.shareMenuItem.setVisible(false);
        }
        if (!TextUtils.isEmpty(this.videoId)) {
            try {
                OneLogVideo.log(Long.parseLong(this.videoId), SimplePlayerOperation.loaded);
                BusVideoHelper.getVideoInfo(this.videoId);
            } catch (RuntimeException shouldNeverHappen) {
                Logger.m177e("Failed to convert videoId String to long: %s", shouldNeverHappen);
                showError(2131166747, new Object[0]);
                OneLogVideo.logCrash(Long.valueOf(this.videoId).longValue(), Log.getStackTraceString(shouldNeverHappen));
            }
        } else if (TextUtils.isEmpty(this.videoUrl)) {
            Logger.m176e("Unknown video state");
            showError(2131166747, new Object[0]);
        } else {
            this.spinnerView.setVisibility(8);
            showPlayer();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("VIDEO_ID", this.videoId);
        outState.putString("VIDEO_URL", this.videoUrl);
    }

    private boolean isUseExoPlayer() {
        return VERSION.SDK_INT > 15 && VideoPreferences.isUseExoPlayer(this) && ExoSupportUtils.isUsesExoForCpuModel();
    }

    private Fragment createVideoFragment(VideoGetResponse currentResponse, int position) {
        if (currentResponse != null && !currentResponse.isContentTypeVideo()) {
            try {
                return YoutubeFragment.forVideo(Uri.parse(currentResponse.urlExternal), currentResponse.id);
            } catch (MalformedURLException e) {
            }
        } else if (!TextUtils.isEmpty(this.videoUrl) && YoutubeFragment.isYouTubeUrl(this.videoUrl)) {
            try {
                return YoutubeFragment.forVideo(Uri.parse(this.videoUrl), currentResponse.id);
            } catch (MalformedURLException e2) {
            }
        }
        VideoData videoData = (VideoData) getIntent().getParcelableExtra("VIDEO_STAT_DATA");
        if (isUseExoPlayer()) {
            return OkVideoFragment.newInstance(currentResponse, this.videoUrl, videoData, (long) position);
        }
        return CompatVideoFragment.newInstance(currentResponse, this.videoUrl, videoData);
    }

    private void startTarget(Advertisement advertisement, Fragment playerFragment) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("target") == null) {
            VideoTargetFragment fragmentTarget = VideoTargetFragment.newInstance(advertisement, this.videoId);
            fragmentTarget.setPlayerFragment(playerFragment);
            fm.beginTransaction().add(C0263R.id.container, playerFragment, "player").add(C0263R.id.container, fragmentTarget, "target").hide(fragmentTarget).commit();
        }
    }

    private void showPlayer() {
        replacePlayer(this.currentResponse, 0);
    }

    public void replacePlayer(VideoGetResponse response, int position) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("player") == null) {
            fm.beginTransaction().replace(C0263R.id.container, createVideoFragment(response, position), "player").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }
    }

    public void addPlayer(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("player") == null) {
            fm.beginTransaction().add(C0263R.id.container, fragment, "player").commit();
        }
    }

    private void showSimilarVideos(VideoGetResponse response) {
        if (!TextUtils.isEmpty(this.videoId) && this.videoId.equals(response.id)) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag("similar movies") == null) {
                fm.beginTransaction().replace(C0263R.id.container, SimilarMoviesFragment.newInstance(response), "similar movies").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).setCustomAnimations(2130968599, 2130968598).commit();
                logSimpleEvent(SimplePlayerOperation.backscreen_shown);
            }
        }
    }

    public void showCastFragment(VideoGetResponse response, int position) {
        showToolbar();
        if (!TextUtils.isEmpty(this.videoId) && this.videoId.equals(response.id)) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag("cast") == null) {
                fm.beginTransaction().replace(C0263R.id.container, VideoCastFragment.newInstance(response, position), "cast").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).setCustomAnimations(2130968599, 2130968598).commit();
            }
        }
    }

    public void toggleOrientation() {
        if (isLandscape()) {
            setRequestedOrientation(7);
        } else {
            setRequestedOrientation(6);
        }
    }

    public boolean isLandscape() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels > metrics.heightPixels;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                finish();
                return true;
            case 2131625503:
                if (this.currentResponse == null || TextUtils.isEmpty(this.currentResponse.permalink)) {
                    return true;
                }
                startActivity(VideoCompatUtils.createShareIntentForLink(this, this.currentResponse.permalink, this.currentResponse.title));
                logSimpleEvent(SimplePlayerOperation.link);
                return true;
            case 2131625504:
                likeClickVideo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void likeClickVideo() {
        if (this.currentResponse != null && this.currentResponse.likeSummary != null) {
            if (this.currentResponse.likeSummary.isSelf()) {
                VideoCompatUtils.unLikeVideo(this.currentResponse);
                logSimpleEvent(SimplePlayerOperation.unlike);
                return;
            }
            VideoCompatUtils.likeVideo(this.currentResponse);
            logSimpleEvent(SimplePlayerOperation.like);
        }
    }

    private void logSimpleEvent(SimplePlayerOperation operation) {
        String videoId = this.currentResponse.id;
        if (!TextUtils.isEmpty(videoId)) {
            OneLogVideo.log(Long.valueOf(videoId).longValue(), operation, Quality.Auto);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689517, menu);
        this.castManager.addMediaRouterButton(menu, C0158R.id.media_route_menu_item);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        this.likeMenuItem = menu.findItem(2131625504);
        this.shareMenuItem = menu.findItem(2131625503);
        this.castMenuItem = menu.findItem(C0158R.id.media_route_menu_item);
        this.targetMenuItem = menu.findItem(2131625505);
        if (this.currentResponse != null) {
            VideoCompatUtils.validResponseForMenu(this.currentResponse, this.likeMenuItem, this.shareMenuItem);
        }
        return true;
    }

    protected void showError(int resError, Object... args) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(C0263R.id.container);
        if (currentFragment == null || currentFragment.isHidden()) {
            String strError = getStringLocalized(resError, args);
            if (this.errorView == null) {
                this.errorView = (TextView) this.errorStub.inflate();
            }
            this.errorView.setText(strError);
            this.errorView.setOnClickListener(new C13552());
            this.errorView.setVisibility(0);
        }
    }

    protected void hideError() {
        if (this.errorView != null) {
            this.errorView.setVisibility(8);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624267)
    public void onVideoInfoFetched(BusEvent event) {
        if (BusVideoHelper.getIds(event.bundleInput).contains(this.videoId)) {
            try {
                if (event.resultCode == -1) {
                    VideoGetResponse response = getResponseById(event, this.videoId);
                    if (response.status != VideoStatus.OK) {
                        throw new ParseVideoGetException("Failed to get video", response.status);
                    }
                    onCurrentResponseChanged(response);
                    setTitle(response.title);
                    if (!handleLiveStream(response)) {
                        Fragment player = createVideoFragment(response, this.currentResponse.fromTime * LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
                        if (response.advertisement != null) {
                            startTarget(response.advertisement, player);
                        } else {
                            addPlayer(player);
                            VideoCompatUtils.validResponseForMenu(response, this.likeMenuItem, this.shareMenuItem);
                        }
                        hideError();
                    }
                } else {
                    ErrorType error = ErrorType.from(event.bundleOutput);
                    if (error == ErrorType.NO_INTERNET) {
                        showError(2131165842, new Object[0]);
                        this.requestError = true;
                    } else {
                        showError(error.getDefaultErrorMessage(), new Object[0]);
                    }
                }
            } catch (ParseVideoGetException e) {
                Logger.m176e("error parsing response: " + e.getMessage());
                showError(e.getErrorResId(), new Object[0]);
                OneLogVideo.logCrash(Long.valueOf(this.videoId).longValue(), Log.getStackTraceString(e));
            }
            this.spinnerView.setVisibility(8);
        }
    }

    private boolean handleLiveStream(VideoGetResponse response) {
        if (!response.isLiveStreamResponse()) {
            return false;
        }
        LiveStream stream = response.liveStream;
        if (stream.getStart() > 0) {
            showError(2131166643, DateFormatter.formatDeltaTimeFuture(this, stream.getStart() * 1000));
            return true;
        } else if (stream.getEnd() < 0) {
            showError(2131166639, new Object[0]);
            return true;
        } else {
            onCurrentResponseChanged(response);
            showPlayer();
            VideoCompatUtils.validResponseForMenu(response, this.likeMenuItem, this.shareMenuItem);
            hideError();
            return true;
        }
    }

    private VideoGetResponse getResponseById(BusEvent event, String videoId) throws ParseVideoGetException {
        Iterator i$ = event.bundleOutput.getParcelableArrayList("VIDEO_INFOS").iterator();
        while (i$.hasNext()) {
            VideoGetResponse response = (VideoGetResponse) i$.next();
            if (TextUtils.equals(response.id, videoId)) {
                return response;
            }
        }
        throw new ParseVideoGetException("No response for id=" + videoId);
    }

    @Subscribe(on = 2131623946, to = 2131624185)
    public void onVideoLiked(BusEvent event) {
        if (PlayerUtil.videoLiked(this, event, this.currentResponse)) {
            this.currentResponse.likeSummary.setSelf(true);
            VideoCompatUtils.notifyLikeSummary(this.likeMenuItem, this.currentResponse.likeSummary);
            Fragment similarFragment = getSupportFragmentManager().findFragmentByTag("similar movies");
            if (similarFragment != null && (similarFragment instanceof SimilarMoviesFragment)) {
                ((SimilarMoviesFragment) similarFragment).setLikeValue(true);
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624262)
    public void onVideoUnlike(BusEvent event) {
        if (PlayerUtil.videoUnlike(this, event, this.currentResponse) && this.currentResponse != null && this.currentResponse.likeSummary != null) {
            this.currentResponse.likeSummary.setSelf(false);
            VideoCompatUtils.notifyLikeSummary(this.likeMenuItem, this.currentResponse.likeSummary);
            Fragment similarFragment = getSupportFragmentManager().findFragmentByTag("similar movies");
            if (similarFragment != null && (similarFragment instanceof SimilarMoviesFragment)) {
                ((SimilarMoviesFragment) similarFragment).setLikeValue(false);
            }
        }
    }

    public void onVideoFinish() {
        if (this.currentResponse == null || !isUseExoPlayer() || this.currentResponse.isLiveStreamResponse()) {
            finish();
        } else {
            showSimilarVideos(this.currentResponse);
        }
    }

    public void onRepeatClick() {
        showPlayer();
        OneLogVideo.log(Long.valueOf(this.videoId).longValue(), SimplePlayerOperation.replay, Quality.Auto);
    }

    public void onTargetFinish(VideoTargetFragment targetFragment, Fragment fragmentPlayer) {
        VideoCompatUtils.validResponseForMenu(this.currentResponse, this.likeMenuItem, this.shareMenuItem);
        setVisibilityTargetClickText(false, "");
        if (fragmentPlayer != null) {
            getSupportFragmentManager().beginTransaction().show(fragmentPlayer).remove(targetFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            if ((fragmentPlayer instanceof VideoPlayerFragment) && ((VideoPlayerFragment) fragmentPlayer).getPlayer() != null) {
                VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentPlayer;
                videoPlayerFragment.getPlayer().setPlayWhenReady(true);
                videoPlayerFragment.showPlayer();
                return;
            } else if (fragmentPlayer instanceof CompatVideoFragment) {
                CompatVideoFragment compatVideoFragment = (CompatVideoFragment) fragmentPlayer;
                compatVideoFragment.showPlayer();
                compatVideoFragment.setPlayWhenReady(true);
                return;
            } else {
                return;
            }
        }
        replacePlayer(this.currentResponse, this.currentResponse.fromTime * LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
    }

    public void onTargetStart(VideoTargetFragment targetFragment, Fragment fragmentPlayer) {
        if (fragmentPlayer != null) {
            if (fragmentPlayer instanceof VideoPlayerFragment) {
                VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentPlayer;
                videoPlayerFragment.getPlayer().setPlayWhenReady(false);
                videoPlayerFragment.hidePlayer();
            }
            if (fragmentPlayer instanceof CompatVideoFragment) {
                CompatVideoFragment compatVideoFragment = (CompatVideoFragment) fragmentPlayer;
                compatVideoFragment.setPlayWhenReady(false);
                compatVideoFragment.hidePlayer();
            }
            getSupportFragmentManager().beginTransaction().hide(fragmentPlayer).show(targetFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }
    }

    public void setVisibilityMenuItem(boolean value) {
        if (this.likeMenuItem != null) {
            this.likeMenuItem.setVisible(value);
        }
        if (this.shareMenuItem != null) {
            this.shareMenuItem.setVisible(value);
        }
        if (this.castMenuItem != null) {
            this.castMenuItem.setVisible(value);
        }
    }

    public void setVisibilityTargetClickText(boolean value, String targetClickText) {
        if (this.targetMenuItem != null) {
            if (targetClickText != null) {
                this.targetMenuItem.setTitle(targetClickText);
            } else {
                this.targetMenuItem.setTitle("");
            }
            this.targetMenuItem.setVisible(value);
        }
    }

    public void onCurrentResponseChanged(VideoGetResponse newResponse) {
        if (!(newResponse == null || TextUtils.isEmpty(newResponse.title))) {
            this.toolbar.setTitle(newResponse.title);
        }
        this.currentResponse = newResponse;
    }

    public void onSelectMovie(MovieInfo movie) {
        if (movie != null) {
            String id = movie.getId();
            try {
                Long.parseLong(id);
                this.videoId = id;
                this.spinnerView.setVisibility(0);
                initVideo();
                removeAll();
                if (!TextUtils.isEmpty(movie.title)) {
                    this.toolbar.setTitle(movie.title);
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to show video: " + e);
                OneLogVideo.logError(Long.parseLong(this.videoId), Log.getStackTraceString(e));
            }
        }
    }

    public void removeAll() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                transaction.remove(fragment);
            }
        }
        transaction.commit();
    }

    public void showToolbar() {
        if (this.toolbar != null) {
            this.toolbar.setVisibility(0);
        }
    }

    public void hideToolBar() {
        if (this.toolbar != null) {
            this.toolbar.setVisibility(8);
        }
    }
}
