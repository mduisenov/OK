package ru.ok.android.utils.controls.music;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Pair;
import android.view.View;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.dialogs.ChangeTrackStateBase.OnChangeTrackStateListener;
import ru.ok.android.ui.dialogs.actions.ChangeTrackActionBox;
import ru.ok.android.ui.fragments.handlers.BaseMusicPlayListHandler;
import ru.ok.android.utils.controls.TracksListControl;
import ru.ok.model.wmf.Track;

public abstract class MusicListControl extends TracksListControl implements OnChangeTrackStateListener {
    private ChangeTrackActionBox changeTrackActionBox;
    private TracksControl control;
    protected Messenger mMessenger;

    /* renamed from: ru.ok.android.utils.controls.music.MusicListControl.1 */
    class C14541 extends Handler {
        C14541() {
        }

        public void handleMessage(Message msg) {
            if (MusicListControl.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    class TracksControl extends ChangeTrackControl {
        public TracksControl(Context context) {
            super(context);
        }

        protected void onTracksDelete(Track[] tracks) {
            super.onTracksDelete(tracks);
            for (Track track : tracks) {
                MusicListControl.this.playListHandler.deleteTrack(track);
            }
        }
    }

    public MusicListControl(Activity context, BaseMusicPlayListHandler playListFragment, MusicListType musicListType) {
        super(context, playListFragment, musicListType);
        this.changeTrackActionBox = null;
        this.mMessenger = new Messenger(new C14541());
        this.control = new TracksControl(context);
    }

    public void onDeleteTrack(Track track) {
        this.control.deleteTrack(track);
    }

    public void onDotsClick(Track track, View view) {
        StatisticManager.getInstance().addStatisticEvent("music-add_swipe", new Pair[0]);
        this.changeTrackActionBox = null;
        if (this.currentType == MusicListType.MY_MUSIC) {
            this.changeTrackActionBox = ChangeTrackActionBox.createDeleteTrackBox(this.context, track, view);
        } else {
            this.changeTrackActionBox = ChangeTrackActionBox.createAddTrackBox(this.context, track, view);
        }
        this.changeTrackActionBox.setOnChangeTrackStateListener(this);
        this.changeTrackActionBox.show();
    }

    public void onAddTrack(Track track) {
        this.control.addTrack(track);
    }

    public void onSetStatusTrack(Track track) {
        this.control.setStatusTrack(track);
    }

    public boolean onHandleMessage(Message msg) {
        return true;
    }

    protected void onError(Message msg) {
        MusicControlUtils.onError(this.context, msg);
    }

    public void showProgress() {
        this.playListHandler.showProgress();
    }
}
