package ru.ok.android.fragments.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.ui.fragments.handlers.AlbumPlayListHandler;
import ru.ok.android.ui.fragments.handlers.BaseMusicPlayListHandler.OnSelectTrackListener;
import ru.ok.android.utils.controls.music.AlbumPlayListControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Track;

public class AlbumFragment extends MusicPlayerInActionBarFragment implements OnSelectTrackListener {
    private AlbumPlayListControl albumPlayListControl;
    private AlbumPlayListHandler handler;

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getContext(), 2131165373);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (getMode().onPrepareOptionsMenu(menu, this)) {
            super.onPrepareOptionsMenu(menu);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        MusicFragmentMode mode = getMode();
        this.handler = new AlbumPlayListHandler(mode, getActivity());
        View view = this.handler.createView(inflater, container, savedInstanceState);
        this.handler.addSelectTrackListener(this);
        this.albumPlayListControl = new AlbumPlayListControl(getActivity(), this.handler, mode);
        this.albumPlayListControl.tryToGetAlbumInfo(getAlbum().id);
        return view;
    }

    public void onMediaPlayerState(BusEvent event) {
        super.onMediaPlayerState(event);
        this.albumPlayListControl.onStreamMediaStatus(event);
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.handler.onDestroyView();
    }

    public void onSelectTrack(AdapterView<?> adapterView, int position, List<? extends Track> list) {
    }

    protected int getLayoutId() {
        return 2130903248;
    }

    public static Bundle newArguments(Album album, MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("ALBUM", album);
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    private Album getAlbum() {
        return (Album) getArguments().getParcelable("ALBUM");
    }
}
