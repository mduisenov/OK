package ru.ok.android.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import ru.ok.android.proto.MessagesProto.Message;

public class StringUtils {
    public static final String[] EMPTY_STRING_ARRAY;

    static {
        EMPTY_STRING_ARRAY = new String[0];
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static int plural(int n, int resZero, int resOne, int resFew, int resMany) {
        return n == 0 ? resZero : plural((long) n, resOne, resFew, resMany);
    }

    public static int plural(long n, int form1, int form2, int form3) {
        int plural = (n % 10 != 1 || n % 100 == 11) ? (n % 10 < 2 || n % 10 > 4 || (n % 100 >= 10 && n % 100 < 20)) ? 2 : 1 : 0;
        switch (plural) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return form2;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return form3;
            default:
                return form1;
        }
    }

    public static String uppercaseFirst(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String removeEmptyLines(String s) {
        return TextUtils.isEmpty(s) ? s : s.replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    public static int linesCount(String s) {
        int ret = 1;
        if (s != null) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '\n') {
                    ret++;
                }
            }
        }
        return ret;
    }

    public static boolean allNotEmpty(String... strings) {
        for (String string : strings) {
            if (string == null || string.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static String trimToLength(@NonNull String str, int length) {
        return str.length() <= length ? str : str.substring(0, length);
    }

    @Nullable
    public static String safeToUpperCase(@Nullable String string) {
        return string == null ? null : string.trim().toUpperCase();
    }
}
