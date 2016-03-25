package ru.ok.android.services.local;

import android.content.Context;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.utils.Logger;

class LocalModifsConnectivityListener {
    private final Context context;
    private final LocalModifsManager manager;

    public LocalModifsConnectivityListener(Context context, LocalModifsManager manager) {
        this.context = context.getApplicationContext();
        this.manager = manager;
    }

    @Subscribe(on = 2131623944, to = 2131624233)
    public void onConnectivityChanged(BusEvent event) {
        Logger.m172d("onConnectivityChanged");
        this.manager.sync();
    }
}
