package ru.ok.android.utils.image;

import android.graphics.drawable.Drawable;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.memory.PooledByteBuffer;
import com.facebook.imagepipeline.memory.PooledByteBufferInputStream;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.messaging.drawable.DrawableFactory;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;

public class SmilesLoader {
    private static SmilesLoader instance;
    private final DrawableFactory drawableFactory;

    private static class DrawableSubscriber extends BaseDataSubscriber<CloseableReference<PooledByteBuffer>> {
        private final DrawableFactory drawableFactory;
        private final AtomicReference<Drawable> resultTaker;

        private DrawableSubscriber(AtomicReference<Drawable> resultTaker, DrawableFactory drawableFactory) {
            this.resultTaker = resultTaker;
            this.drawableFactory = drawableFactory;
        }

        protected void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
            CloseableReference ref = (CloseableReference) dataSource.getResult();
            if (ref != null) {
                try {
                    this.resultTaker.set(this.drawableFactory.createDrawableFromStream(new PooledByteBufferInputStream((PooledByteBuffer) ref.get())));
                } catch (Throwable e) {
                    Logger.m178e(e);
                } finally {
                    CloseableReference.closeSafely(ref);
                }
            }
            synchronized (this.resultTaker) {
                this.resultTaker.notify();
            }
        }

        protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
            synchronized (this.resultTaker) {
                this.resultTaker.notify();
            }
        }
    }

    public SmilesLoader() {
        this.drawableFactory = new DrawableFactory(OdnoklassnikiApplication.getContext());
    }

    static {
        instance = new SmilesLoader();
    }

    public Drawable getDrawableByUrl(String url, int width) {
        AtomicReference<Drawable> result = new AtomicReference(null);
        Fresco.getImagePipeline().fetchEncodedImage(ImageRequest.fromUri(url), null).subscribe(new DrawableSubscriber(this.drawableFactory, null), ThreadUtil.executorService);
        return getResult(result);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable getResult(java.util.concurrent.atomic.AtomicReference<android.graphics.drawable.Drawable> r5) {
        /*
        r4 = this;
        monitor-enter(r5);
    L_0x0001:
        r1 = r5.get();	 Catch:{ all -> 0x0019 }
        if (r1 != 0) goto L_0x000c;
    L_0x0007:
        r2 = 30000; // 0x7530 float:4.2039E-41 double:1.4822E-319;
        r5.wait(r2);	 Catch:{ InterruptedException -> 0x0014 }
    L_0x000c:
        r1 = r5.get();	 Catch:{ all -> 0x0019 }
        r1 = (android.graphics.drawable.Drawable) r1;	 Catch:{ all -> 0x0019 }
        monitor-exit(r5);	 Catch:{ all -> 0x0019 }
        return r1;
    L_0x0014:
        r0 = move-exception;
        r0.printStackTrace();	 Catch:{ all -> 0x0019 }
        goto L_0x0001;
    L_0x0019:
        r1 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0019 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.utils.image.SmilesLoader.getResult(java.util.concurrent.atomic.AtomicReference):android.graphics.drawable.Drawable");
    }

    public static SmilesLoader getInstance() {
        return instance;
    }
}
