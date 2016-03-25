package ru.ok.android.utils.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;

public class Settings {
    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static Editor getEditorInvariable(Context context) {
        return getPreferencesInvariable(context).edit();
    }

    public static void storeLibverifySession(Context context, String sessionId) {
        storeStrValueInvariable(context, "libverify_session_id", sessionId);
    }

    public static String getLibverifySession(Context context) {
        return getStrValueInvariable(context, "libverify_session_id", null);
    }

    public static void storeUserName(Context context, String name) {
        Editor editor = getEditor(context);
        editor.putString("login", name);
        commitEditor(editor);
    }

    public static void storeStrValue(Context context, String key, String value) {
        Editor editor = getEditor(context);
        editor.putString(key, value);
        commitEditor(editor);
    }

    public static String getStrValue(Context context, String key) {
        return getPreferences(context).getString(key, "");
    }

    public static void storeIntValue(Context context, String key, int value) {
        Editor editor = getEditor(context);
        editor.putInt(key, value);
        commitEditor(editor);
    }

    public static void storeLongValue(Context context, String key, long value) {
        Editor editor = getEditor(context);
        editor.putLong(key, value);
        commitEditor(editor);
    }

    public static int getIntValue(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    public static long getLongValue(Context context, String key, long defValue) {
        return getPreferences(context).getLong(key, defValue);
    }

    public static void storeBoolValue(Context context, String key, boolean value) {
        Editor editor = getEditor(context);
        editor.putBoolean(key, value);
        commitEditor(editor);
    }

    public static boolean getBoolValue(Context context, String key, boolean value) {
        return getPreferences(context).getBoolean(key, value);
    }

    public static boolean hasValueInvariable(Context context, String key) {
        return getPreferencesInvariable(context).contains(key);
    }

    public static void setAppLastUpgradeVersion(Context context, int upgradeId) {
        commitEditor(getEditor(context).putInt("last_app_upgrade_version", upgradeId));
    }

    public static int getLastAppUpgradeVersion(Context context) {
        return getIntValueInvariable(context, "last_app_upgrade_version", 0);
    }

    public static void storeBoolValueInvariable(Context context, String key, boolean value) {
        Editor editor = getEditorInvariable(context);
        editor.putBoolean(key, value);
        commitEditor(editor);
    }

    public static void storeIntValueInvariable(Context context, String key, int value) {
        Editor editor = getEditorInvariable(context);
        editor.putInt(key, value);
        commitEditor(editor);
    }

    public static void storeLongValueInvariable(Context context, String key, long value) {
        Editor editor = getEditorInvariable(context);
        editor.putLong(key, value);
        commitEditor(editor);
    }

    public static void storeStrValueInvariable(Context context, String key, String value) {
        Editor editor = getEditorInvariable(context);
        editor.putString(key, value);
        commitEditor(editor);
    }

    public static boolean getBoolValueInvariable(Context context, String key, boolean defaultValue) {
        return getPreferencesInvariable(context).getBoolean(key, defaultValue);
    }

    public static int getIntValueInvariable(Context context, String key, int defaultValue) {
        return getPreferencesInvariable(context).getInt(key, defaultValue);
    }

    public static long getLongValueInvariable(Context context, String key, long defaultValue) {
        return getPreferencesInvariable(context).getLong(key, defaultValue);
    }

    public static void setAuthorizedUserCount(Context context, int count) {
        if (AuthorizationPreferences.getMultipleLoginEnabled()) {
            storeAuthorizedUserCount(context, count);
        }
    }

    public static void removeAuthorizedUser(Context context) {
        if (AuthorizationPreferences.getMultipleLoginEnabled()) {
            storeAuthorizedUserCount(context, getAuthorizedUserCount(context) - 1);
        }
    }

    public static String getStrValueInvariable(Context context, String key, String defaultValue) {
        return getPreferencesInvariable(context).getString(key, defaultValue);
    }

    public static String getToken(Context context) {
        return getStrValue(context, "userToken");
    }

    public static void storeToken(Context context, String token) {
        storeStrValue(context, "userToken", token);
    }

    public static boolean getLibVerifyCompleted(Context context) {
        return getBoolValue(context, "libVerifyCompleted", false);
    }

    public static void setLibVerifyCompleted(Context context, boolean isCompleted) {
        storeBoolValue(context, "libVerifyCompleted", isCompleted);
    }

    public static void clearLoginData(Context context) {
        Editor editor = getEditor(context);
        storeToken(context, "");
        editor.putString("authHash", null);
        commitEditor(editor);
    }

    public static boolean hasLoginData(Context context) {
        return ("".equals(getPreferences(context).getString("login", "")) || "".equals(getToken(context))) ? false : true;
    }

    public static String getUserName(Context context) {
        return getPreferences(context).getString("login", "");
    }

    public static void clearSettingByKey(Context context, String key) {
        Editor editor = getEditor(context);
        editor.remove(key);
        commitEditor(editor);
    }

    public static void clearSettingInvariableByKey(Context context, String key) {
        Editor editor = getEditorInvariable(context);
        editor.remove(key);
        commitEditor(editor);
    }

    public static void clear(Context context) {
        Editor editor = getEditor(context);
        editor.clear();
        commitEditor(editor);
    }

    public static UserInfo getCurrentUser(Context context) {
        UserGenderType userGenderType;
        SharedPreferences settings = getPreferences(context);
        String string = settings.getString("uid", "");
        String string2 = settings.getString("first_name", "");
        String string3 = settings.getString("last_name", "");
        String string4 = settings.getString("pic", "");
        String string5 = settings.getString("pic224x224", "");
        String string6 = settings.getString("pic288x288", "");
        String string7 = settings.getString("pic600x600", "");
        int i = settings.getInt("age", 0);
        UserOnlineType userOnlineType = UserOnlineType.MOBILE;
        long j = settings.getLong("last_online", 0);
        if (settings.getBoolean("sex", true)) {
            userGenderType = UserGenderType.MALE;
        } else {
            userGenderType = UserGenderType.FEMALE;
        }
        return new UserInfo(string, string2, string3, null, string4, string5, string6, string7, i, null, userOnlineType, j, userGenderType, false, false, "", settings.getString("pid", null), settings.getString("big_pic", ""), false, true, false, null, null, false, false);
    }

    public static void storeAuthorizedUserCount(Context context, int userCount) {
        if (userCount < 0) {
            userCount = 0;
        }
        storeIntValueInvariable(context, "authorized_user_count", userCount);
    }

    public static int getAuthorizedUserCount(Context context) {
        if (AuthorizationPreferences.getMultipleLoginEnabled() && hasValueInvariable(context, "authorized_user_count")) {
            return getIntValueInvariable(context, "authorized_user_count", 0);
        }
        return 0;
    }

    public static void storeCurrentUserValue(Context context, UserInfo user) {
        Editor editor = getEditor(context);
        if (user != null) {
            fillCurrentUserFields(editor, user);
        } else {
            clearCurrentUserValue(editor);
        }
        commitEditor(editor);
    }

    private static void fillCurrentUserFields(Editor editor, UserInfo user) {
        editor.putString("first_name", user.firstName);
        editor.putString("last_name", user.lastName);
        editor.putString("uid", user.uid);
        editor.putString("pic", user.picUrl);
        editor.putString("pic224x224", user.pic224);
        editor.putString("pic288x288", user.pic288);
        editor.putString("pic600x600", user.pic600);
        editor.putInt("age", user.age);
        editor.putLong("last_online", user.lastOnline);
        editor.putBoolean("sex", user.genderType == UserGenderType.MALE);
        editor.putString("pid", user.pid);
        editor.putString("big_pic", user.bigPicUrl);
    }

    private static void clearCurrentUserValue(Editor editor) {
        editor.remove("first_name");
        editor.remove("last_name");
        editor.remove("uid");
        editor.remove("pic");
        editor.remove("pic224x224");
        editor.remove("pic288x288");
        editor.remove("pic600x600");
        editor.remove("age");
        editor.remove("last_online");
        editor.remove("sex");
        editor.remove("pid");
        editor.remove("big_pic");
    }

    public static String getCurrentLocale(Context context) {
        try {
            return getPreferencesInvariable(context).getString("locale", "");
        } catch (ClassCastException e) {
            return "ru";
        }
    }

    public static long getLastRequestTime(Context context) {
        return getPreferences(context).getLong("request_time", -1);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("PrefsFile1", 0);
    }

    private static SharedPreferences getPreferencesInvariable(Context context) {
        return context.getSharedPreferences("PrefsFileSavedAfterLogout", 0);
    }

    public static void setHttpRequestTime(Context context, long time) {
        commitEditor(getEditor(context).putLong("request_time", time));
    }

    public static void setCurrentLocale(Context context, String locale) {
        commitEditor(getEditorInvariable(context).putString("locale", locale));
    }

    public static void setLocaleLastUpdate(Context context, int seconds) {
        commitEditor(getEditor(context).putInt("localeLastUpdate", seconds));
    }

    public static int getLocaleLastUpdate(Context context) {
        return getPreferences(context).getInt("localeLastUpdate", 0);
    }

    public static void setLocaleModifiedAndPackage(Context context, String version, String currentPackage) {
        commitEditor(getEditor(context).putString("localeVersion", version).putString("localeLastPackage", currentPackage));
    }

    public static void setPlayListType(Context context, MusicListType type) {
        Editor editor = getEditor(context);
        if (type == null) {
            type = MusicListType.NO_DIRECTION;
        }
        commitEditor(editor.putString("playlist_type", type.name()));
    }

    public static MusicListType getPlayListType(Context context, MusicListType defType) {
        return MusicListType.valueOf(getPreferences(context).getString("playlist_type", defType.name()));
    }

    public static String getLocaleModified(Context context) {
        return getPreferences(context).getString("localeVersion", "");
    }

    public static String getLocaleLastPackage(Context context) {
        return getPreferences(context).getString("localeLastPackage", "");
    }

    public static void commitEditor(Editor editor) {
        editor.apply();
    }

    public static void setNoLoginState(Context context) {
        String userName = getUserName(context);
        storeCurrentUserValue(context, null);
        int version = getIntValue(context, "app_code_version", 0);
        clear(context);
        storeStrValue(context, "login", userName);
        storeIntValue(context, "app_code_version", version);
    }

    public static boolean isPlayOnlyCache(Context context) {
        return getBoolValue(context, context.getString(2131166373), false);
    }

    public static void setPlayOnlyCache(Context context, boolean value) {
        storeBoolValue(context, context.getString(2131166373), value);
    }

    public static List<String> getSuccessfulUsernames(Context context) {
        String userNames = getPreferencesInvariable(context).getString("UserNamesSuccessful", "");
        List<String> result = new ArrayList();
        for (String str : userNames.split(",")) {
            if (!TextUtils.isEmpty(str)) {
                result.add(str);
            }
        }
        return result;
    }

    public static void addSuccessfulUsername(Context context, String userName) {
        List<String> currentUserNames = getSuccessfulUsernames(context);
        currentUserNames.remove(userName);
        currentUserNames.add(0, userName);
        while (currentUserNames.size() > 3) {
            currentUserNames.remove(currentUserNames.size() - 1);
        }
        commitEditor(getEditorInvariable(context).putString("UserNamesSuccessful", TextUtils.join(",", currentUserNames)));
    }
}
