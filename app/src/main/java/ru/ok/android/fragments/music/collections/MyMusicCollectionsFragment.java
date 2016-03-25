package ru.ok.android.fragments.music.collections;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Toast;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.dialogs.ChangeMusicCollectionActionBox;
import ru.ok.android.ui.dialogs.ChangeMusicCollectionActionBox.OnDeleteTrackCollectionsListener;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.controls.music.MusicControlUtils;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.wmf.UserTrackCollection;

public class MyMusicCollectionsFragment extends MusicCollectionsFragment implements OnDeleteTrackCollectionsListener {
    protected Messenger mMessenger;

    /* renamed from: ru.ok.android.fragments.music.collections.MyMusicCollectionsFragment.1 */
    class C03201 extends Handler {
        C03201() {
        }

        public void handleMessage(Message msg) {
            if (MyMusicCollectionsFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public MyMusicCollectionsFragment() {
        this.mMessenger = new Messenger(new C03201());
    }

    public static Bundle newArguments(MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    public static Fragment newInstance(MusicFragmentMode mode) {
        Fragment result = new MyMusicCollectionsFragment();
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
                String selection = "collections2users.user_id = " + OdnoklassnikiApplication.getCurrentUser().uid;
                List<String> projections = MusicStorageFacade.getProjectionForCollections();
                return new CursorLoader(getActivity(), OdklProvider.collectionRelationsUri(), (String[]) projections.toArray(new String[projections.size()]), selection, null, "_index");
            default:
                return null;
        }
    }

    protected void onSelectCollection(UserTrackCollection collection) {
        NavigationHelper.showMusicCollectionFragment(getActivity(), collection, MusicListType.MY_COLLECTION, getMode());
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }

    protected void getData() {
        showProgressStub();
        getMyMusicCollections();
    }

    protected void onDotsClickToCollection(UserTrackCollection collection, View view) {
        if (getActivity() != null) {
            ChangeMusicCollectionActionBox actionBox = ChangeMusicCollectionActionBox.createDeleteCollectionBox(getActivity(), collection, view);
            actionBox.setListenerDelete(this);
            actionBox.show();
        }
    }

    public void onDeleteCollection(UserTrackCollection collection) {
        unSubscribe(collection.id);
    }

    public void getMyMusicCollections() {
        Message msg = Message.obtain(null, 2131624058, 0, 0);
        msg.replyTo = this.mMessenger;
        GlobalBus.sendMessage(msg);
    }

    public void unSubscribe(long pid) {
        Message msg = Message.obtain(null, 2131624083, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = Long.valueOf(pid);
        GlobalBus.sendMessage(msg);
    }

    public boolean onHandleMessage(Message msg) {
        Context context = getContext();
        switch (msg.what) {
            case 219:
                onWebLoadSuccess(Type.MUSIC_MY_COLLECTIONS, ((UserTrackCollection[]) ((UserTrackCollection[]) msg.obj)).length != 0);
                return false;
            case 220:
                if (context != null) {
                    MusicControlUtils.onError(context, msg);
                }
                onWebLoadError(msg.obj);
                return false;
            case 292:
                if (context == null) {
                    return false;
                }
                Toast.makeText(context, 2131166458, 0).show();
                return false;
            case 293:
                if (context == null) {
                    return false;
                }
                MusicControlUtils.onError(context, msg);
                return false;
            default:
                return true;
        }
    }
}
