package ru.ok.android.services.processors.poll;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.json.JsonAppPollParser;
import ru.ok.model.poll.AppPollAnswer;
import ru.ok.model.poll.TablePollQuestion.TablePollItem;

public class AppPollPreferences {
    public static final List<TablePollItem> defaultItems;
    private static Boolean isStarted;
    private static Integer version;

    static {
        defaultItems = new ArrayList(9);
        defaultItems.add(getMusicItem());
        defaultItems.add(getVideoItem());
        defaultItems.add(getFriendsItem());
        defaultItems.add(getGamesItem());
        defaultItems.add(getHolidaysItem());
        defaultItems.add(getMessageItem());
        defaultItems.add(getPhotosItem());
        defaultItems.add(getFeedItem());
        defaultItems.add(getOtherItem());
        version = null;
        isStarted = null;
    }

    public static synchronized boolean isTimingReadyToUpdate(Context context) {
        boolean z;
        synchronized (AppPollPreferences.class) {
            z = System.currentTimeMillis() - getLastTimingTime(context) > getTimingInterval(context);
        }
        return z;
    }

    private static long getTimingInterval(Context context) {
        return 1000 * Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("app_poll_timing_interval", Long.toString(21600)));
    }

    public static synchronized boolean isTimeToLoadPoll(Context context) {
        boolean z;
        synchronized (AppPollPreferences.class) {
            z = System.currentTimeMillis() - getLastUpdateTime(context) > getUpdateInterval(context) && !isStarted(context) && "ru".equals(Settings.getCurrentLocale(context));
        }
        return z;
    }

    private static long getUpdateInterval(Context context) {
        return 1000 * Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("app_poll_load_interval", Long.toString(259200)));
    }

    public static synchronized boolean isTimeToShowPoll(Context context) {
        boolean z;
        synchronized (AppPollPreferences.class) {
            z = System.currentTimeMillis() - getLastDisplayTime(context) > getShowInterval(context) && getVersion(context) != 0;
        }
        return z;
    }

    public static synchronized boolean isInShowingInterval(Context context) {
        boolean z;
        synchronized (AppPollPreferences.class) {
            z = System.currentTimeMillis() - getLastDisplayTime(context) < getShowingInterval(context);
        }
        return z;
    }

    private static long getShowInterval(Context context) {
        return 1000 * Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("app_poll_display_interval", Long.toString(259200)));
    }

    private static long getShowingInterval(Context context) {
        return 1000 * Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("app_poll_display_duration_interval", Long.toString(180)));
    }

    public static synchronized int getStep(Context context) {
        int intValue;
        synchronized (AppPollPreferences.class) {
            intValue = Settings.getIntValue(context, "app_poll_step", 0);
        }
        return intValue;
    }

    public static synchronized void setStep(Context context, int step) {
        synchronized (AppPollPreferences.class) {
            Settings.storeIntValue(context, "app_poll_step", step);
        }
    }

    public static synchronized long getLastDisplayTime(Context context) {
        long longValue;
        synchronized (AppPollPreferences.class) {
            longValue = Settings.getLongValue(context, "app_poll_last_see", 0);
        }
        return longValue;
    }

    public static synchronized void setLastDisplayTime(Context context, long lastDisplayTime) {
        synchronized (AppPollPreferences.class) {
            Settings.storeLongValue(context, "app_poll_last_see", lastDisplayTime);
        }
    }

    public static synchronized long getLastTimingTime(Context context) {
        long longValue;
        synchronized (AppPollPreferences.class) {
            longValue = Settings.getLongValue(context, "app_poll_last_timing", 0);
        }
        return longValue;
    }

    public static synchronized void setLastTimingTime(Context context, long lastTimingTime) {
        synchronized (AppPollPreferences.class) {
            Settings.storeLongValue(context, "app_poll_last_timing", lastTimingTime);
        }
    }

    public static synchronized long getLastUpdateTime(Context context) {
        long longValue;
        synchronized (AppPollPreferences.class) {
            longValue = Settings.getLongValue(context, "app_poll_last_load", 0);
        }
        return longValue;
    }

    public static synchronized void setLastUpdateTime(Context context, long lastLoadTime) {
        synchronized (AppPollPreferences.class) {
            Settings.storeLongValue(context, "app_poll_last_load", lastLoadTime);
        }
    }

    public static synchronized void clearAppPoll(Context context) {
        synchronized (AppPollPreferences.class) {
            setStarted(context, false);
            setAnswers(context, new ArrayList());
            setStepsJson(context, "");
            setVersion(context, 0);
            setStep(context, 0);
        }
    }

    public static synchronized boolean isAppPollRepeatMode(Context context) {
        boolean z;
        synchronized (AppPollPreferences.class) {
            z = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("app_poll_repeat_mode", false);
        }
        return z;
    }

    public static synchronized int getVersion(Context context) {
        int intValue;
        synchronized (AppPollPreferences.class) {
            if (version == null) {
                version = Integer.valueOf(Settings.getIntValue(context, "app_poll_version", 0));
            }
            intValue = version.intValue();
        }
        return intValue;
    }

    public static synchronized void setVersion(Context context, int version) {
        synchronized (AppPollPreferences.class) {
            version = Integer.valueOf(version);
            Settings.storeIntValue(context, "app_poll_version", version);
        }
    }

    public static synchronized String getTextByKey(Context context, String key) {
        String strValue;
        synchronized (AppPollPreferences.class) {
            strValue = Settings.getStrValue(context, key);
        }
        return strValue;
    }

    public static synchronized boolean isStarted(Context context) {
        boolean booleanValue;
        synchronized (AppPollPreferences.class) {
            if (isStarted == null) {
                isStarted = Boolean.valueOf(Settings.getBoolValue(context, "app_poll_started", false));
            }
            booleanValue = isStarted.booleanValue();
        }
        return booleanValue;
    }

    public static synchronized void setStarted(Context context, boolean started) {
        synchronized (AppPollPreferences.class) {
            isStarted = Boolean.valueOf(started);
            Settings.storeBoolValue(context, "app_poll_started", started);
        }
    }

    public static synchronized void setStepsJson(Context context, String stepsJson) {
        synchronized (AppPollPreferences.class) {
            Settings.storeStrValue(context, "app_poll_steps_json", stepsJson);
        }
    }

    public static synchronized String getStepsJson(Context context) {
        String strValue;
        synchronized (AppPollPreferences.class) {
            strValue = Settings.getStrValue(context, "app_poll_steps_json");
        }
        return strValue;
    }

    public static synchronized void setAnswers(Context context, List<AppPollAnswer> answers) {
        synchronized (AppPollPreferences.class) {
            JSONArray answersJson = new JSONArray();
            for (AppPollAnswer answer : answers) {
                answersJson.put(JsonAppPollParser.toJson(answer));
            }
            Settings.storeStrValue(context, "app_poll_answers_json", answersJson.toString());
        }
    }

    public static synchronized ArrayList<AppPollAnswer> getAnswers(Context context) {
        ArrayList<AppPollAnswer> arrayList;
        synchronized (AppPollPreferences.class) {
            String answersString = Settings.getStrValue(context, "app_poll_answers_json");
            if (answersString == null || "".equals(answersString)) {
                arrayList = new ArrayList();
            } else {
                try {
                    JSONArray answersJson = new JSONArray(answersString);
                    arrayList = new ArrayList(answersJson.length());
                    for (int i = 0; i < answersJson.length(); i++) {
                        arrayList.add(JsonAppPollParser.answerByJson(answersJson.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    Settings.storeStrValue(context, "app_poll_answers_json", "");
                    arrayList = new ArrayList();
                }
            }
        }
        return arrayList;
    }

    public static void parseAndSave(Context context, JSONObject responseJson) {
        if (JsonAppPollParser.isNewPoll(responseJson)) {
            try {
                String steps = JsonAppPollParser.getStepsString(responseJson, defaultItems);
                JsonAppPollParser.Settings settings = JsonAppPollParser.getSettings(responseJson);
                Logger.m173d("Loaded AppPoll. Version: %d", Integer.valueOf(JsonAppPollParser.getVersion(responseJson)));
                synchronized (AppPollPreferences.class) {
                    setStepsJson(context, steps);
                    setVersion(context, version);
                    Settings.storeStrValue(context, "app_poll_actionbar_title", settings.actionBarTitle);
                    Settings.storeStrValue(context, "app_poll_final", settings.finalString);
                    Settings.storeStrValue(context, "app_poll_button_next", settings.buttonNext);
                    Settings.storeStrValue(context, "app_poll_button_next_final", settings.buttonNextFinal);
                    Settings.storeStrValue(context, "app_poll_stream_cancel", settings.streamCancel);
                    Settings.storeStrValue(context, "app_poll_stream_description", settings.streamDescription);
                    Settings.storeStrValue(context, "app_poll_stream_title", settings.streamTitle);
                    Settings.storeStrValue(context, "app_poll_stream_resume", settings.streamResume);
                    Settings.storeStrValue(context, "app_poll_stream_start", settings.streamStart);
                }
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        } else {
            synchronized (AppPollPreferences.class) {
                if (!isStarted(context)) {
                    clearAppPoll(context);
                }
            }
            Logger.m172d("Last AppPoll is finished");
        }
        setLastUpdateTime(context, System.currentTimeMillis());
    }

    public static TablePollItem getMusicItem() {
        return new TablePollItem("0", "\u041f\u043e\u0441\u043b\u0443\u0448\u0430\u0442\u044c \u043c\u0443\u0437\u044b\u043a\u0443", 2130837636, 2130837637);
    }

    public static TablePollItem getVideoItem() {
        return new TablePollItem("1", "\u041f\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0440\u043e\u043b\u0438\u043a\u0438", 2130837646, 2130837647);
    }

    public static TablePollItem getGamesItem() {
        return new TablePollItem("2", "\u041f\u043e\u0438\u0433\u0440\u0430\u0442\u044c \u0432 \u0438\u0433\u0440\u044b", 2130837626, 2130837627);
    }

    public static TablePollItem getMessageItem() {
        return new TablePollItem("3", "\u041e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u044c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435", 2130837634, 2130837635);
    }

    public static TablePollItem getHolidaysItem() {
        return new TablePollItem("4", "\u041f\u043e\u0437\u0434\u0440\u0430\u0432\u0438\u0442\u044c \u0434\u0440\u0443\u0437\u0435\u0439", 2130837630, 2130837631);
    }

    public static TablePollItem getPhotosItem() {
        return new TablePollItem("5", "\u041f\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0444\u043e\u0442\u043e", 2130837640, 2130837641);
    }

    public static TablePollItem getFriendsItem() {
        return new TablePollItem("6", "\u041d\u0430\u0439\u0442\u0438 \u043d\u043e\u0432\u044b\u0445 \u0434\u0440\u0443\u0437\u0435\u0439", 2130837624, 2130837625);
    }

    public static TablePollItem getFeedItem() {
        return new TablePollItem("7", "\u0423\u0437\u043d\u0430\u0442\u044c \u043e \u043d\u043e\u0432\u044b\u0445 \u0441\u043e\u0431\u044b\u0442\u0438\u044f\u0445", 2130837622, 2130837623);
    }

    public static TablePollItem getOtherItem() {
        return new TablePollItem("8", "\u0421\u0432\u043e\u0439 \u0432\u0430\u0440\u0438\u0430\u043d\u0442", 2130837628, 2130837629, "\u041e\u043f\u0438\u0448\u0438\u0442\u0435 \u0441\u0432\u043e\u0439 \u0432\u0430\u0440\u0438\u0430\u043d\u0442");
    }

    public static void parseAndSaveTimingFields(Context context, JSONObject settingsResponse) {
        Scanner display = new Scanner(settingsResponse.optString("app.poll.interval.display"));
        Scanner displayDuration = new Scanner(settingsResponse.optString("app.poll.interval.display.duration"));
        Scanner update = new Scanner(settingsResponse.optString("app.poll.interval.update"));
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        if (display.hasNextLong()) {
            editor.putString("app_poll_display_interval", Long.toString(display.nextLong()));
        }
        if (displayDuration.hasNextLong()) {
            editor.putString("app_poll_display_duration_interval", Long.toString(displayDuration.nextLong()));
        }
        if (update.hasNextLong()) {
            editor.putString("app_poll_load_interval", Long.toString(update.nextLong()));
        }
        editor.apply();
        setLastTimingTime(context, System.currentTimeMillis());
    }
}
