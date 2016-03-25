package ru.ok.android.app;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import ru.ok.android.receivers.ConnectivityReceiver;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.PhotoUtil;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.presents.AnimationProperties;
import ru.ok.model.presents.IPresentType;
import ru.ok.sprites.SpriteMetadata;
import ru.ok.sprites.Sprites;
import ru.ok.sprites.disk.DiskLruCache;
import ru.ok.sprites.fileLoader.URLConnectionFileLoader;
import ru.ok.sprites.memory.BitmapLruCache;
import ru.ok.sprites.memory.BitmapRegionDecoderLruCache;

public final class SpritesHelper {
    public static void initialize(@NonNull Context context) {
        ThreadPoolExecutor loadSpriteExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new PriorityBlockingQueue());
        ThreadPoolExecutor rotateSpriteExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new PriorityBlockingQueue());
        ThreadPoolExecutor renderSpriteExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        ThreadPoolExecutor decodingExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new PriorityBlockingQueue());
        BitmapLruCache<String> bitmapLruCache = new BitmapLruCache(1572864);
        BitmapRegionDecoderLruCache<String> bitmapRegionDecoderLruCache = new BitmapRegionDecoderLruCache(GravityCompat.RELATIVE_LAYOUT_DIRECTION);
        DiskLruCache spriteDiskCache = new DiskLruCache(new File(context.getCacheDir(), "sprites"), 1, 1, 20971520);
        Sprites.initialize(loadSpriteExecutor, renderSpriteExecutor, rotateSpriteExecutor, decodingExecutor, new File(context.getCacheDir(), "sprites_tmp"), spriteDiskCache, new URLConnectionFileLoader(5), bitmapRegionDecoderLruCache, bitmapLruCache, context);
    }

    @Nullable
    public static String prefetch(@NonNull IPresentType presentType, @NonNull Point size) {
        return prefetch(presentType, size, true);
    }

    @Nullable
    public static String prefetch(@NonNull IPresentType presentType, @NonNull Point size, boolean onWifiOnly) {
        if (onWifiOnly && !ConnectivityReceiver.isWifi) {
            return null;
        }
        if (!PresentSettingsHelper.isAnimatedPresentsEnabled()) {
            Logger.m176e("prefetch present sprite when it's disabled");
            return null;
        } else if (!presentType.isAnimated() || presentType.getSprites() == null || presentType.getSprites().isEmpty() || presentType.getAnimationProperties() == null) {
            return null;
        } else {
            PhotoSize sprite = PhotoUtil.getClosestSize(size.x, size.y, presentType.getSprites());
            if (sprite == null) {
                return null;
            }
            Sprites.prefetch(sprite.getUrl(), createSpriteMetadata(presentType.getAnimationProperties(), sprite.getWidth()));
            return sprite.getUrl();
        }
    }

    public static void prefetchSync(@NonNull IPresentType presentType, @NonNull Point size) {
        String prefetchUrl = prefetch(presentType, size, false);
        if (prefetchUrl != null) {
            Sprites.waitForAnimationPrepared(prefetchUrl, 15000);
        }
    }

    @NonNull
    public static SpriteMetadata createSpriteMetadata(@NonNull AnimationProperties animationProperties, int frameSize) {
        return new SpriteMetadata(frameSize, animationProperties.duration / animationProperties.framesCount, animationProperties.replayDelay);
    }
}
