package ru.ok.android.ui.custom.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LinearSetPressedLayout extends LinearLayout {
    public LinearSetPressedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void dispatchSetPressed(boolean pressed) {
        SetPressedUtils.dispatchSetPressed(this, pressed);
    }
}
