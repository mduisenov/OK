package ru.ok.android.utils.controls.music;

import android.app.Activity;
import java.util.Collection;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.fragments.handlers.AlbumPlayListHandler;
import ru.ok.android.ui.fragments.handlers.ArtistPlayListHandler;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Track;

public class BestMatchListControl {
    private AlbumPlayListControl albumPlayListControl;
    private AlbumPlayListHandler albumPlayListHandler;
    private ArtistPlayListControl artistPlayListControl;
    private ArtistPlayListHandler artistPlayListHandler;

    public BestMatchListControl(Activity context, AlbumPlayListHandler albumPlayListHandler, ArtistPlayListHandler artistPlayListHandler, MusicFragmentMode mode) {
        this.albumPlayListHandler = albumPlayListHandler;
        this.artistPlayListHandler = artistPlayListHandler;
        this.albumPlayListControl = new AlbumPlayListControl(context, albumPlayListHandler, mode);
        albumPlayListHandler.setNoneRefresh();
        this.artistPlayListControl = new ArtistPlayListControl(context, artistPlayListHandler);
        albumPlayListHandler.setNoneRefresh();
    }

    public void setData(Artist artist, List<? extends Track> tracks) {
        this.artistPlayListControl.setData(artist, tracks);
        this.artistPlayListHandler.onResult();
    }

    public void setData(Album album, Collection<? extends Track> tracks) {
        this.albumPlayListControl.setData(album, tracks);
        this.albumPlayListHandler.onResult();
    }

    public void onStreamMediaStatus(BusEvent event) {
        this.artistPlayListControl.onStreamMediaStatus(event);
        this.albumPlayListControl.onStreamMediaStatus(event);
    }
}
