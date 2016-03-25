package ru.ok.java.api.exceptions;

public abstract class BaseApiException extends Exception {
    protected BaseApiException() {
    }

    protected BaseApiException(String message) {
        super(message);
    }

    protected BaseApiException(Exception ex) {
        super(ex);
    }

    public BaseApiException(String message, Exception ex) {
        super(message, ex);
    }
}
