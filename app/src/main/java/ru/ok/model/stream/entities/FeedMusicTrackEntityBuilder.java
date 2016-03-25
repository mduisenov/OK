package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.ok.model.stream.EntityRefNotResolvedException;
import ru.ok.model.stream.FeedObjectException;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedMusicTrackEntityBuilder extends BaseEntityBuilder<FeedMusicTrackEntityBuilder, FeedMusicTrackEntity> {
    public static final Creator<FeedMusicTrackEntityBuilder> CREATOR;
    String albumName;
    List<String> albumRefs;
    String artistName;
    List<String> artistRefs;
    int duration;
    String fullName;
    String imageUrl;
    String title;

    /* renamed from: ru.ok.model.stream.entities.FeedMusicTrackEntityBuilder.1 */
    static class C16221 implements Creator<FeedMusicTrackEntityBuilder> {
        C16221() {
        }

        public FeedMusicTrackEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedMusicTrackEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedMusicTrackEntityBuilder[] newArray(int size) {
            return new FeedMusicTrackEntityBuilder[size];
        }
    }

    public FeedMusicTrackEntityBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public FeedMusicTrackEntityBuilder withAlbumName(String albumName) {
        this.albumName = albumName;
        return this;
    }

    public FeedMusicTrackEntityBuilder withArtistName(String artistName) {
        this.artistName = artistName;
        return this;
    }

    public FeedMusicTrackEntityBuilder withDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public FeedMusicTrackEntityBuilder withImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public FeedMusicTrackEntityBuilder withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public FeedMusicTrackEntityBuilder addAlbumRef(String albumRef) {
        if (this.albumRefs == null) {
            this.albumRefs = new ArrayList();
        }
        this.albumRefs.add(albumRef);
        return this;
    }

    public FeedMusicTrackEntityBuilder addArtistRef(String artistRef) {
        if (this.artistRefs == null) {
            this.artistRefs = new ArrayList();
        }
        this.artistRefs.add(artistRef);
        return this;
    }

    protected FeedMusicTrackEntity doPreBuild() throws FeedObjectException {
        String id = getId();
        if (id != null) {
            return new FeedMusicTrackEntity(id, this.title, this.albumName, this.artistName, this.imageUrl, this.fullName, this.duration, getLikeInfo());
        }
        throw new FeedObjectException("Music track ID is null");
    }

    protected void resolveRefs(Map<String, BaseEntity> resolvedEntities, FeedMusicTrackEntity entity) throws EntityRefNotResolvedException {
        if (this.albumRefs != null) {
            List<FeedMusicAlbumEntity> albums = new ArrayList(this.albumRefs.size());
            for (String albumRef : this.albumRefs) {
                BaseEntity albumEntity = (BaseEntity) resolvedEntities.get(albumRef);
                if (albumEntity instanceof FeedMusicAlbumEntity) {
                    albums.add((FeedMusicAlbumEntity) albumEntity);
                } else {
                    throw new EntityRefNotResolvedException(albumRef, String.valueOf(albumEntity));
                }
            }
            entity.setAlbums(albums);
        }
        if (this.artistRefs != null) {
            List<FeedMusicArtistEntity> artists = new ArrayList(this.artistRefs.size());
            for (String artistRef : this.artistRefs) {
                BaseEntity artistEntity = (BaseEntity) resolvedEntities.get(artistRef);
                if (artistEntity instanceof FeedMusicArtistEntity) {
                    artists.add((FeedMusicArtistEntity) artistEntity);
                } else {
                    throw new EntityRefNotResolvedException(artistRef, String.valueOf(artistEntity));
                }
            }
            entity.setArtists(artists);
        }
    }

    public void getRefs(List<String> outRefs) {
        if (this.albumRefs != null) {
            outRefs.addAll(this.albumRefs);
        }
        if (this.artistRefs != null) {
            outRefs.addAll(this.artistRefs);
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i = 0;
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.albumName);
        dest.writeString(this.artistName);
        dest.writeString(this.imageUrl);
        dest.writeString(this.fullName);
        dest.writeInt(this.duration);
        dest.writeInt(this.albumRefs == null ? 0 : 1);
        if (this.albumRefs != null) {
            dest.writeStringList(this.albumRefs);
        }
        if (this.artistRefs != null) {
            i = 1;
        }
        dest.writeInt(i);
        if (this.artistRefs != null) {
            dest.writeStringList(this.artistRefs);
        }
    }

    public FeedMusicTrackEntityBuilder() {
        super(10);
    }

    protected FeedMusicTrackEntityBuilder readFromParcel(Parcel src) throws RecoverableUnParcelException {
        try {
            super.readFromParcel(src);
            return this;
        } finally {
            this.title = src.readString();
            this.albumName = src.readString();
            this.artistName = src.readString();
            this.imageUrl = src.readString();
            this.fullName = src.readString();
            this.duration = src.readInt();
            if (src.readInt() != 0) {
                this.albumRefs = new ArrayList();
                src.readStringList(this.albumRefs);
            }
            if (src.readInt() != 0) {
                this.artistRefs = new ArrayList();
                src.readStringList(this.artistRefs);
            }
        }
    }

    static {
        CREATOR = new C16221();
    }
}
