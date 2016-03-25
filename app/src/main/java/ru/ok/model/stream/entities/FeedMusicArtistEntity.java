package ru.ok.model.stream.entities;

import ru.ok.model.wmf.Artist;

public class FeedMusicArtistEntity extends BaseEntity {
    private final Artist artist;
    private final String id;

    public String getId() {
        return this.id;
    }

    public Artist getArtist() {
        return this.artist;
    }

    protected FeedMusicArtistEntity(Artist artist) {
        String str = null;
        super(16, null, null);
        this.artist = artist;
        if (artist != null) {
            str = Long.toString(artist.id);
        }
        this.id = str;
    }
}
