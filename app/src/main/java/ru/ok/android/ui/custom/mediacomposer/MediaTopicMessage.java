package ru.ok.android.ui.custom.mediacomposer;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.java.api.utils.ObjectUtils;
import ru.ok.model.mediatopics.MediaItemType;
import ru.ok.model.places.Place;
import ru.ok.model.wmf.Track;

public final class MediaTopicMessage implements Parcelable, Serializable {
    public static final Creator<MediaTopicMessage> CREATOR;
    private static final long serialVersionUID = 1;
    private final ArrayList<MediaItem> items;
    private Place place;
    private ArrayList<String> withFriendsUids;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage.1 */
    static class C06751 implements Creator<MediaTopicMessage> {
        C06751() {
        }

        public MediaTopicMessage createFromParcel(Parcel source) {
            return new MediaTopicMessage(source);
        }

        public MediaTopicMessage[] newArray(int size) {
            return new MediaTopicMessage[size];
        }
    }

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage.2 */
    static /* synthetic */ class C06762 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$mediatopics$MediaItemType;

        static {
            $SwitchMap$ru$ok$model$mediatopics$MediaItemType = new int[MediaItemType.values().length];
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.TEXT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.PHOTO.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.PHOTO_BLOCK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.MUSIC.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.POLL.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.LINK.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public static class Stats {
        public int friendsCount;
        public int linkCount;
        public int photoCount;
        public int pollCount;
        public int textVolume;
        public int trackCount;
    }

    public MediaTopicMessage() {
        this.items = new ArrayList();
        this.withFriendsUids = new ArrayList();
    }

    public int getItemsCount() {
        return this.items.size();
    }

    public MediaItem getItem(int position) {
        return (MediaItem) this.items.get(position);
    }

    public void add(MediaItem item) {
        this.items.add(item);
    }

    public void insert(int position, MediaItem item) {
        this.items.add(position, item);
    }

    public void set(int position, MediaItem item) {
        this.items.set(position, item);
    }

    public void removeItem(int position) {
        this.items.remove(position);
    }

    public boolean hasPhotos() {
        int itemsCount = getItemsCount();
        for (int i = 0; i < itemsCount; i++) {
            MediaItem item = getItem(i);
            if (item.type == MediaItemType.PHOTO) {
                return true;
            }
            if ((item instanceof PhotoBlockItem) && ((PhotoBlockItem) item).size() > 0) {
                return true;
            }
        }
        return false;
    }

    public List<EditablePhotoItem> getPhotos() {
        ArrayList<EditablePhotoItem> photoItems = null;
        int itemsCount = getItemsCount();
        for (int i = 0; i < itemsCount; i++) {
            MediaItem item = getItem(i);
            if (item.type == MediaItemType.PHOTO) {
                if (photoItems == null) {
                    photoItems = new ArrayList();
                }
                photoItems.add((EditablePhotoItem) item);
            } else if (item.type == MediaItemType.PHOTO_BLOCK) {
                PhotoBlockItem photoBlock = (PhotoBlockItem) item;
                int blockSize = photoBlock.size();
                if (blockSize > 0) {
                    if (photoItems == null) {
                        photoItems = new ArrayList();
                    }
                    for (int j = 0; j < blockSize; j++) {
                        photoItems.add(photoBlock.getPhotoItem(j));
                    }
                }
            }
        }
        return photoItems != null ? photoItems : Collections.emptyList();
    }

    public boolean isItemsEmpty() {
        Iterator i$ = this.items.iterator();
        while (i$.hasNext()) {
            if (!((MediaItem) i$.next()).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        return isItemsEmpty() && this.withFriendsUids.isEmpty() && this.place == null;
    }

    public Stats getStats() {
        Stats stats = new Stats();
        stats.friendsCount = this.withFriendsUids == null ? 0 : this.withFriendsUids.size();
        Iterator i$ = this.items.iterator();
        while (i$.hasNext()) {
            MediaItem item = (MediaItem) i$.next();
            switch (C06762.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[item.type.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    int i;
                    String text = ((TextItem) item).getText();
                    int i2 = stats.textVolume;
                    if (text == null) {
                        i = 0;
                    } else {
                        i = TextUtils.getTrimmedLength(text);
                    }
                    stats.textVolume = i + i2;
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    stats.photoCount++;
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    stats.photoCount += ((PhotoBlockItem) item).size();
                    break;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    List<Track> tracks = ((MusicItem) item).getTracks();
                    stats.trackCount = (tracks == null ? 0 : tracks.size()) + stats.trackCount;
                    break;
                case Message.UUID_FIELD_NUMBER /*5*/:
                    stats.pollCount++;
                    break;
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    stats.linkCount++;
                    break;
                default:
                    break;
            }
        }
        return stats;
    }

    public ArrayList<String> getWithFriendsUids() {
        return this.withFriendsUids;
    }

    public Place getWithPlace() {
        return this.place;
    }

    public void setWithFriendsUids(ArrayList<String> withFriendsUids) {
        this.withFriendsUids = withFriendsUids;
    }

    public void setWithPlace(Place place) {
        this.place = place;
    }

    public String toString() {
        return "MediaTopicMessage[items=" + this.items + " with_friends=" + this.withFriendsUids + "]";
    }

    public static boolean equal(MediaTopicMessage m1, MediaTopicMessage m2) {
        if (m1 == null) {
            if (m2 == null) {
                return true;
            }
            return false;
        } else if (m2 == null) {
            return false;
        } else {
            if (ObjectUtils.listsEqual(m1.items, m2.items, MediaItem.EQUAL) && ObjectUtils.listsEqual(m1.withFriendsUids, m2.withFriendsUids)) {
                return true;
            }
            return false;
        }
    }

    MediaTopicMessage(Parcel source) {
        ClassLoader classLoader = MediaTopicMessage.class.getClassLoader();
        this.items = source.readArrayList(classLoader);
        this.withFriendsUids = source.readArrayList(classLoader);
        this.place = (Place) source.readParcelable(Place.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.items);
        dest.writeList(this.withFriendsUids);
        dest.writeParcelable(this.place, flags);
    }

    static {
        CREATOR = new C06751();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.withFriendsUids == null) {
            this.withFriendsUids = new ArrayList();
        }
    }
}
