package ru.ok.android.ui.video.player;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.video.OneLogVideo;
import ru.ok.android.ui.video.activity.VideoActivity;
import ru.ok.android.ui.video.fragments.SelectQualityDialog;
import ru.ok.android.ui.video.fragments.SelectQualityDialog.Listener;
import ru.ok.android.ui.video.player.VideoControllerView.ControlInterface;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.response.video.VideoGetResponse;

public abstract class AbstractVideoFragment extends BaseFragment implements Listener {
    protected ViewStub errorStub;
    private TextView errorView;
    protected View gradientView;
    protected VideoControllerView mediaController;
    protected final OnTouchListener onTouchPlayerView;
    protected long playerPosition;
    private boolean playingVideo;
    private ImageButton repeatButton;
    protected View spinnerView;
    private View viewRoot;

    /* renamed from: ru.ok.android.ui.video.player.AbstractVideoFragment.1 */
    class C13751 implements OnClickListener {
        C13751() {
        }

        public void onClick(View v) {
            AbstractVideoFragment.this.mediaController.onRepeatClick();
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.AbstractVideoFragment.2 */
    class C13762 implements OnSystemUiVisibilityChangeListener {
        C13762() {
        }

        public void onSystemUiVisibilityChange(int visibility) {
            if (!((visibility & 4) != 0)) {
                AbstractVideoFragment.this.mediaController.show();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.video.player.AbstractVideoFragment.3 */
    class C13773 implements OnTouchListener {
        C13773() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            AbstractVideoFragment.this.onTouchSurfaceView(motionEvent);
            return false;
        }
    }

    private class VideoViewCallbacks implements ControlInterface {
        private VideoViewCallbacks() {
        }

        public void toggleFullScreen() {
            FragmentActivity activity = AbstractVideoFragment.this.getActivity();
            if (activity instanceof VideoActivity) {
                ((VideoActivity) activity).toggleOrientation();
            }
        }

        public boolean isFullScreen() {
            FragmentActivity activity = AbstractVideoFragment.this.getActivity();
            return (activity instanceof VideoActivity) && ((VideoActivity) activity).isLandscape();
        }

        public void onShowingChanged(boolean isShowing) {
            FragmentActivity a = AbstractVideoFragment.this.getActivity();
            if (a instanceof VideoActivity) {
                VideoActivity activity = (VideoActivity) a;
                if (isShowing) {
                    activity.showToolbar();
                } else {
                    activity.hideToolBar();
                }
            }
            AbstractVideoFragment.this.onFullscreenOrControlsVisibilityChanged();
        }

        public void onQualityClick() {
            ArrayList<Quality> qualities = AbstractVideoFragment.this.getQualities();
            if (qualities != null && !qualities.isEmpty()) {
                SelectQualityDialog.show(AbstractVideoFragment.this, qualities, AbstractVideoFragment.this.getCurrentQuality());
            }
        }

        public void togglePlayPause(boolean isPlaying) {
            AbstractVideoFragment.this.onClickPlayPause(isPlaying);
            Logger.m172d("click play or pause");
        }

        public void seek(long positionInSec) {
            VideoGetResponse video = AbstractVideoFragment.this.getVideo();
            long logPosition = positionInSec / 1000;
            if (video != null && logPosition > 0) {
                String videoId = video.id;
                if (!TextUtils.isEmpty(videoId)) {
                    OneLogVideo.logSeek(Long.valueOf(videoId).longValue(), AbstractVideoFragment.this.getCurrentQuality(), logPosition);
                }
            }
        }
    }

    @Nullable
    protected abstract Quality getCurrentQuality();

    @Nullable
    protected abstract ArrayList<Quality> getQualities();

    public AbstractVideoFragment() {
        this.onTouchPlayerView = new C13773();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.viewRoot = LocalizationManager.inflate(getContext(), getLayoutId(), container, false);
        this.gradientView = this.viewRoot.findViewById(2131624527);
        this.gradientView.setOnTouchListener(this.onTouchPlayerView);
        this.spinnerView = this.viewRoot.findViewById(2131624536);
        this.mediaController = createController(getActivity());
        this.mediaController.setControlInterface(new VideoViewCallbacks());
        this.repeatButton = (ImageButton) this.viewRoot.findViewById(2131624865);
        if (this.repeatButton != null) {
            this.repeatButton.setOnClickListener(new C13751());
        }
        this.mediaController.setAnchorView((ViewGroup) this.viewRoot.findViewById(2131624864));
        this.errorStub = (ViewStub) this.viewRoot.findViewById(2131624589);
        return this.viewRoot;
    }

    protected VideoControllerView createController(Context context) {
        return new VideoControllerView(context);
    }

    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        if (savedState != null) {
            this.playingVideo = savedState.getBoolean("state.playing_video", false);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mediaController.updateFullScreen();
        onFullscreenOrControlsVisibilityChanged();
    }

    public void onStart() {
        super.onStart();
        getActivity().getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new C13762());
    }

    public void onResume() {
        super.onResume();
        this.mediaController.updatePausePlay();
    }

    public void onPause() {
        super.onPause();
        this.mediaController.pause();
    }

    public void onStop() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(null);
        decorView.setSystemUiVisibility(0);
        super.onStop();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("state.playing_video", this.playingVideo);
    }

    protected void onPlayerReleased(long position) {
        this.playerPosition = position;
        Logger.m172d("player release");
    }

    public void onVideoEnd() {
        Logger.m172d("end video");
        this.mediaController.setEnabled(false);
        hideAllControls();
    }

    protected void displayVideoBuffering() {
        showOnlyPlaybackControl(this.spinnerView);
        Logger.m172d("state: buffering");
        this.mediaController.updatePausePlay();
    }

    protected void displayVideoEnded() {
        if (this.playingVideo) {
            this.mediaController.show(0);
            this.mediaController.updateProgressToFinish();
            onVideoEnd();
            this.playingVideo = false;
            Logger.m172d("state: ended");
            this.mediaController.updatePausePlay();
        }
    }

    protected void displayVideoPreparing() {
        hideError();
    }

    protected void displayVideoPlaying() {
        showOnlyPlaybackControl(this.mediaController.getPauseButton());
        if (!this.playingVideo) {
            this.mediaController.show();
            this.playingVideo = true;
        }
        Logger.m172d("state: ready");
        this.mediaController.updatePausePlay();
    }

    protected void displayVideoPaused() {
        showOnlyPlaybackControl(this.mediaController.getPauseButton());
        this.mediaController.show(0);
        Logger.m172d("state: paused");
        this.mediaController.updatePausePlay();
    }

    protected final void showError(int resError) {
        showError(resError, null);
    }

    protected void showError(int resError, OnClickListener onErrorClick) {
        showOnlyPlaybackControl(null);
        if (this.errorView == null) {
            this.errorView = (TextView) this.errorStub.inflate();
        }
        this.errorView.setText(resError);
        this.errorView.setVisibility(0);
        this.errorView.setOnClickListener(onErrorClick);
    }

    protected void hideError() {
        if (this.errorView != null) {
            this.errorView.setVisibility(8);
        }
    }

    protected void showOnlyPlaybackControl(View control) {
        for (View v : Arrays.asList(new View[]{this.spinnerView, this.mediaController.getPauseButton(), this.repeatButton, this.errorView})) {
            if (v != null) {
                v.setVisibility(v == control ? 0 : 8);
            }
        }
    }

    protected void hideAllControls() {
        this.spinnerView.setVisibility(8);
        this.mediaController.getPauseButton().setVisibility(8);
        if (this.repeatButton != null) {
            this.repeatButton.setVisibility(8);
        }
    }

    protected void onClickPlayPause(boolean isPlaying) {
    }

    private void onFullscreenOrControlsVisibilityChanged() {
        if (this.playingVideo) {
            FragmentActivity activity = getActivity();
            if (activity instanceof VideoActivity) {
                boolean fullscreen = ((VideoActivity) activity).isLandscape();
                boolean showingControls = this.mediaController.isShowing();
                View decorView = activity.getWindow().getDecorView();
                if (!fullscreen || showingControls) {
                    decorView.setSystemUiVisibility(1792);
                } else {
                    decorView.setSystemUiVisibility(3846);
                }
            }
        }
    }

    protected void onTouchSurfaceView(MotionEvent motionEvent) {
        Logger.m172d("On touch to surface");
        if (motionEvent.getAction() == 0) {
            this.mediaController.toggle();
        }
    }

    protected VideoGetResponse getVideo() {
        return (VideoGetResponse) getArguments().getParcelable("video");
    }
}
