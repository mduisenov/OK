package ru.ok.android.utils.controls.music;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;
import ru.ok.android.bus.GlobalBus;
import ru.ok.model.wmf.Track;

public class ChangeTrackControl {
    private Context context;
    protected Messenger mMessenger;
    private long musicServerTripTime;

    /* renamed from: ru.ok.android.utils.controls.music.ChangeTrackControl.1 */
    class C14531 extends Handler {
        C14531() {
        }

        public void handleMessage(Message msg) {
            if (ChangeTrackControl.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public ChangeTrackControl(Context context) {
        this.musicServerTripTime = 0;
        this.mMessenger = new Messenger(new C14531());
        this.context = context;
    }

    public void deleteTrack(Track track) {
        Message msg = Message.obtain(null, 2131624040, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = new Track[]{track};
        GlobalBus.sendMessage(msg);
    }

    public void addTrack(Track track) {
        long t = System.currentTimeMillis();
        if (t - this.musicServerTripTime > 2000) {
            Message msg = Message.obtain(null, 2131624038, 0, 0);
            msg.replyTo = this.mMessenger;
            msg.obj = new Track[]{track};
            GlobalBus.sendMessage(msg);
            this.musicServerTripTime = t;
        }
    }

    public void setStatusTrack(Track track) {
        Message msg = Message.obtain(null, 2131624081, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = track;
        GlobalBus.sendMessage(msg);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 153:
                Toast.makeText(this.context, 2131166239, 0).show();
                return false;
            case 154:
                Toast.makeText(this.context, 2131166238, 0).show();
                return false;
            case 158:
                Toast.makeText(this.context, 2131165339, 0).show();
                return false;
            case 159:
                Toast.makeText(this.context, 2131165797, 0).show();
                return false;
            case 163:
                onTracksDelete((Track[]) msg.obj);
                Toast.makeText(this.context, 2131165687, 0).show();
                return false;
            case 164:
                Toast.makeText(this.context, 2131165804, 0).show();
                return false;
            default:
                return true;
        }
    }

    protected void onTracksDelete(Track[] tracks) {
    }
}
