package ru.ok.android.fragments.music.collections;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import java.util.List;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.controls.music.MusicControlUtils;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.wmf.UserTrackCollection;

public class PopMusicCollectionsFragment extends AddCollectionsFragment {
    protected Messenger mMessenger;

    /* renamed from: ru.ok.android.fragments.music.collections.PopMusicCollectionsFragment.1 */
    class C03211 extends Handler {
        C03211() {
        }

        public void handleMessage(Message msg) {
            if (PopMusicCollectionsFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public PopMusicCollectionsFragment() {
        this.mMessenger = new Messenger(new C03211());
    }

    public static Bundle newArguments(MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    public static Fragment newInstance(MusicFragmentMode mode) {
        Fragment result = new PopMusicCollectionsFragment();
        result.setArguments(newArguments(mode));
        return result;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
        getLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case RECEIVED_VALUE:
                List<String> projections = MusicStorageFacade.getProjectionForCollections();
                return new CursorLoader(getActivity(), OdklProvider.popCollectionsUri(), (String[]) projections.toArray(new String[projections.size()]), null, null, null);
            default:
                return null;
        }
    }

    protected void onSelectCollection(UserTrackCollection collection) {
        NavigationHelper.showMusicCollectionFragment(getActivity(), collection, MusicListType.POP_COLLECTION, getMode());
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }

    protected void getData() {
        showProgressStub();
        getPopMusicCollections();
    }

    private void getPopMusicCollections() {
        Message msg = Message.obtain(null, 2131624064, 0, 0);
        msg.replyTo = this.mMessenger;
        GlobalBus.sendMessage(msg);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 231:
                onWebLoadSuccess(Type.MUSIC, ((UserTrackCollection[]) ((UserTrackCollection[]) msg.obj)).length != 0);
                return false;
            case 232:
                MusicControlUtils.onError(getContext(), msg);
                onWebLoadError(msg.obj);
                return false;
            default:
                return super.onHandleMessage(msg);
        }
    }
}
