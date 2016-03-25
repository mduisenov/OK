package ru.ok.android.services.processors.notification.tasks;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import ru.ok.android.services.app.notification.NotificationSignal;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;

public abstract class LoadNotificationIconTask implements Runnable {
    private NotificationSignal notificationSignal;

    /* renamed from: ru.ok.android.services.processors.notification.tasks.LoadNotificationIconTask.1 */
    class C04831 extends BaseBitmapDataSubscriber {
        C04831() {
        }

        protected void onNewResultImpl(@Nullable Bitmap bitmap) {
            LoadNotificationIconTask.this.onNotificationIconLoaded(bitmap);
        }

        protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            LoadNotificationIconTask.this.onNotificationIconLoaded(null);
        }
    }

    @Nullable
    public abstract Uri getNotificationIconUri() throws Exception;

    public LoadNotificationIconTask(@NonNull NotificationSignal notificationSignal) {
        this.notificationSignal = notificationSignal;
    }

    public void run() {
        try {
            Uri uri = getNotificationIconUri();
            if (uri == null) {
                onNotificationIconLoaded(null);
            }
            Fresco.getImagePipeline().fetchDecodedImage(ImageRequest.fromUri(uri), null).subscribe(new C04831(), ThreadUtil.executorService);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    protected void onNotificationIconLoaded(@Nullable Bitmap bitmap) {
        if (bitmap != null) {
            this.notificationSignal.setLargeIcon(bitmap);
        }
        this.notificationSignal.performNotification();
    }
}
