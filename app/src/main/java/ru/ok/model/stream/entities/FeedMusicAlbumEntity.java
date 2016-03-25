package ru.ok.model.stream.entities;

import ru.ok.model.wmf.Album;

public class FeedMusicAlbumEntity extends BaseEntity {
    private final Album album;
    private final String id;

    public String getId() {
        return this.id;
    }

    public Album getAlbum() {
        return this.album;
    }

    protected FeedMusicAlbumEntity(Album album) {
        String str = null;
        super(15, null, null);
        this.album = album;
        if (album != null) {
            str = Long.toString(album.id);
        }
        this.id = str;
    }
}
