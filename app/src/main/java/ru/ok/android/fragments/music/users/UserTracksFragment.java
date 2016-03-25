package ru.ok.android.fragments.music.users;

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
import ru.ok.android.fragments.music.AddTracksFragment;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.wmf.Track;

public class UserTracksFragment extends AddTracksFragment {
    protected Messenger mMessenger;

    /* renamed from: ru.ok.android.fragments.music.users.UserTracksFragment.1 */
    class C03291 extends Handler {
        C03291() {
        }

        public void handleMessage(Message msg) {
            if (UserTracksFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public UserTracksFragment() {
        this.mMessenger = new Messenger(new C03291());
    }

    public static Bundle newArguments(String userId, MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    public static Fragment newInstance(String userId, MusicFragmentMode mode) {
        Fragment result = new UserTracksFragment();
        result.setArguments(newArguments(userId, mode));
        return result;
    }

    private String getUserId() {
        return getArguments().getString("USER_ID");
    }

    protected MusicFragmentMode getMode() {
        return (MusicFragmentMode) getArguments().getParcelable("music-fragment-mode");
    }

    protected String getPlaylistId() {
        return getUserId();
    }

    protected MusicListType getType() {
        return MusicListType.FRIEND_MUSIC;
    }

    protected void requestTracks() {
        showProgressStub();
        Message msg = Message.obtain(null, 2131624075, 0, 0);
        msg.replyTo = this.mMessenger;
        Bundle data = new Bundle();
        data.putString("user_id", getUserId());
        data.putInt("start_position", 0);
        msg.setData(data);
        GlobalBus.sendMessage(msg);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestTracks();
        getLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case RECEIVED_VALUE:
                if (getUserId() == null) {
                    if (getContext() != null) {
                        break;
                    }
                }
                String selectionTracks = "user_music.user_id = " + getUserId();
                List<String> projections = MusicStorageFacade.getProjectionForUserMusic();
                return new CursorLoader(getActivity(), OdklProvider.userTracksUri(), (String[]) projections.toArray(new String[projections.size()]), selectionTracks, null, "_index DESC");
                break;
        }
        return null;
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

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 148:
                onWebLoadSuccess(Type.MUSIC_USER_TRACKS, ((Track[]) ((Track[]) msg.obj)).length != 0);
                return false;
            case 149:
                onWebLoadError(msg.obj);
                return false;
            default:
                return true;
        }
    }
}
