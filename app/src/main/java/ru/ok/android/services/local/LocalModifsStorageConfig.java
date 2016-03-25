package ru.ok.android.services.local;

public class LocalModifsStorageConfig {
    public final int maxStorageSize;
    public final int trimSize;

    public LocalModifsStorageConfig(int maxStorageSize, int trimSize) {
        this.maxStorageSize = maxStorageSize;
        this.trimSize = trimSize;
    }
}
