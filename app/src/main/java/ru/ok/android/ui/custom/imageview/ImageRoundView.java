package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.request.ImageRequest;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.fresco.controller.RoundedDrawableFactory;

public class ImageRoundView extends UrlImageView {
    private RoundedDrawableFactory roundedDrawableFactory;
    private boolean shadow;
    private float stroke;

    public ImageRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageRequest(ImageRequest request) {
        setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) FrescoOdkl.newDraweeControllerBuilder(getDrawableFactory(), getContext()).setOldController(getController())).setImageRequest(request)).build());
    }

    public void setImageResource(int resId) {
        ((GenericDraweeHierarchy) getHierarchy()).setPlaceholderImage(getDrawableFactory().createDrawable(((BitmapDrawable) getResources().getDrawable(resId)).getBitmap()));
    }

    public void setStroke(float stroke) {
        if (this.stroke != stroke || this.shadow) {
            this.stroke = stroke;
            this.shadow = false;
            this.roundedDrawableFactory = null;
        }
    }

    public void setShadowStroke(float stroke) {
        if (this.stroke != stroke || !this.shadow) {
            this.stroke = stroke;
            this.shadow = true;
            this.roundedDrawableFactory = null;
            setLayerType(1, null);
        }
    }

    @NonNull
    private RoundedDrawableFactory getDrawableFactory() {
        if (this.roundedDrawableFactory == null) {
            this.roundedDrawableFactory = new RoundedDrawableFactory((int) this.stroke, this.shadow, ContextCompat.getColor(getContext(), 2131492919));
        }
        return this.roundedDrawableFactory;
    }
}
