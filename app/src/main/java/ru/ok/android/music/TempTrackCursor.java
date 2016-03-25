package ru.ok.android.music;

import java.util.Arrays;
import ru.ok.android.music.CursorPlayList.CursorIsNullException;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.model.wmf.Track;

public class TempTrackCursor implements CacheTrackCursor {
    private boolean closed;
    private int cursorPosition;
    private Track[] tracks;

    public TempTrackCursor(Track[] tracks, boolean shuffle) throws CursorIsNullException {
        if (tracks == null) {
            throw new CursorIsNullException();
        }
        this.tracks = (Track[]) Arrays.copyOf(tracks, tracks.length);
        this.cursorPosition = -1;
        if (shuffle) {
            Utils.shuffle(this.tracks);
        }
    }

    public TempTrackCursor(TempTrackCursor cacheTrackCursor, boolean shuffle) throws CursorIsNullException {
        this(cacheTrackCursor.tracks, shuffle);
    }

    public boolean next() {
        return !this.closed && moveToPosition(this.cursorPosition + 1);
    }

    public boolean prev() {
        return !this.closed && moveToPosition(this.cursorPosition - 1);
    }

    public Track getTrack() {
        if (this.cursorPosition >= 0 && this.cursorPosition != this.tracks.length) {
            return this.tracks[this.cursorPosition];
        }
        Logger.m176e("TempTrackCursor is out of bounds. Cursor position: " + this.cursorPosition + " Tracks length: " + this.tracks.length);
        return null;
    }

    public long getTrackId() {
        return getTrack().id;
    }

    public void moveToStart() {
        moveToPosition(-1);
    }

    public boolean moveToPosition(int pos) {
        if (pos >= this.tracks.length) {
            this.cursorPosition = this.tracks.length;
            return false;
        } else if (pos < 0) {
            this.cursorPosition = -1;
            return false;
        } else {
            this.cursorPosition = pos;
            return true;
        }
    }

    public void moveToFirst() {
        this.cursorPosition = 0;
    }

    public void moveToLast() {
        this.cursorPosition = this.tracks.length - 1;
    }

    public boolean isLastTrack() {
        return this.cursorPosition == this.tracks.length + -1;
    }

    public boolean isAfterLastTrack() {
        return this.cursorPosition == this.tracks.length;
    }

    public boolean isFirstTrack() {
        return this.cursorPosition == 0;
    }

    public int getPosition() {
        return this.cursorPosition;
    }

    public void close() {
        this.closed = true;
    }
}
