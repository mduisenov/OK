package ru.ok.android.utils.controls;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.widget.Toast;
import ru.ok.android.C0206R;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.model.music.MusicInfoContainer;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.app.MusicService.InformationState;
import ru.ok.android.services.app.MusicService.MusicState;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.dialogs.ChangeTrackStateBase.OnChangeTrackStateListener;
import ru.ok.android.ui.fragments.PlayerFragment;
import ru.ok.android.ui.fragments.PlayerFragment.OnControlListener;
import ru.ok.android.ui.fragments.PlayerFragment.OnPlayControlListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.controls.PlayerSetter.RepeatState;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Track;

public class PlayerControl implements OnChangeTrackStateListener, OnControlListener, OnPlayControlListener {
    private DataCache cache;
    private final Activity context;
    private DataUpdateReceiver dataUpdateReceiver;
    private Messenger mMessenger;
    private final MusicFragmentMode mode;
    private long musicServerTripTime;
    private final PlayerFragment playerFragment;
    private boolean seekNow;

    /* renamed from: ru.ok.android.utils.controls.PlayerControl.1 */
    class C14391 extends Handler {
        C14391() {
        }

        public void handleMessage(Message msg) {
            if (PlayerControl.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    class DataCache {
        private MusicInfoContainer musicInfoContainer;

        DataCache() {
        }

        Track getTrack() {
            return this.musicInfoContainer == null ? null : this.musicInfoContainer.track;
        }

        public void updateCache(MusicInfoContainer musicInfoContainer) {
            this.musicInfoContainer = musicInfoContainer;
        }
    }

    public class DataUpdateReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("refresh_player_progress")) {
                onProgressChange(intent);
            } else if (intent.getAction().equals("finish_play_track")) {
                setFinishPlay();
            } else if (intent.getAction().equals("ru.odnoklassniki.android.music.play.state")) {
                setState(intent);
            } else if (intent.getAction().equals("start_play_track")) {
                onStart(intent);
            } else if (intent.getAction().equals("set_seek_finish")) {
                PlayerControl.this.seekNow = false;
            } else if (intent.getAction().equals("buffering_play_track")) {
                onBuffering();
            } else if (intent.getAction().equals("set_shuffle")) {
                setShuffle(intent);
            } else if (intent.getAction().equals("set_repeat")) {
                setRepeat(intent);
            } else if (!intent.getAction().equals("critical_download_speed")) {
            }
        }

        private void onProgressChange(Intent intent) {
            int progress = 0;
            int valueSec = intent.getIntExtra("play_progress_update_sec", 0);
            int duration = intent.getIntExtra("play_track_duration", 0);
            if (duration != 0) {
                if (duration != 0) {
                    progress = (int) ((((double) valueSec) / ((double) duration)) * 100.0d);
                }
                PlayerControl.this.playerFragment.setProgress(progress, duration, valueSec);
            }
        }

        private void setState(Intent intent) {
            MusicState playing = (MusicState) intent.getSerializableExtra("playState");
            int downloadValue = intent.getIntExtra("downloadState", 0);
            boolean shuffle = intent.getBooleanExtra("shuffleState", false);
            MusicInfoContainer musicInfoContainer = (MusicInfoContainer) intent.getParcelableExtra("music_info");
            RepeatState repeat = (RepeatState) intent.getSerializableExtra("repeatState");
            PlayerControl.this.playerFragment.setDownloadProgress(downloadValue);
            PlayerControl.this.playerFragment.setShuffle(shuffle);
            PlayerControl.this.playerFragment.setRepeat(repeat);
            PlayerControl.this.playerFragment.callUpdate();
            onProgressChange(intent);
            PlayerControl.this.cache.updateCache(musicInfoContainer);
        }

        private void onStart(Intent intent) {
            PlayerControl.this.cache.updateCache((MusicInfoContainer) intent.getParcelableExtra("music_info"));
        }

        private void setRepeat(Intent intent) {
            switch (intent.getIntExtra("play_repeat_update", 0)) {
                case RECEIVED_VALUE:
                    PlayerControl.this.playerFragment.setRepeat(RepeatState.none);
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    PlayerControl.this.playerFragment.setRepeat(RepeatState.repeat);
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    PlayerControl.this.playerFragment.setRepeat(RepeatState.repeatOne);
                default:
            }
        }

        private void setShuffle(Intent intent) {
            PlayerControl.this.playerFragment.setShuffle(intent.getBooleanExtra("play_shuffle_update", false));
        }

        private void onBuffering() {
            PlayerControl.this.playerFragment.setEnabledProgress(true);
            PlayerControl.this.playerFragment.hideLoadProgress();
        }

        private void setFinishPlay() {
            PlayerControl.this.playerFragment.setProgress(0, 0, 0);
            PlayerControl.this.playerFragment.setEnabledProgress(false);
        }
    }

    public PlayerControl(Activity context, PlayerFragment playerFragment) {
        this.cache = new DataCache();
        this.seekNow = false;
        this.mMessenger = new Messenger(new C14391());
        this.musicServerTripTime = 0;
        this.context = context;
        this.playerFragment = playerFragment;
        MusicFragmentMode mode = (MusicFragmentMode) playerFragment.getArguments().getParcelable("music-fragment-mode");
        if (mode == null) {
            mode = MusicFragmentMode.STANDARD;
        }
        this.mode = mode;
        this.playerFragment.setOnControlListener(this);
        this.playerFragment.setPlayControlListener(this);
    }

    public void registerBus() {
        GlobalBus.register(this);
    }

    public void unRegisterBus() {
        GlobalBus.unregister(this);
    }

    public void registerReceiver() {
        if (this.dataUpdateReceiver == null) {
            this.dataUpdateReceiver = new DataUpdateReceiver();
        }
        for (String action : new String[]{"refresh_player_progress", "start_play_track", "finish_play_track", "set_shuffle", "set_repeat", "buffering_play_track", "play_track", "pause_play_track", "ru.odnoklassniki.android.music.play.state", "set_seek_finish", "critical_download_speed"}) {
            LocalBroadcastManager.getInstance(this.context).registerReceiver(this.dataUpdateReceiver, new IntentFilter(action));
        }
    }

    public void unRegisterReceiver() {
        if (this.dataUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(this.context).unregisterReceiver(this.dataUpdateReceiver);
        }
    }

    private void play() {
        this.context.startService(MusicService.getPlayIntent(this.context));
    }

    private boolean seekToPosition(int position) {
        this.context.startService(MusicService.getSeekIntent(this.context, position));
        return true;
    }

    private void pause() {
        this.context.startService(MusicService.getTogglePlayIntent(this.context));
    }

    private void repeat() {
        this.context.startService(MusicService.getRepeatIntent(this.context));
    }

    private void shuffle() {
        this.context.startService(MusicService.getShuffleIntent(this.context));
    }

    public void onResume() {
        this.context.startService(MusicService.getStateIntent(this.context));
    }

    public void onStop() {
    }

    public void onPlayMusic() {
        play();
    }

    public void onPauseMusic() {
        pause();
    }

    public void onShuffleUpdate() {
        shuffle();
    }

    public void onRepeatUpdate() {
        repeat();
    }

    public void onStartSeek() {
        this.seekNow = true;
    }

    public void onChangeTrackPosition(int position) {
        seekToPosition(position);
    }

    public void onChangeTrackSeekPosition(int position) {
        if (this.cache.getTrack() != null) {
            int duration = this.cache.getTrack().duration;
            this.playerFragment.setTime((int) (((float) position) * (((float) duration) / 100.0f)), duration);
        }
    }

    public void onSearchAlbumMusic() {
        if (this.cache.getTrack() != null && this.cache.getTrack().album != null) {
            NavigationHelper.showAlbumPage(this.context, this.cache.getTrack().album, this.mode);
        }
    }

    public void onSearchArtistMusic() {
        if (this.cache.getTrack() != null && this.cache.getTrack().artist != null) {
            NavigationHelper.showArtistPage(this.context, this.cache.getTrack().artist, this.mode);
        }
    }

    public void onSearchMusic() {
        if (this.cache.getTrack() == null) {
            return;
        }
        if (this.cache.getTrack().artist != null || this.cache.getTrack() != null) {
            Artist artist = this.cache.getTrack().artist;
            Album album = this.cache.getTrack().album;
            if (artist != null && album != null) {
                NavigationHelper.showSearchMusic(this.context, artist.name + " " + album.name, this.mode);
            } else if (artist == null && album != null) {
                NavigationHelper.showSearchMusic(this.context, album.name, this.mode);
            } else if (artist != null && album == null) {
                NavigationHelper.showSearchMusic(this.context, artist.name, this.mode);
            }
        }
    }

    public boolean onHandleMessage(Message msg) {
        int messageId;
        switch (msg.what) {
            case 153:
                messageId = 2131166239;
                break;
            case 154:
                messageId = 2131166238;
                break;
            case 158:
                messageId = 2131165339;
                break;
            case 159:
                messageId = 2131165797;
                break;
            default:
                return true;
        }
        Toast.makeText(this.context, LocalizationManager.getString(this.context, messageId), 0).show();
        return false;
    }

    public void onDeleteTrack(Track track) {
    }

    public void onAddTrack(Track track) {
        if (track != null) {
            long t = System.currentTimeMillis();
            if (t - this.musicServerTripTime > 2000) {
                StatisticManager.getInstance().addStatisticEvent("music-add_touch", new Pair[0]);
                Message msg = Message.obtain(null, 2131624038, 0, 0);
                msg.replyTo = this.mMessenger;
                msg.obj = new Track[]{track};
                GlobalBus.sendMessage(msg);
                this.musicServerTripTime = t;
            }
        }
    }

    public void onSetStatusTrack(Track track) {
        Message msg = Message.obtain(null, 2131624081, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = track;
        GlobalBus.sendMessage(msg);
    }

    @Subscribe(on = 2131623946, to = 2131624251)
    public final void onProgressChange(BusEvent event) {
        if (!this.seekNow) {
            int progress;
            int valueSec = event.bundleOutput.getInt(BusProtocol.PREF_MEDIA_PLAYER_PROGRESS, 0);
            int duration = event.bundleOutput.getInt(BusProtocol.PREF_MEDIA_PLAYER_DURATION, 1);
            if (duration != 0) {
                progress = (int) ((((double) valueSec) / ((double) duration)) * 100.0d);
            } else {
                progress = 0;
            }
            this.playerFragment.setProgress(progress, duration, valueSec);
            Logger.m173d("Player progress %d/%d", Integer.valueOf(valueSec), Integer.valueOf(duration));
        }
    }

    @Subscribe(on = 2131623946, to = 2131624250)
    public final void onDownloadProgressChange(BusEvent event) {
        this.playerFragment.setDownloadProgress(event.bundleOutput.getInt("play_progress_update", 0));
    }

    @Subscribe(on = 2131623946, to = 2131624243)
    public void onError(BusEvent event) {
        if (this.playerFragment.getActivity() != null && !this.playerFragment.isHidden()) {
            this.playerFragment.hideLoadProgress();
            this.playerFragment.callUpdate();
            this.playerFragment.setPause();
            if (event.bundleOutput.getInt(BusProtocol.PREF_PLAY_INFO_ERROR_KEY, 404) == C0206R.styleable.Theme_radioButtonStyle) {
                this.playerFragment.showErrorMusicDialog();
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624252)
    public void onStreamMediaStatus(BusEvent event) {
        InformationState playState = (InformationState) event.bundleOutput.getSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE);
        Logger.m173d("set state music: %s", playState.name());
        if (playState == InformationState.PAUSE) {
            this.playerFragment.setPause();
        } else if (playState == InformationState.PLAY) {
            this.playerFragment.setPlay();
        } else if (playState == InformationState.DATA_QUERY) {
            this.playerFragment.setProgress(0, 0, 0);
            this.playerFragment.setDownloadProgress(0);
            this.playerFragment.setEnabledProgress(false);
        } else if (playState == InformationState.BUFFERED) {
            this.playerFragment.setEnabledProgress(true);
        } else if (playState == InformationState.ERROR) {
            this.playerFragment.setDownloadProgress(0);
            this.playerFragment.setEnabledProgress(false);
            this.playerFragment.setPause();
            String errorMessage = event.bundleOutput.getString(BusProtocol.PREF_MEDIA_PLAYER_ERROR_MESSAGE);
            if (errorMessage == null) {
                if (errorMessage != null) {
                    Toast.makeText(this.context, errorMessage, 0).show();
                }
            } else if (errorMessage != null) {
                Toast.makeText(this.context, errorMessage, 0).show();
            }
        } else if (playState == InformationState.STOP) {
            this.playerFragment.setProgress(0, 0, 0);
            this.playerFragment.setPause();
            this.playerFragment.setScrollDirection(true);
        }
        this.playerFragment.callUpdate();
    }
}
