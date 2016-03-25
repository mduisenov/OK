package ru.ok.android.statistics.local.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class StaticsticsContract {

    public interface EventColumns extends BaseColumns {
    }

    public static final class Events implements EventColumns {
        public static final Uri CONTENT_URI;
        public static final Uri INSERT_WITH_PARAMS_URI;

        static {
            CONTENT_URI = Uri.parse("content://ru.ok.android.stat/events");
            INSERT_WITH_PARAMS_URI = Uri.parse("content://ru.ok.android.stat/insert_event_params");
        }
    }

    static boolean isEventColumn(String colums) {
        return "event_name".equals(colums) || "event_ts".equals(colums) || "_id".equals(colums);
    }
}
