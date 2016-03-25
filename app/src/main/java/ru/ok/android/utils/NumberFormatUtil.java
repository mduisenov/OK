package ru.ok.android.utils;

import java.text.NumberFormat;
import java.util.Locale;

public final class NumberFormatUtil {
    private static final NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getIntegerInstance(Locale.FRENCH);
    }

    public static String getFormatFrenchText(int count) {
        return numberFormat.format((long) count);
    }
}
