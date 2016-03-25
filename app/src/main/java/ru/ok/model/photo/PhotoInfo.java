package ru.ok.model.photo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import ru.ok.model.Identifiable;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;

public final class PhotoInfo implements Parcelable, Serializable, Identifiable, HasMp4 {
    public static final Creator<PhotoInfo> CREATOR;
    private static final long serialVersionUID = 1;
    protected String albumId;
    protected boolean blocked;
    protected String comment;
    protected int commentsCount;
    protected long createdMs;
    DiscussionSummary discussionSummary;
    protected String gifUrl;
    protected String id;
    protected LikeInfoContext likeInfo;
    protected String markAverage;
    protected int markBonusCount;
    protected int marksCount;
    protected String mediaTopicId;
    protected String mp4Url;
    protected String ownerId;
    OwnerType ownerType;
    protected PhotoContext photoContext;
    PhotoFlags photoFlags;
    protected Uri previewUri;
    protected long rowId;
    TreeSet<PhotoSize> sizes;
    protected int standartHeight;
    protected int standartWidth;
    protected int tagCount;
    protected int viewerMark;

    /* renamed from: ru.ok.model.photo.PhotoInfo.1 */
    static class C15601 implements Creator<PhotoInfo> {
        C15601() {
        }

        public PhotoInfo createFromParcel(Parcel source) {
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.readFromParcel(source);
            return photoInfo;
        }

        public PhotoInfo[] newArray(int size) {
            return new PhotoInfo[size];
        }
    }

    public enum PhotoContext {
        NORMAL,
        MEDIATOPIC
    }

    public PhotoInfo() {
        this.sizes = new TreeSet();
        this.photoContext = PhotoContext.NORMAL;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TreeSet<PhotoSize> getSizes() {
        return this.sizes;
    }

    public void addSize(PhotoSize size) {
        if (size != null && !TextUtils.isEmpty(size.getUrl())) {
            this.sizes.add(size);
        }
    }

    public void addSizes(List<PhotoSize> sizes) {
        if (sizes != null) {
            for (PhotoSize size : sizes) {
                addSize(size);
            }
        }
    }

    public final PhotoSize getLargestSize() {
        if (this.sizes.isEmpty()) {
            return null;
        }
        return (PhotoSize) this.sizes.first();
    }

    private PhotoSize getSmallestSize() {
        if (this.sizes.isEmpty()) {
            return null;
        }
        return (PhotoSize) this.sizes.last();
    }

    public boolean hasMp4() {
        return !TextUtils.isEmpty(this.mp4Url);
    }

    public void setMp4Url(String url) {
        this.mp4Url = url;
    }

    public String getMp4Url() {
        return this.mp4Url;
    }

    public boolean hasGif() {
        return !TextUtils.isEmpty(this.gifUrl);
    }

    public void setGifUrl(String url) {
        this.gifUrl = url;
    }

    public String getGifUrl() {
        return this.gifUrl;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ru.ok.model.photo.PhotoSize getClosestSize(int r5, int r6) {
        /*
        r4 = this;
        r0 = 0;
        r3 = r4.sizes;
        r1 = r3.iterator();
    L_0x0007:
        r3 = r1.hasNext();
        if (r3 == 0) goto L_0x0021;
    L_0x000d:
        r2 = r1.next();
        r2 = (ru.ok.model.photo.PhotoSize) r2;
        r3 = r2.getWidth();
        if (r3 < r5) goto L_0x0021;
    L_0x0019:
        r3 = r2.getHeight();
        if (r3 < r6) goto L_0x0021;
    L_0x001f:
        r0 = r2;
        goto L_0x0007;
    L_0x0021:
        if (r0 != 0) goto L_0x0027;
    L_0x0023:
        r0 = r4.getLargestSize();
    L_0x0027:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.model.photo.PhotoInfo.getClosestSize(int, int):ru.ok.model.photo.PhotoSize");
    }

    public final PhotoSize getSizeFloor(int width) {
        PhotoSize closest = null;
        Iterator<PhotoSize> it = this.sizes.descendingIterator();
        while (it.hasNext()) {
            PhotoSize size = (PhotoSize) it.next();
            if (size.getWidth() > width) {
                break;
            }
            closest = size;
        }
        if (closest == null) {
            return getSmallestSize();
        }
        return closest;
    }

    @Nullable
    public final String getSizeFloorUrl(int width) {
        PhotoSize photoSize = getSizeFloor(width);
        return photoSize != null ? photoSize.getUrl() : null;
    }

    @Nullable
    public final String getClosestSizeUrl(int width, int height) {
        PhotoSize photoSize = getClosestSize(width, height);
        return photoSize != null ? photoSize.getUrl() : null;
    }

    @Nullable
    public final Uri getClosestSizeUri(int width, int height) {
        PhotoSize photoSize = getClosestSize(width, height);
        return photoSize != null ? Uri.parse(photoSize.getUrl()) : null;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getCommentsCount() {
        return this.commentsCount;
    }

    public int getAnyCommentsCount() {
        if (this.commentsCount > 0) {
            return this.commentsCount;
        }
        return this.discussionSummary != null ? this.discussionSummary.commentsCount : 0;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public DiscussionSummary getDiscussionSummary() {
        return this.discussionSummary;
    }

    public void setDiscussionSummary(DiscussionSummary discussionSummary) {
        this.discussionSummary = discussionSummary;
    }

    public int getMarksCount() {
        return this.marksCount;
    }

    public void setMarksCount(int marksCount) {
        this.marksCount = marksCount;
    }

    public void setMarkBonusCount(int markBonusCount) {
        this.markBonusCount = markBonusCount;
    }

    public void setMarkAverage(String markAverage) {
        this.markAverage = markAverage;
    }

    public int getViewerMark() {
        return this.viewerMark;
    }

    public void setViewerMark(int viewerMark) {
        this.viewerMark = viewerMark;
    }

    public int getTagCount() {
        return this.tagCount;
    }

    public void setTagCount(int tagCount) {
        this.tagCount = tagCount;
    }

    public int getStandartWidth() {
        return this.standartWidth;
    }

    public void setStandartWidth(int standartWidth) {
        this.standartWidth = standartWidth;
    }

    public int getStandartHeight() {
        return this.standartHeight;
    }

    public void setStandartHeight(int standartHeight) {
        this.standartHeight = standartHeight;
    }

    public long getCreatedMs() {
        return this.createdMs;
    }

    public void setCreatedMs(long createdMs) {
        this.createdMs = createdMs;
    }

    public boolean isModifyAllowed() {
        return this.photoFlags != null && this.photoFlags.hasFlags(32);
    }

    public boolean isDeleteAllowed() {
        return this.photoFlags != null && this.photoFlags.hasFlags(4);
    }

    public boolean isMarkAllowed() {
        return this.photoFlags != null && this.photoFlags.hasFlags(64);
    }

    public boolean isMarkAsSpamAllowed() {
        return this.photoFlags != null && this.photoFlags.hasFlags(8);
    }

    public void setPreviewUri(Uri previewUri) {
        this.previewUri = previewUri;
    }

    public Uri getPreviewUri() {
        return this.previewUri;
    }

    public void setPhotoFlags(PhotoFlags photoFlags) {
        this.photoFlags = photoFlags;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void setMediaTopicId(String mediaTopicId) {
        this.mediaTopicId = mediaTopicId;
    }

    public String getMediaTopicId() {
        return this.mediaTopicId;
    }

    public void setLikeInfo(LikeInfoContext likeInfo) {
        this.likeInfo = likeInfo;
    }

    public LikeInfoContext getLikeInfo() {
        return this.likeInfo;
    }

    public PhotoContext getPhotoContext() {
        return this.photoContext;
    }

    public void setPhotoContext(PhotoContext photoContext) {
        this.photoContext = photoContext;
    }

    public void setOwnerType(OwnerType ownerType) {
        this.ownerType = ownerType;
    }

    public OwnerType getOwnerType() {
        return this.ownerType;
    }

    public float calculateAspectRatio() {
        return this.standartHeight == 0 ? (float) this.standartWidth : ((float) this.standartWidth) / ((float) this.standartHeight);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return TextUtils.equals(this.id, ((PhotoInfo) o).id);
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public String toString() {
        return "PhotoInfo[id=" + this.id + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        byte b = (byte) 1;
        dest.writeLong(this.rowId);
        dest.writeString(this.id);
        dest.writeString(this.comment);
        dest.writeString(this.albumId);
        dest.writeString(this.ownerId);
        dest.writeInt(this.commentsCount);
        dest.writeInt(this.marksCount);
        dest.writeInt(this.markBonusCount);
        dest.writeString(this.markAverage);
        dest.writeInt(this.viewerMark);
        dest.writeInt(this.tagCount);
        dest.writeInt(this.standartWidth);
        dest.writeInt(this.standartHeight);
        dest.writeLong(this.createdMs);
        dest.writeParcelable(this.photoFlags, flags);
        dest.writeByte((byte) (this.blocked ? 1 : 0));
        dest.writeParcelable(this.likeInfo, flags);
        dest.writeParcelableArray((Parcelable[]) this.sizes.toArray(new PhotoSize[0]), flags);
        if (this.photoContext == null) {
            b = (byte) 0;
        }
        dest.writeByte(b);
        if (this.photoContext != null) {
            dest.writeInt(this.photoContext.ordinal());
        }
        dest.writeInt(this.ownerType != null ? this.ownerType.ordinal() : -1);
        dest.writeParcelable(this.discussionSummary, flags);
        dest.writeString(this.mediaTopicId);
        dest.writeString(this.mp4Url);
        dest.writeString(this.gifUrl);
        dest.writeParcelable(this.previewUri, 0);
    }

    public final void readFromParcel(Parcel src) {
        boolean z;
        boolean hasContext;
        ClassLoader cl = PhotoInfo.class.getClassLoader();
        this.rowId = src.readLong();
        this.id = src.readString();
        this.comment = src.readString();
        this.albumId = src.readString();
        this.ownerId = src.readString();
        this.commentsCount = src.readInt();
        this.marksCount = src.readInt();
        this.markBonusCount = src.readInt();
        this.markAverage = src.readString();
        this.viewerMark = src.readInt();
        this.tagCount = src.readInt();
        this.standartWidth = src.readInt();
        this.standartHeight = src.readInt();
        this.createdMs = src.readLong();
        this.photoFlags = (PhotoFlags) src.readParcelable(cl);
        if (src.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.blocked = z;
        this.likeInfo = (LikeInfoContext) src.readParcelable(cl);
        for (Parcelable parcelable : src.readParcelableArray(cl)) {
            this.sizes.add((PhotoSize) parcelable);
        }
        if (src.readByte() > null) {
            hasContext = true;
        } else {
            hasContext = false;
        }
        this.photoContext = hasContext ? PhotoContext.values()[src.readInt()] : null;
        int ownerTypeIndex = src.readInt();
        if (ownerTypeIndex >= 0) {
            this.ownerType = OwnerType.values()[ownerTypeIndex];
        }
        this.discussionSummary = (DiscussionSummary) src.readParcelable(DiscussionSummary.class.getClassLoader());
        this.mediaTopicId = src.readString();
        this.mp4Url = src.readString();
        this.gifUrl = src.readString();
        this.previewUri = (Uri) src.readParcelable(getClass().getClassLoader());
    }

    static {
        CREATOR = new C15601();
    }
}
