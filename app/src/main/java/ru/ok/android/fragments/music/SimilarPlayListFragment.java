package ru.ok.android.fragments.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.fragments.handlers.MusicPlayListHandler;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Artist;

public final class SimilarPlayListFragment extends BasePlayListFragment {
    protected CharSequence getTitle() {
        String artistName = "";
        if (getArguments() != null) {
            artistName = ((Artist) getArguments().getParcelable("ARTIST")).name;
        }
        return LocalizationManager.from(OdnoklassnikiApplication.getContext()).getString(2131166567) + " \"" + artistName + "\"";
    }

    protected int getLayoutId() {
        return 2130903393;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (getMode().onPrepareOptionsMenu(menu, this)) {
            super.onPrepareOptionsMenu(menu);
        }
    }

    protected void requestData(MusicPlayListHandler handler) {
        handler.setNoneRefresh();
        this.control.tryToGetSimilarArtistMusic(getArtist().id);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static Bundle newArguments(Artist artist, MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("ARTIST", artist);
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }
}
