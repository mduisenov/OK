package ru.ok.java.api.exceptions;

public final class HttpStatusException extends LogicLevelException {
    public HttpStatusException(int httpStatus, String message) {
        super(String.format("HTTP RESULT: %d. Error reason: %s", new Object[]{Integer.valueOf(httpStatus), message}));
    }
}
