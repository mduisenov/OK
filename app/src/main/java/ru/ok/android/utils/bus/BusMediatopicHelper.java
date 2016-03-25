package ru.ok.android.utils.bus;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;

public final class BusMediatopicHelper {
    public static void delete(long mediaTopicId, String mediaTopicDeleteId, String logContext) {
        Bundle bundle = new Bundle();
        bundle.putLong("mediatopic_id", mediaTopicId);
        bundle.putString("mediatopic_delete_id", mediaTopicDeleteId);
        bundle.putString("log_context", logContext);
        GlobalBus.send(2131624018, new BusEvent(bundle));
    }

    public static void setToStatus(long mediaTopicId, String logContext) {
        Bundle bundle = new Bundle();
        bundle.putLong("mediatopic_id", mediaTopicId);
        bundle.putString("log_context", logContext);
        GlobalBus.send(2131624020, new BusEvent(bundle));
    }

    public static void pin(long mediaTopicId, boolean pinOn, String logContext) {
        Bundle bundle = new Bundle();
        bundle.putLong("mediatopic_id", mediaTopicId);
        bundle.putBoolean("pin_on", pinOn);
        bundle.putString("log_context", logContext);
        GlobalBus.send(2131624019, new BusEvent(bundle));
    }

    public static void editText(String mediaTopicId, String newText, int blockIndex) {
        Bundle bundle = new Bundle();
        bundle.putString("mediatopic_id", mediaTopicId);
        bundle.putString("new_text", newText);
        bundle.putInt("block_index", blockIndex);
        GlobalBus.send(2131624090, new BusEvent(bundle));
    }
}
