package ru.ok.android.utils.controls.authorization;

public interface OnLogoutListener {
    void onLogoutError(Exception exception);

    void onLogoutSuccessful();

    void onStartLogout();
}
