package ru.ok.model.stream.entities;

import android.support.v4.app.NotificationCompat;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.LikeInfoContext;

public class FeedAchievementTypeEntity extends BaseEntity implements IPresentEntity {
    private final String id;
    private final TreeSet<PhotoSize> pics;
    private final String title;

    FeedAchievementTypeEntity(String id, String title, TreeSet<PhotoSize> pics, LikeInfoContext likeInfo) {
        super(19, likeInfo, null);
        this.id = id;
        this.title = title;
        this.pics = pics;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getLargestPicUrl() {
        if (this.pics == null || this.pics.isEmpty()) {
            return null;
        }
        PhotoSize pic = (PhotoSize) this.pics.first();
        if (pic != null) {
            return pic.getUrl();
        }
        return null;
    }

    public boolean isBig() {
        if (!(this.pics == null || this.pics.isEmpty())) {
            PhotoSize pic = (PhotoSize) this.pics.first();
            if (pic != null && pic.getHeight() >= NotificationCompat.FLAG_HIGH_PRIORITY && pic.getWidth() >= NotificationCompat.FLAG_HIGH_PRIORITY) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (this.id.equals(((FeedAchievementTypeEntity) o).id)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean isLive() {
        return false;
    }
}
