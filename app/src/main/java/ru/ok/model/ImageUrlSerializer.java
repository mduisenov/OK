package ru.ok.model;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class ImageUrlSerializer {
    public static void write(SimpleSerialOutputStream out, ImageUrl imageUrl) throws IOException {
        out.writeInt(1);
        out.writeString(imageUrl.urlPrefix);
        out.writeInt(imageUrl.width);
        out.writeInt(imageUrl.height);
    }

    public static ImageUrl read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new ImageUrl(in.readString(), in.readInt(), in.readInt());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
