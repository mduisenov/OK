package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.Utils;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedPollEntity;

public class MediaItemPollBuilder extends MediaItemBuilder<MediaItemPollBuilder, MediaItemPoll> {
    public static final Creator<MediaItemPollBuilder> CREATOR;
    final List<String> pollRefs;

    /* renamed from: ru.ok.model.mediatopics.MediaItemPollBuilder.1 */
    static class C15341 implements Creator<MediaItemPollBuilder> {
        C15341() {
        }

        public MediaItemPollBuilder createFromParcel(Parcel source) {
            return new MediaItemPollBuilder(source);
        }

        public MediaItemPollBuilder[] newArray(int size) {
            return new MediaItemPollBuilder[size];
        }
    }

    public MediaItemPollBuilder(List<String> pollRefs) {
        this.pollRefs = pollRefs;
    }

    public MediaItemPollBuilder() {
        this.pollRefs = new ArrayList();
    }

    public MediaItemPollBuilder addPollRef(String ref) {
        this.pollRefs.add(ref);
        return this;
    }

    public void getRefs(List<String> outRefs) {
        super.getRefs(outRefs);
        outRefs.addAll(this.pollRefs);
    }

    public MediaItemPoll resolveRefs(Map<String, BaseEntity> resolvedEntities) throws EntityRefNotResolvedException {
        MediaItemPoll item = new MediaItemPoll(new ArrayList());
        Utils.resolveRefs(resolvedEntities, this.pollRefs, item.polls, FeedPollEntity.class);
        return item;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.pollRefs);
    }

    MediaItemPollBuilder(Parcel src) {
        super(src);
        this.pollRefs = src.readArrayList(MediaItemPollBuilder.class.getClassLoader());
    }

    static {
        CREATOR = new C15341();
    }
}
