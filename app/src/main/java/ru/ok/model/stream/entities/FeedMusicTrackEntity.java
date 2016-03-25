package ru.ok.model.stream.entities;

import android.text.TextUtils;
import java.util.List;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;

public final class FeedMusicTrackEntity extends BaseEntity {
    private final String albumName;
    private List<FeedMusicAlbumEntity> albums;
    private final String artistName;
    private List<FeedMusicArtistEntity> artists;
    private final int duration;
    private String fullName;
    private final String id;
    private String imageUrl;
    private final transient String initialImageUrl;
    private final String title;

    public FeedMusicTrackEntity(String id, String title, String albumName, String artistName, String imageUrl, String fullName, int duration, LikeInfoContext likeInfo) {
        super(10, likeInfo, null);
        this.id = id;
        this.title = title;
        this.albumName = albumName;
        this.artistName = artistName;
        this.fullName = fullName;
        this.initialImageUrl = imageUrl;
        this.imageUrl = imageUrl;
        this.fullName = fullName;
        this.duration = duration;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getFullName() {
        return this.fullName;
    }

    public List<FeedMusicAlbumEntity> getAlbums() {
        return this.albums;
    }

    public List<FeedMusicArtistEntity> getArtists() {
        return this.artists;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean hasImage() {
        return !TextUtils.isEmpty(this.imageUrl);
    }

    void setAlbums(List<FeedMusicAlbumEntity> albums) {
        this.albums = albums;
        updateImage();
    }

    void setArtists(List<FeedMusicArtistEntity> artists) {
        this.artists = artists;
        updateImage();
    }

    private void updateImage() {
        if (TextUtils.isEmpty(this.initialImageUrl)) {
            if (this.albums != null) {
                for (FeedMusicAlbumEntity albumEntity : this.albums) {
                    Album album = albumEntity.getAlbum();
                    if (album != null && !TextUtils.isEmpty(album.imageUrl)) {
                        this.imageUrl = album.imageUrl;
                        return;
                    }
                }
            }
            if (this.artists != null) {
                for (FeedMusicArtistEntity artistEntity : this.artists) {
                    Artist artist = artistEntity.getArtist();
                    if (artist != null && !TextUtils.isEmpty(artist.imageUrl)) {
                        this.imageUrl = artist.imageUrl;
                        return;
                    }
                }
            }
            this.imageUrl = null;
            return;
        }
        this.imageUrl = this.initialImageUrl;
    }
}
