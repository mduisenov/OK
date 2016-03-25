package ru.ok.android.services.transport.exception;

public class NetworkException extends TransportLevelException {
    public NetworkException(Exception ex) {
        super(ex);
    }
}
