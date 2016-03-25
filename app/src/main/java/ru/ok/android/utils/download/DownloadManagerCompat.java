package ru.ok.android.utils.download;

import android.content.Context;

public class DownloadManagerCompat {
    private static DownloadManager instance;

    public static DownloadManager getDownloadManager(Context context) {
        if (instance == null) {
            instance = getVersionedImpl(context);
        }
        return instance;
    }

    private static DownloadManager getVersionedImpl(Context context) {
        if (null == null) {
            return new DownloadManagerBase(context);
        }
        return null;
    }
}
