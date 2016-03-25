package ru.ok.android.services.utils.users;

import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.utils.Logger;

public final class OnlineUsersManager {
    private static OnlineUsersManager instance;
    private boolean isWaitingResult;
    private long timeCallBack;

    private OnlineUsersManager() {
    }

    public static OnlineUsersManager getInstance() {
        if (instance == null) {
            instance = new OnlineUsersManager();
        }
        return instance;
    }

    public boolean getOnlineUsers() {
        if (System.currentTimeMillis() - this.timeCallBack < 120000 || this.isWaitingResult) {
            return false;
        }
        requestGetOnlineUsers();
        return true;
    }

    public void getOnlineUsersNow() {
        this.timeCallBack = 0;
        getOnlineUsers();
    }

    public void clear() {
        this.timeCallBack = 0;
    }

    private void requestGetOnlineUsers() {
        Logger.m172d("|>>>>>> get online users");
        this.isWaitingResult = true;
        GlobalBus.register(this);
        GlobalBus.send(2131623986, new BusEvent());
    }

    @Subscribe(on = 2131623946, to = 2131624165)
    public void onOnlineFetched(BusEvent event) {
        this.isWaitingResult = false;
        GlobalBus.unregister(this);
        if (event.resultCode == -1) {
            this.timeCallBack = System.currentTimeMillis();
        } else {
            this.timeCallBack = 0;
        }
    }
}
