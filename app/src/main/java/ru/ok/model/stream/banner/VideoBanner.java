package ru.ok.model.stream.banner;

import java.util.List;
import ru.ok.model.photo.PhotoSize;

public class VideoBanner extends Banner {
    public final VideoData videoData;

    VideoBanner(long db_id, String id, String header, String text, int actionType, int iconType, String iconUrl, String iconUrlHd, List<PhotoSize> imageUrls, String clickUrl, int color, String disclaimer, String info, String ageRestriction, String deepLink, VideoData videoData) {
        super(db_id, id, 5, header, text, actionType, iconType, iconUrl, iconUrlHd, imageUrls, clickUrl, color, disclaimer, info, ageRestriction, null, deepLink);
        this.videoData = videoData;
    }
}
