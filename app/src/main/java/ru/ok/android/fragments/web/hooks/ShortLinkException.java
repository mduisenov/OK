package ru.ok.android.fragments.web.hooks;

public class ShortLinkException extends Exception {
    private static final long serialVersionUID = 1;

    public ShortLinkException(String detailMessage) {
        super(detailMessage);
    }

    public ShortLinkException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
