package ru.ok.android.services.app.upgrade;

public class AppUpgradeException extends Exception {
    private static final long serialVersionUID = 1;

    public AppUpgradeException(String detailMessage) {
        super(detailMessage);
    }
}
