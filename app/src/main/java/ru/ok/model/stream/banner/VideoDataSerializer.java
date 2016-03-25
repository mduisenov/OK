package ru.ok.model.stream.banner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class VideoDataSerializer {
    public static void write(SimpleSerialOutputStream out, VideoData data) throws IOException {
        boolean z = true;
        out.writeInt(1);
        out.writeString(data.videoUrl);
        out.writeInt(data.durationSec);
        if (data.statEventsByType == null) {
            z = false;
        }
        out.writeBoolean(z);
        if (data.statEventsByType != null) {
            out.writeInt(size);
            for (List writeList : data.statEventsByType) {
                out.writeList(writeList);
            }
        }
    }

    public static VideoData read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        String videoUrl = in.readString();
        int durationSec = in.readInt();
        List[] stats = null;
        if (in.readBoolean()) {
            int size = in.readInt();
            stats = new ArrayList[size];
            for (int i = 0; i < size; i++) {
                stats[i] = in.readArrayList();
            }
        }
        return new VideoData(videoUrl, durationSec, stats);
    }
}
