package ru.ok.android.web;

import java.io.IOException;

public final class WebHelperException extends IOException {
    public WebHelperException(String message, Throwable cause) {
        super(message);
    }

    public WebHelperException(String msg) {
        super(msg);
    }
}
