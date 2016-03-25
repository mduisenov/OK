package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;
import ru.ok.model.wmf.Album;

public class FeedMusicAlbumEntityBuilder extends BaseEntityBuilder<FeedMusicAlbumEntityBuilder, FeedMusicAlbumEntity> {
    public static final Creator<FeedMusicAlbumEntityBuilder> CREATOR;
    Album album;

    /* renamed from: ru.ok.model.stream.entities.FeedMusicAlbumEntityBuilder.1 */
    static class C16201 implements Creator<FeedMusicAlbumEntityBuilder> {
        C16201() {
        }

        public FeedMusicAlbumEntityBuilder createFromParcel(Parcel source) {
            FeedMusicAlbumEntityBuilder builder = new FeedMusicAlbumEntityBuilder();
            try {
                builder.readFromParcel(source);
                return builder;
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to read music album from parcel");
                return null;
            }
        }

        public FeedMusicAlbumEntityBuilder[] newArray(int size) {
            return new FeedMusicAlbumEntityBuilder[size];
        }
    }

    public FeedMusicAlbumEntityBuilder() {
        super(15);
    }

    public FeedMusicAlbumEntityBuilder setAlbum(Album album) {
        this.album = album;
        withId(album == null ? null : Long.toString(album.id));
        return this;
    }

    protected FeedMusicAlbumEntity doPreBuild() throws FeedObjectException {
        return new FeedMusicAlbumEntity(this.album);
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.album, flags);
    }

    protected FeedMusicAlbumEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            FeedMusicAlbumEntityBuilder feedMusicAlbumEntityBuilder = (FeedMusicAlbumEntityBuilder) super.readFromParcel(src);
            this.album = (Album) src.readParcelable(FeedMusicAlbumEntityBuilder.class.getClassLoader());
            return feedMusicAlbumEntityBuilder;
        } catch (Throwable th) {
            Throwable th2 = th;
            this.album = (Album) src.readParcelable(FeedMusicAlbumEntityBuilder.class.getClassLoader());
        }
    }

    static {
        CREATOR = new C16201();
    }
}
