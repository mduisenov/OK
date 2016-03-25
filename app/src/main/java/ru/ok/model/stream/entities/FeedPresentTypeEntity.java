package ru.ok.model.stream.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.presents.AnimationProperties;
import ru.ok.model.presents.IPresentType;
import ru.ok.model.stream.LikeInfoContext;

public class FeedPresentTypeEntity extends BaseEntity implements IPresentType, IPresentEntity {
    @Nullable
    private final AnimationProperties animationProperties;
    @NonNull
    private final String id;
    private final boolean isAnimated;
    private final boolean isLive;
    @NonNull
    private final TreeSet<PhotoSize> pics;
    @NonNull
    private final TreeSet<PhotoSize> sprites;

    FeedPresentTypeEntity(@NonNull String id, @NonNull TreeSet<PhotoSize> pics, @NonNull TreeSet<PhotoSize> sprites, @Nullable LikeInfoContext likeInfo, @Nullable AnimationProperties animationProperties, boolean isAnimated, boolean isLive) {
        super(5, likeInfo, null);
        this.id = id;
        this.pics = pics;
        this.sprites = sprites;
        this.isAnimated = isAnimated;
        this.isLive = isLive;
        this.animationProperties = animationProperties;
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    @NonNull
    public String getStaticImage() {
        return getLargestPicUrl();
    }

    @Nullable
    public AnimationProperties getAnimationProperties() {
        return this.animationProperties;
    }

    @NonNull
    public TreeSet<PhotoSize> getSprites() {
        return this.sprites;
    }

    public boolean isAnimated() {
        return this.isAnimated;
    }

    public boolean isLive() {
        return this.isLive;
    }

    @Nullable
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
}
