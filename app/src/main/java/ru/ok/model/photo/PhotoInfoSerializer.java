package ru.ok.model.photo;

import java.io.IOException;
import java.util.Iterator;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo.PhotoContext;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;

public final class PhotoInfoSerializer {
    public static void write(SimpleSerialOutputStream out, PhotoInfo info) throws IOException {
        boolean z = true;
        out.writeInt(1);
        out.writeLong(info.rowId);
        out.writeString(info.id);
        out.writeInt(info.sizes.size());
        Iterator i$ = info.sizes.iterator();
        while (i$.hasNext()) {
            out.writeObject((PhotoSize) i$.next());
        }
        out.writeString(info.comment);
        out.writeString(info.albumId);
        out.writeString(info.ownerId);
        out.writeInt(info.commentsCount);
        out.writeObject(info.discussionSummary);
        out.writeInt(info.marksCount);
        out.writeInt(info.markBonusCount);
        out.writeString(info.markAverage);
        out.writeInt(info.viewerMark);
        out.writeInt(info.tagCount);
        out.writeInt(info.standartWidth);
        out.writeInt(info.standartHeight);
        out.writeLong(info.createdMs);
        out.writeEnum(info.ownerType);
        if (info.photoFlags == null) {
            z = false;
        }
        out.writeBoolean(z);
        if (info.photoFlags != null) {
            out.writeInt(info.photoFlags.flags);
        }
        out.writeBoolean(info.blocked);
        out.writeString(info.mediaTopicId);
        out.writeString(info.mp4Url);
        out.writeString(info.gifUrl);
        out.writeObject(info.likeInfo);
        out.writeEnum(info.photoContext);
    }

    public static PhotoInfo read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        PhotoInfo info = new PhotoInfo();
        info.rowId = in.readLong();
        info.id = in.readString();
        int sizesCount = in.readInt();
        for (int i = 0; i < sizesCount; i++) {
            info.sizes.add((PhotoSize) in.readObject());
        }
        info.comment = in.readString();
        info.albumId = in.readString();
        info.ownerId = in.readString();
        info.commentsCount = in.readInt();
        info.discussionSummary = (DiscussionSummary) in.readObject();
        info.marksCount = in.readInt();
        info.markBonusCount = in.readInt();
        info.markAverage = in.readString();
        info.viewerMark = in.readInt();
        info.tagCount = in.readInt();
        info.standartWidth = in.readInt();
        info.standartHeight = in.readInt();
        info.createdMs = in.readLong();
        info.ownerType = (OwnerType) in.readEnum(OwnerType.class);
        if (in.readBoolean()) {
            info.photoFlags = new PhotoFlags(in.readInt());
        }
        info.blocked = in.readBoolean();
        info.mediaTopicId = in.readString();
        info.mp4Url = in.readString();
        info.gifUrl = in.readString();
        info.likeInfo = (LikeInfoContext) in.readObject();
        info.photoContext = (PhotoContext) in.readEnum(PhotoContext.class);
        return info;
    }
}
