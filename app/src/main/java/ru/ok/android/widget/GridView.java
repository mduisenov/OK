package ru.ok.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import ru.ok.android.utils.DeviceUtils;

public class GridView extends LinearLayout {
    private boolean isBottomPadding;
    private boolean isTopPadding;
    private int padding;

    public GridView(Context context) {
        super(context);
        init(context);
    }

    public GridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(11)
    public GridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.padding = context.getResources().getDimensionPixelSize(2131231007);
    }

    public static int getCountColumns(Context context) {
        return DeviceUtils.isPortrait(context) ? 3 : 5;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int columnCount = getCountColumns(getContext());
        int widthBlock = ((r - l) - (this.padding * 2)) / columnCount;
        int height = b - t;
        for (int i = 0; i < columnCount; i++) {
            if (getChildAt(i) != null) {
                if (getChildAt(i).getVisibility() == 8) {
                    getChildAt(i).layout(0, 0, 0, 0);
                } else {
                    getChildAt(i).layout(this.padding + (i * widthBlock), this.isTopPadding ? this.padding : 0, ((i + 1) * widthBlock) + this.padding, height - (this.isBottomPadding ? this.padding : 0));
                }
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int i2 = 0;
        int columnCount = getCountColumns(getContext());
        int width = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        int widthBlock = (width - (this.padding * 2)) / columnCount;
        int height = 0;
        for (int i3 = 0; i3 < columnCount; i3++) {
            if (getChildAt(i3) != null) {
                getChildAt(i3).measure(MeasureSpec.makeMeasureSpec(widthBlock, 1073741824), heightMeasureSpec);
                height = Math.max(getChildAt(0).getMeasuredHeight(), height);
            }
        }
        height = resolveSize(height, heightMeasureSpec);
        if (this.isTopPadding) {
            i = this.padding;
        } else {
            i = 0;
        }
        i += height;
        if (this.isBottomPadding) {
            i2 = this.padding;
        }
        setMeasuredDimension(width, i + i2);
    }

    public void setBottomPadding(boolean isBottomPadding) {
        this.isBottomPadding = isBottomPadding;
    }

    public void setTopPadding(boolean isTopPadding) {
        this.isTopPadding = isTopPadding;
    }
}
