package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.View;

public class GifMarkerDrawableHelper {
    private boolean drawGifMarker;
    private GifMarkerDrawable gifMarkerDrawable;

    public void drawGifMarkerIfNecessary(@NonNull View view, @NonNull Canvas canvas) {
        if (this.drawGifMarker) {
            initGifMarkerDrawableIfNecessary(view.getContext());
            GifMarkerDrawable.draw(this.gifMarkerDrawable, view, canvas);
        }
    }

    private void initGifMarkerDrawableIfNecessary(@NonNull Context context) {
        if (this.gifMarkerDrawable == null) {
            this.gifMarkerDrawable = new GifMarkerDrawable(context);
        }
    }

    public void setShouldDrawGifMarker(boolean shouldDrawGifMarker) {
        this.drawGifMarker = shouldDrawGifMarker;
    }
}
