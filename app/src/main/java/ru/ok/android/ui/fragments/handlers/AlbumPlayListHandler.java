package ru.ok.android.ui.fragments.handlers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Collection;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Track;

public final class AlbumPlayListHandler extends MusicPlayListHandler {
    private AlbumBestMatchHandler albumBestMatchHandler;
    private View albumView;
    private View divider;

    public AlbumPlayListHandler(MusicFragmentMode mode, Context context) {
        super(mode, context);
        this.albumBestMatchHandler = new AlbumBestMatchHandler();
    }

    public void setData(Album album, Collection<? extends Track> tracks) {
        setData(tracks);
        this.albumBestMatchHandler.setData(album);
    }

    protected void postListViewInited(ListAdapter adapter, LayoutInflater inflater) {
        if (this.albumView == null) {
            this.albumView = this.albumBestMatchHandler.createView(inflater, null, null);
        }
        this.divider = inflater.inflate(2130903430, null);
        ((TextView) this.divider.findViewById(C0263R.id.text)).setText(2131165371);
        this.playListView.addHeaderView(this.albumView);
        this.playListView.addHeaderView(this.divider);
        this.playListView.setAdapter(adapter);
    }

    public void onDestroyView() {
        if (this.albumView != null) {
            this.albumBestMatchHandler.onDestroyView();
        }
        super.onDestroyView();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i >= ((ListView) adapterView).getHeaderViewsCount() && this.adapter.getItem(i - ((ListView) adapterView).getHeaderViewsCount()) != this.adapter.getPlayingTrack()) {
            notifySelectTrack(i);
        }
    }

    private void changeViewsVisibility(boolean hasDataToDisplay) {
        int i;
        int i2 = 0;
        View view = this.albumView;
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
