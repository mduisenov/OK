package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.Utils;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;
import ru.ok.model.stream.entities.BaseEntity;

public class MediaItemPhotoBuilder extends MediaItemBuilder<MediaItemPhotoBuilder, MediaItemPhoto> {
    public static final Creator<MediaItemPhotoBuilder> CREATOR;
    final List<String> photoRefs;

    /* renamed from: ru.ok.model.mediatopics.MediaItemPhotoBuilder.1 */
    static class C15331 implements Creator<MediaItemPhotoBuilder> {
        C15331() {
        }

        public MediaItemPhotoBuilder createFromParcel(Parcel source) {
            return new MediaItemPhotoBuilder(source);
        }

        public MediaItemPhotoBuilder[] newArray(int size) {
            return new MediaItemPhotoBuilder[size];
        }
    }

    MediaItemPhotoBuilder(List<String> photoRefs) {
        this.photoRefs = photoRefs;
    }

    public MediaItemPhotoBuilder() {
        this.photoRefs = new ArrayList();
    }

    public MediaItemPhotoBuilder addPhotoRef(String ref) {
        this.photoRefs.add(ref);
        return this;
    }

    public void getRefs(List<String> outRefs) {
        super.getRefs(outRefs);
        outRefs.addAll(this.photoRefs);
    }

    public MediaItemPhoto resolveRefs(Map<String, BaseEntity> resolvedEntities) throws EntityRefNotResolvedException {
        List<AbsFeedPhotoEntity> photos = new ArrayList(this.photoRefs.size());
        Utils.resolveRefs(resolvedEntities, this.photoRefs, photos, AbsFeedPhotoEntity.class);
        return new MediaItemPhoto(photos, resolveReshareOwners(resolvedEntities), isReshare());
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.photoRefs);
    }

    MediaItemPhotoBuilder(Parcel src) {
        super(src);
        this.photoRefs = src.readArrayList(MediaItemPhotoBuilder.class.getClassLoader());
    }

    static {
        CREATOR = new C15331();
    }
}
