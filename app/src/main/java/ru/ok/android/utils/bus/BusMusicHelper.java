package ru.ok.android.utils.bus;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;

public final class BusMusicHelper {
    public static void getCustomTrack(long trackId) {
        Bundle bundle = new Bundle();
        bundle.putLong("track_id", trackId);
        GlobalBus.send(2131624024, new BusEvent(bundle));
    }
}
