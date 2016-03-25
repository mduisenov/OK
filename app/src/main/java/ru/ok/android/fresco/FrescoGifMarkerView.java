package ru.ok.android.fresco;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.AttributeSet;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import ru.ok.android.ui.custom.imageview.GifMarkerDrawableHelper;

public class FrescoGifMarkerView extends FrescoMaxWidthView implements UriProvider {
    private final GifMarkerDrawableHelper gifMarkerDrawableHelper;
    private Uri uri;

    public FrescoGifMarkerView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
    }

    public FrescoGifMarkerView(Context context) {
        super(context);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
    }

    public FrescoGifMarkerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
    }

    public FrescoGifMarkerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.gifMarkerDrawableHelper.drawGifMarkerIfNecessary(this, canvas);
    }

    public void setShouldDrawGifMarker(boolean shouldDrawGifMarker) {
        this.gifMarkerDrawableHelper.setShouldDrawGifMarker(shouldDrawGifMarker);
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return this.uri;
    }
}
