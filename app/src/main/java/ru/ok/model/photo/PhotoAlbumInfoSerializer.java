package ru.ok.model.photo;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.stream.LikeInfoContext;

public final class PhotoAlbumInfoSerializer {
    public static void write(SimpleSerialOutputStream out, PhotoAlbumInfo info) throws IOException {
        out.writeInt(1);
        out.writeString(info.id);
        out.writeString(info.title);
        out.writeString(info.description);
        out.writeString(info.created);
        out.writeEnum(info.type);
        out.writeEnumList(info.types);
        out.writeBoolean(info.typeChangeEnabled);
        out.writeInt(info.photoCount);
        out.writeInt(info.commentsCount);
        out.writeInt(info.likesCount);
        out.writeBoolean(info.viewerLiked);
        out.writeObject(info.mainPhotoInfo);
        out.writeBoolean(info.canLike);
        out.writeBoolean(info.canModify);
        out.writeBoolean(info.canDelete);
        out.writeBoolean(info.canAddPhoto);
        out.writeEnum(info.ownerType);
        out.writeString(info.userId);
        out.writeString(info.groupId);
        out.writeBoolean(info.virtual);
        out.writeObject(info.likeInfo);
    }

    public static PhotoAlbumInfo read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version != 1) {
            throw new SimpleSerialException("Unsupported serial version: " + version);
        }
        PhotoAlbumInfo info = new PhotoAlbumInfo();
        info.id = in.readString();
        info.title = in.readString();
        info.description = in.readString();
        info.created = in.readString();
        info.type = (AccessType) in.readEnum(AccessType.class);
        info.types = in.readEnumArrayList(AccessType.class);
        info.typeChangeEnabled = in.readBoolean();
        info.photoCount = in.readInt();
        info.commentsCount = in.readInt();
        info.likesCount = in.readInt();
        info.viewerLiked = in.readBoolean();
        info.mainPhotoInfo = (PhotoInfo) in.readObject();
        info.canLike = in.readBoolean();
        info.canModify = in.readBoolean();
        info.canDelete = in.readBoolean();
        info.canAddPhoto = in.readBoolean();
        info.ownerType = (OwnerType) in.readEnum(OwnerType.class);
        info.userId = in.readString();
        info.groupId = in.readString();
        info.virtual = in.readBoolean();
        info.likeInfo = (LikeInfoContext) in.readObject();
        return info;
    }
}
