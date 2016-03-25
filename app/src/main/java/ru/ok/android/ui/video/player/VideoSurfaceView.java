package ru.ok.android.ui.video.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class VideoSurfaceView extends SurfaceView {
    private float videoAspectRatio;

    public VideoSurfaceView(Context context) {
        super(context);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVideoWidthHeightRatio(float widthHeightRatio) {
        if (this.videoAspectRatio != widthHeightRatio) {
            this.videoAspectRatio = widthHeightRatio;
            requestLayout();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.videoAspectRatio != 0.0f) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            if (this.videoAspectRatio > ((float) width) / ((float) height)) {
                height = (int) (((float) width) / this.videoAspectRatio);
            } else {
                width = (int) (this.videoAspectRatio * ((float) height));
            }
            setMeasuredDimension(width, height);
        }
    }
}
