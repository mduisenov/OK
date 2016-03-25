package ru.ok.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.model.cache.ThumbnailHandler;
import ru.ok.android.services.app.notification.NotificationSignal;

public final class NotificationsUtils {
    public static Bitmap createThumbnailForLargeIcon(Context context, Uri imageUri, int rotation) {
        if (imageUri != null) {
            try {
                Resources res = context.getResources();
                return ThumbnailHandler.loadThumbnail(context.getContentResolver(), imageUri, res.getDimensionPixelSize(17104901), res.getDimensionPixelSize(17104902), rotation);
            } catch (Throwable e) {
                Logger.m184w("Failed to load thumbnail for notification: " + e);
                Logger.m178e(e);
            } catch (Throwable e2) {
                Logger.m184w("Failed to load thumbnail for notification: " + e2);
                Logger.m178e(e2);
            }
        }
        return null;
    }

    public static void hideNotificationForConversation(Context context, String conversationId) {
        if (!TextUtils.isEmpty(conversationId)) {
            NotificationSignal.hideNotification(context, conversationId, 0);
        }
    }
}
