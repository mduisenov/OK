package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;
import ru.ok.android.model.cache.ThumbnailHandler;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ViewTagUtils;
import ru.ok.android.utils.ViewTagUtils.ViewTagVisitor;

public class ImageHandler extends Handler {
    private final ContentHandler contentHandler;
    private final ContentResolver contentResolver;
    private final Context context;
    private volatile boolean isStopped;
    private final ViewTagVisitor removeThumbRequestCallbacks;
    private final MediaComposerStyleParams styleParams;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.adapter.ImageHandler.1 */
    class C06861 implements ViewTagVisitor {
        C06861() {
        }

        public void visitViewTag(View view, int key, Object tag) {
            ImageHandler.this.contentHandler.removeCallbacksAndMessages(tag);
            ImageHandler.this.removeCallbacksAndMessages(tag);
            view.setTag(key, null);
        }
    }

    class ContentHandler extends Handler {
        public ContentHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (!ImageHandler.this.isStopped && msg.what == 1) {
                ThumbLoadRequest request = msg.obj;
                try {
                    request.thumbBitmap = ThumbnailHandler.loadThumbnail(ImageHandler.this.contentResolver, request.imageUri, request.thumbWidthPx, 0, request.orientation);
                    ImageHandler.this.postSetBitmap(request);
                } catch (Throwable e) {
                    Logger.m177e("failed to load thumbnail for %s: %s", imageUri, e);
                    Logger.m178e(e);
                    ImageHandler.this.postLoadThumbFailed(request);
                }
            }
        }

        void postLoadThumbnail(ThumbLoadRequest req) {
            sendMessage(Message.obtain(this, 1, req));
        }
    }

    static class ThumbLoadRequest {
        final Uri imageUri;
        final int orientation;
        final ImageView targetImageView;
        Bitmap thumbBitmap;
        final int thumbWidthPx;

        ThumbLoadRequest(Uri imageUri, ImageView targetImageView, int orientation, int thumbWidthPx) {
            this.imageUri = imageUri;
            this.targetImageView = targetImageView;
            this.orientation = orientation;
            this.thumbWidthPx = thumbWidthPx;
        }
    }

    public ImageHandler(Context context, MediaComposerStyleParams styleParams) {
        this.isStopped = false;
        this.removeThumbRequestCallbacks = new C06861();
        this.context = context;
        this.contentResolver = context.getContentResolver();
        this.styleParams = styleParams;
        HandlerThread bgThread = new HandlerThread("MediaComposerThumbnails");
        bgThread.start();
        this.contentHandler = new ContentHandler(bgThread.getLooper());
    }

    public void stop() {
        this.isStopped = true;
        this.contentHandler.removeCallbacksAndMessages(null);
        this.contentHandler.getLooper().quit();
        removeCallbacksAndMessages(null);
    }

    public void handleMessage(Message msg) {
        if (!this.isStopped) {
            ThumbLoadRequest req = msg.obj;
            ImageView targetImageView = req != null ? req.targetImageView : null;
            if (targetImageView != null && targetImageView.getTag(2131624344) != req) {
                return;
            }
            if (msg.what == 1) {
                if (targetImageView != null && req.thumbBitmap != null) {
                    targetImageView.setImageBitmap(req.thumbBitmap);
                }
            } else if (msg.what == 2) {
                Toast.makeText(this.context, 2131166091, 1).show();
            }
        }
    }

    public void setImage(Uri imageUri, ImageView targetImageView, int orientation) {
        int targetWidth;
        int thumbWidth;
        LayoutParams lp = targetImageView.getLayoutParams();
        if (lp.width > 0) {
            targetWidth = lp.width;
        } else {
            targetWidth = 0;
        }
        if (targetWidth == 0) {
            int viewWidth = targetImageView.getWidth();
            if (viewWidth > 0) {
                targetWidth = viewWidth;
            }
        }
        if (targetWidth == 0 || targetWidth > this.styleParams.thumbnailWidth) {
            thumbWidth = this.styleParams.thumbnailWidth;
        } else {
            thumbWidth = targetWidth;
        }
        Logger.m173d("setImage: %s orientation=%d targetWidth=%d thumbWidth=%d", imageUri, Integer.valueOf(orientation), Integer.valueOf(targetWidth), Integer.valueOf(thumbWidth));
        ThumbLoadRequest req = new ThumbLoadRequest(imageUri, targetImageView, orientation, thumbWidth);
        targetImageView.setTag(2131624344, req);
        this.contentHandler.postLoadThumbnail(req);
    }

    void onViewRemoved(View childView) {
        ViewTagUtils.traverseViewTags(childView, 2131624344, this.removeThumbRequestCallbacks);
    }

    void postSetBitmap(ThumbLoadRequest req) {
        sendMessage(Message.obtain(this, 1, req));
    }

    void postLoadThumbFailed(ThumbLoadRequest req) {
        sendMessage(Message.obtain(this, 2, req));
    }
}
