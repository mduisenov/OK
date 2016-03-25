package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public abstract class DrawListenedView extends View {
    private OnDrawListener onDrawListener;

    public interface OnDrawListener {
        void onPostDraw(Canvas canvas);

        void onPreDraw(Canvas canvas);
    }

    protected abstract void doDraw(Canvas canvas);

    public DrawListenedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawListenedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawListenedView(Context context) {
        super(context);
    }

    public void setOnDrawListener(OnDrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }

    protected final void onDraw(Canvas canvas) {
        if (this.onDrawListener != null) {
            this.onDrawListener.onPreDraw(canvas);
        }
        super.onDraw(canvas);
        doDraw(canvas);
        if (this.onDrawListener != null) {
            this.onDrawListener.onPostDraw(canvas);
        }
    }
}
