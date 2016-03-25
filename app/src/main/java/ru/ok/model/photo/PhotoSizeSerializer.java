package ru.ok.model.photo;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class PhotoSizeSerializer {
    public static void write(SimpleSerialOutputStream out, PhotoSize photoSize) throws IOException {
        out.writeInt(1);
        out.writeString(photoSize.url);
        out.writeInt(photoSize.width);
        out.writeInt(photoSize.height);
        out.writeString(photoSize.jsonKey);
    }

    public static PhotoSize read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        PhotoSize photoSize = new PhotoSize();
        photoSize.url = in.readString();
        photoSize.width = in.readInt();
        photoSize.height = in.readInt();
        photoSize.jsonKey = in.readString();
        return photoSize;
    }
}
