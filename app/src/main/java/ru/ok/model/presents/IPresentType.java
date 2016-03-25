package ru.ok.model.presents;

import android.support.annotation.Nullable;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;

public interface IPresentType {
    @Nullable
    AnimationProperties getAnimationProperties();

    @Nullable
    TreeSet<PhotoSize> getSprites();

    @Nullable
    String getStaticImage();

    boolean isAnimated();

    boolean isLive();
}
