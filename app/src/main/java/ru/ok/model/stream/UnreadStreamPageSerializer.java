package ru.ok.model.stream;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class UnreadStreamPageSerializer {
    public static void write(SimpleSerialOutputStream out, UnreadStreamPage page) throws IOException {
        out.writeInt(1);
        StreamPageSerializer.write(out, page);
        out.writeInt(page.totalUnreadFeedsCount);
    }

    public static UnreadStreamPage read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        UnreadStreamPage page = new UnreadStreamPage();
        StreamPageSerializer.read(in, page);
        page.totalUnreadFeedsCount = in.readInt();
        return page;
    }
}
