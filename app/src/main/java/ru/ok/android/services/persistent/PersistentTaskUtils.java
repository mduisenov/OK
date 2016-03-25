package ru.ok.android.services.persistent;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import ru.ok.android.utils.Storage.External.Application;

public final class PersistentTaskUtils {
    public static boolean checkForInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo info = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean checkHasExternalStorage(Context context) {
        return Application.getCacheDir(context) != null && "mounted".equals(Environment.getExternalStorageState());
    }
}
