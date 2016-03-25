package ru.ok.android.storage.serializer;

import java.io.IOException;

public class SimpleSerialException extends IOException {
    private static final long serialVersionUID = 1;

    public SimpleSerialException(String detailMessage) {
        super(detailMessage);
    }

    public SimpleSerialException(String message, Throwable cause) {
        super(message, cause);
    }
}
