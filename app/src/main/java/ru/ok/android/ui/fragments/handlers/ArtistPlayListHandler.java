package ru.ok.android.ui.fragments.handlers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Collection;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Track;

public final class ArtistPlayListHandler extends MusicPlayListHandler {
    private ArtistBestMatchHandler artistBestMatchHandler;
    private View artistView;
    private View divider;

    public ArtistPlayListHandler(MusicFragmentMode mode, Context context) {
        super(mode, context);
        this.artistBestMatchHandler = new ArtistBestMatchHandler();
    }

    public void setData(Artist artist, Collection<? extends Track> tracks) {
        setData(tracks);
        this.artistBestMatchHandler.setData(artist);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int headers = ((ListView) adapterView).getHeaderViewsCount();
        if (i >= headers && this.adapter.getItem(i - headers) != this.adapter.getPlayingTrack()) {
            notifySelectTrack(i);
        }
    }

    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.artistView = this.artistBestMatchHandler.createView(inflater, container, savedInstanceState);
        return super.createView(inflater, container, savedInstanceState);
    }

    public ListView preparePlayList(Context context, View mMainView) {
        ListView playListView = super.preparePlayList(context, mMainView);
        this.divider = LocalizationManager.inflate(context, 2130903430, null, false);
        this.divider.setClickable(false);
        this.divider.setEnabled(false);
        ((TextView) this.divider.findViewById(C0263R.id.text)).setText(LocalizationManager.getString(context, 2131165406));
        playListView.addHeaderView(this.artistView);
        playListView.addHeaderView(this.divider);
        return playListView;
    }

    public void onDestroyView() {
        this.artistBestMatchHandler.onDestroyView();
        super.onDestroyView();
    }

    public ArtistBestMatchHandler getArtistBestMatchHandler() {
        return this.artistBestMatchHandler;
    }

    private void changeViewsVisibility(boolean hasDataToDisplay) {
        int i;
        int i2 = 0;
        View view = this.artistView;
        if (hasDataToDisplay) {
            i = 0;
        } else {
            i = 8;
        }
        view.setVisibility(i);
        View view2 = this.divider;
        if (!hasDataToDisplay) {
            i2 = 8;
        }
        view2.setVisibility(i2);
    }

    public void onError(Object description) {
        changeViewsVisibility(false);
        super.onError(description);
    }

    public void onResult() {
        changeViewsVisibility(!getData().isEmpty());
        super.onResult();
    }
}
