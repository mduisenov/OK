package ru.ok.android.ui.custom.mediacomposer;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.model.mediatopics.MediaItemType;

public class EditablePhotoItem extends MediaItem {
    public static final Creator<EditablePhotoItem> CREATOR;
    private static final long serialVersionUID = 1;
    private ImageEditInfo imageEditInfo;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.EditablePhotoItem.1 */
    static class C06691 implements Creator<EditablePhotoItem> {
        C06691() {
        }

        public EditablePhotoItem createFromParcel(Parcel source) {
            return new EditablePhotoItem(null);
        }

        public EditablePhotoItem[] newArray(int size) {
            return new EditablePhotoItem[size];
        }
    }

    public EditablePhotoItem(@NonNull ImageEditInfo imageEditInfo) {
        super(MediaItemType.PHOTO);
        this.imageEditInfo = imageEditInfo;
    }

    private EditablePhotoItem(Parcel source) {
        super(MediaItemType.PHOTO, source);
        this.imageEditInfo = (ImageEditInfo) source.readParcelable(EditablePhotoItem.class.getClassLoader());
    }

    @NonNull
    public ImageEditInfo getImageEditInfo() {
        return this.imageEditInfo;
    }

    public boolean isEmpty() {
        return this.imageEditInfo == null;
    }

    public Uri getImageUri() {
        Uri uri = this.imageEditInfo.getOriginalUri();
        if (uri == null || this.imageEditInfo.wasEdited()) {
            return this.imageEditInfo.getUri();
        }
        return uri;
    }

    public int getOrientation() {
        return this.imageEditInfo.getRotation();
    }

    public String getSampleText() {
        return "";
    }

    public String toString() {
        return "EditablePhotoItem[" + this.imageEditInfo + "]";
    }

    public static boolean equal(EditablePhotoItem a1, EditablePhotoItem a2) {
        if (a1 == null) {
            if (a2 == null) {
                return true;
            }
            return false;
        } else if (a2 == null) {
            return false;
        } else {
            if (a1.getOrientation() != a2.getOrientation()) {
                return false;
            }
            Uri uri1 = a1.getImageUri();
            Uri uri2 = a2.getImageUri();
            if (uri1 == null && uri2 == null) {
                return true;
            }
            if (uri1 == null || !uri1.equals(uri2)) {
                return false;
            }
            return true;
        }
    }

    public int hashCode() {
        return this.imageEditInfo != null ? this.imageEditInfo.hashCode() : 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.imageEditInfo, flags);
    }

    static {
        CREATOR = new C06691();
    }
}
