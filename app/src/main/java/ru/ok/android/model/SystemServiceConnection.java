package ru.ok.android.model;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import java.util.ArrayList;
import java.util.Iterator;

public class SystemServiceConnection implements ServiceConnection {
    private ArrayList<OnConnectionServiceListener> listeners;
    private boolean mBound;
    private Messenger mService;

    public interface OnConnectionServiceListener {
        void onConnected(Messenger messenger);

        void onDisConnected();
    }

    public SystemServiceConnection() {
        this.listeners = new ArrayList();
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mService = new Messenger(service);
        this.mBound = true;
        Iterator i$ = this.listeners.iterator();
        while (i$.hasNext()) {
            ((OnConnectionServiceListener) i$.next()).onConnected(this.mService);
        }
    }

    public void onServiceDisconnected(ComponentName name) {
        this.mService = null;
        this.mBound = false;
        Iterator i$ = this.listeners.iterator();
        while (i$.hasNext()) {
            ((OnConnectionServiceListener) i$.next()).onDisConnected();
        }
    }

    public void addOnConnectionServiceListener(OnConnectionServiceListener listener) {
        this.listeners.add(listener);
    }
}
