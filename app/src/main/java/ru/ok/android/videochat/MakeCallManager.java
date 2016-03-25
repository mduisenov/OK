package ru.ok.android.videochat;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.model.call.VideoCallInfo;

public class MakeCallManager {
    private static Boolean isCallSupports;
    private Context context;
    private String from;
    private OnCallErrorListener listenerError;
    private Messenger mMessenger;
    private String to;

    public interface OnCallErrorListener {
        void onCallError();
    }

    /* renamed from: ru.ok.android.videochat.MakeCallManager.1 */
    class C14761 extends Handler {
        C14761() {
        }

        public void handleMessage(Message msg) {
            if (MakeCallManager.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public static MakeCallManager createCallManager(Context context, String to) {
        return new MakeCallManager(context, to);
    }

    private MakeCallManager(Context context, String to) {
        this.mMessenger = new Messenger(new C14761());
        this.context = context;
        this.to = to;
    }

    public static boolean isCallSupports() {
        if (isCallSupports == null) {
            try {
                System.loadLibrary("odnoklassniki-android");
                isCallSupports = Boolean.valueOf(true);
            } catch (UnsatisfiedLinkError e) {
                isCallSupports = Boolean.valueOf(false);
            }
        }
        return isCallSupports.booleanValue();
    }

    public void call() {
        if (OdnoklassnikiApplication.getCurrentUser() == null || OdnoklassnikiApplication.getCurrentUser().uid.length() == 0) {
            tryGetCurrentUser();
            return;
        }
        this.from = OdnoklassnikiApplication.getCurrentUser().uid;
        tryGetCallInfo();
    }

    private void tryGetCurrentUser() {
        Message msg = Message.obtain(null, 2131624051, 0, 0);
        msg.replyTo = this.mMessenger;
        GlobalBus.sendMessage(msg);
    }

    private void tryGetCallInfo() {
        Message msg = Message.obtain(null, 2131624048, 0, 0);
        msg.obj = this.to;
        msg.replyTo = this.mMessenger;
        GlobalBus.sendMessage(msg);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case C0206R.styleable.Theme_actionDropDownStyle /*45*/:
                notifyError();
                return true;
            case 177:
                VideoCallInfo info = msg.obj;
                try {
                    VideochatController.instance().processOutgoingCall(this.from, this.to, ConfigurationPreferences.getInstance().getAppKey(), info.getDisp(), info.getUserName(), info.getUserPic(), info.getSid());
                } catch (Exception e) {
                    notifyError();
                }
                return true;
            case 178:
                notifyError();
                return true;
            default:
                return false;
        }
    }

    private void notifyError() {
        if (this.listenerError != null) {
            this.listenerError.onCallError();
        }
    }

    public void setListenerCallError(OnCallErrorListener listenerError) {
        this.listenerError = listenerError;
    }
}
