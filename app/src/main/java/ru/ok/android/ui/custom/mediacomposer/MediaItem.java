package ru.ok.android.ui.custom.mediacomposer;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import ru.ok.java.api.utils.ObjectUtils.ObjectsEqual;
import ru.ok.model.mediatopics.MediaItemType;

public abstract class MediaItem implements Parcelable, Serializable {
    public static final ObjectsEqual<MediaItem> EQUAL;
    private static final long serialVersionUID = 1;
    private transient MediaItemContentListener mediaItemContentListener;
    public final MediaItemType type;
    transient int viewId;

    interface MediaItemContentListener {
        void onMediaItemContentChanged(MediaItem mediaItem);
    }

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.MediaItem.1 */
    static class C06741 implements ObjectsEqual<MediaItem> {
        C06741() {
        }

        public boolean equal(MediaItem o1, MediaItem o2) {
            if (o1 == null) {
                if (o2 == null) {
                    return true;
                }
                return false;
            } else if (o2 == null || o1.type != o2.type || o1.getClass() != o2.getClass()) {
                return false;
            } else {
                if (o1 instanceof TextItem) {
                    return TextItem.equal((TextItem) o1, (TextItem) o2);
                }
                if (o1 instanceof EditablePhotoItem) {
                    return EditablePhotoItem.equal((EditablePhotoItem) o1, (EditablePhotoItem) o2);
                }
                if (o1 instanceof PollItem) {
                    return PollItem.equal((PollItem) o1, (PollItem) o2);
                }
                if (o1 instanceof MusicItem) {
                    return MusicItem.equal((MusicItem) o1, (MusicItem) o2);
                }
                if (o1 instanceof PhotoBlockItem) {
                    return PhotoBlockItem.equal((PhotoBlockItem) o1, (PhotoBlockItem) o2);
                }
                return o1.equals(o2);
            }
        }
    }

    public abstract String getSampleText();

    MediaItem(MediaItemType type) {
        this.type = type;
        this.viewId = 0;
    }

    MediaItem(MediaItemType type, Parcel source) {
        this.type = type;
        this.viewId = source.readInt();
    }

    void setMediaItemContentListener(MediaItemContentListener listener) {
        this.mediaItemContentListener = listener;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public int getViewId() {
        return this.viewId;
    }

    public MediaItem append(MediaItem other) {
        return null;
    }

    public boolean isEmpty() {
        return true;
    }

    public static TextItem emptyText() {
        return new TextItem();
    }

    public static TextItem text(String text) {
        TextItem textItem = new TextItem();
        textItem.setText(text);
        return textItem;
    }

    public String toString() {
        return this.type.toString();
    }

    public void notifyContentChanged() {
        if (this.mediaItemContentListener != null) {
            this.mediaItemContentListener.onMediaItemContentChanged(this);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.viewId);
    }

    static {
        EQUAL = new C06741();
    }
}
