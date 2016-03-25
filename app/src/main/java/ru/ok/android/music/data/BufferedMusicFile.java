package ru.ok.android.music.data;

import android.support.annotation.NonNull;
import java.io.File;
import java.io.IOException;
import ru.ok.android.utils.Logger;

public class BufferedMusicFile {
    private final boolean complete;
    private final long expectedLength;
    private final File file;

    public BufferedMusicFile(@NonNull File file, long expectedLength, boolean complete) {
        this.file = file;
        this.expectedLength = expectedLength;
        this.complete = complete;
    }

    public File getFile() {
        return this.file;
    }

    public long getExpectedLength() {
        return this.expectedLength;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public BufferedMusicFile move(@NonNull File newFile) throws IOException {
        if (newFile.exists() && !newFile.delete()) {
            Logger.m184w("Can't delete existing buffered music file");
        }
        if (this.file.renameTo(newFile)) {
            return new BufferedMusicFile(newFile, this.expectedLength, this.complete);
        }
        Logger.m184w("Can't move buffered music file.");
        throw new IOException("Can't move music buffer file");
    }
}
