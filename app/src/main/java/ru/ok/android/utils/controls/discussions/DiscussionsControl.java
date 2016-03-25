package ru.ok.android.utils.controls.discussions;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import ru.ok.android.bus.GlobalBus;

public class DiscussionsControl {
    private Context context;
    private OnMarkAsReadListener listener;
    private Messenger mMessenger;

    public interface OnMarkAsReadListener {
        void onMarkAsReadError();

        void onMarkAsReadSuccessful();
    }

    /* renamed from: ru.ok.android.utils.controls.discussions.DiscussionsControl.1 */
    class C14451 extends Handler {
        C14451() {
        }

        public void handleMessage(Message msg) {
            if (DiscussionsControl.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public DiscussionsControl(Context context) {
        this.mMessenger = new Messenger(new C14451());
        this.context = context;
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 186:
                if (this.listener == null) {
                    return false;
                }
                this.listener.onMarkAsReadSuccessful();
                return false;
            case 187:
                if (this.listener == null) {
                    return false;
                }
                this.listener.onMarkAsReadError();
                return false;
            default:
                return true;
        }
    }

    public void setListener(OnMarkAsReadListener listener) {
        this.listener = listener;
    }

    public void sendMarkAsRead() {
        tryMarkAsRead();
    }

    protected void tryMarkAsRead() {
        Message msg = Message.obtain(null, 2131624079, 0, 0);
        msg.replyTo = this.mMessenger;
        GlobalBus.sendMessage(msg);
    }
}
