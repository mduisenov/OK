package ru.ok.android.utils.controls.nativeregistration;

public abstract class RegistrationConstants {
    public static String KEY_SESSION_ID;
    public static String KEY_TOKEN;
    public static String KEY_USER_ITEM;

    public enum EnterPasswordReason {
        RECOVER,
        REGAIN,
        CHANGE_AFTER_REGISTRATION
    }

    static {
        KEY_USER_ITEM = "userItem";
        KEY_TOKEN = "token";
        KEY_SESSION_ID = "sessionID";
    }
}
