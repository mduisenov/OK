package ru.mail.libverify.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import ru.mail.libverify.api.VerificationFactory;
import ru.mail.libverify.utils.C0204d;

public class IpcMessageService extends Service {
    private C0187h f16a;

    public IBinder onBind(Intent intent) {
        C0204d.m141c("IpcMessageService", "onBind from initiator %s", intent.getStringExtra("bind_initiator"));
        if (this.f16a == null) {
            this.f16a = new C0187h(VerificationFactory.getIpcApi(this));
        }
        return this.f16a.m67a().getBinder();
    }

    public void onDestroy() {
        C0204d.m139c("IpcMessageService", "onDestroy");
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        C0204d.m139c("IpcMessageService", "onStartCommand");
        return super.onStartCommand(intent, i, i2);
    }
}
