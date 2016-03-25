package ru.ok.model.mediatopics;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class MediaItemAppBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, MediaItemAppBuilder item) throws IOException {
        out.writeInt(1);
        out.writeString(item.appRef);
        out.writeString(item.actionMark);
        out.writeString(item.actionText);
        out.writeString(item.image);
        out.writeString(item.imageMark);
        out.writeString(item.imageTitle);
        out.writeString(item.text);
    }

    public static MediaItemAppBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        MediaItemAppBuilder item = new MediaItemAppBuilder();
        item.appRef = in.readString();
        item.actionMark = in.readString();
        item.actionText = in.readString();
        item.image = in.readString();
        item.imageMark = in.readString();
        item.imageTitle = in.readString();
        item.text = in.readString();
        return item;
    }
}
