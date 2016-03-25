package ru.ok.android.services.processors.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.ok.android.storage.ISerializer;
import ru.ok.android.storage.serializer.stream.StreamSerialInputStream;
import ru.ok.android.storage.serializer.stream.StreamSerialOutputStream;
import ru.ok.model.stream.StreamPage;

public class StreamSerializer implements ISerializer<StreamPage> {
    public void write(StreamPage obj, OutputStream out) throws IOException {
        new StreamSerialOutputStream(out).writeObject(obj);
    }

    public StreamPage read(InputStream in) throws IOException {
        return (StreamPage) new StreamSerialInputStream(in).readObject();
    }
}
