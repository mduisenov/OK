package ru.ok.android.utils.controls.music;

import android.app.Activity;
import android.os.Message;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.ui.fragments.handlers.ArtistPlayListHandler;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.ArtistInfo;
import ru.ok.model.wmf.Track;

public class ArtistPlayListControl extends MusicListControl {
    private long artistId;

    public ArtistPlayListControl(Activity context, ArtistPlayListHandler playListHandler) {
        super(context, playListHandler, MusicListType.ARTIST);
        playListHandler.setNoneRefresh();
    }

    public void setData(Artist artist, Collection<? extends Track> tracks) {
        ((ArtistPlayListHandler) this.playListHandler).setData(artist, tracks);
    }

    public void tryToGetArtistInfo(long artistId) {
        this.artistId = artistId;
        showProgress();
        Message msg = Message.obtain(null, 2131624045, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = Long.valueOf(artistId);
        GlobalBus.sendMessage(msg);
    }

    public void onGetNextTrackList() {
        tryToGetArtistInfo(this.artistId);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 257:
                ArtistInfo infoAr = msg.obj;
                List<Track> tracksArtist = Arrays.asList(infoAr.tracks);
                Artist artist = infoAr.artist;
                if (artist != null) {
                    setData(artist, tracksArtist);
                }
                this.playListHandler.onResult();
                return false;
            case 258:
                this.playListHandler.onError(msg.obj);
                onError(msg);
                return false;
            default:
                return super.onHandleMessage(msg);
        }
    }
}
