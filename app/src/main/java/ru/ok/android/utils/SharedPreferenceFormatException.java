package ru.ok.android.utils;

public class SharedPreferenceFormatException extends Exception {
    private static final long serialVersionUID = 1;

    public SharedPreferenceFormatException(String detailMessage) {
        super(detailMessage);
    }

    public SharedPreferenceFormatException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
