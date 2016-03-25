package ru.ok.android.utils.bus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.ui.presents.BusWithEventStates;

public class BusReceivePresentHelper {
    @NonNull
    public static String loadPresentNotification(@NonNull String presentNotificationId) {
        Bundle bundle = new Bundle();
        bundle.putString("PRESENT_NOTIFICATION_ID", presentNotificationId);
        return BusWithEventStates.getInstance().send(2131624014, 2131624189, new BusEvent(bundle));
    }

    @NonNull
    public static String acceptPresent(@NonNull String presentNotificationId) {
        Bundle bundle = new Bundle();
        bundle.putString("PRESENT_NOTIFICATION_ID", presentNotificationId);
        return BusWithEventStates.getInstance().send(2131623947, 2131624126, new BusEvent(bundle));
    }

    @NonNull
    public static String declinePresent(@NonNull String presentNotificationId) {
        Bundle bundle = new Bundle();
        bundle.putString("PRESENT_NOTIFICATION_ID", presentNotificationId);
        return BusWithEventStates.getInstance().send(2131623962, 2131624141, new BusEvent(bundle));
    }
}
