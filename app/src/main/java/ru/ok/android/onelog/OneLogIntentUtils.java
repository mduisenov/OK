package ru.ok.android.onelog;

import android.content.Intent;
import android.support.annotation.NonNull;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import ru.ok.onelog.Item;

public class OneLogIntentUtils {
    public static void logIntent(@NonNull Intent intent) {
        Serializable serializable = intent.getSerializableExtra("extra_one_log_items");
        if (serializable != null) {
            if (serializable instanceof List) {
                Iterator i$ = ((List) serializable).iterator();
                while (i$.hasNext()) {
                    OneLog.log((Item) i$.next());
                }
            } else if (serializable instanceof Item) {
                OneLog.log((Item) serializable);
            }
            intent.removeExtra("extra_one_log_items");
        }
    }
}
