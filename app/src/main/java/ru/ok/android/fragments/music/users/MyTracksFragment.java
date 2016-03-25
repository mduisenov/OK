package ru.ok.android.fragments.music.users;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.BaseTracksFragment;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.controls.music.MusicMultiDeleteAddControl;
import ru.ok.android.utils.controls.music.MusicMultiDeleteAddControl.OnDeleteTrackListener;
import ru.ok.model.wmf.Track;

public class MyTracksFragment extends BaseTracksFragment implements OnDeleteTrackListener {
    protected Messenger mMessenger;
    private MusicMultiDeleteAddControl multiDeleteAddControl;

    /* renamed from: ru.ok.android.fragments.music.users.MyTracksFragment.1 */
    class C03281 extends Handler {
        C03281() {
        }

        public void handleMessage(Message msg) {
            if (MyTracksFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public MyTracksFragment() {
        this.mMessenger = new Messenger(new C03281());
    }

    public static Bundle newArguments(MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    public static Fragment newInstance(MusicFragmentMode mode) {
        Fragment result = new MyTracksFragment();
        result.setArguments(newArguments(mode));
        return result;
    }

    protected MusicListType getType() {
        return MusicListType.MY_MUSIC;
    }

    protected MusicFragmentMode getMode() {
        return (MusicFragmentMode) getArguments().getParcelable("music-fragment-mode");
    }

    protected void requestTracks() {
        showProgressStub();
        Message msg = Message.obtain(null, 2131624057, 0, 0);
        msg.replyTo = this.mMessenger;
        GlobalBus.sendMessage(msg);
    }

    public void showSelectedMode() {
        BaseCompatToolbarActivity activity = (BaseCompatToolbarActivity) getActivity();
        if (activity != null) {
            this.actionMode = activity.getSupportToolbar().startActionMode(this);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareDeleteControl();
        requestTracks();
        getLoaderManager().initLoader(0, null, this);
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (inflateMenuLocalized(2131689484, menu)) {
            this.item = menu.findItem(C0263R.id.delete);
        }
        return true;
    }

    private void prepareDeleteControl() {
        this.multiDeleteAddControl = new MusicMultiDeleteAddControl();
        this.multiDeleteAddControl.setDeleteTrackListener(this);
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case C0263R.id.delete /*2131624801*/:
                List<Track> list = getSelectionTracks();
                if (list.isEmpty()) {
                    TimeToast.show(getContext(), 2131166504, 0);
                } else {
                    this.multiDeleteAddControl.deleteTracks((Track[]) list.toArray(new Track[list.size()]));
                }
                return true;
            default:
                return false;
        }
    }

    public void onDeleteTracksSuccessful(Track[] tracks) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), getStringLocalized(2131165689), 0).show();
            hideSelectedMode();
        }
    }

    public void onDeleteTracksFailed() {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), getStringLocalized(2131165804), 0).show();
            hideSelectedMode();
        }
    }

    private String getUserId() {
        return OdnoklassnikiApplication.getCurrentUser().uid;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case RECEIVED_VALUE:
                if (getUserId() != null) {
                    String selectionTracks = "user_music.user_id = " + getUserId();
                    List<String> projections = MusicStorageFacade.getProjectionForUserMusic();
                    return new CursorLoader(getActivity(), OdklProvider.userTracksUri(), (String[]) projections.toArray(new String[projections.size()]), selectionTracks, null, "_index DESC");
                }
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
            case 129:
                onWebLoadSuccess(Type.MUSIC_MY_TRACKS, ((Track[]) ((Track[]) msg.obj)).length != 0);
                return false;
            case 130:
                onWebLoadError(msg.obj);
                return false;
            default:
                return true;
        }
    }
}
