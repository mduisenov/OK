package ru.ok.android.fragments.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.fragments.handlers.AlbumsMusicViewHandler;
import ru.ok.android.utils.controls.music.AlbumsControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Artist;

public class AlbumsFragment extends MusicPlayerInActionBarFragment {
    private AlbumsControl albumsControl;
    private AlbumsMusicViewHandler handler;

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getContext(), 2131165405);
    }

    public boolean isPlayFloatingButtonRequired() {
        return getArguments() != null && getArguments().getBoolean("extra_show_player_button", false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        this.handler = new AlbumsMusicViewHandler(getActivity(), getMode());
        View view = this.handler.createView(inflater, container, savedInstanceState);
        this.albumsControl = new AlbumsControl(getActivity(), this.handler, false);
        this.albumsControl.tryToGetArtistAlbums(getArtist());
        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.handler.onDestroyView();
    }

    protected int getLayoutId() {
        return 2130903097;
    }

    public static Bundle newArguments(Artist artist, MusicFragmentMode mode, boolean showPlayerButton) {
        Bundle args = new Bundle();
        args.putParcelable("ARTIST", artist);
        args.putParcelable("music-fragment-mode", mode);
        args.putBoolean("extra_show_player_button", showPlayerButton);
        return args;
    }

    private Artist getArtist() {
        return (Artist) getArguments().getParcelable("ARTIST");
    }
}
