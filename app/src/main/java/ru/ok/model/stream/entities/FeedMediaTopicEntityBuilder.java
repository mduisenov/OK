package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ru.ok.model.mediatopics.MediaItem;
import ru.ok.model.mediatopics.MediaItemBuilder;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedMediaTopicEntityBuilder extends BaseEntityBuilder<FeedMediaTopicEntityBuilder, FeedMediaTopicEntity> {
    public static final Creator<FeedMediaTopicEntityBuilder> CREATOR;
    String authorRef;
    long createdDate;
    String deleteId;
    List<String> friendRefs;
    boolean hasMore;
    boolean isPromo;
    boolean isSticky;
    boolean isUnmodifiable;
    String markAsSpamId;
    final ArrayList<MediaItemBuilder> mediaItemsBuilders;
    String ownerRef;
    List<String> placesRefs;

    /* renamed from: ru.ok.model.stream.entities.FeedMediaTopicEntityBuilder.1 */
    static class C16191 implements Creator<FeedMediaTopicEntityBuilder> {
        C16191() {
        }

        public FeedMediaTopicEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedMediaTopicEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedMediaTopicEntityBuilder[] newArray(int size) {
            return new FeedMediaTopicEntityBuilder[size];
        }
    }

    public FeedMediaTopicEntityBuilder() {
        super(9);
        this.mediaItemsBuilders = new ArrayList();
    }

    public FeedMediaTopicEntityBuilder withCreatedDate(long createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public FeedMediaTopicEntityBuilder addMediaItem(MediaItemBuilder builder) {
        this.mediaItemsBuilders.add(builder);
        return this;
    }

    public FeedMediaTopicEntityBuilder withHasMore(boolean hasMore) {
        this.hasMore = hasMore;
        return this;
    }

    public FeedMediaTopicEntityBuilder withIsSticky(boolean isSticky) {
        this.isSticky = isSticky;
        return this;
    }

    public FeedMediaTopicEntityBuilder withIsUnmodifiable(boolean isUnmodifiable) {
        this.isUnmodifiable = isUnmodifiable;
        return this;
    }

    public FeedMediaTopicEntityBuilder withAuthorRef(String authorRef) {
        this.authorRef = authorRef;
        return this;
    }

    public FeedMediaTopicEntityBuilder withOwnerRef(String ownerRef) {
        this.ownerRef = ownerRef;
        return this;
    }

    public FeedMediaTopicEntityBuilder withFriendRefs(List<String> friendRefs) {
        this.friendRefs = friendRefs;
        return this;
    }

    public FeedMediaTopicEntityBuilder withPlacesRefs(List<String> placesRefs) {
        this.placesRefs = placesRefs;
        return this;
    }

    public FeedMediaTopicEntityBuilder withMarkAsSpamId(String markAsSpamId) {
        this.markAsSpamId = markAsSpamId;
        return this;
    }

    public FeedMediaTopicEntityBuilder withDeleteId(String deleteId) {
        this.deleteId = deleteId;
        return this;
    }

    public FeedMediaTopicEntityBuilder withIsPromo(boolean isPromo) {
        this.isPromo = isPromo;
        return this;
    }

    protected FeedMediaTopicEntity doPreBuild() throws FeedObjectException {
        String id = getId();
        if (id != null) {
            return new FeedMediaTopicEntity(id, this.createdDate, this.hasMore, getLikeInfo(), getDiscussionSummary(), this.markAsSpamId, this.deleteId, this.isSticky, this.isUnmodifiable, this.isPromo, new ArrayList(this.mediaItemsBuilders.size()));
        }
        throw new FeedObjectException("Media topic ID is null");
    }

    public void resolveRefs(Map<String, BaseEntity> resolvedEntities, FeedMediaTopicEntity entity) throws EntityRefNotResolvedException {
        Iterator i$ = this.mediaItemsBuilders.iterator();
        while (i$.hasNext()) {
            MediaItemBuilder<?, ? extends MediaItem> builder = (MediaItemBuilder) i$.next();
            MediaItem mediaItem = (MediaItem) builder.resolveRefs(resolvedEntities);
            if (mediaItem == null) {
                throw new EntityRefNotResolvedException("Cant resolve refs for " + builder.toString());
            }
            entity.mediaItems.add(mediaItem);
        }
        entity.setAuthor((BaseEntity) resolvedEntities.get(this.authorRef));
        entity.setOwner((BaseEntity) resolvedEntities.get(this.ownerRef));
        if (this.friendRefs != null) {
            List<BaseEntity> friends = new ArrayList();
            for (String friendRef : this.friendRefs) {
                BaseEntity friend = (BaseEntity) resolvedEntities.get(friendRef);
                if (friend != null) {
                    friends.add(friend);
                }
            }
            entity.setWithFriends(friends);
        }
        if (this.placesRefs != null) {
            List<BaseEntity> places = new ArrayList();
            for (String placeRef : this.placesRefs) {
                BaseEntity place = (BaseEntity) resolvedEntities.get(placeRef);
                if (place != null) {
                    places.add(place);
                }
            }
            entity.setPlaces(places);
        }
    }

    public void getRefs(List<String> outRefs) {
        Iterator i$ = this.mediaItemsBuilders.iterator();
        while (i$.hasNext()) {
            ((MediaItemBuilder) i$.next()).getRefs(outRefs);
        }
        if (this.authorRef != null) {
            outRefs.add(this.authorRef);
        }
        if (this.friendRefs != null) {
            outRefs.addAll(this.friendRefs);
        }
        if (this.placesRefs != null) {
            outRefs.addAll(this.placesRefs);
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        super.writeToParcel(dest, flags);
        dest.writeLong(this.createdDate);
        dest.writeInt(this.hasMore ? 1 : 0);
        if (this.isSticky) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.isUnmodifiable) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        dest.writeList(this.mediaItemsBuilders);
        dest.writeString(this.authorRef);
        dest.writeString(this.markAsSpamId);
        dest.writeStringList(this.friendRefs);
        dest.writeStringList(this.placesRefs);
        if (!this.isPromo) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    protected FeedMediaTopicEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        boolean z = true;
        try {
            super.readFromParcel(src);
            return this;
        } finally {
            boolean z2;
            ClassLoader cl = FeedMediaTopicEntityBuilder.class.getClassLoader();
            this.createdDate = src.readLong();
            if (src.readInt() > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.hasMore = z2;
            if (src.readInt() > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.isSticky = z2;
            if (src.readInt() > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.isUnmodifiable = z2;
            src.readList(this.mediaItemsBuilders, cl);
            this.authorRef = src.readString();
            this.markAsSpamId = src.readString();
            List<String> friendRefsTmp = new ArrayList();
            src.readStringList(friendRefsTmp);
            if (!friendRefsTmp.isEmpty()) {
                this.friendRefs = friendRefsTmp;
            }
            List<String> placesRefsTmp = new ArrayList();
            src.readStringList(placesRefsTmp);
            if (!placesRefsTmp.isEmpty()) {
                this.placesRefs = placesRefsTmp;
            }
            if (src.readInt() <= 0) {
                z = false;
            }
            this.isPromo = z;
        }
    }

    static {
        CREATOR = new C16191();
    }
}
