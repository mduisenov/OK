package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class FeedVideoEntityBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, FeedVideoEntityBuilder video) throws IOException {
        out.writeInt(1);
        BaseEntityBuilderSerializer.write(out, video);
        out.writeString(video.title);
        out.writeString(video.description);
        out.writeList(video.thumbnailUrls);
        out.writeLong(video.duration);
    }

    public static FeedVideoEntityBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        FeedVideoEntityBuilder video = new FeedVideoEntityBuilder();
        BaseEntityBuilderSerializer.read(in, video);
        video.title = in.readString();
        video.description = in.readString();
        video.thumbnailUrls = in.readArrayList();
        video.duration = in.readLong();
        return video;
    }
}
