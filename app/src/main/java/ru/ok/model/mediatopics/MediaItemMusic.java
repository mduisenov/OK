package ru.ok.model.mediatopics;

import android.text.TextUtils;
import java.util.Collections;
import java.util.List;
import ru.ok.model.stream.entities.FeedMusicAlbumEntity;
import ru.ok.model.stream.entities.FeedMusicArtistEntity;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;

public final class MediaItemMusic extends MediaItem {
    private final boolean hasCoverImages;
    private final List<FeedMusicTrackEntity> tracks;

    public MediaItemMusic(List<FeedMusicTrackEntity> tracks) {
        super(MediaItemType.MUSIC);
        this.tracks = Collections.unmodifiableList(tracks);
        this.hasCoverImages = hasCoverImages(tracks);
    }

    public List<FeedMusicTrackEntity> getTracks() {
        return this.tracks;
    }

    private static final boolean hasCoverImages(List<FeedMusicTrackEntity> tracks) {
        if (tracks == null) {
            return false;
        }
        for (FeedMusicTrackEntity track : tracks) {
            if (!TextUtils.isEmpty(track.getImageUrl())) {
                return true;
            }
            List<FeedMusicAlbumEntity> albums = track.getAlbums();
            if (albums != null) {
                for (FeedMusicAlbumEntity album : albums) {
                    if (!TextUtils.isEmpty(album.getAlbum().imageUrl)) {
                        return true;
                    }
                }
            }
            List<FeedMusicArtistEntity> artists = track.getArtists();
            if (artists != null) {
                for (FeedMusicArtistEntity artist : artists) {
                    if (!TextUtils.isEmpty(artist.getArtist().imageUrl)) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }
}
