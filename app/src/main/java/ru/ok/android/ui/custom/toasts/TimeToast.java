package ru.ok.android.ui.custom.toasts;

import android.content.Context;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import ru.ok.android.utils.localization.LocalizationManager;

public class TimeToast {
    private static long DELAY;
    private static Map<Integer, Long> times;

    static {
        times = new HashMap();
        DELAY = 3000;
    }

    public static void show(Context context, int textId, int duration) {
        long t = System.currentTimeMillis();
        if (t - (times.containsKey(Integer.valueOf(textId)) ? ((Long) times.get(Integer.valueOf(textId))).longValue() : 0) > DELAY) {
            times.put(Integer.valueOf(textId), Long.valueOf(t));
            Toast.makeText(context, LocalizationManager.getString(context, textId), duration).show();
        }
    }

    public static void show(Context context, String text, int duration) {
        long t = System.currentTimeMillis();
        if (t - (times.containsKey(Integer.valueOf(text.hashCode())) ? ((Long) times.get(Integer.valueOf(text.hashCode()))).longValue() : 0) > DELAY) {
            times.put(Integer.valueOf(text.hashCode()), Long.valueOf(t));
            Toast.makeText(context, text, duration).show();
        }
    }
}
