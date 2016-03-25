package ru.ok.model.stream;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class StreamPageSerializer {
    public static void write(SimpleSerialOutputStream out, StreamPage page) throws IOException {
        out.writeInt(1);
        out.writeObject(page.key);
        out.writeObject(page.topKey);
        out.writeObject(page.bottomKey);
        out.writeList(page.feeds);
        out.writeStringMap(page.entities);
        out.writeLong(page.pageTs);
        out.writeLong(page.streamTs);
    }

    public static StreamPage read(SimpleSerialInputStream in) throws IOException {
        StreamPage page = new StreamPage();
        read(in, page);
        return page;
    }

    static void read(SimpleSerialInputStream in, StreamPage page) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        page.key = (StreamPageKey) in.readObject();
        page.topKey = (StreamPageKey) in.readObject();
        page.bottomKey = (StreamPageKey) in.readObject();
        in.readArrayList(page.feeds);
        in.readStringHashMap(page.entities);
        page.pageTs = in.readLong();
        page.streamTs = in.readLong();
    }
}
