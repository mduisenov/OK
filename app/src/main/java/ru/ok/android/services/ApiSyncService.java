package ru.ok.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.utils.Logger;

public class ApiSyncService extends Service {
    private static AtomicInteger syncInProgressCount;
    private int lastStartedId;
    private WakeLock powerLock;
    private final StopHandler stopHandler;
    private WifiLock wifiLock;

    static class StopHandler extends Handler {
        private final WeakReference<ApiSyncService> serviceRef;

        StopHandler(ApiSyncService service) {
            this.serviceRef = new WeakReference(service);
        }

        public void handleMessage(Message msg) {
            ApiSyncService service = (ApiSyncService) this.serviceRef.get();
            if (service != null) {
                switch (msg.what) {
                    case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                        Logger.m173d("stopSelf: startId=%d", Integer.valueOf(msg.arg1));
                        service.stopSelf(startId);
                        service.onStopSync();
                    default:
                }
            }
        }
    }

    public ApiSyncService() {
        this.stopHandler = new StopHandler(this);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    static {
        syncInProgressCount = new AtomicInteger(0);
    }

    public void onCreate() {
        Logger.m172d("");
    }

    public void onDestroy() {
        Logger.m172d("");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? null : intent.getAction();
        Logger.m173d("onStartCommand >>> action=%s, start id=%d", action, Integer.valueOf(startId));
        if ("ru.ok.android.action.START_SYNC".equals(action)) {
            if (this.lastStartedId != 0) {
                Logger.m173d("onStartCommand: stopping previous start id=%d", Integer.valueOf(this.lastStartedId));
                postStopSelf(this.lastStartedId);
            } else {
                onStartSync();
            }
            this.lastStartedId = startId;
        } else if ("ru.ok.android.action.STOP_SYNC".equals(action)) {
            if (this.lastStartedId != 0) {
                Logger.m173d("onStartCommand: stopping previous start id=%d", Integer.valueOf(this.lastStartedId));
                postStopSelf(this.lastStartedId);
            }
            Logger.m173d("onStartCommand: stopping start id=%d", Integer.valueOf(startId));
            postStopSelf(startId);
            this.lastStartedId = 0;
            onStopSync();
        } else {
            Logger.m185w("onStartCommand: unexpected action=%s, stopping start id=%d", action, Integer.valueOf(startId));
            postStopSelf(startId);
        }
        Logger.m172d("<<< onStartCommand");
        return 3;
    }

    private void onStartSync() {
        Logger.m172d("");
        if (this.powerLock == null) {
            this.powerLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "ru.ok.android:ApiSyncService");
            this.powerLock.setReferenceCounted(true);
        }
        this.powerLock.acquire();
        if (this.wifiLock == null) {
            this.wifiLock = ((WifiManager) getSystemService("wifi")).createWifiLock(1, "ru.ok.android:ApiSyncService");
            this.wifiLock.setReferenceCounted(true);
        }
        this.wifiLock.acquire();
    }

    private void onStopSync() {
        Logger.m172d("");
        if (this.powerLock != null) {
            this.powerLock.release();
            if (!this.powerLock.isHeld()) {
                this.powerLock = null;
            }
        }
        if (this.wifiLock != null) {
            this.wifiLock.release();
            if (!this.wifiLock.isHeld()) {
                this.wifiLock = null;
            }
        }
    }

    public static void startSync(Context context) {
        Logger.m173d("startSync: syncCount=%d", Integer.valueOf(syncInProgressCount.incrementAndGet()));
        if (syncInProgressCount.incrementAndGet() == 1) {
            Logger.m172d("startSync: starting sync service...");
            Intent start = new Intent(context, ApiSyncService.class);
            start.setAction("ru.ok.android.action.START_SYNC");
            context.startService(start);
        }
    }

    public static void stopSync(Context context) {
        Logger.m173d("stopSync: syncCount=%d", Integer.valueOf(syncInProgressCount.decrementAndGet()));
        if (syncInProgressCount.decrementAndGet() == 0) {
            Logger.m172d("stopSync: stopping sync service...");
            Intent start = new Intent(context, ApiSyncService.class);
            start.setAction("ru.ok.android.action.STOP_SYNC");
            context.startService(start);
        }
    }

    private void postStopSelf(int startId) {
        Logger.m173d("postStopSelf: startId=%d", Integer.valueOf(startId));
        this.stopHandler.sendMessageDelayed(Message.obtain(this.stopHandler, 1, startId, 0, null), 30000);
    }
}
