package ru.ok.android.services.processors.registration;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.StringUtils;

public class AuthorizationPreferences {

    private enum PreferencesField {
        NATIVE_REGISTRATION_ENABLED(Boolean.class, "native.registration.enabled"),
        LIBVERIFY_ENABLED(Boolean.class, "registration.libverify.enabled"),
        LIBVERIFY_PHONE_VALIDATION_ENABLED(Boolean.class, "registration.libverify.validation.enabled"),
        MULTIPLE_LOGIN_ENABLED(Boolean.class, "multiple.login.enabled"),
        GOOGLE_INFO_THROUGH_OAUTH(Boolean.class, "google.info.through.oauth"),
        PASSWORD_BEFORE_PROFILE(Boolean.class, "registration.password.before.profile"),
        PASSWORD_OBLIGATORY(Boolean.class, "registration.password.obligatory"),
        RECOMMENDED_FRIENDS_BY_PHONEBOOK(Boolean.class, "recommended.friends.by.phonebook"),
        PASSWORD_VALIDATION(Boolean.class, "registration.password.validation"),
        LOGIN_SCREEN_IMAGES(Set.class, "login.screen.image.urls"),
        PERMISSIONS_SEPARATE_SCREEN(Boolean.class, "registration.permissions.separate.screen"),
        PERMISSIONS_REQUEST_LOCATION(Boolean.class, "registration.permissions.request.location"),
        PERMISSIONS_REQUEST_PHONE_STATE(Boolean.class, "registration.permissions.request.phone.state"),
        PERMISSIONS_REQUEST_GET_ACCOUNTS(Boolean.class, "registration.permissions.request.get.accounts"),
        PERMISSIONS_REQUEST_SMS(Boolean.class, "registration.permissions.request.sms"),
        PERMISSIONS_REQUEST_SMS_ON_INIT(Boolean.class, "registration.permissions.request.sms.on.init");
        
        private Class<?> clazz;
        private String key;

        private PreferencesField(Class<?> clazz, String key) {
            this.clazz = clazz;
            this.key = key;
        }
    }

    public static String[] getSettingsKeys() {
        PreferencesField[] fields = PreferencesField.values();
        String[] keys = new String[fields.length];
        for (PreferencesField field : fields) {
            keys[field.ordinal()] = field.key;
        }
        return keys;
    }

    public static SharedPreferences getPreferences() {
        return OdnoklassnikiApplication.getContext().getSharedPreferences("authorization_prefs", 0);
    }

    public static boolean getLibVerifyEnabled() {
        return getBoolean(PreferencesField.LIBVERIFY_ENABLED);
    }

    public static boolean getLibVerifyPhoneValidationEnabled() {
        return getBoolean(PreferencesField.LIBVERIFY_PHONE_VALIDATION_ENABLED);
    }

    public static String[] filterNotGrantedPermissions(Context context, @NonNull String... necessaryPermissions) {
        if (PermissionUtils.checkAnySelfPermission(context, necessaryPermissions) == 0) {
            return StringUtils.EMPTY_STRING_ARRAY;
        }
        return necessaryPermissions;
    }

    public static String[] getNecessaryPermissions(Context context) {
        List<String> permissions = new ArrayList();
        if (getRequestLocationPermission()) {
            Collections.addAll(permissions, filterNotGrantedPermissions(context, "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"));
        }
        if (getRequestSmsPermission()) {
            Collections.addAll(permissions, filterNotGrantedPermissions(context, "android.permission.READ_SMS", "android.permission.RECEIVE_SMS"));
        }
        if (getRequestPhoneStatePermission()) {
            Collections.addAll(permissions, filterNotGrantedPermissions(context, "android.permission.READ_PHONE_STATE"));
        }
        if (getRequestGetAccountsPermission()) {
            Collections.addAll(permissions, filterNotGrantedPermissions(context, "android.permission.GET_ACCOUNTS"));
        }
        return (String[]) permissions.toArray(new String[permissions.size()]);
    }

    public static String[] getInitNecessaryPermissions(Context context) {
        List<String> permissions = new ArrayList();
        if (getRequestLocationPermission()) {
            Collections.addAll(permissions, filterNotGrantedPermissions(context, "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"));
        }
        if (getRequestSmsPermissionOnInit()) {
            Collections.addAll(permissions, filterNotGrantedPermissions(context, "android.permission.READ_SMS", "android.permission.RECEIVE_SMS"));
        }
        if (getRequestPhoneStatePermission()) {
            Collections.addAll(permissions, filterNotGrantedPermissions(context, "android.permission.READ_PHONE_STATE"));
        }
        if (getRequestGetAccountsPermission()) {
            Collections.addAll(permissions, filterNotGrantedPermissions(context, "android.permission.GET_ACCOUNTS"));
        }
        return (String[]) permissions.toArray(new String[permissions.size()]);
    }

    public static boolean getPermissionsRequestOnSeparateScreen() {
        return getBoolean(PreferencesField.PERMISSIONS_SEPARATE_SCREEN);
    }

    public static boolean getRequestPhoneStatePermission() {
        return getBoolean(PreferencesField.PERMISSIONS_REQUEST_PHONE_STATE);
    }

    public static boolean getRequestLocationPermission() {
        return getBoolean(PreferencesField.PERMISSIONS_REQUEST_LOCATION);
    }

    public static boolean getRequestSmsPermission() {
        return getBoolean(PreferencesField.PERMISSIONS_REQUEST_SMS);
    }

    public static boolean getRequestGetAccountsPermission() {
        return getBoolean(PreferencesField.PERMISSIONS_REQUEST_GET_ACCOUNTS);
    }

    public static String[] getRequestSmsPermissionArrayBeforeUsage() {
        if (VERSION.SDK_INT < 23 || !getRequestSmsPermissionBeforeUsage()) {
            return StringUtils.EMPTY_STRING_ARRAY;
        }
        return new String[]{"android.permission.RECEIVE_SMS", "android.permission.READ_SMS"};
    }

    public static boolean getRequestSmsPermissionOnInit() {
        return getRequestSmsPermission() && getBoolean(PreferencesField.PERMISSIONS_REQUEST_SMS_ON_INIT) && !getPermissionsRequestOnSeparateScreen();
    }

    public static boolean getRequestSmsPermissionBeforeUsage() {
        return (!getRequestSmsPermission() || getBoolean(PreferencesField.PERMISSIONS_REQUEST_SMS_ON_INIT) || getPermissionsRequestOnSeparateScreen()) ? false : true;
    }

    public static boolean getPasswordValidationEnabled() {
        return getBoolean(PreferencesField.PASSWORD_VALIDATION);
    }

    public static boolean getNativeRegistrationEnabled() {
        return getBoolean(PreferencesField.NATIVE_REGISTRATION_ENABLED);
    }

    public static boolean getRecommendedFriendsByPhonebookEnabled() {
        return getBoolean(PreferencesField.RECOMMENDED_FRIENDS_BY_PHONEBOOK);
    }

    public static boolean getMultipleLoginEnabled() {
        return getBoolean(PreferencesField.MULTIPLE_LOGIN_ENABLED);
    }

    public static boolean getGoogleInfoThroughOAuth() {
        return getBoolean(PreferencesField.GOOGLE_INFO_THROUGH_OAUTH);
    }

    public static HashSet<String> getLoginScreenImageUrls() {
        return (HashSet) getSet(PreferencesField.LOGIN_SCREEN_IMAGES);
    }

    public static boolean getPasswordObligatory() {
        return getBoolean(PreferencesField.PASSWORD_OBLIGATORY);
    }

    public static boolean getPasswordBeforeProfile() {
        return getBoolean(PreferencesField.PASSWORD_BEFORE_PROFILE);
    }

    public static boolean getPasswordObligatoryBeforeProfile() {
        return getPasswordObligatory() && getPasswordBeforeProfile();
    }

    private static boolean getBoolean(PreferencesField preferencesField) {
        return getPreferences().getBoolean(preferencesField.key, false);
    }

    private static Set<String> getSet(PreferencesField preferencesField) {
        return getPreferences().getStringSet(preferencesField.key, null);
    }

    public static void savePreferences(JSONObject jsonObject) {
        Editor editor = getPreferences().edit();
        for (PreferencesField preference : PreferencesField.values()) {
            if (preference.clazz.equals(Boolean.class)) {
                editor.putBoolean(preference.key, jsonObject.optBoolean(preference.key, false));
            } else if (preference.clazz.equals(Set.class)) {
                editor.putStringSet(preference.key, parseStringToSet(jsonObject.optString(preference.key, null)));
            } else if (preference.clazz.equals(String.class)) {
                editor.putString(preference.key, jsonObject.optString(preference.key, ""));
            }
        }
        editor.apply();
    }

    private static HashSet<String> parseStringToSet(String string) {
        if (string == null) {
            return null;
        }
        return new HashSet(Arrays.asList(string.split(",")));
    }
}
