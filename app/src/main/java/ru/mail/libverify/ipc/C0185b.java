package ru.mail.libverify.ipc;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import ru.mail.libverify.api.C0178c;

/* renamed from: ru.mail.libverify.ipc.b */
abstract class C0185b extends Handler {
    public final C0178c f18a;
    private Messenger f19b;

    public C0185b(C0178c c0178c) {
        this.f18a = c0178c;
    }

    public final Messenger m67a() {
        if (this.f19b == null) {
            this.f19b = new Messenger(this);
        }
        return this.f19b;
    }

    protected abstract void m68a(@NonNull Message message);

    public void handleMessage(Message message) {
        super.handleMessage(message);
        if (message != null) {
            m68a(message);
        }
    }
}
