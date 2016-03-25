package ru.ok.android.ui.relations;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import ru.ok.android.C0206R;

public final class RelativeLayoutMaxWidth extends RelativeLayout {
    private int maxWidth;

    public RelativeLayoutMaxWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.RelativeLayoutMaxWidth);
        try {
            this.maxWidth = a.getDimensionPixelSize(0, 0);
        } finally {
            a.recycle();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(Math.min(this.maxWidth, MeasureSpec.getSize(widthMeasureSpec)), LinearLayoutManager.INVALID_OFFSET), heightMeasureSpec);
    }
}
