package ru.ok.android.statistics;

import ru.ok.android.utils.Bithacks;

public final class StatsUtils {
    public static int getDelayValue(long delayMs) {
        return Bithacks.ceilPow2((int) ((delayMs / 1000) + ((long) (delayMs % 1000 == 0 ? 0 : 1))));
    }

    public static int getRangedValue(int rawValue) {
        return Bithacks.ceilPow2(rawValue);
    }
}
