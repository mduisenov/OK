package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.Utils;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;

public class MediaItemMusicBuilder extends MediaItemBuilder<MediaItemMusicBuilder, MediaItemMusic> {
    public static final Creator<MediaItemMusicBuilder> CREATOR;
    final List<String> trackRefs;

    /* renamed from: ru.ok.model.mediatopics.MediaItemMusicBuilder.1 */
    static class C15321 implements Creator<MediaItemMusicBuilder> {
        C15321() {
        }

        public MediaItemMusicBuilder createFromParcel(Parcel source) {
            return new MediaItemMusicBuilder(source);
        }

        public MediaItemMusicBuilder[] newArray(int size) {
            return new MediaItemMusicBuilder[size];
        }
    }

    MediaItemMusicBuilder(List<String> trackRefs) {
        this.trackRefs = trackRefs;
    }

    public MediaItemMusicBuilder() {
        this.trackRefs = new ArrayList();
    }

    public MediaItemMusicBuilder addTrackRef(String ref) {
        this.trackRefs.add(ref);
        return this;
    }

    public void getRefs(List<String> outRefs) {
        super.getRefs(outRefs);
        outRefs.addAll(this.trackRefs);
    }

    public MediaItemMusic resolveRefs(Map<String, BaseEntity> resolvedEntities) throws EntityRefNotResolvedException {
        List<FeedMusicTrackEntity> tracks = new ArrayList(this.trackRefs.size());
        Utils.resolveRefs(resolvedEntities, this.trackRefs, tracks, FeedMusicTrackEntity.class);
        return new MediaItemMusic(tracks);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.trackRefs);
    }

    MediaItemMusicBuilder(Parcel src) {
        super(src);
        this.trackRefs = src.readArrayList(MediaItemMusicBuilder.class.getClassLoader());
    }

    static {
        CREATOR = new C15321();
    }
}
