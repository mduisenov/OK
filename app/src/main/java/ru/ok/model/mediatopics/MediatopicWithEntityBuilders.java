package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.EntityReferenceResolver;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.BaseEntityBuilder;
import ru.ok.model.stream.entities.FeedMediaTopicEntity;
import ru.ok.model.stream.entities.FeedMediaTopicEntityBuilder;

public final class MediatopicWithEntityBuilders implements Parcelable {
    public static final Creator<MediatopicWithEntityBuilders> CREATOR;
    public final Map<String, BaseEntityBuilder> entities;
    public final List<FeedMediaTopicEntityBuilder> mediatopics;
    public final Map<String, BaseEntity> resolvedEntities;

    /* renamed from: ru.ok.model.mediatopics.MediatopicWithEntityBuilders.1 */
    static class C15391 implements Creator<MediatopicWithEntityBuilders> {
        C15391() {
        }

        public MediatopicWithEntityBuilders createFromParcel(Parcel source) {
            ClassLoader cl = MediatopicWithEntityBuilders.class.getClassLoader();
            ArrayList<FeedMediaTopicEntityBuilder> mediatopics = new ArrayList();
            source.readList(mediatopics, cl);
            return new MediatopicWithEntityBuilders(mediatopics, source.readHashMap(cl));
        }

        public MediatopicWithEntityBuilders[] newArray(int size) {
            return new MediatopicWithEntityBuilders[size];
        }
    }

    public MediatopicWithEntityBuilders(List<FeedMediaTopicEntityBuilder> mediatopics, Map<String, BaseEntityBuilder> entities) {
        this.mediatopics = mediatopics;
        this.entities = entities;
        this.resolvedEntities = EntityReferenceResolver.resolveEntityRefs(entities);
    }

    public FeedMediaTopicEntity getMediaTopicEntity(String mediaTopicId) {
        for (FeedMediaTopicEntityBuilder builder : this.mediatopics) {
            if (builder.getId() != null && builder.getId().equals(mediaTopicId)) {
                try {
                    builder.preBuild();
                    return (FeedMediaTopicEntity) builder.build(this.resolvedEntities);
                } catch (FeedObjectException e) {
                    Logger.m172d("unable to build mediatopicEntity with id: " + mediaTopicId);
                }
            }
        }
        return null;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.mediatopics);
        dest.writeMap(this.entities);
    }

    static {
        CREATOR = new C15391();
    }
}
