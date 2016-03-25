package ru.ok.android.utils.bus;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;

public final class BusStreamHelper {
    public static void feedMarkAsSpam(long feedId, String spamId, String logContext) {
        Bundle input = new Bundle();
        input.putLong("FEED_ID", feedId);
        input.putString("SPAM_ID", spamId);
        input.putString("LOG_CONTEXT", logContext);
        GlobalBus.send(2131624107, new BusEvent(input));
    }
}
