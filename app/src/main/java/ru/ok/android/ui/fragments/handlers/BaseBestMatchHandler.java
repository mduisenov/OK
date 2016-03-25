package ru.ok.android.ui.fragments.handlers;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.util.concurrent.ExecutorService;
import ru.ok.android.fresco.FrescoBackgroundRelativeLayout;
import ru.ok.android.fresco.postprocessors.ImageBlurPostprocessor;

abstract class BaseBestMatchHandler {
    private ExecutorService blurExecutor;
    private volatile boolean destroyed;
    private final Handler handler;
    protected FrescoBackgroundRelativeLayout mainView;

    /* renamed from: ru.ok.android.ui.fragments.handlers.BaseBestMatchHandler.1 */
    class C08181 extends Handler {
        C08181() {
        }

        public void handleMessage(Message msg) {
            if (!BaseBestMatchHandler.this.destroyed) {
                BaseBestMatchHandler.this.mainView.setBackgroundDrawable((Drawable) msg.obj);
            }
        }
    }

    BaseBestMatchHandler() {
        this.handler = new C08181();
    }

    public void onDestroyView() {
        this.destroyed = true;
        if (this.blurExecutor != null) {
            this.blurExecutor.shutdown();
            this.blurExecutor = null;
        }
        this.handler.removeCallbacksAndMessages(null);
    }

    public void blurBackground(Uri uri) {
        if (this.mainView != null) {
            this.mainView.setBackgroundController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setImageRequest(ImageRequestBuilder.newBuilderWithSource(uri).setPostprocessor(new ImageBlurPostprocessor(uri.toString(), 2, 2, MotionEventCompat.ACTION_MASK, 3)).build())).setOldController(this.mainView.getBackgroundController())).build());
        }
    }
}
