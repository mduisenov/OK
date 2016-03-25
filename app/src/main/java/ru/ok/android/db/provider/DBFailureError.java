package ru.ok.android.db.provider;

public class DBFailureError extends Error {
    public DBFailureError(String message, Throwable throwable) {
        super(message, throwable);
    }
}
