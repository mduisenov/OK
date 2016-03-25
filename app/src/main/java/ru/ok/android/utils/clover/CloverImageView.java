package ru.ok.android.utils.clover;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.drawee.view.MultiDraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest.ImageType;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.lang.ref.WeakReference;
import java.util.List;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.fresco.postprocessors.ImageRoundPostprocessor;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;
import ru.ok.android.utils.Logger;

public class CloverImageView extends View {
    private static Paint borderPaint;
    private static final SparseArray<RoundedBitmapDrawable> placeHoldersCache;
    private int allLeafLoadedBites;
    private CloverImageHandler cloverImageHandler;
    List<LeafInfo> currentLeafInfos;
    private int leafSize;
    private int loadedLeafBites;
    private final MultiDraweeHolder<GenericDraweeHierarchy> multiDraweeHolder;

    public interface CloverImageHandler {
        void consumeImage(Bitmap bitmap);

        int getSize();
    }

    private static class LeafImageLoadListener extends BaseControllerListener<ImageInfo> {
        private final int leafBite;
        private final WeakReference<CloverImageView> ref;

        /* renamed from: ru.ok.android.utils.clover.CloverImageView.LeafImageLoadListener.1 */
        class C14371 implements Runnable {
            final /* synthetic */ CloverImageHandler val$handler;
            final /* synthetic */ CloverImageView val$view;

            C14371(CloverImageHandler cloverImageHandler, CloverImageView cloverImageView) {
                this.val$handler = cloverImageHandler;
                this.val$view = cloverImageView;
            }

            public void run() {
                int size = this.val$handler.getSize();
                Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                this.val$view.customDraw(new Canvas(bitmap), size, size);
                this.val$handler.consumeImage(bitmap);
            }
        }

        private LeafImageLoadListener(CloverImageView cloverImageView, int leafBite) {
            this.ref = new WeakReference(cloverImageView);
            this.leafBite = leafBite;
        }

        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            CloverImageView view = (CloverImageView) this.ref.get();
            if (view == null) {
                Logger.m172d("Skip");
                return;
            }
            CloverImageView.access$176(view, this.leafBite);
            if (view.loadedLeafBites == view.allLeafLoadedBites) {
                CloverImageHandler handler = view.cloverImageHandler;
                if (handler != null) {
                    view.post(new C14371(handler, view));
                } else {
                    view.invalidate();
                }
            }
        }
    }

    public static class LeafInfo {
        @DrawableRes
        public final int placeholderId;
        @NonNull
        public final Uri uri;

        public LeafInfo(@NonNull Uri uri, @DrawableRes int placeholderId) {
            this.uri = uri;
            this.placeholderId = placeholderId;
        }
    }

    static /* synthetic */ int access$176(CloverImageView x0, int x1) {
        int i = x0.loadedLeafBites | x1;
        x0.loadedLeafBites = i;
        return i;
    }

    static {
        placeHoldersCache = new SparseArray();
    }

    public CloverImageView(Context context) {
        super(context);
        this.multiDraweeHolder = new MultiDraweeHolder();
        this.allLeafLoadedBites = 0;
        this.loadedLeafBites = 0;
    }

    public CloverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.multiDraweeHolder = new MultiDraweeHolder();
        this.allLeafLoadedBites = 0;
        this.loadedLeafBites = 0;
    }

    public CloverImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.multiDraweeHolder = new MultiDraweeHolder();
        this.allLeafLoadedBites = 0;
        this.loadedLeafBites = 0;
    }

    public void setLeafSize(int leafSize) {
        this.leafSize = leafSize;
    }

    public void setLeaves(@NonNull List<LeafInfo> leafInfos) {
        if (leafInfos.size() > 4) {
            throw new IllegalArgumentException("Support only 0, 1, 2, 3 or 4 images. Received: " + leafInfos.size());
        } else if (this.currentLeafInfos == null || leafInfos == null || this.currentLeafInfos.size() != leafInfos.size() || 0 >= this.currentLeafInfos.size() || ((LeafInfo) leafInfos.get(0)).placeholderId != ((LeafInfo) this.currentLeafInfos.get(0)).placeholderId || !((LeafInfo) leafInfos.get(0)).uri.equals(((LeafInfo) this.currentLeafInfos.get(0)).uri)) {
            this.currentLeafInfos = leafInfos;
            this.allLeafLoadedBites = (1 << leafInfos.size()) - 1;
            this.loadedLeafBites = 0;
            this.multiDraweeHolder.clear();
            int leafNumber = 0;
            boolean drawBorder = leafInfos.size() != 1;
            if (drawBorder && borderPaint == null) {
                borderPaint = ImageRoundPostprocessor.createBorderPaint();
            }
            for (LeafInfo leafInfo : leafInfos) {
                RoundedBitmapDrawable placeholder = getPlaceholder(leafInfo.placeholderId, drawBorder);
                if (drawBorder) {
                    placeholder.setStroke(borderPaint.getStrokeWidth(), borderPaint.getColor());
                } else {
                    placeholder.setStroke(0.0f, 0);
                }
                DraweeHolder<GenericDraweeHierarchy> holder = FrescoOdkl.createAvatarDraweeHolder(getContext(), placeholder);
                ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(leafInfo.uri).setPostprocessor(new ImageRoundPostprocessor(leafInfo.uri, drawBorder)).setImageType(ImageType.SMALL);
                if (this.leafSize != 0) {
                    int size = (int) getOneBitmapDesiredSize(this.leafSize, this.leafSize, leafInfos.size());
                    Logger.m173d("Clover size: %d", Integer.valueOf(size));
                    builder.setResizeOptions(new ResizeOptions(size, size));
                }
                int leafNumber2 = leafNumber + 1;
                holder.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setControllerListener(new LeafImageLoadListener(1 << leafNumber, null))).setImageRequest(builder.build())).build());
                this.multiDraweeHolder.add(holder);
                holder.getTopLevelDrawable().setCallback(this);
                leafNumber = leafNumber2;
            }
        }
    }

    private RoundedBitmapDrawable getPlaceholder(@DrawableRes int placeholderId, boolean drawBorder) {
        int key = drawBorder ? placeholderId : -placeholderId;
        RoundedBitmapDrawable drawable = (RoundedBitmapDrawable) placeHoldersCache.get(key);
        if (drawable != null) {
            return drawable;
        }
        drawable = new RoundedBitmapDrawable(((BitmapDrawable) getResources().getDrawable(placeholderId)).getBitmap(), 0);
        placeHoldersCache.put(key, drawable);
        return drawable;
    }

    protected boolean verifyDrawable(Drawable who) {
        return this.multiDraweeHolder.verifyDrawable(who) || super.verifyDrawable(who);
    }

    public void onDraw(Canvas canvas) {
        if (this.leafSize != 0) {
            canvas.save();
            canvas.translate((float) getPaddingLeft(), (float) getPaddingTop());
            customDraw(canvas, this.leafSize, this.leafSize);
            canvas.restore();
            return;
        }
        customDraw(canvas, getWidth(), getHeight());
    }

    private void customDraw(Canvas canvas, int width, int height) {
        switch (this.multiDraweeHolder.size()) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                draw1(canvas, width, height);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                draw2(canvas, width, height);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                draw3(canvas, width, height);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                draw4(canvas, width, height);
            default:
        }
    }

    private static float getScaleFactor(int count) {
        switch (count) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return 1.0f;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 1.5f;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 1.87f;
            default:
                return 1.8f;
        }
    }

    private void draw1(Canvas canvas, int width, int height) {
        Drawable drawable = getLeafDrawable(0);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
    }

    private Drawable getLeafDrawable(int number) {
        return this.multiDraweeHolder.get(number).getTopLevelDrawable();
    }

    private void draw2(Canvas canvas, int width, int height) {
        Drawable drawable1 = getLeafDrawable(0);
        Drawable drawable2 = getLeafDrawable(1);
        int desiredSize = (int) getOneBitmapDesiredSize(width, height, 2);
        drawable1.setBounds(0, 0, desiredSize, desiredSize);
        drawable1.draw(canvas);
        drawable2.setBounds((width - desiredSize) - 1, (height - desiredSize) - 1, width - 1, height - 1);
        drawable2.draw(canvas);
    }

    private void draw3(Canvas canvas, int width, int height) {
        Drawable drawable1 = getLeafDrawable(0);
        Drawable drawable2 = getLeafDrawable(1);
        Drawable drawable3 = getLeafDrawable(2);
        int desiredSize = (int) getOneBitmapDesiredSize(width, height, 3);
        drawable1.setBounds(0, 0, desiredSize, desiredSize);
        drawable1.draw(canvas);
        drawable2.setBounds(0, (height - desiredSize) - 1, desiredSize, height - 1);
        drawable2.draw(canvas);
        drawable3.setBounds((width - desiredSize) - 1, (height - desiredSize) / 2, width - 1, (height + desiredSize) / 2);
        drawable3.draw(canvas);
    }

    private void draw4(Canvas canvas, int width, int height) {
        Drawable drawable1 = getLeafDrawable(0);
        Drawable drawable2 = getLeafDrawable(1);
        Drawable drawable3 = getLeafDrawable(2);
        Drawable drawable4 = getLeafDrawable(3);
        int desiredSize = (int) getOneBitmapDesiredSize(width, height, 4);
        drawable1.setBounds(0, 0, desiredSize, desiredSize);
        drawable1.draw(canvas);
        drawable2.setBounds(0, (height - desiredSize) - 1, desiredSize, height - 1);
        drawable2.draw(canvas);
        drawable3.setBounds((width - desiredSize) - 1, 0, width - 1, desiredSize);
        drawable3.draw(canvas);
        drawable4.setBounds((width - desiredSize) - 1, (height - desiredSize) - 1, width - 1, height - 1);
        drawable4.draw(canvas);
    }

    public void setCloverImageHandler(CloverImageHandler cloverImageHandler) {
        this.cloverImageHandler = cloverImageHandler;
    }

    private static float getOneBitmapDesiredSize(int width, int height, int bitmapsCount) {
        float scaleFactor = getScaleFactor(bitmapsCount);
        return Math.min(((float) width) / scaleFactor, ((float) height) / scaleFactor);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.multiDraweeHolder != null) {
            this.multiDraweeHolder.onDetach();
        }
    }

    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (this.multiDraweeHolder != null) {
            this.multiDraweeHolder.onDetach();
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.multiDraweeHolder != null) {
            this.multiDraweeHolder.onAttach();
        }
    }

    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (this.multiDraweeHolder != null) {
            this.multiDraweeHolder.onAttach();
        }
    }
}
