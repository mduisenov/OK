package ru.ok.android.ui.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.widget.CheckBox;

public final class CheckBoxAlphaView extends CheckBox {
    public CheckBoxAlphaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(enabled ? MotionEventCompat.ACTION_MASK : 100);
        }
    }
}
