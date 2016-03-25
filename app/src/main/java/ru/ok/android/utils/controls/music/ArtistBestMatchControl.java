package ru.ok.android.utils.controls.music;

import ru.ok.android.ui.fragments.handlers.ArtistBestMatchHandler;
import ru.ok.android.ui.fragments.handlers.ArtistBestMatchHandler.OnSelectArtistAlbumsListener;
import ru.ok.android.ui.fragments.handlers.ArtistBestMatchHandler.OnSelectArtistRadioTracksListener;
import ru.ok.model.wmf.Artist;

public class ArtistBestMatchControl implements OnSelectArtistAlbumsListener, OnSelectArtistRadioTracksListener {
    private ArtistBestMatchHandler handler;
    private OnSelectAlbumsForArtistListener onSelectAlbumsForArtistListener;
    private OnSelectArtistSimilarMusicListener onSelectArtistSimilarMusicListener;

    public ArtistBestMatchControl(ArtistBestMatchHandler handler) {
        this.handler = handler;
        this.handler.setOnSelectArtistRadioTracksListener(this);
        this.handler.setOnSelectArtistAlbumsListener(this);
    }

    public void setOnSelectArtistSimilarMusicListener(OnSelectArtistSimilarMusicListener onSelectArtistSimilarMusicListener) {
        this.onSelectArtistSimilarMusicListener = onSelectArtistSimilarMusicListener;
    }

    public void setOnSelectAlbumsForArtistListener(OnSelectAlbumsForArtistListener onSelectAlbumsForArtistListener) {
        this.onSelectAlbumsForArtistListener = onSelectAlbumsForArtistListener;
    }

    public void onSelectArtistRadio(Artist artist) {
        if (this.onSelectArtistSimilarMusicListener != null) {
            this.onSelectArtistSimilarMusicListener.onShowArtistSimilarPage(artist);
        }
    }

    public void onSelectArtistAlbums(Artist artist) {
        if (this.onSelectAlbumsForArtistListener != null) {
            this.onSelectAlbumsForArtistListener.onSelectAlbumsForArtist(artist);
        }
    }
}
