package ru.ok.android.ui.custom.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import ru.ok.android.C0206R;

public final class RelativeAlignWidthLayout extends RelativeLayout {
    private final int aligningId;
    private View aligningView;
    private final int anchorId;
    private View anchorView;

    public RelativeAlignWidthLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.AlignChildren);
        this.anchorId = a.getResourceId(0, 0);
        this.aligningId = a.getResourceId(1, 0);
        a.recycle();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.anchorView = findViewById(this.anchorId);
        this.aligningView = findViewById(this.aligningId);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.aligningView.layout(this.anchorView.getLeft(), this.aligningView.getTop(), this.anchorView.getRight(), this.aligningView.getBottom());
    }
}
