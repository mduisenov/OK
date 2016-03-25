package ru.ok.android.ui.custom.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FrameSetPressedLayout extends FrameLayout {
    public FrameSetPressedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void dispatchSetPressed(boolean pressed) {
        SetPressedUtils.dispatchSetPressed(this, pressed);
    }
}
