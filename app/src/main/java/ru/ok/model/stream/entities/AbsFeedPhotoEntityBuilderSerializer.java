package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.photo.PhotoInfo;

final class AbsFeedPhotoEntityBuilderSerializer {
    static void write(SimpleSerialOutputStream out, AbsFeedPhotoEntityBuilder entity) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, entity);
        out.writeObject(entity.photoInfo);
    }

    static void read(SimpleSerialInputStream in, AbsFeedPhotoEntityBuilder entity) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        BaseEntityBuilderSerializer.read(in, entity);
        entity.photoInfo = (PhotoInfo) in.readObject();
    }
}
