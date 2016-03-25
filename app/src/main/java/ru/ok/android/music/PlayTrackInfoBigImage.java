package ru.ok.android.music;

import ru.ok.model.wmf.PlayTrackInfo;

public class PlayTrackInfoBigImage extends PlayTrackInfo {
    public static PlayTrackInfoBigImage create(PlayTrackInfo info) {
        return new PlayTrackInfoBigImage(info.trackId, info.imageUrl == null ? null : PlayTrackInfo.getBigImageUrl(info.imageUrl), info.contentUrl, info.size, info.duration, info.userName, info.userId);
    }

    private PlayTrackInfoBigImage(long trackId, String imageUrl, String contentUrl, long size, long duration, String userName, String userId) {
        super(trackId, imageUrl == null ? null : PlayTrackInfo.getBigImageUrl(imageUrl), contentUrl, size, duration, userName, userId);
    }
}
