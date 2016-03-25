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
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.controls.music.MusicControlUtils;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.wmf.UserTrackCollection;

public class UserMusicCollectionsFragment extends AddCollectionsFragment {
    protected Messenger mMessenger;

    /* renamed from: ru.ok.android.fragments.music.collections.UserMusicCollectionsFragment.1 */
    class C03221 extends Handler {
        C03221() {
        }

        public void handleMessage(Message msg) {
            if (UserMusicCollectionsFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public UserMusicCollectionsFragment() {
        this.mMessenger = new Messenger(new C03221());
    }

    public static Bundle newArguments(String userId, MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    private String getUserId() {
        return getArguments().getString("USER_ID");
    }

    public static Fragment newInstance(String userId, MusicFragmentMode mode) {
        Fragment result = new UserMusicCollectionsFragment();
        result.setArguments(newArguments(userId, mode));
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
                String selection = "collections2users.user_id = " + getUserId();
                List<String> projections = MusicStorageFacade.getProjectionForCollections();
                return new CursorLoader(getActivity(), OdklProvider.collectionsUri(), (String[]) projections.toArray(new String[projections.size()]), selection, null, "_index");
            default:
                return null;
        }
    }

    protected void onSelectCollection(UserTrackCollection collection) {
        NavigationHelper.showMusicCollectionFragment(getActivity(), collection, MusicListType.USER_COLLECTION, getMode());
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }

    protected void getData() {
        showProgressStub();
        getUserMusicCollections(getUserId());
    }

    public void getUserMusicCollections(String userId) {
        Message msg = Message.obtain(null, 2131624076, 0, 0);
        msg.replyTo = this.mMessenger;
        Bundle data = new Bundle();
        data.putString("user_id", userId);
        msg.setData(data);
        GlobalBus.sendMessage(msg);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 215:
                onWebLoadSuccess(Type.MUSIC_USER_COLLECTIONS, ((UserTrackCollection[]) ((UserTrackCollection[]) msg.obj)).length != 0);
                return false;
            case 216:
                MusicControlUtils.onError(OdnoklassnikiApplication.getContext(), msg);
                onWebLoadError(msg.obj);
                return false;
            default:
                return super.onHandleMessage(msg);
        }
    }
}
