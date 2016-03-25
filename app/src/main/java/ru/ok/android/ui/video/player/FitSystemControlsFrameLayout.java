package ru.ok.android.ui.video.player;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FitSystemControlsFrameLayout extends FrameLayout {
    public FitSystemControlsFrameLayout(Context context) {
        super(context);
    }

    public FitSystemControlsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitSystemControlsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected boolean fitSystemWindows(Rect insets) {
        setPadding(insets.left, insets.top, insets.right, insets.bottom);
        return false;
    }
}
