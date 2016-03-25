package ru.ok.android.utils.controls.authorization;

public interface OnLoginListener {
    void onLoginError(String str, int i, int i2);

    void onLoginSuccessful(String str, String str2);
}
