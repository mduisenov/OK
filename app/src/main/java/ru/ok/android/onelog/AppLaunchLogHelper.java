package ru.ok.android.onelog;

import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import java.io.Serializable;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;
import ru.ok.onelog.Item;
import ru.ok.onelog.app.launch.AppLaunchPushNotificationSubSource;
import ru.ok.onelog.app.push.PushDeliveryClickTimeFactory;
import ru.ok.onelog.builtin.DurationInterval;

public class AppLaunchLogHelper {
    public static boolean isShareIntent(@NonNull Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return false;
        }
        boolean z = true;
        switch (action.hashCode()) {
            case -1173264947:
                if (action.equals("android.intent.action.SEND")) {
                    z = false;
                    break;
                }
                break;
            case -58484670:
                if (action.equals("android.intent.action.SEND_MULTIPLE")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case RECEIVED_VALUE:
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return true;
            default:
                return false;
        }
    }

    public static void logIntent(@NonNull Intent intent) {
        logPushDeliveryClickTime(intent);
        OneLogIntentUtils.logIntent(intent);
        AppLaunchMonitor.notify(intent);
    }

    private static void logPushDeliveryClickTime(Intent intent) {
        long deliveryTime = intent.getLongExtra("extra_push_delivery_time", 0);
        if (deliveryTime != 0) {
            intent.removeExtra("extra_push_delivery_time");
            Logger.m173d("Delivery: %d. Delta seconds: %f", Long.valueOf(deliveryTime), Double.valueOf(((double) (SystemClock.elapsedRealtime() - deliveryTime)) / 60.0d));
            OneLog.log(PushDeliveryClickTimeFactory.get(DurationInterval.valueOfMillis(delta), (AppLaunchPushNotificationSubSource) intent.getSerializableExtra("extra_push_delivery_type")));
        }
    }

    static void fillIntent(@NonNull Intent intent, @NonNull Item item) {
        fillIntent(intent, (Serializable) item);
    }

    private static void fillIntent(@NonNull Intent intent, @NonNull Serializable item) {
        intent.putExtra("extra_one_log_items", item);
        intent.putExtra("extra_notify_app_launch_monitor", true);
    }
}
