package ru.ok.model.stream;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class LikeInfoSerializer {
    public static void write(SimpleSerialOutputStream out, LikeInfo likeInfo) throws IOException {
        out.writeInt(1);
        out.writeInt(likeInfo.count);
        out.writeBoolean(likeInfo.self);
        out.writeLong(likeInfo.lastDate);
        out.writeString(likeInfo.likeId);
        out.writeBoolean(likeInfo.likePossible);
        out.writeBoolean(likeInfo.unlikePossible);
    }

    public static LikeInfo read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new LikeInfo(in.readInt(), in.readBoolean(), in.readLong(), in.readString(), in.readBoolean(), in.readBoolean());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
