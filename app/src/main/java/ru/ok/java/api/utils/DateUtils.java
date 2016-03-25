package ru.ok.java.api.utils;

import android.annotation.TargetApi;
import android.text.format.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    public static String getShortStringFromDate(Time date) {
        return date.monthDay + "-" + (date.month + 1) + "-" + date.year;
    }

    @TargetApi(3)
    public static boolean isBirthdayDate(Date dateTime) {
        if (dateTime == null) {
            return false;
        }
        Calendar calendarNow = new GregorianCalendar();
        Calendar calendarDay = new GregorianCalendar();
        calendarDay.setTime(dateTime);
        if (calendarNow.get(2) == calendarDay.get(2) && calendarNow.get(5) == calendarDay.get(5)) {
            return true;
        }
        return false;
    }

    public static Date getUserDateFromString(String dateTimeString) {
        try {
            return getBirthdayFormat().parse(dateTimeString);
        } catch (Exception e) {
            return null;
        }
    }

    public static SimpleDateFormat getBirthdayFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }
}
