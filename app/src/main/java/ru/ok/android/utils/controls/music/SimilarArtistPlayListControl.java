package ru.ok.android.utils.controls.music;

import android.app.Activity;
import android.os.Message;
import java.util.Arrays;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.ui.fragments.handlers.MusicPlayListHandler;
import ru.ok.model.wmf.Track;

public class SimilarArtistPlayListControl extends MusicListControl {
    private long artistId;

    public SimilarArtistPlayListControl(Activity context, MusicPlayListHandler playListFragment) {
        super(context, playListFragment, MusicListType.SIMILAR_TRACKS_FOR_ARTIST);
    }

    public boolean onHandleMessage(Message msg) {
        if (!super.onHandleMessage(msg)) {
            return false;
        }
        switch (msg.what) {
            case 241:
                setData(Arrays.asList((Track[]) msg.obj), false);
                this.playListHandler.onResult();
                return false;
            case 242:
                onError(msg);
                return false;
            default:
                return true;
        }
    }

    public void onGetNextTrackList() {
        if (this.artistId != 0) {
            showProgress();
            tryToGetSimilarArtistMusic(this.artistId);
        }
    }

    public void tryToGetSimilarArtistMusic(long artistId) {
        this.artistId = artistId;
        Message msg = Message.obtain(null, 2131624046, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = Long.valueOf(artistId);
        GlobalBus.sendMessage(msg);
        setPlaylistId(String.valueOf(artistId));
    }

    protected void onError(Message msg) {
        super.onError(msg);
        this.playListHandler.onError(msg);
    }
}
