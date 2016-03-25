package ru.mail.libverify.api;

import android.support.annotation.NonNull;
import ru.mail.libverify.requests.i.b;

/* renamed from: ru.mail.libverify.api.q */
public interface C0184q {

    /* renamed from: ru.mail.libverify.api.q.a */
    public static class C0182a {
        public final String f9a;
        public final String f10b;
        public final String f11c;
        public final Boolean f12d;
        public final String f13e;
        public final String f14f;
        public final String f15g;

        public C0182a(@NonNull String str, @NonNull String str2, @NonNull String str3, @NonNull String str4, String str5, Boolean bool, String str6) {
            this.f9a = str2;
            this.f10b = str4;
            this.f11c = str5;
            this.f12d = bool;
            this.f13e = str3;
            this.f14f = str;
            this.f15g = str6;
        }

        public final String toString() {
            return "UINotificationInfo{message='" + this.f9a + '\'' + ", from='" + this.f10b + '\'' + ", confirmText='" + this.f11c + '\'' + ", confirmEnabled=" + this.f12d + ", phone='" + this.f13e + '\'' + ", notificationId='" + this.f14f + '\'' + ", description='" + this.f15g + '\'' + '}';
        }
    }

    /* renamed from: ru.mail.libverify.api.q.b */
    public interface C0183b {
        void m54a(C0182a c0182a);
    }

    void m55a(@NonNull String str, int i);

    void m56a(@NonNull String str, @NonNull C0183b c0183b);

    void m57a(@NonNull String str, b bVar);

    void m58e(@NonNull String str);

    void m59f(@NonNull String str);
}
