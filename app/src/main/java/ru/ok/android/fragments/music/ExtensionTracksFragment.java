package ru.ok.android.fragments.music;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import java.util.List;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.controls.music.MusicListType;

public class ExtensionTracksFragment extends AddTracksFragment {
    public static Bundle newArguments(MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    public static Fragment newInstance(MusicFragmentMode mode) {
        Fragment result = new ExtensionTracksFragment();
        result.setArguments(newArguments(mode));
        return result;
    }

    protected MusicListType getType() {
        return MusicListType.EXTENSION;
    }

    protected MusicFragmentMode getMode() {
        return (MusicFragmentMode) getArguments().getParcelable("music-fragment-mode");
    }

    protected void requestTracks() {
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onWebLoadSuccess(Type.MUSIC_EXTENSION_TRACKS, true);
        requestTracks();
        getLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case RECEIVED_VALUE:
                List<String> projections = MusicStorageFacade.getProjectionForExtensionMusic();
                return new CursorLoader(getActivity(), OdklProvider.musicExtensionUri(), (String[]) projections.toArray(new String[projections.size()]), null, null, "extension_music._index DESC");
            default:
                return null;
        }
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case RECEIVED_VALUE:
                this.adapter.swapCursor(data);
                dbLoadCompleted();
            default:
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case RECEIVED_VALUE:
                this.adapter.swapCursor(null);
            default:
        }
    }
}
