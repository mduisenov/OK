package ru.ok.android.videochat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import ru.ok.android.ui.custom.OnSizeChangedListener;

public final class RelativeLayoutWithSizeListener extends RelativeLayout {
    private OnSizeChangedListener listener;

    public RelativeLayoutWithSizeListener(Context context) {
        super(context);
    }

    public RelativeLayoutWithSizeListener(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayoutWithSizeListener(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.listener != null) {
            this.listener.onSizeChanged(w, h, oldw, oldh);
        }
    }

    public void setSizeChangeListener(OnSizeChangedListener listener) {
        this.listener = listener;
    }
}
