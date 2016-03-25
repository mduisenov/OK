package ru.ok.model.stream;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.stream.banner.StatPixelHolderImplSerializer;
import ru.ok.model.stream.message.FeedMessage;

public final class FeedSerializer {
    public static void write(SimpleSerialOutputStream out, Feed feed) throws IOException {
        out.writeInt(3);
        out.writeLong(feed.id);
        out.writeString(feed.uuid);
        out.writeString(feed.digest);
        out.writeInt(feed.pattern);
        out.writeLong(feed.date);
        out.writeObject(feed.message);
        out.writeObject(feed.title);
        out.writeObject(feed.likeInfo);
        out.writeObject(feed.discussionSummary);
        out.writeString(feed.spamId);
        out.writeString(feed.deleteId);
        FeedStringRefsSerializer.write(out, feed.entityRefs);
        out.writeInt(feed.dataFlags);
        out.writeInt(feed.feedType);
        out.writeBoolean(feed.pinned);
        out.writeString(feed.feedStatInfo);
        out.writeObject(feed.pageKey);
        StatPixelHolderImplSerializer.write(out, feed.pixels);
        out.writeInt(feed.actionType);
    }

    public static Feed read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version < 1 || version > 3) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        Feed feed = new Feed();
        feed.id = in.readLong();
        feed.uuid = in.readString();
        feed.digest = in.readString();
        feed.pattern = in.readInt();
        feed.date = in.readLong();
        feed.message = (FeedMessage) in.readObject();
        feed.title = (FeedMessage) in.readObject();
        feed.likeInfo = (LikeInfoContext) in.readObject();
        feed.discussionSummary = (DiscussionSummary) in.readObject();
        feed.spamId = in.readString();
        feed.deleteId = in.readString();
        FeedStringRefsSerializer.read(in, feed.entityRefs);
        feed.dataFlags = in.readInt();
        feed.feedType = in.readInt();
        feed.pinned = in.readBoolean();
        feed.feedStatInfo = in.readString();
        feed.pageKey = (StreamPageKey) in.readObject();
        if (version == 1) {
            StatPixelHolderImplSerializer.readPixelsArray(in, feed.pixels);
        } else {
            StatPixelHolderImplSerializer.read(in, feed.pixels);
        }
        if (version >= 3) {
            feed.actionType = in.readInt();
        }
        return feed;
    }
}
