package ru.ok.android.ui.custom;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ButtonBar extends LinearLayout {
    public ButtonBar(Context context) {
        super(context);
    }

    public ButtonBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public View getChildAt(int index) {
        if (VERSION.SDK_INT >= 14) {
            return super.getChildAt((getChildCount() - 1) - index);
        }
        return super.getChildAt(index);
    }
}
