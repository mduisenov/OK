package ru.ok.android.music.io;

import android.support.annotation.NonNull;
import java.io.IOException;
import java.io.InputStream;

public class DoubleSourceInputStream extends InputStream {
    private final InputStream first;
    private boolean firstEnds;
    private InputStream second;
    private final SecondStreamProvider secondStreamProvider;

    public interface SecondStreamProvider {
        InputStream getSecondStream() throws IOException;
    }

    public DoubleSourceInputStream(@NonNull InputStream first, @NonNull SecondStreamProvider secondStreamProvider) {
        this.firstEnds = false;
        this.first = first;
        this.secondStreamProvider = secondStreamProvider;
    }

    public void close() throws IOException {
        try {
            this.first.close();
            if (this.second != null) {
                this.second.close();
            }
        } catch (Throwable th) {
            if (this.second != null) {
                this.second.close();
            }
        }
    }

    public int available() throws IOException {
        if (!this.firstEnds) {
            return this.first.available();
        }
        if (this.second != null) {
            return this.second.available();
        }
        return 0;
    }

    public int read() throws IOException {
        if (!this.firstEnds) {
            int value = this.first.read();
            if (value != -1) {
                return value;
            }
            this.firstEnds = true;
            this.second = this.secondStreamProvider.getSecondStream();
        }
        return this.second.read();
    }

    public int read(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
        checkOffsetAndCount(buffer.length, byteOffset, byteCount);
        if (!this.firstEnds) {
            int n = this.first.read(buffer, byteOffset, byteCount);
            if (n != -1) {
                return n;
            }
            this.firstEnds = true;
            this.second = this.secondStreamProvider.getSecondStream();
        }
        return this.second.read(buffer, byteOffset, byteCount);
    }

    private void checkOffsetAndCount(int arrayLength, int offset, int count) {
        if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }
}
