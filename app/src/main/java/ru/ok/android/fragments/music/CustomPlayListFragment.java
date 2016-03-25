package ru.ok.android.fragments.music;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import java.util.Arrays;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.ui.fragments.handlers.MusicPlayListHandler;
import ru.ok.android.utils.bus.BusMusicHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Track;

public class CustomPlayListFragment extends BasePlayListFragment {
    public void onPrepareOptionsMenu(Menu menu) {
        if (getMode().onPrepareOptionsMenu(menu, this)) {
            super.onPrepareOptionsMenu(menu);
        }
    }

    protected CharSequence getTitle() {
        LocalizationManager localizationManager = LocalizationManager.from(OdnoklassnikiApplication.getContext());
        if (localizationManager == null) {
            return "";
        }
        return localizationManager.getString(2131166223);
    }

    public void onStop() {
        super.onStop();
        GlobalBus.unregister(this);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().startService(MusicService.getStateStreamMediaPlayerIntent(getActivity()));
    }

    public void onStart() {
        super.onStart();
        GlobalBus.register(this);
    }

    public boolean isPlayFloatingButtonRequired() {
        return false;
    }

    @Subscribe(on = 2131623946, to = 2131624199)
    public void onGetCustomTrack(BusEvent event) {
        if (event != null) {
            Track[] tracks = (Track[]) event.bundleOutput.getParcelableArray("key_places_complaint_result");
            if (tracks != null) {
                if (tracks.length > 0) {
                    this.handler.setData(Arrays.asList(tracks));
                    this.handler.setNoneRefresh();
                    this.handler.onRefreshComplete();
                }
                this.handler.onResult();
                return;
            }
            this.handler.onError(event.bundleOutput.get("key_exception_custom_track_result"));
        }
    }

    protected void requestData(MusicPlayListHandler handler) {
        handler.setNoneRefresh();
        BusMusicHelper.getCustomTrack(getArguments().getLong("extra_track_id"));
    }

    protected int getLayoutId() {
        return 2130903392;
    }

    public static Bundle newArguments(long trackId) {
        Bundle bundle = new Bundle();
        bundle.putLong("extra_track_id", trackId);
        return bundle;
    }
}
