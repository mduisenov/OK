package ru.ok.model.messages;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.model.ContentUrl;
import ru.ok.model.Identifiable;
import ru.ok.model.ImageUrl;
import ru.ok.model.photo.HasMp4;
import ru.ok.model.photo.PhotoSize;

public class Attachment implements Parcelable, Serializable, Identifiable, HasMp4 {
    public static final Creator<Attachment> CREATOR;
    private static final long serialVersionUID = -8167914520290090740L;
    public long _id;
    public boolean attachBeReload;
    public boolean attachLoadWithError;
    public String audioProfile;
    public long duration;
    public String gifUrl;
    public String id;
    public String linkDescription;
    public String linkTitle;
    public String linkUrl;
    public List<ImageUrl> linkUrlImages;
    public String localId;
    public long mediaId;
    public List<ContentUrl> mediaUrls;
    public String mp4Url;
    public String name;
    public String path;
    private Uri previewUri;
    public String remoteToken;
    public int rotation;
    public TreeSet<PhotoSize> sizes;
    public int standard_height;
    public int standard_width;
    private String status;
    public String thumbnailUrl;
    public long tokenCreationDate;
    public transient String type;
    public AttachmentType typeValue;
    public int uploadErrorCode;
    private transient Uri uri;

    /* renamed from: ru.ok.model.messages.Attachment.1 */
    static class C15401 implements Creator<Attachment> {
        C15401() {
        }

        public Attachment createFromParcel(Parcel source) {
            return new Attachment(source);
        }

        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    }

    public enum AttachmentType {
        MOVIE("MOVIE"),
        VIDEO("VIDEO"),
        AUDIO_RECORDING("AUDIO_RECORDING"),
        PHOTO("PHOTO"),
        TOPIC("TOPIC"),
        UNKNOWN("UNKNOWN");
        
        private final String strValue;

        private AttachmentType(String strValue) {
            this.strValue = strValue;
        }

        public String getStrValue() {
            return this.strValue;
        }

        public boolean isVideo() {
            return this == VIDEO || this == MOVIE;
        }

        public static AttachmentType getTypeFromString(String strValue) {
            if (!TextUtils.isEmpty(strValue)) {
                Object obj = -1;
                switch (strValue.hashCode()) {
                    case -688619384:
                        if (strValue.equals("AUDIO_RECORDING")) {
                            obj = 2;
                            break;
                        }
                        break;
                    case 73549584:
                        if (strValue.equals("MOVIE")) {
                            obj = null;
                            break;
                        }
                        break;
                    case 76105234:
                        if (strValue.equals("PHOTO")) {
                            obj = 3;
                            break;
                        }
                        break;
                    case 80008463:
                        if (strValue.equals("TOPIC")) {
                            obj = 4;
                            break;
                        }
                        break;
                    case 81665115:
                        if (strValue.equals("VIDEO")) {
                            obj = 1;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case RECEIVED_VALUE:
                        return MOVIE;
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        return VIDEO;
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        return AUDIO_RECORDING;
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        return PHOTO;
                    case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                        return TOPIC;
                }
            }
            return UNKNOWN;
        }
    }

    public boolean isSupport() {
        return this.typeValue != AttachmentType.UNKNOWN;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Attachment(long _id, String id, AttachmentType type, int standard_width, int standard_height, String status, int rotation) {
        this(_id, id, type, standard_width, standard_height, status, 0, null, 0, rotation, null, null, null, null, null, null);
    }

    public Attachment(long _id, String id, AttachmentType type, int standard_width, int standard_height, String status, long duration, String audioProfile, long mediaId, int rotation, String thumbnailUrl, String name, String linkTitle, String linkUrl, String linkDescription, List<ImageUrl> linkUrlImages) {
        this.rotation = 0;
        this.attachBeReload = false;
        this.attachLoadWithError = false;
        this.sizes = new TreeSet();
        this.mediaUrls = new ArrayList();
        this.id = id;
        this._id = _id;
        this.typeValue = type;
        this.standard_width = standard_width;
        this.standard_height = standard_height;
        this.status = status;
        this.duration = duration;
        this.audioProfile = audioProfile;
        this.mediaId = mediaId;
        this.rotation = rotation;
        this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        this.linkTitle = linkTitle;
        this.linkUrl = linkUrl;
        this.linkDescription = linkDescription;
        this.linkUrlImages = linkUrlImages;
    }

    public Attachment(Uri uri, AttachmentType type, int rotation) {
        this(uri, type);
        this.rotation = rotation;
    }

    public Attachment(Uri uri, AttachmentType type) {
        this.rotation = 0;
        this.attachBeReload = false;
        this.attachLoadWithError = false;
        this.sizes = new TreeSet();
        this.mediaUrls = new ArrayList();
        this.typeValue = type;
        this.path = uri.toString();
    }

    public Attachment(String path, AttachmentType type) {
        this.rotation = 0;
        this.attachBeReload = false;
        this.attachLoadWithError = false;
        this.sizes = new TreeSet();
        this.mediaUrls = new ArrayList();
        this.path = path;
        this.typeValue = type;
    }

    public int getRotation() {
        return this.rotation;
    }

    public void fillMedia(Attachment attachment) {
        this.sizes = attachment.sizes;
        this.mediaUrls = attachment.mediaUrls;
    }

    public boolean isDeleted() {
        return "DELETED".equals(this.status);
    }

    public PhotoSize getLargestSize() {
        if (this.sizes.isEmpty()) {
            return null;
        }
        return (PhotoSize) this.sizes.first();
    }

    public Attachment(Parcel parcel) {
        boolean z;
        boolean z2 = true;
        this.rotation = 0;
        this.attachBeReload = false;
        this.attachLoadWithError = false;
        this.sizes = new TreeSet();
        this.mediaUrls = new ArrayList();
        this.id = parcel.readString();
        this.localId = parcel.readString();
        this.remoteToken = parcel.readString();
        this.tokenCreationDate = parcel.readLong();
        this.uploadErrorCode = parcel.readInt();
        this.type = parcel.readString();
        this.standard_width = parcel.readInt();
        this.standard_height = parcel.readInt();
        for (Parcelable parcelable : parcel.readParcelableArray(PhotoSize.class.getClassLoader())) {
            this.sizes.add((PhotoSize) parcelable);
        }
        this.status = parcel.readString();
        this._id = parcel.readLong();
        if (parcel.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.attachBeReload = z;
        if (parcel.readInt() == 0) {
            z2 = false;
        }
        this.attachLoadWithError = z2;
        this.path = parcel.readString();
        this.rotation = parcel.readInt();
        this.duration = parcel.readLong();
        this.audioProfile = parcel.readString();
        this.mediaId = parcel.readLong();
        for (Parcelable parcelable2 : parcel.readParcelableArray(ContentUrl.class.getClassLoader())) {
            this.mediaUrls.add((ContentUrl) parcelable2);
        }
        this.typeValue = AttachmentType.values()[parcel.readInt()];
        this.thumbnailUrl = parcel.readString();
        this.name = parcel.readString();
        this.mp4Url = parcel.readString();
        this.linkTitle = parcel.readString();
        this.linkUrl = parcel.readString();
        this.linkDescription = parcel.readString();
        Parcelable[] urlImagesArray = parcel.readParcelableArray(ImageUrl.class.getClassLoader());
        if (urlImagesArray != null) {
            for (Parcelable parcelable22 : urlImagesArray) {
                this.linkUrlImages.add((ImageUrl) parcelable22);
            }
        }
        this.gifUrl = parcel.readString();
        this.previewUri = (Uri) parcel.readParcelable(getClass().getClassLoader());
    }

    public Uri getUri() {
        if (this.uri == null && !TextUtils.isEmpty(this.path)) {
            this.uri = Uri.parse(this.path);
        }
        return this.uri;
    }

    public boolean hasMp4() {
        return !TextUtils.isEmpty(this.mp4Url);
    }

    public boolean hasGif() {
        return !TextUtils.isEmpty(this.gifUrl);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        int i = 1;
        parcel.writeString(this.id);
        parcel.writeString(this.localId);
        parcel.writeString(this.remoteToken);
        parcel.writeLong(this.tokenCreationDate);
        parcel.writeInt(this.uploadErrorCode);
        parcel.writeString(this.type);
        parcel.writeInt(this.standard_width);
        parcel.writeInt(this.standard_height);
        parcel.writeParcelableArray((Parcelable[]) this.sizes.toArray(new PhotoSize[this.sizes.size()]), flags);
        parcel.writeString(this.status);
        parcel.writeLong(this._id);
        parcel.writeInt(this.attachBeReload ? 1 : 0);
        if (!this.attachLoadWithError) {
            i = 0;
        }
        parcel.writeInt(i);
        parcel.writeString(this.path);
        parcel.writeInt(this.rotation);
        parcel.writeLong(this.duration);
        parcel.writeString(this.audioProfile);
        parcel.writeLong(this.mediaId);
        parcel.writeParcelableArray((Parcelable[]) this.mediaUrls.toArray(new ContentUrl[this.sizes.size()]), flags);
        parcel.writeInt(this.typeValue.ordinal());
        parcel.writeString(this.thumbnailUrl);
        parcel.writeString(this.name);
        parcel.writeString(this.mp4Url);
        parcel.writeString(this.linkTitle);
        parcel.writeString(this.linkUrl);
        parcel.writeString(this.linkDescription);
        parcel.writeParcelableArray(this.linkUrlImages != null ? (ImageUrl[]) this.linkUrlImages.toArray(new ImageUrl[this.linkUrlImages.size()]) : null, flags);
        parcel.writeString(this.gifUrl);
        parcel.writeParcelable(this.previewUri, 0);
    }

    static {
        CREATOR = new C15401();
    }

    public String toString() {
        String typeString = this.type == null ? "null" : this.type.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("Attachment[type=").append(typeString).append(" id=").append(this.id).append(" _id=").append(this._id).append(" localId=").append(this.localId).append(" path=").append(this.path).append(" status=").append(this.status).append(" attachLoadWithError=").append(this.attachLoadWithError).append(" attachBeReload=").append(this.attachBeReload).append(" remoteToken=").append(this.remoteToken).append(" tokenCreationDate=").append(this.tokenCreationDate).append(" mediaId=").append(this.mediaId);
        if (this.typeValue == AttachmentType.PHOTO) {
            sb.append(" standard_width=").append(this.standard_width).append(" standard_height=").append(this.standard_height).append(" rotation=").append(this.rotation);
        }
        if (this.typeValue == AttachmentType.AUDIO_RECORDING) {
            sb.append(" audioProfile=").append(this.audioProfile);
        }
        if (this.typeValue != AttachmentType.PHOTO) {
            sb.append(" duration=").append(this.duration);
        }
        sb.append(" name=").append(this.name).append(" mp4Url=").append(this.mp4Url).append(" gifUrl=").append(this.gifUrl).append("]");
        return sb.toString();
    }

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        inputStream.defaultReadObject();
        if (!TextUtils.isEmpty(this.type)) {
            this.typeValue = AttachmentType.getTypeFromString(this.type);
        }
    }

    public String getId() {
        return this.id;
    }

    public void setPreviewUri(Uri previewUri) {
        this.previewUri = previewUri;
    }

    public Uri getPreviewUri() {
        return this.previewUri;
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Attachment a = (Attachment) o;
        if (a._id != 0) {
            if (a._id != this._id) {
                return false;
            }
            return true;
        } else if (TextUtils.isEmpty(a.id) || !TextUtils.equals(a.id, this.id)) {
            return false;
        } else {
            return true;
        }
    }
}
