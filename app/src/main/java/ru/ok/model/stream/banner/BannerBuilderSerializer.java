package ru.ok.model.stream.banner;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;

public final class BannerBuilderSerializer {
    public static void write(SimpleSerialOutputStream out, BannerBuilder banner) throws IOException {
        out.writeInt(4);
        out.writeLong(banner.db_id);
        out.writeString(banner.id);
        out.writeInt(banner.template);
        out.writeString(banner.header);
        out.writeString(banner.text);
        out.writeInt(banner.actionType);
        out.writeInt(banner.iconType);
        out.writeString(banner.iconUrl);
        out.writeString(banner.iconUrlHd);
        out.writeList(banner.images);
        out.writeString(banner.clickUrl);
        out.writeInt(banner.color);
        out.writeString(banner.disclaimer);
        out.writeString(banner.info);
        out.writeObject(banner.videoData);
        out.writeFloat(banner.rating);
        out.writeInt(banner.votes);
        out.writeInt(banner.users);
        out.writeString(banner.ageRestriction);
        out.writeString(banner.topicId);
        out.writeString(banner.deepLink);
    }

    public static BannerBuilder read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version < 1 || version > 4) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        BannerBuilder banner = new BannerBuilder();
        banner.db_id = in.readLong();
        banner.id = in.readString();
        banner.template = in.readInt();
        banner.header = in.readString();
        banner.text = in.readString();
        banner.actionType = in.readInt();
        banner.iconType = in.readInt();
        banner.iconUrl = in.readString();
        banner.iconUrlHd = in.readString();
        banner.images = in.readArrayList();
        banner.clickUrl = in.readString();
        banner.color = in.readInt();
        banner.disclaimer = in.readString();
        banner.info = in.readString();
        banner.videoData = (VideoData) in.readObject();
        banner.rating = in.readFloat();
        banner.votes = in.readInt();
        banner.users = in.readInt();
        if (version >= 2) {
            banner.ageRestriction = in.readString();
        }
        if (version >= 3) {
            banner.topicId = in.readString();
        }
        if (version >= 4) {
            banner.deepLink = in.readString();
        }
        return banner;
    }
}
