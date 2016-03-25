package ru.ok.android.services.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncContactsService extends Service {
    private static volatile SyncContactsAdapter sSyncAdapter;
    private static final Object sSyncAdapterLock;

    static {
        sSyncAdapter = null;
        sSyncAdapterLock = new Object();
    }

    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncContactsAdapter(getApplicationContext(), true);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
