package ru.ok.android.ui.custom.profiles;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Arrays;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.ui.custom.CompositePresentView;
import ru.ok.android.ui.stream.music.PlayerStateHolder;
import ru.ok.android.ui.stream.view.ProfilePresentTrackView;
import ru.ok.android.ui.stream.view.ProfilePresentTrackView.OnPlayTrackListener;
import ru.ok.android.ui.users.fragments.data.UserMergedPresent;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.bus.BusMusicHelper;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Track;

public class PresentView extends FrameLayout implements OnPlayTrackListener {
    public CompositePresentView image;
    private final PlayerStateHolder playerStateHolder;
    private UserMergedPresent present;
    private ProfilePresentTrackView presentTrackView;
    private ViewStub stubTrack;

    public PresentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.playerStateHolder = new PlayerStateHolder();
        initViews(context, attrs);
    }

    public PresentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.playerStateHolder = new PlayerStateHolder();
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        View mainView = LocalizationManager.inflate(context, 2130903404, (ViewGroup) this, true);
        this.image = (CompositePresentView) mainView.findViewById(C0263R.id.image);
        this.stubTrack = (ViewStub) mainView.findViewById(2131625256);
    }

    public UserMergedPresent getPresent() {
        return this.present;
    }

    public void setPresent(UserMergedPresent present, boolean isCurrent) {
        int i = 8;
        this.present = present;
        if (present != null) {
            int size = (int) Utils.dipToPixels(90.0f);
            this.image.setAnimationEnabled(false);
            this.image.setPresentType(present, new Point(size, size));
            if (present.trackId > 0) {
                ViewStub viewStub = this.stubTrack;
                if (isCurrent) {
                    i = 0;
                }
                viewStub.setVisibility(i);
                if (isCurrent) {
                    if (this.presentTrackView == null) {
                        this.presentTrackView = (ProfilePresentTrackView) findViewById(2131625255);
                        this.presentTrackView.setPlayState();
                        this.presentTrackView.setPlayerStateHolder(this.playerStateHolder);
                        this.presentTrackView.setOnPlayTrackListener(this);
                    }
                    this.presentTrackView.setTrackId(Long.valueOf(present.trackId).longValue());
                    this.presentTrackView.setVisibility(0);
                    return;
                }
                return;
            }
            this.stubTrack.setVisibility(8);
            return;
        }
        this.stubTrack.setVisibility(8);
    }

    public void onPlayTrack(long trackId) {
        BusMusicHelper.getCustomTrack(trackId);
    }

    @Subscribe(on = 2131623946, to = 2131624199)
    public void onGetCustomTrack(BusEvent event) {
        if (event != null) {
            Track[] tracks = (Track[]) event.bundleOutput.getParcelableArray("key_places_complaint_result");
            if (tracks != null && tracks.length > 0) {
                MusicService.startPlayMusic(OdnoklassnikiApplication.getContext(), 0, new ArrayList(Arrays.asList(tracks)), MusicListType.NO_DIRECTION);
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        GlobalBus.register(this);
        this.playerStateHolder.init();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        GlobalBus.unregister(this);
        this.playerStateHolder.clear();
    }
}
