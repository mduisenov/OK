package ru.ok.android.utils.bus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.java.api.request.presents.PresentsRequest.Direction;

public class BusPresentsHelper {
    public static void loadPresents(@Nullable String userId, @Nullable String anchor, @NonNull Direction direction) {
        Bundle bundle = new Bundle();
        bundle.putString("EXTRA_USER_ID", userId);
        bundle.putString("EXTRA_ANCHOR", anchor);
        bundle.putSerializable("EXTRA_PRESENT_DIRECTION", direction);
        GlobalBus.send(2131624012, new BusEvent(bundle));
    }
}
