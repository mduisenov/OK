package ru.ok.java.api.exceptions;

public abstract class LogicLevelException extends BaseApiException {
    protected LogicLevelException(String message) {
        super(message);
    }

    protected LogicLevelException(Exception ex) {
        super(ex);
    }
}
