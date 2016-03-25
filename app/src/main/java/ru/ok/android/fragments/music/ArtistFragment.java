package ru.ok.android.fragments.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.fragments.handlers.ArtistBestMatchHandler.OnSelectArtistAlbumsListener;
import ru.ok.android.ui.fragments.handlers.ArtistBestMatchHandler.OnSelectArtistRadioTracksListener;
import ru.ok.android.ui.fragments.handlers.ArtistPlayListHandler;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.controls.music.ArtistPlayListControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Artist;

public final class ArtistFragment extends MusicPlayerInActionBarFragment implements OnSelectArtistAlbumsListener, OnSelectArtistRadioTracksListener {
    private ArtistPlayListControl artistPlayListControl;
    private ArtistPlayListHandler handler;

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getContext(), 2131165407);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (getMode().onPrepareOptionsMenu(menu, this)) {
            super.onPrepareOptionsMenu(menu);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        this.handler = new ArtistPlayListHandler(getMode(), getActivity());
        View view = this.handler.createView(inflater, container, savedInstanceState);
        this.handler.getArtistBestMatchHandler().setOnSelectArtistAlbumsListener(this);
        this.handler.getArtistBestMatchHandler().setOnSelectArtistRadioTracksListener(this);
        this.artistPlayListControl = new ArtistPlayListControl(getActivity(), this.handler);
        this.artistPlayListControl.tryToGetArtistInfo(getArtist().id);
        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.handler.onDestroyView();
    }

    protected int getLayoutId() {
        return 2130903250;
    }

    public static Bundle newArguments(Artist artist, MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("ARTIST", artist);
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    private Artist getArtist() {
        return (Artist) getArguments().getParcelable("ARTIST");
    }

    public void onSelectArtistAlbums(Artist artist) {
        NavigationHelper.showAlbumsPage(getActivity(), artist, getMode());
    }

    public void onSelectArtistRadio(Artist artist) {
        NavigationHelper.showArtistSimilarPage(getActivity(), artist, getMode());
    }
}
