package ru.ok.android.ui.video.player;

public final class UnsupportedDrmException extends Exception {
    public final int reason;

    public UnsupportedDrmException(int reason) {
        this.reason = reason;
    }

    public UnsupportedDrmException(int reason, Exception cause) {
        super(cause);
        this.reason = reason;
    }
}
