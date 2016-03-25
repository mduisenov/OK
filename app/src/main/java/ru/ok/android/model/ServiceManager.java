package ru.ok.android.model;

import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;
import ru.ok.android.model.SystemServiceConnection.OnConnectionServiceListener;
import ru.ok.android.services.app.OdnoklassnikiService;

public class ServiceManager implements OnConnectionServiceListener {
    private boolean bound;
    private SystemServiceConnection connection;
    private Context context;
    private Messenger service;

    public ServiceManager() {
        this.connection = new SystemServiceConnection();
        this.connection.addOnConnectionServiceListener(this);
    }

    public void bindService(Context context) {
        this.context = context;
        connectToService(context);
        Log.d("ServiceManager", "bind to service");
    }

    private void connectToService(Context context) {
        context.bindService(new Intent(context, OdnoklassnikiService.class), this.connection, 1);
    }

    public void unBindService() {
        if (this.context != null) {
            if (isBound()) {
                this.context.unbindService(this.connection);
            }
            this.bound = false;
            this.context = null;
            Log.d("ServiceManager", "unbind to service");
        }
    }

    public boolean isBound() {
        return this.bound;
    }

    public void onConnected(Messenger service) {
        this.service = service;
        this.bound = true;
        Log.d("ServiceManager", "Connect to service");
        if (!this.bound) {
            unBindService();
        }
    }

    public void onDisConnected() {
        this.service = null;
        this.bound = false;
        Log.d("ServiceManager", "Disconnect to service");
    }
}
