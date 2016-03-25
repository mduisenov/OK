package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedVideoEntityBuilder extends BaseEntityBuilder<FeedVideoEntityBuilder, FeedVideoEntity> {
    public static final Creator<FeedVideoEntityBuilder> CREATOR;
    String description;
    long duration;
    List<PhotoSize> thumbnailUrls;
    String title;

    /* renamed from: ru.ok.model.stream.entities.FeedVideoEntityBuilder.1 */
    static class C16311 implements Creator<FeedVideoEntityBuilder> {
        C16311() {
        }

        public FeedVideoEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedVideoEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedVideoEntityBuilder[] newArray(int size) {
            return new FeedVideoEntityBuilder[size];
        }
    }

    public FeedVideoEntityBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public FeedVideoEntityBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public FeedVideoEntityBuilder withDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public List<PhotoSize> getThumbnailUrls() {
        return this.thumbnailUrls;
    }

    protected FeedVideoEntity doPreBuild() throws FeedObjectException {
        String id = getId();
        if (id != null) {
            return new FeedVideoEntity(id, this.title, this.description, this.thumbnailUrls, this.duration, getLikeInfo(), getDiscussionSummary());
        }
        throw new FeedObjectException("Feed video ID is null");
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeLong(this.duration);
        dest.writeTypedList(this.thumbnailUrls);
    }

    public FeedVideoEntityBuilder() {
        super(13);
        this.thumbnailUrls = new ArrayList();
    }

    protected FeedVideoEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            super.readFromParcel(src);
            return this;
        } finally {
            String title = src.readString();
            String description = src.readString();
            long duration = src.readLong();
            List<PhotoSize> thumbnailUrls = new ArrayList();
            src.readTypedList(thumbnailUrls, PhotoSize.CREATOR);
            this.title = title;
            this.description = description;
            this.duration = duration;
            this.thumbnailUrls = thumbnailUrls;
        }
    }

    static {
        CREATOR = new C16311();
    }
}
