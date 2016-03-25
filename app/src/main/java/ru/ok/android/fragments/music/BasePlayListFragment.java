package ru.ok.android.fragments.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.ui.fragments.handlers.MusicPlayListHandler;
import ru.ok.android.utils.controls.music.SimilarArtistPlayListControl;
import ru.ok.model.wmf.Artist;

public abstract class BasePlayListFragment extends MusicPlayerInActionBarFragment {
    protected SimilarArtistPlayListControl control;
    protected MusicPlayListHandler handler;

    protected abstract void requestData(MusicPlayListHandler musicPlayListHandler);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.handler = new MusicPlayListHandler(getMode(), getActivity());
        View view = this.handler.createView(inflater, container, savedInstanceState);
        this.control = new SimilarArtistPlayListControl(getActivity(), this.handler);
        requestData(this.handler);
        return view;
    }

    public void onMediaPlayerState(BusEvent event) {
        super.onMediaPlayerState(event);
        this.control.onStreamMediaStatus(event);
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.handler.onDestroyView();
    }

    protected Artist getArtist() {
        return (Artist) getArguments().getParcelable("ARTIST");
    }
}
