package ru.ok.android.music;

import ru.ok.model.wmf.Track;

public interface CacheTrackCursor {
    void close();

    int getPosition();

    Track getTrack();

    long getTrackId();

    boolean isAfterLastTrack();

    boolean isFirstTrack();

    boolean isLastTrack();

    void moveToFirst();

    void moveToLast();

    boolean moveToPosition(int i);

    void moveToStart();

    boolean next();

    boolean prev();
}
