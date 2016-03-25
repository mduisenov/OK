package ru.mail.libverify.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import ru.mail.libverify.api.C0179e;
import ru.mail.libverify.api.VerificationFactory;
import ru.ok.android.proto.MessagesProto.Message;

public class NetworkStateReceiver extends BroadcastReceiver {
    private static volatile C0201b f51a;

    /* renamed from: ru.mail.libverify.utils.NetworkStateReceiver.1 */
    static /* synthetic */ class C01991 {
        static final /* synthetic */ int[] f48a;

        static {
            f48a = new int[C0200a.values().length];
            try {
                f48a[C0200a.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f48a[C0200a.WIFI.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f48a[C0200a.CELLULAR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f48a[C0200a.ROAMING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* renamed from: ru.mail.libverify.utils.NetworkStateReceiver.a */
    public enum C0200a {
        NONE,
        WIFI,
        CELLULAR,
        ROAMING
    }

    /* renamed from: ru.mail.libverify.utils.NetworkStateReceiver.b */
    public static class C0201b {
        public final C0200a f49a;
        public final String f50b;

        public C0201b(@NonNull Context context, C0200a c0200a) {
            this.f49a = c0200a;
            this.f50b = NetworkStateReceiver.m113a(context, c0200a);
        }

        public C0201b(C0200a c0200a) {
            this.f49a = c0200a;
            this.f50b = null;
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            C0201b c0201b = (C0201b) obj;
            if (this.f49a != c0201b.f49a) {
                return false;
            }
            if (this.f50b != null) {
                if (this.f50b.equals(c0201b.f50b)) {
                    return true;
                }
            } else if (c0201b.f50b == null) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            int i = 0;
            int hashCode = (this.f49a != null ? this.f49a.hashCode() : 0) * 31;
            if (this.f50b != null) {
                i = this.f50b.hashCode();
            }
            return hashCode + i;
        }
    }

    static {
        f51a = new C0201b(C0200a.NONE);
    }

    static /* synthetic */ String m113a(Context context, C0200a c0200a) {
        switch (C01991.f48a[c0200a.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "No network";
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                if (wifiManager == null) {
                    return "Unknown Wi-Fi network";
                }
                WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                return connectionInfo != null ? connectionInfo.getSSID() : "Unknown Wi-Fi network";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "Cellular network";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "In roaming";
            default:
                return "WTF";
        }
    }

    private static void m114a(@NonNull Context context, boolean z) {
        C0201b c0201b;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null) {
            c0201b = new C0201b(context, C0200a.NONE);
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            c0201b = (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) ? new C0201b(context, C0200a.NONE) : activeNetworkInfo.getType() == 1 ? new C0201b(context, C0200a.WIFI) : activeNetworkInfo.isRoaming() ? new C0201b(context, C0200a.ROAMING) : new C0201b(context, C0200a.CELLULAR);
        }
        if (!f51a.equals(c0201b)) {
            f51a = c0201b;
            C0204d.m137b("NetworkStateReceiver", "state changed to %s on %s", f51a.f49a, f51a.f50b);
            if (z) {
                C0179e networkApi = VerificationFactory.getNetworkApi();
                if (networkApi != null) {
                    boolean a = m116a(context);
                    networkApi.m49a(a);
                    if (!a) {
                        NetworkCheckService.m112a(context);
                    }
                }
            }
        }
    }

    public static boolean m115a() {
        return f51a.f49a != C0200a.NONE;
    }

    public static boolean m116a(@NonNull Context context) {
        m114a(context, false);
        return m115a();
    }

    public static void m117b(@NonNull Context context) {
        m114a(context, true);
    }

    public void onReceive(Context context, Intent intent) {
        if (context != null && intent != null && "android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            C0204d.m135b("NetworkStateReceiver", "onReceive");
            m114a(context, true);
        }
    }
}
