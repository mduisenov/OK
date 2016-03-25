package ru.ok.android.ui.video;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteButton;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.StartVideoUploadActivity;
import ru.ok.android.ui.utils.FabHelper;
import ru.ok.android.utils.ConfigurationPreferences;

public final class VideoWebFragment extends WebFragment implements OnClickListener {
    private VideoCastManager castManager;
    private FloatingActionButton fabVideo;

    class VideoWebViewClient extends DefaultWebViewClient {
        public VideoWebViewClient(Context context) {
            super(context);
        }

        public boolean isExternalUrl(String url) {
            return false;
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.castManager = VideoCastManager.getInstance();
        this.fabVideo = FabHelper.createVideoFab(getActivity(), getCoordinatorManager().coordinatorLayout);
        this.fabVideo.setOnClickListener(this);
    }

    protected void ensureFab() {
        super.ensureFab();
        getCoordinatorManager().ensureFab(this.fabVideo);
    }

    protected void removeFab() {
        super.removeFab();
        getCoordinatorManager().remove(this.fabVideo);
    }

    public void onResume() {
        super.onResume();
        this.castManager.incrementUiCounter();
    }

    public void onPause() {
        super.onPause();
        this.castManager.decrementUiCounter();
    }

    public String getStartUrl() {
        return ShortLinkUtils.getUrlByPath(ConfigurationPreferences.getInstance().getWebServer(), "video");
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getWebView().saveState(outState);
    }

    public DefaultWebViewClient createWebViewClient() {
        return new VideoWebViewClient(getContext());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (inflateMenuLocalized(2131689476, menu)) {
            ((MediaRouteButton) MenuItemCompat.getActionView(menu.findItem(C0158R.id.media_route_menu_item))).setRouteSelector(this.castManager.getMediaRouteSelector());
        }
    }

    public void onClick(View v) {
        StartVideoUploadActivity.startVideoUpload(getContext(), null);
        StatisticManager.getInstance().addStatisticEvent("video-upload-clicked", Pair.create("type", "native"));
    }

    protected int getTitleResId() {
        return 2131166606;
    }
}
