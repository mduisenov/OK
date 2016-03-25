package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView.ScaleType;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.lang.ref.WeakReference;
import ru.ok.android.C0206R;
import ru.ok.android.fresco.DraweeHolderView;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.fresco.ResizeOptionsProvider;
import ru.ok.android.fresco.UriProvider;
import ru.ok.android.fresco.postprocessors.ImageRoundPostprocessor;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.image.upload.ResizeSettings;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.URLUtil;

@Deprecated
public class AsyncDraweeView extends DraweeHolderView implements ResizeOptionsProvider, UriProvider {
    private static final ScaleType[] imageViewScaleTypeArray;
    private final int bottomLeftCornerRadius;
    private final int bottomRightCornerRadius;
    private int emptyImageResId;
    private int errorImageResId;
    protected boolean isImageFromUri;
    private OnImageSetListener onImageSetListener;
    private ResizeOptions resizeOptions;
    private final int roundedMargin;
    private final RoundingType roundingType;
    private int stubImageResId;
    private final int topLeftCornerRadius;
    private final int topRightCornerRadius;
    private Uri uri;

    /* renamed from: ru.ok.android.ui.custom.imageview.AsyncDraweeView.1 */
    static /* synthetic */ class C06541 {
        static final /* synthetic */ int[] f96x90eabb0;

        static {
            f96x90eabb0 = new int[RoundingType.values().length];
            try {
                f96x90eabb0[RoundingType.CIRCLE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f96x90eabb0[RoundingType.ROUNDED_CORNERS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    static class ControllerCallback extends BaseControllerListener<ImageInfo> {
        private WeakReference<AsyncDraweeView> reference;

        public ControllerCallback(AsyncDraweeView view) {
            this.reference = new WeakReference(view);
        }

        public void onFailure(String id, Throwable throwable) {
            AsyncDraweeView view = (AsyncDraweeView) this.reference.get();
            if (view != null) {
                view.onImageFailed(true);
            }
        }

        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            AsyncDraweeView view = (AsyncDraweeView) this.reference.get();
            if (view != null) {
                if (view.onImageSetListener != null) {
                    view.onImageSetListener.onFinishedSetImage(view, true);
                }
                view.onImageLoaded(true);
            }
        }
    }

    public interface OnImageSetListener {
        void onFinishedSetImage(View view, boolean z);

        void onJustSetImage(AsyncDraweeView asyncDraweeView);
    }

    public enum RoundingType {
        NONE,
        SQUARED,
        ROUNDED,
        CIRCLE,
        ROUNDED_CORNERS;

        public static RoundingType fromInt(int ordinal) {
            for (RoundingType roundingType : values()) {
                if (roundingType.ordinal() == ordinal) {
                    return roundingType;
                }
            }
            return NONE;
        }
    }

    public ResizeOptions getResizeOptions() {
        return this.resizeOptions;
    }

    static {
        imageViewScaleTypeArray = new ScaleType[]{ScaleType.MATRIX, ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE};
    }

    public AsyncDraweeView(Context context) {
        this(context, null);
    }

    public AsyncDraweeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AsyncDraweeView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, 0, defStyle);
    }

    public AsyncDraweeView(Context context, AttributeSet attrs, int defAttr, int defStyle) {
        super(context, attrs, defAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.AsyncDraweeView, defAttr, defStyle);
        GenericDraweeHierarchyBuilder builder = GenericDraweeHierarchyBuilder.newInstance(getResources());
        this.stubImageResId = a.getResourceId(1, 0);
        this.emptyImageResId = a.getResourceId(2, 0);
        if (this.stubImageResId != 0) {
            builder.setPlaceholderImage(ResourcesCompat.getDrawable(getResources(), this.stubImageResId, context.getTheme()));
        }
        this.roundedMargin = a.getDimensionPixelOffset(5, 0);
        this.roundingType = RoundingType.fromInt(a.getInt(4, RoundingType.NONE.ordinal()));
        switch (C06541.f96x90eabb0[this.roundingType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                builder.setRoundingParams(RoundingParams.asCircle());
                this.bottomLeftCornerRadius = 0;
                this.bottomRightCornerRadius = 0;
                this.topRightCornerRadius = 0;
                this.topLeftCornerRadius = 0;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.topLeftCornerRadius = a.getDimensionPixelOffset(6, 0);
                this.topRightCornerRadius = a.getDimensionPixelOffset(6, 0);
                this.bottomRightCornerRadius = a.getDimensionPixelOffset(6, 0);
                this.bottomLeftCornerRadius = a.getDimensionPixelOffset(6, 0);
                builder.setRoundingParams(RoundingParams.fromCornersRadii((float) this.topLeftCornerRadius, (float) this.topRightCornerRadius, (float) this.bottomRightCornerRadius, (float) this.bottomLeftCornerRadius));
                break;
            default:
                this.bottomLeftCornerRadius = 0;
                this.bottomRightCornerRadius = 0;
                this.topRightCornerRadius = 0;
                this.topLeftCornerRadius = 0;
                break;
        }
        int scaleType = a.getInt(0, -1);
        if (scaleType >= 0) {
            builder.setActualImageScaleType(FrescoOdkl.convertScaleType(imageViewScaleTypeArray[scaleType]));
        }
        builder.setFadeDuration(a.getInteger(3, 0));
        setHolder(DraweeHolder.create(builder.build(), getContext()));
        ((GenericDraweeHierarchy) getHolder().getHierarchy()).getTopLevelDrawable().setCallback(this);
        a.recycle();
    }

    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        drawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        drawable.draw(canvas);
    }

    public void setOnImageSetListener(OnImageSetListener listener) {
        this.onImageSetListener = listener;
    }

    public void setLocalUri(Uri uri, int rotation) {
        ResizeSettings rs = new ResizeSettings(rotation);
        this.resizeOptions = new ResizeOptions(rs.getDesiredWidth(), rs.getDesiredHeight());
        setUriInvetrnal(uri, null);
    }

    public void setUri(Uri uri) {
        this.resizeOptions = null;
        setUriInvetrnal(uri, null);
    }

    public void setUri(Uri uri, Uri lowQualityUri) {
        this.resizeOptions = null;
        setUriInvetrnal(uri, lowQualityUri);
    }

    private void setUriInvetrnal(Uri uri, Uri lowQualityUri) {
        this.isImageFromUri = true;
        if (this.uri == null || !this.uri.equals(uri)) {
            boolean stub = uri == null || URLUtil.isStubUrl(uri.toString());
            if (stub) {
                uri = null;
                lowQualityUri = null;
            }
            if (stub && !(this.stubImageResId == 0 && this.emptyImageResId == 0)) {
                uri = FrescoOdkl.uriFromResId(this.stubImageResId != 0 ? this.stubImageResId : this.emptyImageResId);
            }
            this.uri = uri;
            if (uri == null || uri.equals(lowQualityUri)) {
                lowQualityUri = null;
            }
            ((GenericDraweeHierarchy) getHolder().getHierarchy()).setPlaceholderImage(applyRoundingLogic(this.emptyImageResId));
            ((GenericDraweeHierarchy) getHolder().getHierarchy()).setFailureImage(applyRoundingLogic(this.errorImageResId));
            getHolder().setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) getControllerBuilder().setRetainImageOnFailure(true)).setLowResImageRequest(buildImageRequest(lowQualityUri))).setImageRequest(buildImageRequest(uri))).setOldController(getHolder().getController())).build());
        } else if (this.onImageSetListener != null) {
            this.onImageSetListener.onJustSetImage(this);
            this.onImageSetListener.onFinishedSetImage(this, true);
        }
    }

    private Drawable applyRoundingLogic(int resId) {
        if (resId == 0) {
            return null;
        }
        Drawable drawable = getResources().getDrawable(resId);
        if (!(drawable instanceof BitmapDrawable)) {
            return drawable;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (this.roundingType == RoundingType.ROUNDED) {
            return new RoundedBitmapDrawable(bitmap, 0);
        }
        if (this.roundingType == RoundingType.ROUNDED_CORNERS) {
            return new RoundCornersBitmapDrawable(bitmap, (float) this.topLeftCornerRadius, (float) this.topRightCornerRadius, (float) this.bottomRightCornerRadius, (float) this.bottomLeftCornerRadius);
        }
        if (this.roundingType == RoundingType.CIRCLE) {
            return new CircleBitmapDrawable(bitmap, this.roundedMargin);
        }
        return new BitmapDrawable(bitmap);
    }

    private ImageRequest buildImageRequest(@Nullable Uri uri) {
        if (uri == null) {
            return null;
        }
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (this.resizeOptions != null) {
            builder.setResizeOptions(this.resizeOptions);
        }
        if (this.roundingType == RoundingType.ROUNDED) {
            builder.setPostprocessor(new ImageRoundPostprocessor(uri));
        }
        return builder.build();
    }

    public void setScaleType(ScaleType scaleType) {
        DraweeHolder<GenericDraweeHierarchy> holder = getHolder();
        if (holder != null) {
            ((GenericDraweeHierarchy) holder.getHierarchy()).setActualImageScaleType(FrescoOdkl.convertScaleType(scaleType));
        }
    }

    public void setImageDrawable(Drawable drawable) {
        ((GenericDraweeHierarchy) getHolder().getHierarchy()).setImage(drawable, 1.0f, true);
    }

    public void setImageResource(int resId) {
        this.isImageFromUri = false;
        setUri(FrescoOdkl.uriFromResId(resId));
    }

    private PipelineDraweeControllerBuilder getControllerBuilder() {
        return (PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setControllerListener(new ControllerCallback(this));
    }

    public void setImageResource(int resId, boolean animate) {
        setImageResource(resId);
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    protected int getContentWidth() {
        return (getMeasuredWidth() - getPaddingRight()) - getPaddingLeft();
    }

    protected int getContentHeight() {
        return (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
    }

    public void setErrorImageResId(int errorImageResId) {
        this.errorImageResId = errorImageResId;
        ((GenericDraweeHierarchy) getHolder().getHierarchy()).setFailureImage(applyRoundingLogic(errorImageResId));
    }

    public void setEmptyImageResId(int emptyImageResId) {
        this.emptyImageResId = emptyImageResId;
        ((GenericDraweeHierarchy) getHolder().getHierarchy()).setPlaceholderImage(applyRoundingLogic(emptyImageResId));
    }

    @Deprecated
    protected void onImageLoaded(boolean animate) {
    }

    protected void onImageFailed(boolean stub) {
    }

    public void setRoundCornersEnabled(boolean enabled) {
        if (enabled) {
            ((GenericDraweeHierarchy) getHolder().getHierarchy()).setRoundingParams(RoundingParams.fromCornersRadius((float) DimenUtils.getRealDisplayPixels(2, getContext())));
        } else {
            ((GenericDraweeHierarchy) getHolder().getHierarchy()).setRoundingParams(RoundingParams.fromCornersRadius(0.0f));
        }
    }

    public Uri getUri() {
        return this.uri;
    }
}
