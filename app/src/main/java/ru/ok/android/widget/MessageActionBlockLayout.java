package ru.ok.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MessageActionBlockLayout extends ViewGroup {
    public MessageActionBlockLayout(Context context) {
        super(context);
    }

    public MessageActionBlockLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageActionBlockLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onLayout(boolean b, int i1, int i2, int i3, int i4) {
        int size = getChildCount();
        if (size > 0) {
            View view = getChildAt(0);
            layout(getMeasuredWidth() - view.getMeasuredWidth(), 0, getMeasuredWidth(), view.getMeasuredHeight());
        }
        int currentRight = getMeasuredWidth();
        for (int i = size - 1; i > 0; i--) {
            getChildAt(i);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hFirst = 0;
        int hElse = 0;
        int wFirst = 0;
        int wElse = 0;
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != 8) {
                measureChild(view, widthMeasureSpec, heightMeasureSpec);
                if (i == 0) {
                    hFirst = view.getMeasuredHeight();
                    wFirst = view.getMeasuredWidth();
                } else {
                    hElse = Math.max(hElse, view.getMeasuredHeight());
                    wElse += view.getMeasuredWidth();
                }
            }
        }
        setMeasuredDimension(Math.max(wFirst, wElse), hElse + hFirst);
    }
}
