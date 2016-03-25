package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;
import ru.ok.model.wmf.Artist;

public class FeedMusicArtistEntityBuilder extends BaseEntityBuilder<FeedMusicArtistEntityBuilder, FeedMusicArtistEntity> {
    public static final Creator<FeedMusicArtistEntityBuilder> CREATOR;
    Artist artist;

    /* renamed from: ru.ok.model.stream.entities.FeedMusicArtistEntityBuilder.1 */
    static class C16211 implements Creator<FeedMusicArtistEntityBuilder> {
        C16211() {
        }

        public FeedMusicArtistEntityBuilder createFromParcel(Parcel source) {
            FeedMusicArtistEntityBuilder builder = new FeedMusicArtistEntityBuilder();
            try {
                builder.readFromParcel(source);
                return builder;
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to read music artist from parcel");
                return null;
            }
        }

        public FeedMusicArtistEntityBuilder[] newArray(int size) {
            return new FeedMusicArtistEntityBuilder[size];
        }
    }

    public FeedMusicArtistEntityBuilder() {
        super(16);
    }

    public FeedMusicArtistEntityBuilder setArtist(Artist artist) {
        this.artist = artist;
        withId(artist == null ? null : Long.toString(artist.id));
        return this;
    }

    protected FeedMusicArtistEntity doPreBuild() throws FeedObjectException {
        return new FeedMusicArtistEntity(this.artist);
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.artist, flags);
    }

    protected FeedMusicArtistEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            FeedMusicArtistEntityBuilder feedMusicArtistEntityBuilder = (FeedMusicArtistEntityBuilder) super.readFromParcel(src);
            this.artist = (Artist) src.readParcelable(FeedMusicArtistEntityBuilder.class.getClassLoader());
            return feedMusicArtistEntityBuilder;
        } catch (Throwable th) {
            Throwable th2 = th;
            this.artist = (Artist) src.readParcelable(FeedMusicArtistEntityBuilder.class.getClassLoader());
        }
    }

    static {
        CREATOR = new C16211();
    }
}
