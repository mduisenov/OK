package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.BaseEntityBuilder;

public class StreamPage implements Parcelable {
    public static final Creator<StreamPage> CREATOR;
    @Nullable
    StreamPageKey bottomKey;
    @NonNull
    public final HashMap<String, BaseEntityBuilder> entities;
    @NonNull
    public final ArrayList<Feed> feeds;
    @NonNull
    StreamPageKey key;
    long pageTs;
    long streamTs;
    @Nullable
    StreamPageKey topKey;

    /* renamed from: ru.ok.model.stream.StreamPage.1 */
    static class C16011 implements Creator<StreamPage> {
        C16011() {
        }

        public StreamPage createFromParcel(Parcel source) {
            return new StreamPage(source);
        }

        public StreamPage[] newArray(int size) {
            return new StreamPage[size];
        }
    }

    StreamPage() {
        this.feeds = new ArrayList();
        this.entities = new HashMap();
    }

    public StreamPage(@NonNull ArrayList<Feed> feeds, @NonNull HashMap<String, BaseEntityBuilder> entities, @NonNull StreamPageKey key, @Nullable StreamPageKey bottomKey) {
        this.feeds = feeds;
        this.entities = entities;
        this.key = key;
        this.bottomKey = bottomKey;
    }

    public StreamPage(@NonNull StreamPage page) {
        this.feeds = page.feeds;
        this.entities = page.entities;
        this.key = page.key;
        this.bottomKey = page.bottomKey;
        this.topKey = page.topKey;
    }

    public StreamPageKey getKey() {
        return this.key;
    }

    public StreamPageKey getTopKey() {
        return this.topKey;
    }

    public StreamPageKey getBottomKey() {
        return this.bottomKey;
    }

    public long getPageTs() {
        return this.pageTs;
    }

    public long getStreamTs() {
        return this.streamTs;
    }

    public void setTopKey(StreamPageKey topKey) {
        this.topKey = topKey;
    }

    public void setPageTs(long pageTs) {
        this.pageTs = pageTs;
    }

    public void setStreamTs(long streamTs) {
        this.streamTs = streamTs;
    }

    public void resolveRefs() {
        Map<String, BaseEntity> resolvedEntities = EntityReferenceResolver.resolveEntityRefs(this.entities);
        FeedGraphTraverser traverser = new FeedGraphTraverser(this.entities);
        Iterator i$ = this.feeds.iterator();
        while (i$.hasNext()) {
            Feed feed = (Feed) i$.next();
            feed.resolveRefs(resolvedEntities);
            traverser.traverse(null, feed, new FeedEntitiesAccumulator(resolvedEntities, feed.entitiesByRefId));
        }
    }

    public String toString() {
        return "StreamPage[key=" + this.key + " feeds.size=" + this.feeds.size() + " entities.size=" + this.entities.size() + " topKey=" + this.topKey + " bottomKey=" + this.bottomKey + " pageTs=" + this.pageTs + " streamTs=" + this.streamTs + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.feeds);
        dest.writeMap(this.entities);
        dest.writeParcelable(this.key, flags);
        dest.writeParcelable(this.topKey, flags);
        dest.writeParcelable(this.bottomKey, flags);
        dest.writeLong(this.pageTs);
        dest.writeLong(this.streamTs);
    }

    protected StreamPage(Parcel src) {
        ClassLoader cl = StreamPage.class.getClassLoader();
        this.feeds = src.readArrayList(cl);
        this.entities = src.readHashMap(cl);
        this.key = (StreamPageKey) src.readParcelable(cl);
        this.topKey = (StreamPageKey) src.readParcelable(cl);
        this.bottomKey = (StreamPageKey) src.readParcelable(cl);
        this.pageTs = src.readLong();
        this.streamTs = src.readLong();
    }

    static {
        CREATOR = new C16011();
    }
}
