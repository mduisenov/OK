package ru.ok.android.ui.custom.transform.overlay;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class TransformOverlayView extends View {
    public TransformOverlayView(Context context) {
        super(context);
        onCreate();
    }

    public TransformOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public TransformOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate();
    }

    private void onCreate() {
    }
}
