package ru.ok.android.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.Time;
import io.github.eterverda.sntp.SNTP;
import ru.ok.android.utils.localization.LocalizationManager;

public class DateFormatter {
    private static final String[] cached_00_09;
    private static final ThreadLocal<Time> nowLocal;
    private static final ThreadLocal<Time> timeLocal;

    static {
        cached_00_09 = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09"};
        timeLocal = new ThreadLocal();
        nowLocal = new ThreadLocal();
    }

    public static String getFormatStringFromDate(Context context, long date) {
        return getFormatStringFromDate(context, date, null);
    }

    public static String getFormatStringFromDate(Context context, long date, String timeZone) {
        if (context == null || date == 0) {
            return "";
        }
        Time time = timeZone == null ? obtainTimeDefaultTimeZone(date) : createTime(date, timeZone);
        Time now = timeZone == null ? obtainNowDefaultTimeZone() : createNowTime(timeZone);
        if (isSameDay(time, now)) {
            return getToDayString(time);
        }
        if (isPreviousDay(time, now)) {
            return getYesterdayString(context, time);
        }
        if (time.year == now.year) {
            return getThisYearDayString(context, time, false);
        }
        return getShortStringFromDate(context, time, false);
    }

    private static Time createTime(long date, String timeZone) {
        Time time = new Time(timeZone);
        time.set(date);
        return time;
    }

    private static Time createNowTime(String timeZone) {
        Time time = new Time(timeZone);
        time.set(SNTP.safeCurrentTimeMillisFromCache());
        return time;
    }

    public static String formatTodayTimeOrOlderDate(Context context, long date) {
        if (context == null || date == 0) {
            return "";
        }
        Time time = obtainTimeDefaultTimeZone(date);
        Time now = obtainNowDefaultTimeZone();
        if (isSameDay(time, now)) {
            return getToDayString(time);
        }
        if (isPreviousDay(time, now)) {
            return getYesterdayStringNoTime(context);
        }
        if (time.year == now.year) {
            return getThisYearDayStringNoTime(context, time);
        }
        return getShortStringFromDateNoTime(context, time);
    }

    @NonNull
    public static String getFormatStringFromDateNoTime(Context context, long date) {
        if (context == null || date == 0) {
            return "";
        }
        Time time = obtainTimeDefaultTimeZone(date);
        Time now = obtainNowDefaultTimeZone();
        if (isSameDay(time, now)) {
            return LocalizationManager.from(context).getString(2131166733);
        }
        if (isPreviousDay(time, now)) {
            return LocalizationManager.from(context).getString(2131166883);
        }
        if (now.year == time.year) {
            return getShortStringFromDateNoTime(context, time);
        }
        return getShortStringFromDateNoTime(context, time);
    }

    public static String getPhotoTimeString(Context context, long date) {
        if (context == null || date == 0) {
            return "";
        }
        return getFormatStringFromDateNoTime(context, date) + " " + LocalizationManager.getString(context, 2131165410) + " " + formatHHmm(date);
    }

    public static String getTimeStringFromSec(int sec) {
        if (sec <= 0) {
            return "00:00";
        }
        if (sec < 60) {
            return "00:" + toTwoDigitsString(sec);
        }
        int nMin = sec / 60;
        return toTwoDigitsString(nMin) + ":" + toTwoDigitsString(sec - (nMin * 60));
    }

    public static String formatHHmm(long date) {
        Time time = obtainTimeDefaultTimeZone(date);
        return toTwoDigitsString(time.hour) + ":" + toTwoDigitsString(time.minute);
    }

    public static String toTwoDigitsString(int value) {
        if (value < 0 || value >= 10) {
            return Integer.toString(value);
        }
        return cached_00_09[value];
    }

    public static String formatDeltaTimeFuture(Context context, long delta) {
        if (context == null) {
            return "";
        }
        if (delta < 60000) {
            int sec = (int) (delta / 1000);
            return sec + " " + LocalizationManager.getString(context, StringUtils.plural((long) sec, 2131166495, 2131166496, 2131166497));
        } else if (delta < 3600000) {
            int minutes = (int) ((delta / 60000) % 60);
            return minutes + " " + LocalizationManager.getString(context, StringUtils.plural((long) minutes, 2131166209, 2131166210, 2131166211));
        } else {
            int hours = (int) (delta / 3600000);
            return hours + " " + LocalizationManager.getString(context, StringUtils.plural((long) hours, 2131165980, 2131165981, 2131165982));
        }
    }

    public static String formatDeltaTimePast(Context context, long lastTime, boolean showInPrefix, boolean shortMonthes) {
        if (context == null) {
            return "";
        }
        long delta = SNTP.safeCurrentTimeMillisFromCache() - lastTime;
        if (delta < 60000) {
            return LocalizationManager.getString(context, 2131166471);
        }
        if (delta < 3600000) {
            int minutes = (int) ((delta / 60000) % 60);
            return minutes + " " + LocalizationManager.getString(context, StringUtils.plural((long) minutes, 2131166209, 2131166210, 2131166211)) + " " + LocalizationManager.getString(context, 2131165367);
        } else if (delta >= 10800000) {
            return getFormatStringFromInDate(context, lastTime, showInPrefix, shortMonthes);
        } else {
            int hours = (int) (delta / 3600000);
            return hours + " " + LocalizationManager.getString(context, StringUtils.plural((long) hours, 2131165980, 2131165981, 2131165982)) + " " + LocalizationManager.getString(context, 2131165367);
        }
    }

    private static String getToDayString(Time time) {
        return time.hour + ":" + toTwoDigitsString(time.minute);
    }

    private static String getYesterdayString(Context context, Time time) {
        return LocalizationManager.getString(context, 2131166883) + " " + time.hour + ":" + toTwoDigitsString(time.minute);
    }

    private static String getYesterdayStringNoTime(Context context) {
        return LocalizationManager.getString(context, 2131166883);
    }

    private static String getThisYearDayString(Context context, Time time, boolean shortMonths) {
        return time.monthDay + " " + LocalizationManager.getStringArray(context, shortMonths ? 2131558421 : 2131558420)[time.month] + " " + time.hour + ":" + toTwoDigitsString(time.minute);
    }

    private static String getThisYearDayStringNoTime(Context context, Time time) {
        return time.monthDay + " " + LocalizationManager.getStringArray(context, 2131558420)[time.month];
    }

    private static String getShortStringFromDate(Context context, Time time, boolean shortMonths) {
        return time.monthDay + " " + LocalizationManager.getStringArray(context, shortMonths ? 2131558421 : 2131558420)[time.month] + " " + time.year + " " + time.hour + ":" + toTwoDigitsString(time.minute);
    }

    private static String getShortStringFromDateNoTime(Context context, Time time) {
        return getShortStringFromDateNoTime(time, LocalizationManager.getStringArray(context, 2131558420));
    }

    private static String getShortStringFromDateNoTime(Time time, String[] monthsNamesArray) {
        if (monthsNamesArray == null || time.month >= monthsNamesArray.length) {
            return toTwoDigitsString(time.monthDay) + "/" + toTwoDigitsString(time.month + 1) + "/" + time.year;
        }
        return time.monthDay + " " + monthsNamesArray[time.month] + (time.year == obtainNowDefaultTimeZone().year ? "" : " " + time.year);
    }

    private static int getDaysInYear(int year) {
        if (isLeapYear(year)) {
            return 366;
        }
        return 365;
    }

    public static boolean isLeapYear(int year) {
        return year > 1918 && (year % 400 == 0 || ((year & 3) == 0 && year % 100 != 0));
    }

    private static boolean isSameDay(Time t1, Time t2) {
        return t1.year == t2.year && t1.yearDay == t2.yearDay;
    }

    private static boolean isPreviousDay(Time t1, Time t2) {
        return (t1.year == t2.year && t1.yearDay == t2.yearDay - 1) || (t1.year == t2.year - 1 && t2.yearDay == 0 && t1.yearDay == getDaysInYear(t1.year) - 1);
    }

    private static String getFormatStringFromInDate(Context context, long date, boolean showInPrefix, boolean shortMonths) {
        if (context == null || date == 0) {
            return "";
        }
        Time time = obtainTimeDefaultTimeZone(date);
        Time now = obtainNowDefaultTimeZone();
        if (isSameDay(time, now)) {
            if (showInPrefix) {
                return getToDayInString(context, time);
            }
            return getToDayString(time);
        } else if (isPreviousDay(time, now)) {
            if (showInPrefix) {
                return getYesterdayInString(context, time);
            }
            return getYesterdayString(context, time);
        } else if (time.year == now.year) {
            if (showInPrefix) {
                return getThisYearDayInString(context, time, shortMonths);
            }
            return getThisYearDayString(context, time, shortMonths);
        } else if (showInPrefix) {
            return getShortStringFromInDate(context, time, shortMonths);
        } else {
            return getShortStringFromDate(context, time, shortMonths);
        }
    }

    private static String getToDayInString(Context context, Time date) {
        return LocalizationManager.getString(context, 2131165997) + " " + date.hour + ':' + toTwoDigitsString(date.minute);
    }

    private static String getYesterdayInString(Context context, Time date) {
        return LocalizationManager.getString(context, 2131166883) + " " + LocalizationManager.getString(context, 2131165997) + " " + date.hour + ":" + toTwoDigitsString(date.minute);
    }

    private static String getThisYearDayInString(Context context, Time date, boolean shortMonths) {
        return date.monthDay + " " + LocalizationManager.getStringArray(context, shortMonths ? 2131558421 : 2131558420)[date.month] + " " + LocalizationManager.getString(context, 2131165997) + " " + date.hour + ":" + toTwoDigitsString(date.minute);
    }

    private static String getShortStringFromInDate(Context context, Time date, boolean shortMonths) {
        return date.monthDay + " " + LocalizationManager.getStringArray(context, shortMonths ? 2131558421 : 2131558420)[date.month] + " " + date.year + ", " + LocalizationManager.getString(context, 2131165997) + " " + date.hour + ":" + toTwoDigitsString(date.minute);
    }

    private static Time obtainTimeDefaultTimeZone(long time) {
        Time t = (Time) timeLocal.get();
        if (t == null) {
            t = new Time();
            timeLocal.set(t);
        }
        t.set(time);
        return t;
    }

    private static Time obtainNowDefaultTimeZone() {
        Time now = (Time) nowLocal.get();
        if (now == null) {
            now = new Time();
            nowLocal.set(now);
        }
        now.setToNow();
        return now;
    }
}
