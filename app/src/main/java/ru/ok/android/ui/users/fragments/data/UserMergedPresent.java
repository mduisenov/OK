package ru.ok.android.ui.users.fragments.data;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.presents.AnimationProperties;
import ru.ok.model.presents.IPresentType;

public final class UserMergedPresent implements IPresentType {
    public final AnimationProperties animationProperties;
    public final boolean isAnimated;
    public final boolean isBig;
    public final String picture;
    public final String presentId;
    public final String senderId;
    public final PhotoSize sprite;
    public final long trackId;

    public UserMergedPresent(String presentId, String senderId, String picture, boolean isBig, long trackId, boolean isAnimated, PhotoSize sprite, AnimationProperties animationProperties) {
        this.presentId = presentId;
        this.senderId = senderId;
        this.picture = picture;
        this.isBig = isBig;
        this.trackId = trackId;
        this.isAnimated = isAnimated;
        this.sprite = sprite;
        this.animationProperties = animationProperties;
    }

    public boolean equals(Object o) {
        if (!(o instanceof UserMergedPresent)) {
            return false;
        }
        UserMergedPresent present = (UserMergedPresent) o;
        if (TextUtils.equals(present.presentId, this.presentId) && TextUtils.equals(present.senderId, this.senderId) && TextUtils.equals(present.picture, this.picture) && present.isBig == this.isBig && this.trackId == present.trackId) {
            return true;
        }
        return false;
    }

    @Nullable
    public String getStaticImage() {
        return this.picture;
    }

    @Nullable
    public AnimationProperties getAnimationProperties() {
        return this.animationProperties;
    }

    @Nullable
    public TreeSet<PhotoSize> getSprites() {
        TreeSet<PhotoSize> sprites = new TreeSet();
        sprites.add(this.sprite);
        return sprites;
    }

    public boolean isAnimated() {
        return this.isAnimated;
    }

    public boolean isLive() {
        return false;
    }
}
