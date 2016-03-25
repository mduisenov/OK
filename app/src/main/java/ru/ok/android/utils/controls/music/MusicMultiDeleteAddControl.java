package ru.ok.android.utils.controls.music;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Pair;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.model.wmf.Track;

public class MusicMultiDeleteAddControl {
    private OnAddTrackListener addTrackListener;
    private OnDeleteTrackListener deleteTrackListener;
    private Messenger mMessenger;

    public interface OnAddTrackListener {
        void onAddTracksFailed();

        void onAddTracksSuccessful(Track[] trackArr);
    }

    public interface OnDeleteTrackListener {
        void onDeleteTracksFailed();

        void onDeleteTracksSuccessful(Track[] trackArr);
    }

    /* renamed from: ru.ok.android.utils.controls.music.MusicMultiDeleteAddControl.1 */
    class C14551 extends Handler {
        C14551() {
        }

        public void handleMessage(Message msg) {
            if (MusicMultiDeleteAddControl.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public MusicMultiDeleteAddControl() {
        this.mMessenger = new Messenger(new C14551());
    }

    public void setAddTrackListener(OnAddTrackListener addTrackListener) {
        this.addTrackListener = addTrackListener;
    }

    public void setDeleteTrackListener(OnDeleteTrackListener deleteTrackListener) {
        this.deleteTrackListener = deleteTrackListener;
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 158:
                if (this.addTrackListener != null) {
                    this.addTrackListener.onAddTracksSuccessful((Track[]) msg.obj);
                }
                return false;
            case 159:
                if (this.addTrackListener != null) {
                    this.addTrackListener.onAddTracksFailed();
                }
                return false;
            case 163:
                Track[] tracks = (Track[]) msg.obj;
                if (this.deleteTrackListener != null) {
                    this.deleteTrackListener.onDeleteTracksSuccessful(tracks);
                }
                return false;
            case 164:
                if (this.deleteTrackListener != null) {
                    this.deleteTrackListener.onDeleteTracksFailed();
                }
                return false;
            default:
                return true;
        }
    }

    public void addTracks(Track[] tracks) {
        StatisticManager.getInstance().addStatisticEvent("music-add_multiple", new Pair[0]);
        Message msg = Message.obtain(null, 2131624038, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = tracks;
        GlobalBus.sendMessage(msg);
    }

    public void deleteTracks(Track[] tracks) {
        Message msg = Message.obtain(null, 2131624040, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = tracks;
        GlobalBus.sendMessage(msg);
    }
}
