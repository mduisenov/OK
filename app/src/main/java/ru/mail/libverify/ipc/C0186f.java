package ru.mail.libverify.ipc;

import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import ru.mail.libverify.api.C0178c;
import ru.mail.libverify.utils.C0203c;
import ru.mail.libverify.utils.C0204d;
import ru.ok.android.proto.MessagesProto;

/* renamed from: ru.mail.libverify.ipc.f */
final class C0186f extends C0185b {
    public C0186f(C0178c c0178c) {
        super(c0178c);
    }

    protected final void m69a(@NonNull Message message) {
        C0204d.m141c("IpcNotifyHandler", "handleMessage %s", message.toString());
        switch (message.what) {
            case MessagesProto.Message.UUID_FIELD_NUMBER /*5*/:
                try {
                    String string = message.getData().getString("data");
                    long j = message.getData().getLong("timestamp");
                    if (TextUtils.isEmpty(string)) {
                        C0204d.m129a("IpcNotifyHandler", "processCancelNotificationsMessage serverNotificationId can't be empty");
                        return;
                    }
                    C0204d.m141c("IpcNotifyHandler", "processCancelNotificationsMessage from %s", string);
                    this.a.m46a(string, j);
                } catch (Throwable e) {
                    C0204d.m130a("IpcNotifyHandler", "processCancelNotificationsMessage", e);
                }
            default:
                C0203c.m127a("IpcNotifyHandler", "handleMessage", new IllegalArgumentException("Can't process message with type " + message.what));
        }
    }
}
