package ru.ok.android.ui.stream.music;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.music.MusicInfoContainer;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.app.MusicService.InformationState;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.bus.BusProtocol;

public final class PlayerStateHolder {
    private InformationState lastPlayState;
    private final Map<PlayerStateHolderListener, Object> listeners;
    private Set<PlayerStateHolderListener> listenersCopy;
    private MusicInfoContainer musicInfoContainer;
    private final Object obj;
    private float progress;

    public interface PlayerStateHolderListener {
        void onMusicStateChanged();
    }

    public PlayerStateHolder() {
        this.listeners = new WeakHashMap();
        this.obj = new Object();
        this.listenersCopy = new HashSet();
    }

    public void addStateChangeListener(PlayerStateHolderListener listener) {
        this.listeners.put(listener, this.obj);
    }

    public void init() {
        GlobalBus.register(this);
        onStreamMediaStatus(MusicService.getLastState());
    }

    public void close() {
        GlobalBus.unregister(this);
    }

    public void clear() {
        this.listeners.clear();
    }

    @Subscribe(on = 2131623946, to = 2131624243)
    public void onPlayTrackError(BusEvent event) {
        if (event != null) {
            this.musicInfoContainer = (MusicInfoContainer) event.bundleOutput.getParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER);
            this.progress = 0.0f;
            this.lastPlayState = InformationState.ERROR;
            Logger.m173d("Error received: MusicInfo: %s", this.musicInfoContainer);
            notifyListeners();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624252)
    public void onStreamMediaStatus(BusEvent event) {
        if (event != null) {
            this.lastPlayState = (InformationState) event.bundleOutput.getSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE);
            long oldTrackId = this.musicInfoContainer != null ? this.musicInfoContainer.track.id : -1;
            this.musicInfoContainer = (MusicInfoContainer) event.bundleOutput.getParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER);
            if (this.musicInfoContainer == null || oldTrackId != this.musicInfoContainer.track.id) {
                this.progress = 0.0f;
            }
            Logger.m173d("State: %s, MusicInfo: %s", this.lastPlayState, this.musicInfoContainer);
            notifyListeners();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624251)
    public final void onProgressChange(BusEvent event) {
        this.progress = ((float) event.bundleOutput.getInt(BusProtocol.PREF_MEDIA_PLAYER_PROGRESS, 0)) / ((float) event.bundleOutput.getInt(BusProtocol.PREF_MEDIA_PLAYER_DURATION, 1));
        notifyListeners();
    }

    private void notifyListeners() {
        this.listenersCopy.addAll(this.listeners.keySet());
        for (PlayerStateHolderListener listener : this.listenersCopy) {
            listener.onMusicStateChanged();
        }
        this.listenersCopy.clear();
    }

    public float getProgress(long trackId) {
        if (isSongCurrent(trackId)) {
            return this.progress;
        }
        return 0.0f;
    }

    public boolean isSongCurrent(long trackId) {
        if (this.musicInfoContainer != null && trackId == this.musicInfoContainer.track.id) {
            return true;
        }
        return false;
    }

    public long getCurrentPlayingSong() {
        if (this.musicInfoContainer == null) {
            return 0;
        }
        return this.musicInfoContainer.track.id;
    }

    public boolean isSongPlaying(long trackId) {
        if (isSongCurrent(trackId) && this.lastPlayState == InformationState.PLAY) {
            return true;
        }
        return false;
    }

    public boolean isSongBuffering(long trackId) {
        if (!isSongCurrent(trackId)) {
            return false;
        }
        if (this.lastPlayState == InformationState.START || this.lastPlayState == InformationState.DATA_QUERY || this.lastPlayState == InformationState.BUFFERED) {
            return true;
        }
        return false;
    }

    public boolean isSongError(long trackId) {
        if (isSongCurrent(trackId) && this.lastPlayState == InformationState.ERROR) {
            return true;
        }
        return false;
    }

    public int getSecondsLeft(long trackId) {
        if (isSongPlaying(trackId)) {
            return (int) (((float) this.musicInfoContainer.playTrackInfo.duration) * (1.0f - this.progress));
        }
        return -1;
    }
}
