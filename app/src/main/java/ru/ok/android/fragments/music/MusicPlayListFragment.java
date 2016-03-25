package ru.ok.android.fragments.music;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.wmf.Track;

public class MusicPlayListFragment extends AddTracksFragment {
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onWebLoadSuccess(Type.MUSIC, true);
        getLoaderManager().initLoader(0, null, this);
    }

    protected MusicListType getType() {
        return MusicListType.PLAYLIST;
    }

    public boolean isPlayFloatingButtonRequired() {
        return false;
    }

    protected void requestTracks() {
    }

    public void onSelectPosition(int position, ArrayList<Track> tracks) {
        MusicService.startPlayMusic(getContext(), position, tracks, getType());
    }

    protected MusicFragmentMode getMode() {
        MusicFragmentMode mode = (MusicFragmentMode) getArguments().getParcelable("music-fragment-mode");
        if (mode == null) {
            return MusicFragmentMode.STANDARD;
        }
        return mode;
    }

    protected String getTitle() {
        return getStringLocalized(2131166372);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflateMenuLocalized(2131689519, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem playListItem = menu.findItem(2131625228);
        if (!(playListItem == null || isHidden())) {
            playListItem.setVisible(false);
        }
        MenuItem playOnlyCacheItem = menu.findItem(2131625508);
        Context context = getActivity();
        if (context != null) {
            boolean value = Settings.isPlayOnlyCache(context);
            if (playOnlyCacheItem != null) {
                playOnlyCacheItem.setVisible(false);
            }
            if (playOnlyCacheItem == null) {
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625438:
                showSelectedMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        List<String> projections = MusicStorageFacade.getProjectionForCollection();
        return new CursorLoader(getActivity(), OdklProvider.playListUri(), (String[]) projections.toArray(new String[projections.size()]), null, null, "playlist._index");
    }

    private int getCurrentTrackPosition() {
        BusEvent event = MusicService.getLastState();
        if (event == null) {
            return 0;
        }
        return event.bundleOutput.getInt("playlist_track_position", -1);
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursorLoader.getId() == 0) {
            this.adapter.swapCursor(cursor);
            this.recyclerLayoutManager.scrollToPositionWithOffset(getCurrentTrackPosition(), this.listView.getHeight() / 2);
            dbLoadCompleted();
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }
}
