package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.Utils;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedVideoEntity;

public class MediaItemVideoBuilder extends MediaItemBuilder<MediaItemVideoBuilder, MediaItemVideo> {
    public static final Creator<MediaItemVideoBuilder> CREATOR;
    final List<String> videoRefs;

    /* renamed from: ru.ok.model.mediatopics.MediaItemVideoBuilder.1 */
    static class C15381 implements Creator<MediaItemVideoBuilder> {
        C15381() {
        }

        public MediaItemVideoBuilder createFromParcel(Parcel source) {
            return new MediaItemVideoBuilder(source);
        }

        public MediaItemVideoBuilder[] newArray(int size) {
            return new MediaItemVideoBuilder[size];
        }
    }

    public MediaItemVideoBuilder(List<String> videoRefs) {
        this.videoRefs = videoRefs;
    }

    public MediaItemVideoBuilder() {
        this.videoRefs = new ArrayList();
    }

    public MediaItemVideoBuilder addVideoRef(String ref) {
        this.videoRefs.add(ref);
        return this;
    }

    public void getRefs(List<String> outRefs) {
        super.getRefs(outRefs);
        outRefs.addAll(this.videoRefs);
    }

    public MediaItemVideo resolveRefs(Map<String, BaseEntity> resolvedEntities) throws EntityRefNotResolvedException {
        List<FeedVideoEntity> videos = new ArrayList(this.videoRefs.size());
        Utils.resolveRefs(resolvedEntities, this.videoRefs, videos, FeedVideoEntity.class);
        return new MediaItemVideo(videos, resolveReshareOwners(resolvedEntities), isReshare());
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.videoRefs);
    }

    MediaItemVideoBuilder(Parcel src) {
        super(src);
        this.videoRefs = src.readArrayList(MediaItemMusicBuilder.class.getClassLoader());
    }

    static {
        CREATOR = new C15381();
    }
}
