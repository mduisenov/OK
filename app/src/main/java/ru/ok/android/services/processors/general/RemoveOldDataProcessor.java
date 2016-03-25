package ru.ok.android.services.processors.general;

import android.content.Context;
import java.util.concurrent.TimeUnit;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;

public final class RemoveOldDataProcessor {
    private static final long CLEAR_PERIOD_MS;
    private static final long MAX_DATA_AGE_MS;

    static {
        CLEAR_PERIOD_MS = TimeUnit.DAYS.toMillis(10);
        MAX_DATA_AGE_MS = TimeUnit.DAYS.toMillis(178);
    }

    @Subscribe(on = 2131623944, to = 2131624097)
    public void removeOldData(BusEvent event) {
        Context context = OdnoklassnikiApplication.getContext();
        long lastCallTime = Settings.getLongValueInvariable(context, "remove_old_data:last_call_ms", 0);
        long currentTime = System.currentTimeMillis();
        Logger.m173d("shouldProceed: %s, current delta: %d", Boolean.valueOf(currentTime - lastCallTime > CLEAR_PERIOD_MS), Long.valueOf(currentTime - lastCallTime));
        if (currentTime - lastCallTime > CLEAR_PERIOD_MS) {
            Logger.m172d("Start all data removing...");
            int count = context.getContentResolver().delete(OdklProvider.allTablesSilentUri(), "_last_update < ? AND _last_update <> 0", new String[]{String.valueOf(currentTime - MAX_DATA_AGE_MS)});
            Logger.m173d("Removing all data finished. Records removed: %d", Integer.valueOf(count));
            Settings.storeLongValueInvariable(context, "remove_old_data:last_call_ms", currentTime);
        }
    }
}
