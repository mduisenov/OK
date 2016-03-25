package ru.ok.android.storage;

import java.io.IOException;

public final class StorageException extends IOException {
    public StorageException(String msg, Exception root) {
        super(msg, root);
    }

    public StorageException(String msg) {
        super(msg);
    }
}
