package ru.ok.model.stream.banner;

import java.util.List;
import ru.ok.model.photo.PhotoSize;

public class BannerWithRating extends Banner {
    public final float rating;
    public final int users;
    public final int votes;

    BannerWithRating(long db_id, String id, int template, String header, String text, int actionType, int iconType, String iconUrl, String iconUrlHd, List<PhotoSize> imageUrls, String clickUrl, int color, String disclaimer, String info, String ageRestriction, String topicId, String deepLink, int votes, int users, float rating) {
        super(db_id, id, template, header, text, actionType, iconType, iconUrl, iconUrlHd, imageUrls, clickUrl, color, disclaimer, info, ageRestriction, topicId, deepLink);
        this.votes = votes;
        this.users = users;
        this.rating = rating;
    }
}
