package ru.ok.android.ui.custom.profiles;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ReverseDrawingLinearLayout extends LinearLayout {
    public ReverseDrawingLinearLayout(Context context) {
        super(context);
        setChildrenDrawingOrderEnabled(true);
    }

    public ReverseDrawingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    public ReverseDrawingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setChildrenDrawingOrderEnabled(true);
    }

    @TargetApi(21)
    public ReverseDrawingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setChildrenDrawingOrderEnabled(true);
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        return (childCount - 1) - i;
    }
}
