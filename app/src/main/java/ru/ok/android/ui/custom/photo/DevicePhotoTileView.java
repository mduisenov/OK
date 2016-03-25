package ru.ok.android.ui.custom.photo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import ru.ok.android.fresco.FrescoGifMarkerView;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.image.pick.GalleryImageInfo;
import ru.ok.android.utils.MimeTypes;

public class DevicePhotoTileView extends FrescoGifMarkerView {
    private Paint checkBoxBackgroundPaint;
    private Drawable checkbox;
    private int checkboxPadding;
    private int errorImageResId;
    private boolean haveSelectedVisualFeedback;
    protected OnImageSelectionListener mOnImageSelectionListener;
    private GalleryImageInfo photo;
    private boolean selected;
    private Drawable selector;
    protected boolean touched;

    public interface OnImageSelectionListener {
        void onImageSelection(DevicePhotoTileView devicePhotoTileView, boolean z);
    }

    public DevicePhotoTileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.errorImageResId = -777;
        init(1);
    }

    public DevicePhotoTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.errorImageResId = -777;
        init(1);
    }

    public DevicePhotoTileView(Context context, int viewType) {
        super(context);
        this.errorImageResId = -777;
        init(viewType);
    }

    public void init(int viewType) {
        ScaleType scalingType;
        int padding = getResources().getDimensionPixelSize(2131231111);
        setPadding(padding, padding, padding, padding);
        callSetCropToPadding(true);
        if (viewType == 1) {
            scalingType = ScaleType.CENTER_CROP;
        } else {
            scalingType = ScaleType.CENTER_INSIDE;
        }
        setHierarchy(GenericDraweeHierarchyBuilder.newInstance(getResources()).setBackground(new ColorDrawable(getResources().getColor(2131493102))).setActualImageScaleType(scalingType).build());
        this.selector = new ColorDrawable(-1);
        this.checkbox = getResources().getDrawable(2130837786);
        this.checkBoxBackgroundPaint = new Paint();
        this.checkBoxBackgroundPaint.setColor(getResources().getColor(2131493205));
        this.checkboxPadding = getResources().getDimensionPixelSize(2131231110);
    }

    @SuppressLint({"NewApi"})
    private void callSetCropToPadding(boolean crop) {
        if (VERSION.SDK_INT >= 16) {
            setCropToPadding(true);
        }
    }

    public void setPhoto(GalleryImageInfo photo) {
        this.errorImageResId = -777;
        this.haveSelectedVisualFeedback = true;
        if (this.photo == null || !this.photo.uri.equals(photo.uri)) {
            setShouldDrawGifMarker(MimeTypes.isGif(photo.mimeType));
            this.photo = photo;
            setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(getController())).setImageRequest(ImageRequestBuilder.newBuilderWithSource(photo.uri).setResizeOptions(new ResizeOptions(320, 320)).build())).build());
            setAspectRatio(1.0f);
        }
    }

    public void setErrorImageResId(GalleryImageInfo photo, int errorImageResId) {
        this.photo = photo;
        this.haveSelectedVisualFeedback = false;
        if (this.errorImageResId == -777 || this.errorImageResId != errorImageResId) {
            this.errorImageResId = errorImageResId;
            setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(getController())).setImageRequest(ImageRequestBuilder.newBuilderWithResourceId(errorImageResId).setResizeOptions(new ResizeOptions(320, 320)).build())).build());
            setAspectRatio(1.0f);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSelection(canvas);
    }

    private void drawSelection(Canvas canvas) {
        if (this.haveSelectedVisualFeedback) {
            if (this.touched || this.selected) {
                this.selector.setBounds(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
                if (this.touched) {
                    this.selector.setAlpha(68);
                } else if (this.selected) {
                    this.selector.setAlpha(50);
                }
                this.selector.draw(canvas);
            }
            if (this.selected) {
                int top = getPaddingTop() + this.checkboxPadding;
                int right = (getMeasuredWidth() - getPaddingRight()) - this.checkboxPadding;
                int left = right - this.checkbox.getIntrinsicWidth();
                int bottom = top + this.checkbox.getIntrinsicHeight();
                canvas.drawRect((float) left, (float) top, (float) right, (float) bottom, this.checkBoxBackgroundPaint);
                this.checkbox.setBounds(left, top, right, bottom);
                this.checkbox.draw(canvas);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean z = false;
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                this.touched = true;
                invalidate();
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.touched = false;
                if (!this.selected) {
                    z = true;
                }
                this.selected = z;
                invalidate();
                if (this.mOnImageSelectionListener != null) {
                    this.mOnImageSelectionListener.onImageSelection(this, this.selected);
                    break;
                }
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (this.touched) {
                    this.touched = false;
                    invalidate();
                    break;
                }
                break;
        }
        return true;
    }

    public void setPhotoSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            invalidate();
        }
    }

    public void setOnImageSelectionListener(OnImageSelectionListener onImageSelectionListener) {
        this.mOnImageSelectionListener = onImageSelectionListener;
    }

    public GalleryImageInfo getPhoto() {
        return this.photo;
    }
}
