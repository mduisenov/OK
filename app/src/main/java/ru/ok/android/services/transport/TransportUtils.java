package ru.ok.android.services.transport;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import java.net.HttpURLConnection;
import java.util.Locale;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.http.HttpRequest;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.Logger;

public class TransportUtils {
    private static String apiUserAgent;

    public static void addGeneralHeaders(HttpRequest httpRequest) {
        if (httpRequest != null) {
            httpRequest.addHeader("User-Agent", getAPIUserAgent());
        }
    }

    public static void addGeneralHeaders(HttpURLConnection connection) {
        if (connection != null) {
            connection.setRequestProperty("User-Agent", getAPIUserAgent());
        }
    }

    public static String getAPIUserAgent() {
        if (apiUserAgent == null) {
            String densityString;
            StringBuilder sb = new StringBuilder();
            sb.append("OKAndroid/");
            Context context = OdnoklassnikiApplication.getContext();
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                sb.append(info.versionName).append(" b").append(info.versionCode);
            } catch (Throwable e) {
                Logger.m178e(e);
            }
            sb.append(" (Android ").append(VERSION.RELEASE).append("; ");
            sb.append(Locale.getDefault()).append("; ");
            sb.append(Build.BRAND).append(" ").append(Build.MODEL).append(" Build/").append(Build.DISPLAY).append("; ");
            if (DeviceUtils.getType(context) == DeviceLayoutType.LARGE) {
                sb.append("tablet; ");
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int densityDpi = displayMetrics.densityDpi;
            switch (densityDpi) {
                case 120:
                    densityString = "ldpi";
                    break;
                case 160:
                    densityString = "mdpi";
                    break;
                case 240:
                    densityString = "hdpi";
                    break;
                case 320:
                    densityString = "xhdpi";
                    break;
                case 480:
                    densityString = "xxhdpi";
                    break;
                case 640:
                    densityString = "xxxhdpi";
                    break;
                default:
                    densityString = "unknown" + densityDpi;
                    break;
            }
            sb.append(densityString).append(" ").append(densityDpi).append("dpi").append(" ").append(displayMetrics.widthPixels).append(MUCUser.ELEMENT).append(displayMetrics.heightPixels);
            sb.append(")");
            Logger.m173d("API User Agent: %s", sb);
            apiUserAgent = sb.toString();
        }
        return apiUserAgent;
    }
}
