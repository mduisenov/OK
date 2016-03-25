package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedPlayListEntityBuilder extends BaseEntityBuilder<FeedPlayListEntityBuilder, FeedPlayListEntity> {
    public static final Creator<FeedPlayListEntityBuilder> CREATOR;
    String imageUrl;
    String title;
    List<String> trackRefs;

    /* renamed from: ru.ok.model.stream.entities.FeedPlayListEntityBuilder.1 */
    static class C16241 implements Creator<FeedPlayListEntityBuilder> {
        C16241() {
        }

        public FeedPlayListEntityBuilder createFromParcel(Parcel source) {
            FeedPlayListEntityBuilder builder = new FeedPlayListEntityBuilder();
            try {
                builder.readFromParcel(source);
                return builder;
            } catch (RecoverableUnParcelException e) {
                Logger.m185w("Failed to un-parcel: %s", e);
                return null;
            }
        }

        public FeedPlayListEntityBuilder[] newArray(int size) {
            return new FeedPlayListEntityBuilder[size];
        }
    }

    public FeedPlayListEntityBuilder() {
        super(18);
    }

    protected FeedPlayListEntity doPreBuild() throws FeedObjectException {
        return new FeedPlayListEntity(getId(), this.title, this.imageUrl, getLikeInfo(), getDiscussionSummary());
    }

    public void getRefs(List<String> outRefs) {
        if (this.trackRefs != null) {
            outRefs.addAll(this.trackRefs);
        }
    }

    protected void resolveRefs(Map<String, BaseEntity> resolvedEntities, FeedPlayListEntity playListEntity) throws EntityRefNotResolvedException {
        if (this.trackRefs != null) {
            List<FeedMusicTrackEntity> tracks = new ArrayList(this.trackRefs.size());
            for (String trackRef : this.trackRefs) {
                BaseEntity entity = (BaseEntity) resolvedEntities.get(trackRef);
                if (entity instanceof FeedMusicTrackEntity) {
                    tracks.add((FeedMusicTrackEntity) entity);
                } else {
                    throw new EntityRefNotResolvedException(trackRef, "Entity not found: " + entity);
                }
            }
            playListEntity.setTracks(tracks);
        }
    }

    public FeedPlayListEntityBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public FeedPlayListEntityBuilder withImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public FeedPlayListEntityBuilder addTrackRef(String trackRef) {
        if (this.trackRefs == null) {
            this.trackRefs = new ArrayList();
        }
        this.trackRefs.add(trackRef);
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.imageUrl);
        dest.writeInt(this.trackRefs == null ? 0 : this.trackRefs.size());
        if (this.trackRefs != null) {
            dest.writeStringList(this.trackRefs);
        }
    }

    protected FeedPlayListEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            super.readFromParcel(src);
            return this;
        } finally {
            this.title = src.readString();
            this.imageUrl = src.readString();
            int trackRefsSize = src.readInt();
            if (trackRefsSize > 0) {
                ArrayList<String> trackRefs = new ArrayList(trackRefsSize);
                src.readStringList(trackRefs);
                this.trackRefs = trackRefs;
            }
        }
    }

    static {
        CREATOR = new C16241();
    }
}
