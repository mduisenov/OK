package ru.mail.libverify.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import ru.mail.libverify.api.VerificationFactory;
import ru.mail.libverify.utils.C0204d;

public class IpcNotificationService extends Service {
    private C0186f f17a;

    public IBinder onBind(Intent intent) {
        C0204d.m141c("IpcNotifyService", "onBind from initiator %s", intent.getStringExtra("bind_initiator"));
        if (this.f17a == null) {
            this.f17a = new C0186f(VerificationFactory.getIpcApi(this));
        }
        return this.f17a.m67a().getBinder();
    }

    public void onDestroy() {
        C0204d.m139c("IpcNotifyService", "onDestroy");
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        C0204d.m139c("IpcNotifyService", "onStartCommand");
        return super.onStartCommand(intent, i, i2);
    }
}
