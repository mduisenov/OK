package ru.ok.model.stream;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class StreamPageKeySerializer {
    public static void write(SimpleSerialOutputStream out, StreamPageKey pageKey) throws IOException {
        out.writeInt(2);
        out.writeString(pageKey.anchor);
        out.writeInt(pageKey.count);
        out.writeInt(pageKey.pageNumber);
    }

    public static StreamPageKey read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version < 1 || version > 2) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        StreamPageKey pageKey = new StreamPageKey();
        pageKey.anchor = in.readString();
        pageKey.count = in.readInt();
        if (version >= 2) {
            pageKey.pageNumber = in.readInt();
        } else {
            pageKey.pageNumber = -1;
        }
        return pageKey;
    }
}
