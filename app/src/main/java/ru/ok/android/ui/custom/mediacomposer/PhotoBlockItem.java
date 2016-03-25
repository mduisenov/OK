package ru.ok.android.ui.custom.mediacomposer;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collection;
import ru.ok.java.api.utils.ObjectUtils;
import ru.ok.model.mediatopics.MediaItemType;

public class PhotoBlockItem extends MediaItem {
    public static final Creator<PhotoBlockItem> CREATOR;
    private static final long serialVersionUID = 1;
    private final ArrayList<EditablePhotoItem> imageItems;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.PhotoBlockItem.1 */
    static class C06781 implements Creator<PhotoBlockItem> {
        C06781() {
        }

        public PhotoBlockItem createFromParcel(Parcel source) {
            return new PhotoBlockItem(source);
        }

        public PhotoBlockItem[] newArray(int size) {
            return new PhotoBlockItem[size];
        }
    }

    public PhotoBlockItem() {
        super(MediaItemType.PHOTO_BLOCK);
        this.imageItems = new ArrayList();
    }

    public PhotoBlockItem(Collection<? extends EditablePhotoItem> imageItems) {
        super(MediaItemType.PHOTO_BLOCK);
        this.imageItems = new ArrayList();
        this.imageItems.addAll(imageItems);
    }

    PhotoBlockItem(Parcel source) {
        super(MediaItemType.PHOTO_BLOCK, source);
        this.imageItems = new ArrayList();
        source.readList(this.imageItems, PhotoBlockItem.class.getClassLoader());
    }

    public MediaItem append(MediaItem other) {
        if (!(other instanceof PhotoBlockItem)) {
            return null;
        }
        MediaItem newBlock = new PhotoBlockItem(this.imageItems);
        newBlock.imageItems.addAll(((PhotoBlockItem) other).imageItems);
        return newBlock;
    }

    public void add(EditablePhotoItem item) {
        this.imageItems.add(item);
    }

    public void remove(int position) {
        if (position >= 0 && position < this.imageItems.size()) {
            this.imageItems.remove(position);
        }
    }

    public void replace(int position, EditablePhotoItem newItem) {
        if (position >= 0 && position < this.imageItems.size()) {
            this.imageItems.set(position, newItem);
        }
    }

    public int size() {
        return this.imageItems.size();
    }

    public boolean isEmpty() {
        return this.imageItems.isEmpty();
    }

    public EditablePhotoItem getPhotoItem(int position) {
        return (EditablePhotoItem) this.imageItems.get(position);
    }

    public String getSampleText() {
        return "";
    }

    public String toString() {
        return "PhotoBlock[size=" + this.imageItems.size() + "]";
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.imageItems);
    }

    public static boolean equal(PhotoBlockItem block1, PhotoBlockItem block2) {
        if (block1 == null) {
            if (block2 == null) {
                return true;
            }
            return false;
        } else if (block2 == null || !ObjectUtils.listsEqual(block1.imageItems, block2.imageItems, MediaItem.EQUAL)) {
            return false;
        } else {
            return true;
        }
    }

    static {
        CREATOR = new C06781();
    }
}
