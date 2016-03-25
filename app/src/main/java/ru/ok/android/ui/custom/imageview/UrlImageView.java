package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView.ScaleType;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.fresco.UriProvider;

public class UrlImageView extends SimpleDraweeView implements UriProvider {
    private String url;

    public UrlImageView(Context context) {
        super(context);
    }

    public UrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UrlImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void setUri(Uri uri) {
        this.url = uri != null ? uri.toString() : null;
        setImageRequest(ImageRequest.fromUri(uri));
    }

    @Deprecated
    public void setUrl(String url) {
        setUri(url != null ? Uri.parse(url) : null);
    }

    public String getImageUrl() {
        return this.url;
    }

    public void setImageRequest(ImageRequest request) {
        setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setImageRequest(request)).setOldController(getController())).build());
    }

    public void setImageResource(int resId) {
        setUri(FrescoOdkl.uriFromResId(resId));
    }

    @Deprecated
    public void setScaleType(ScaleType scaleType) {
        ((GenericDraweeHierarchy) getHierarchy()).setActualImageScaleType(FrescoOdkl.convertScaleType(scaleType));
    }

    public boolean equalsUrl(String eUrl) {
        if (getImageUrl() == null || eUrl == null) {
            return false;
        }
        return getImageUrl().equals(eUrl);
    }

    public void setIsAlpha(boolean isAlpha) {
        ((GenericDraweeHierarchy) getHierarchy()).setFadeDuration(isAlpha ? 400 : 0);
    }

    public void setMeasuredDimensionExposed(int width, int height) {
        setMeasuredDimension(width, height);
    }

    public void setPlaceholderResource(int placeholderResId) {
        ((GenericDraweeHierarchy) getHierarchy()).setPlaceholderImage(placeholderResId);
    }

    public Uri getUri() {
        return this.url == null ? null : Uri.parse(this.url);
    }
}
