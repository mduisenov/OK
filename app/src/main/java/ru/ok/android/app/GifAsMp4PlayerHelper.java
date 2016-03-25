package ru.ok.android.app;

import android.support.annotation.NonNull;
import bo.pic.android.media.ImageLoader;
import bo.pic.android.media.ImageLoader.LoadHandle;
import bo.pic.android.media.view.AnimatedMediaContentView;
import ru.ok.android.services.processors.settings.GifSettings;
import ru.ok.model.photo.HasMp4;

public class GifAsMp4PlayerHelper {

    public enum AutoplayContext {
        FEED {
            protected boolean shouldPlayInPlace() {
                return GifSettings.isAutoPlay();
            }
        },
        CONVERSATION {
            protected boolean shouldPlayInPlace() {
                return GifSettings.isAutoPlayInConversation();
            }
        };

        protected abstract boolean shouldPlayInPlace();
    }

    public static void resetAndStopPlaying(@NonNull AnimatedMediaContentView mediaContentView) {
        LoadHandle handle = (LoadHandle) ImageLoader.LOAD_HANDLE_KEY.get(mediaContentView.getAdditionalData());
        if (handle != null) {
            handle.cancelDownloadRequest();
        }
        mediaContentView.stopDrawing();
        mediaContentView.setMediaContent(null, false);
    }

    public static boolean shouldPlayGifAsMp4InPlace(@NonNull HasMp4 image, @NonNull AutoplayContext autoplayContext) {
        return shouldShowGifAsMp4(image) && autoplayContext.shouldPlayInPlace();
    }

    public static boolean shouldShowGifAsMp4(@NonNull HasMp4 image) {
        return GifSettings.isGifEnabled() && image.hasMp4();
    }
}
