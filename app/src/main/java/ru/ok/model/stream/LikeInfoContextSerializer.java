package ru.ok.model.stream;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class LikeInfoContextSerializer {
    public static void write(SimpleSerialOutputStream out, LikeInfoContext info) throws IOException {
        out.writeInt(1);
        out.writeInt(info.count);
        out.writeBoolean(info.self);
        out.writeLong(info.lastDate);
        out.writeString(info.likeId);
        out.writeBoolean(info.likePossible);
        out.writeBoolean(info.unlikePossible);
        out.writeInt(info.entityType);
        out.writeString(info.entityId);
    }

    public static LikeInfoContext read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new LikeInfoContext(in.readInt(), in.readBoolean(), in.readLong(), in.readString(), in.readBoolean(), in.readBoolean(), in.readInt(), in.readString());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
