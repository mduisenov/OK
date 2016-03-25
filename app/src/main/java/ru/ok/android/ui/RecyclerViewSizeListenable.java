package ru.ok.android.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import ru.ok.android.ui.custom.OnSizeChangedListener;

public class RecyclerViewSizeListenable extends RecyclerView {
    private int layoutHeight;
    private int layoutWidth;
    private OnSizeChangedListener onSizeChangedListener;

    public RecyclerViewSizeListenable(Context context) {
        super(context);
    }

    public RecyclerViewSizeListenable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewSizeListenable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        if (!(width == this.layoutWidth && height == this.layoutHeight)) {
            if (this.onSizeChangedListener != null) {
                this.onSizeChangedListener.onSizeChanged(width, height, this.layoutWidth, this.layoutHeight);
            }
            this.layoutWidth = width;
            this.layoutHeight = height;
        }
        super.onLayout(changed, l, t, r, b);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        this.onSizeChangedListener = listener;
    }
}
