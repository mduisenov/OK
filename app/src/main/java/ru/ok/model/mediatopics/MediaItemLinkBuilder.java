package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import ru.ok.model.ImageUrl;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.entities.BaseEntity;

public class MediaItemLinkBuilder extends MediaItemBuilder<MediaItemLinkBuilder, MediaItemLink> {
    public static final Creator<MediaItemLinkBuilder> CREATOR;
    String description;
    @NonNull
    final ArrayList<ImageUrl> imageUrls;
    String title;
    String url;

    /* renamed from: ru.ok.model.mediatopics.MediaItemLinkBuilder.1 */
    static class C15311 implements Creator<MediaItemLinkBuilder> {
        C15311() {
        }

        public MediaItemLinkBuilder createFromParcel(Parcel source) {
            return new MediaItemLinkBuilder(source);
        }

        public MediaItemLinkBuilder[] newArray(int size) {
            return new MediaItemLinkBuilder[size];
        }
    }

    public MediaItemLinkBuilder() {
        this.imageUrls = new ArrayList();
    }

    public MediaItemLinkBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public MediaItemLinkBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public MediaItemLinkBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public MediaItemLinkBuilder addImageUrls(Collection<ImageUrl> imageUrls) {
        this.imageUrls.addAll(imageUrls);
        return this;
    }

    public MediaItemLink resolveRefs(Map<String, BaseEntity> resolvedEntities) throws EntityRefNotResolvedException {
        return new MediaItemLink(this.title, this.description, this.url, this.imageUrls, resolveReshareOwners(resolvedEntities), isReshare());
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.url);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeList(this.imageUrls);
    }

    MediaItemLinkBuilder(Parcel src) {
        super(src);
        this.url = src.readString();
        this.title = src.readString();
        this.description = src.readString();
        this.imageUrls = src.readArrayList(ImageUrl.class.getClassLoader());
    }

    static {
        CREATOR = new C15311();
    }
}
