package ru.mail.libverify.notifications;

import android.os.Handler;
import android.os.Looper;
import java.lang.ref.WeakReference;
import ru.mail.libverify.api.C0184q.C0182a;
import ru.mail.libverify.api.C0184q.C0183b;
import ru.mail.libverify.notifications.a.1;

/* renamed from: ru.mail.libverify.notifications.a */
final class C0194a implements C0183b {
    private static Handler f32a;
    private final WeakReference<C0188b> f33b;

    static {
        f32a = new Handler(Looper.getMainLooper());
    }

    C0194a(C0188b c0188b) {
        this.f33b = new WeakReference(c0188b);
    }

    public final void m79a(C0182a c0182a) {
        C0188b c0188b = (C0188b) this.f33b.get();
        if (c0188b != null) {
            f32a.post(new 1(this, c0188b, c0182a));
        }
    }
}
