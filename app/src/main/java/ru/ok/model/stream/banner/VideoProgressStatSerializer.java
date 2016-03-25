package ru.ok.model.stream.banner;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class VideoProgressStatSerializer {
    public static void write(SimpleSerialOutputStream out, VideoProgressStat stat) throws IOException {
        out.writeInt(1);
        out.writeString(stat.url);
        out.writeInt(stat.positionSec);
    }

    public static VideoProgressStat read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new VideoProgressStat(in.readString(), in.readInt());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
