package ru.ok.android.fragments.music.collections;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.Toast;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.ui.dialogs.ChangeMusicCollectionActionBox;
import ru.ok.android.ui.dialogs.ChangeMusicCollectionActionBox.OnAddTrackCollectionsListener;
import ru.ok.android.utils.controls.music.MusicControlUtils;
import ru.ok.model.wmf.UserTrackCollection;

public abstract class AddCollectionsFragment extends MusicCollectionsFragment implements OnAddTrackCollectionsListener {
    protected Messenger mMessenger;

    /* renamed from: ru.ok.android.fragments.music.collections.AddCollectionsFragment.1 */
    class C03171 extends Handler {
        C03171() {
        }

        public void handleMessage(Message msg) {
            if (AddCollectionsFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public AddCollectionsFragment() {
        this.mMessenger = new Messenger(new C03171());
    }

    protected void onDotsClickToCollection(UserTrackCollection collection, View view) {
        if (getActivity() != null) {
            ChangeMusicCollectionActionBox actionBox = ChangeMusicCollectionActionBox.createAddCollectionBox(getActivity(), collection, view);
            actionBox.setListenerAdd(this);
            actionBox.show();
        }
    }

    public final void onAddCollection(UserTrackCollection collection) {
        subscribe(collection.id);
    }

    public void subscribe(long cid) {
        Message msg = Message.obtain(null, 2131624082, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = Long.valueOf(cid);
        GlobalBus.sendMessage(msg);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 290:
                if (getContext() == null) {
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
            default:
                return true;
        }
    }
}
