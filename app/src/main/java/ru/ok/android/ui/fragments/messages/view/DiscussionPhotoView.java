package ru.ok.android.ui.fragments.messages.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import ru.ok.android.ui.custom.imageview.AspectRatioAsyncDraweeView;
import ru.ok.android.ui.custom.imageview.GifMarkerDrawableHelper;

public class DiscussionPhotoView extends AspectRatioAsyncDraweeView {
    private final GifMarkerDrawableHelper gifMarkerDrawableHelper;

    public DiscussionPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.gifMarkerDrawableHelper.drawGifMarkerIfNecessary(this, canvas);
    }

    public void setShouldDrawGifMarker(boolean shouldDrawGifMarker) {
        this.gifMarkerDrawableHelper.setShouldDrawGifMarker(shouldDrawGifMarker);
    }
}
