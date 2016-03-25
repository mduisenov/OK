package ru.ok.android.fragments.music.collections;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.Serializable;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.fragments.music.AddTracksFragment;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.music.MusicControlUtils;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.wmf.Track;
import ru.ok.model.wmf.UserTrackCollection;

public final class MusicCollectionFragment extends AddTracksFragment {
    protected Messenger mMessenger;

    /* renamed from: ru.ok.android.fragments.music.collections.MusicCollectionFragment.1 */
    class C03181 extends Handler {
        C03181() {
        }

        public void handleMessage(Message msg) {
            if (MusicCollectionFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    /* renamed from: ru.ok.android.fragments.music.collections.MusicCollectionFragment.2 */
    static /* synthetic */ class C03192 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$utils$controls$music$MusicListType;

        static {
            $SwitchMap$ru$ok$android$utils$controls$music$MusicListType = new int[MusicListType.values().length];
            try {
                $SwitchMap$ru$ok$android$utils$controls$music$MusicListType[MusicListType.USER_COLLECTION.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$controls$music$MusicListType[MusicListType.MY_COLLECTION.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$controls$music$MusicListType[MusicListType.POP_COLLECTION.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public MusicCollectionFragment() {
        this.mMessenger = new Messenger(new C03181());
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case RECEIVED_VALUE:
                String selectionTracks = "collection_tracks.collection_id = " + getCollection().id;
                List<String> projections = MusicStorageFacade.getProjectionForCollection();
                return new CursorLoader(getActivity(), OdklProvider.collectionTracksUri(), (String[]) projections.toArray(new String[projections.size()]), selectionTracks, null, null);
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                return new CursorLoader(getActivity(), OdklProvider.collectionRelationsUri(), null, "collections2users.collection_id = " + getCollection().id + " and " + "collections2users" + "." + "user_id" + " = " + OdnoklassnikiApplication.getCurrentUser().uid, null, null);
            default:
                return null;
        }
    }

    public boolean isPlayFloatingButtonRequired() {
        return true;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case RECEIVED_VALUE:
                this.adapter.swapCursor(data);
                dbLoadCompleted();
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                if (data.getCount() > 0) {
                    Logger.m172d("load type for collection: MY");
                } else {
                    Logger.m172d("load type for collection: No my");
                }
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

    protected String getPlaylistId() {
        return "" + getCollection().id;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (getMode().onPrepareOptionsMenu(menu, this)) {
            super.onPrepareOptionsMenu(menu);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestTracks();
        getLoaderManager().initLoader(0, null, this);
    }

    protected String getTitle() {
        return getCollection().name;
    }

    public static Bundle newArguments(UserTrackCollection collection, MusicListType type, MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("COLLECTION", collection);
        args.putSerializable("COLLECTION_TYPE", type);
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    private UserTrackCollection getCollection() {
        return (UserTrackCollection) getArguments().getParcelable("COLLECTION");
    }

    protected MusicFragmentMode getMode() {
        return (MusicFragmentMode) getArguments().getParcelable("music-fragment-mode");
    }

    protected MusicListType getType() {
        Serializable serializable = getArguments().getSerializable("COLLECTION_TYPE");
        if (serializable == null) {
            return MusicListType.NO_DIRECTION;
        }
        return (MusicListType) serializable;
    }

    protected void requestTracks() {
        if (getCollection() != null) {
            tryToGetData(getCollection().id, getType() == null ? MusicListType.MY_COLLECTION : getType());
        }
    }

    private void tryToGetData(long collection, MusicListType currentType) {
        Message msg;
        switch (C03192.$SwitchMap$ru$ok$android$utils$controls$music$MusicListType[currentType.ordinal()]) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                msg = Message.obtain(null, 2131624050, 0, 0);
                break;
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                msg = Message.obtain(null, 2131624062, 0, 0);
                break;
            default:
                return;
        }
        msg.replyTo = this.mMessenger;
        msg.obj = Long.valueOf(collection);
        Bundle data = new Bundle();
        data.putInt("start_position", 0);
        msg.setData(data);
        GlobalBus.sendMessage(msg);
        showProgressStub();
    }

    public void subscribe(long pid) {
        Message msg = Message.obtain(null, 2131624082, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = Long.valueOf(pid);
        GlobalBus.sendMessage(msg);
    }

    public void unSubscribe(long pid) {
        Message msg = Message.obtain(null, 2131624083, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = Long.valueOf(pid);
        GlobalBus.sendMessage(msg);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getMode().onCreateOptionsMenu(menu, inflater, this, false) && inflateMenuLocalized(2131689510, menu)) {
            MenuItem itemSubscribe = menu.findItem(2131625490);
            switch (C03192.$SwitchMap$ru$ok$android$utils$controls$music$MusicListType[getType().ordinal()]) {
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    prepareUnSubscribeItem(itemSubscribe);
                default:
                    prepareSubscribeItem(itemSubscribe);
            }
        }
    }

    private void prepareSubscribeItem(MenuItem item) {
        item.setTitle(2131166659);
    }

    private void prepareUnSubscribeItem(MenuItem item) {
        item.setTitle(2131166739);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625489:
                showSelectedMode();
                return true;
            case 2131625490:
                if (getType() == MusicListType.MY_COLLECTION) {
                    unSubscribe(getCollection().id);
                    return true;
                }
                subscribe(getCollection().id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 223:
            case 236:
                onWebLoadSuccess(Type.MUSIC, ((Track[]) ((Track[]) msg.obj)).length != 0);
                return false;
            case 224:
            case 237:
                onWebLoadError(msg.obj);
                return false;
            case 290:
                if (getActivity() == null) {
                    return false;
                }
                Toast.makeText(getContext(), 2131165334, 0).show();
                return false;
            case 291:
                if (getContext() == null) {
                    return false;
                }
                MusicControlUtils.onError(getContext(), msg);
                return false;
            case 292:
                if (getContext() == null) {
                    return false;
                }
                Toast.makeText(getContext(), 2131166458, 0).show();
                return false;
            case 293:
                if (getContext() == null) {
                    return false;
                }
                MusicControlUtils.onError(getContext(), msg);
                return false;
            default:
                return true;
        }
    }
}
