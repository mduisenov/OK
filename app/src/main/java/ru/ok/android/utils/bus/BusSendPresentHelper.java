package ru.ok.android.utils.bus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.ui.presents.BusWithEventStates;

public class BusSendPresentHelper {
    @NonNull
    public static String loadPresentAndUser(@NonNull String presentId, @NonNull String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("EXTRA_PRESENT_ID", presentId);
        bundle.putString("EXTRA_USER_ID", userId);
        return BusWithEventStates.getInstance().send(2131624013, 2131624188, new BusEvent(bundle));
    }

    @NonNull
    public static String sendPresent(@NonNull String presentId, @NonNull String userId, @Nullable String token, @Nullable String message, @Nullable String holidayId, @NonNull String presentType) {
        Bundle bundle = new Bundle();
        bundle.putString("EXTRA_PRESENT_ID", presentId);
        bundle.putString("EXTRA_USER_ID", userId);
        bundle.putString("EXTRA_MESSAGE", message);
        bundle.putString("EXTRA_HOLIDAY_ID", holidayId);
        bundle.putString("EXTRA_PRESENT_TYPE", presentType);
        bundle.putString("EXTRA_TOKEN", token);
        return BusWithEventStates.getInstance().send(2131624100, 2131624242, new BusEvent(bundle));
    }
}
