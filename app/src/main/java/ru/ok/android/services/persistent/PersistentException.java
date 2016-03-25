package ru.ok.android.services.persistent;

public class PersistentException extends Exception {
    PersistentException(String message) {
        super(message);
    }

    PersistentException(String message, Throwable e) {
        super(message, e);
    }
}
