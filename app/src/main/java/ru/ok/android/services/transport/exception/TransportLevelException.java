package ru.ok.android.services.transport.exception;

import ru.ok.java.api.exceptions.BaseApiException;

public class TransportLevelException extends BaseApiException {
    protected TransportLevelException() {
    }

    public TransportLevelException(Exception ex) {
        super(ex);
    }
}
