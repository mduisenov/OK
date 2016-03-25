package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.Utils;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedMediaTopicEntity;

public class MediaItemTopicBuilder extends MediaItemBuilder<MediaItemTopicBuilder, MediaItemTopic> {
    public static final Creator<MediaItemTopicBuilder> CREATOR;
    List<String> mediaTopicRefs;

    /* renamed from: ru.ok.model.mediatopics.MediaItemTopicBuilder.1 */
    static class C15371 implements Creator<MediaItemTopicBuilder> {
        C15371() {
        }

        public MediaItemTopicBuilder createFromParcel(Parcel source) {
            return new MediaItemTopicBuilder(source);
        }

        public MediaItemTopicBuilder[] newArray(int size) {
            return new MediaItemTopicBuilder[size];
        }
    }

    public MediaItemTopic resolveRefs(Map<String, BaseEntity> resolvedEntities) throws EntityRefNotResolvedException {
        List<FeedMediaTopicEntity> mediaTopics;
        if (this.mediaTopicRefs == null) {
            mediaTopics = Collections.emptyList();
        } else {
            mediaTopics = new ArrayList(this.mediaTopicRefs.size());
            Utils.resolveRefs(resolvedEntities, this.mediaTopicRefs, mediaTopics, FeedMediaTopicEntity.class);
        }
        return new MediaItemTopic(resolveReshareOwners(resolvedEntities), mediaTopics, isReshare());
    }

    public MediaItemTopicBuilder addMediaTopicRef(String ref) {
        if (this.mediaTopicRefs == null) {
            this.mediaTopicRefs = new ArrayList();
        }
        this.mediaTopicRefs.add(ref);
        return this;
    }

    public void getRefs(List<String> outRefs) {
        super.getRefs(outRefs);
        if (this.mediaTopicRefs != null) {
            outRefs.addAll(this.mediaTopicRefs);
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mediaTopicRefs == null ? -1 : this.mediaTopicRefs.size());
        if (this.mediaTopicRefs != null) {
            dest.writeStringList(this.mediaTopicRefs);
        }
    }

    protected MediaItemTopicBuilder(Parcel src) {
        super(src);
        int mediaTopicsSize = src.readInt();
        if (mediaTopicsSize >= 0) {
            this.mediaTopicRefs = new ArrayList(mediaTopicsSize);
            src.readStringList(this.mediaTopicRefs);
        }
    }

    static {
        CREATOR = new C15371();
    }
}
