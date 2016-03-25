package ru.ok.model.mediatopics;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.Utils;
import ru.ok.model.stream.entities.BaseEntity;

public abstract class MediaItemBuilder<TBuilder extends MediaItemBuilder, TMediaItem> implements Parcelable {
    boolean isReshare;
    List<String> reshareOwnerRefs;

    public abstract TMediaItem resolveRefs(Map<String, BaseEntity> map) throws EntityRefNotResolvedException;

    protected List<BaseEntity> resolveReshareOwners(Map<String, BaseEntity> resolvedEntities) throws EntityRefNotResolvedException {
        if (this.reshareOwnerRefs == null) {
            return Collections.emptyList();
        }
        List<BaseEntity> reshareOwners = new ArrayList(this.reshareOwnerRefs.size());
        Utils.resolveRefs(resolvedEntities, this.reshareOwnerRefs, reshareOwners, BaseEntity.class);
        return reshareOwners;
    }

    public void getRefs(List<String> outRefs) {
        if (this.reshareOwnerRefs != null) {
            outRefs.addAll(this.reshareOwnerRefs);
        }
    }

    public MediaItemBuilder<TBuilder, TMediaItem> setIsReshare(boolean isReshare) {
        this.isReshare = isReshare;
        return this;
    }

    public boolean isReshare() {
        return this.isReshare;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.reshareOwnerRefs == null ? -1 : this.reshareOwnerRefs.size());
        if (this.reshareOwnerRefs != null) {
            dest.writeStringList(this.reshareOwnerRefs);
        }
        dest.writeByte(this.isReshare ? (byte) 1 : (byte) 0);
    }

    MediaItemBuilder(Parcel src) {
        int reshareOwnersSize = src.readInt();
        if (reshareOwnersSize >= 0) {
            this.reshareOwnerRefs = new ArrayList(reshareOwnersSize);
            src.readStringList(this.reshareOwnerRefs);
        }
        this.isReshare = src.readByte() != null;
    }

    public MediaItemBuilder addReshareOwnerRef(String ref) {
        if (this.reshareOwnerRefs == null) {
            this.reshareOwnerRefs = new ArrayList();
        }
        this.reshareOwnerRefs.add(ref);
        return this;
    }
}
