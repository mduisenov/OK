package ru.ok.android.utils.controls;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.fragments.music.TrackSelectionControl;
import ru.ok.android.model.music.MusicInfoContainer;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.ui.adapters.CheckChangeAdapter.OnCheckStateChangeListener;
import ru.ok.android.ui.adapters.music.DotsCursorAdapter.OnDotsClickListener;
import ru.ok.android.ui.fragments.handlers.BaseMusicPlayListHandler;
import ru.ok.android.ui.fragments.handlers.BaseMusicPlayListHandler.OnGetNextTracksListener;
import ru.ok.android.ui.fragments.handlers.BaseMusicPlayListHandler.OnSelectTrackListener;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.music.MusicPlayerUtils;
import ru.ok.model.wmf.Track;

public abstract class TracksListControl implements OnDotsClickListener<Track>, OnGetNextTracksListener, OnSelectTrackListener {
    protected Context context;
    protected final MusicListType currentType;
    protected BaseMusicPlayListHandler playListHandler;
    private String playlistId;

    public abstract void onDotsClick(Track track, View view);

    public TracksListControl(Context context, BaseMusicPlayListHandler playListFragment, MusicListType musicListType) {
        this.context = context;
        this.playListHandler = playListFragment;
        this.currentType = musicListType;
        playListFragment.addSelectTrackListener(this);
        playListFragment.setOnGetNextTracksListener(this);
        playListFragment.addOnDotsClickTrackListener(this);
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public void setData(Collection<? extends Track> tracks, boolean more) {
        this.playListHandler.setData(tracks);
        if (!more) {
            this.playListHandler.setNoneRefresh();
        }
        int position = this.playListHandler.getPlayPosition();
        if (position > 0) {
            this.playListHandler.scrollToPosition(position);
        }
        this.playListHandler.onResult();
    }

    public void addData(Collection<? extends Track> tracks, boolean more) {
        if (!more) {
            this.playListHandler.setNoneRefresh();
        }
        this.playListHandler.addData(tracks);
        this.playListHandler.onResult();
    }

    public Collection<? extends Track> getData() {
        return this.playListHandler.getData();
    }

    public Track[] getSelectedData() {
        return this.playListHandler.getSelectedData();
    }

    public void setOnCheckStateChangeListener(OnCheckStateChangeListener listener) {
        this.playListHandler.setOnCheckStateChangeListener(listener);
    }

    public void onSelectTrack(AdapterView<?> adapterView, int position, List<? extends Track> tracks) {
        int headers = ((ListView) adapterView).getHeaderViewsCount();
        if (position >= headers) {
            MusicService.startPlayMusic(this.context, position - headers, (ArrayList) tracks, this.currentType, this.playlistId);
        }
    }

    public boolean switchToSelectionMode(TrackSelectionControl trackSelectionMode) {
        return this.playListHandler.switchToSelectionMode(trackSelectionMode);
    }

    public void switchToStandardMode() {
        this.playListHandler.switchToStandardMode();
    }

    public void onStreamMediaStatus(BusEvent event) {
        MusicInfoContainer musicInfoContainer = (MusicInfoContainer) event.bundleOutput.getParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER);
        if (musicInfoContainer == null || !MusicPlayerUtils.isShowPlay(event.bundleOutput, this.currentType, this.playlistId)) {
            this.playListHandler.hidePlay();
        } else {
            this.playListHandler.showPlay(musicInfoContainer.track);
        }
    }
}
