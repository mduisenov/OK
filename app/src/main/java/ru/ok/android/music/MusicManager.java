package ru.ok.android.music;

public interface MusicManager {
    boolean isPlaying();

    void next();

    void notifyConnectionAvailable();

    boolean pause();

    boolean play();

    void prev();
}
