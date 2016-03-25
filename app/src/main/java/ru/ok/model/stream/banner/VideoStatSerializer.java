package ru.ok.model.stream.banner;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class VideoStatSerializer {
    public static void write(SimpleSerialOutputStream out, VideoStat stat) throws IOException {
        out.writeInt(1);
        out.writeInt(stat.type);
        out.writeString(stat.url);
    }

    public static VideoStat read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new VideoStat(in.readInt(), in.readString());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
