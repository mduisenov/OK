package ru.ok.android.fragments.music.pop;

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

public class PopTracksFragment extends AddTracksFragment {
    protected Messenger mMessenger;

    /* renamed from: ru.ok.android.fragments.music.pop.PopTracksFragment.1 */
    class C03231 extends Handler {
        C03231() {
        }

        public void handleMessage(Message msg) {
            if (PopTracksFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public PopTracksFragment() {
        this.mMessenger = new Messenger(new C03231());
    }

    public static Bundle newArguments(MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    protected int getLayoutId() {
        return super.getLayoutId();
    }

    public static Fragment newInstance(MusicFragmentMode mode) {
        Fragment result = new PopTracksFragment();
        result.setArguments(newArguments(mode));
        return result;
    }

    protected MusicListType getType() {
        return MusicListType.POP_MUSIC;
    }

    protected MusicFragmentMode getMode() {
        return (MusicFragmentMode) getArguments().getParcelable("music-fragment-mode");
    }

    protected void requestTracks() {
        Message msg = Message.obtain(null, 2131624063, 0, 0);
        msg.replyTo = this.mMessenger;
        Bundle data = new Bundle();
        data.putInt("start_position", 0);
        msg.setData(data);
        GlobalBus.sendMessage(msg);
        showProgressStub();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestTracks();
        getLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case RECEIVED_VALUE:
                List<String> projections = MusicStorageFacade.getProjectionForPopMusic();
                return new CursorLoader(getActivity(), OdklProvider.popTracksUri(), (String[]) projections.toArray(new String[projections.size()]), null, null, "pop_music._index DESC");
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

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 227:
                onWebLoadSuccess(Type.MUSIC, ((Track[]) ((Track[]) msg.obj)).length != 0);
                return false;
            case 228:
                onWebLoadError(msg.obj);
                return false;
            default:
                return true;
        }
    }
}
