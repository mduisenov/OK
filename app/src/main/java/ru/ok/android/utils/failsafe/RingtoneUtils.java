package ru.ok.android.utils.failsafe;

import android.media.Ringtone;
import ru.ok.android.graylog.GrayLog;
import ru.ok.android.utils.Logger;

public class RingtoneUtils {
    public static void play(Ringtone ringtone) {
        try {
            ringtone.play();
        } catch (Throwable e) {
            GrayLog.log("RingtoneUtils.play error", e);
            Logger.m186w(e, "RingtoneUtils.play error");
        }
    }
}
