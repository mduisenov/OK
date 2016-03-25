package ru.mail.libverify.ipc;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.List;
import ru.mail.libverify.api.C0178c;
import ru.mail.libverify.utils.C0203c;
import ru.mail.libverify.utils.C0204d;
import ru.mail.libverify.utils.C0205j;
import ru.ok.android.proto.MessagesProto;

/* renamed from: ru.mail.libverify.ipc.h */
final class C0187h extends C0185b {
    public C0187h(C0178c c0178c) {
        super(c0178c);
    }

    protected final void m70a(@NonNull Message message) {
        C0204d.m141c("SmsTextServiceHandler", "handleMessage %s", message.toString());
        String string;
        switch (message.what) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                C0204d.m139c("SmsTextServiceHandler", "processGetSessionsMessage");
                List<String> c = this.a.m48c();
                if (c.isEmpty()) {
                    C0204d.m129a("SmsTextServiceHandler", "processGetSessionsMessage skipped");
                    return;
                }
                try {
                    Messenger messenger = message.replyTo;
                    Message obtain = Message.obtain(this, 2);
                    obtain.replyTo = m67a();
                    Bundle bundle = new Bundle();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String string2 : c) {
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(C0205j.m145a(string2));
                    }
                    bundle.putString("data", stringBuilder.toString());
                    obtain.setData(bundle);
                    messenger.send(obtain);
                } catch (Throwable e) {
                    C0204d.m130a("SmsTextServiceHandler", "processGetSessionsMessage", e);
                }
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                try {
                    string2 = message.getData().getString("data");
                    String string3 = message.getData().getString("receiver");
                    if (TextUtils.isEmpty(string2) || TextUtils.isEmpty(string3)) {
                        C0204d.m129a("SmsTextServiceHandler", "processPostSmsTextMessage smsText and receiver shouldn't be empty");
                        return;
                    }
                    C0204d.m141c("SmsTextServiceHandler", "processPostSmsTextMessage sms %s for receiver %s", string2, string3);
                    this.a.m47a(string3, string2);
                    Messenger messenger2 = message.replyTo;
                    Message obtain2 = Message.obtain(this, 4);
                    obtain2.replyTo = m67a();
                    obtain2.setData(new Bundle());
                    messenger2.send(obtain2);
                } catch (Throwable e2) {
                    C0204d.m130a("SmsTextServiceHandler", "processPostSmsTextMessage", e2);
                }
            default:
                C0203c.m127a("SmsTextServiceHandler", "handleMessage", new IllegalArgumentException("Can't process message with type " + message.what));
        }
    }
}
