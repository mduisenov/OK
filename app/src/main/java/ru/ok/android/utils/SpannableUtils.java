package ru.ok.android.utils;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.Spanned;

public final class SpannableUtils {
    public static <T> T[] getSpans(@NonNull Spanned text, @NonNull Class<T> kind) {
        return text.getSpans(0, text.length(), kind);
    }

    public static int getFirstSpanStart(@NonNull Spanned text, @NonNull Class<?> kind) {
        return getFirstSpanStart(text, 0, text.length(), kind);
    }

    public static int getFirstSpanStart(@NonNull Spanned text, int indexFrom, int indexTo, @NonNull Class<?> kind) {
        int firstSpanStart = -1;
        for (Object what : text.getSpans(indexFrom, indexTo, kind)) {
            int spanStart = text.getSpanStart(what);
            if (firstSpanStart < 0 || spanStart < firstSpanStart) {
                firstSpanStart = spanStart;
            }
        }
        return firstSpanStart;
    }

    public static void setSpanOverSpan(@NonNull Spannable text, Object proto, Object what) {
        text.setSpan(what, text.getSpanStart(proto), text.getSpanEnd(proto), text.getSpanFlags(proto));
    }
}
