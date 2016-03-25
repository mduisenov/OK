package ru.ok.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.C0000R;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class ScrimInsetsListView extends ListView implements OnApplyWindowInsetsListener {
    private Drawable mInsetForeground;
    private Rect mInsets;

    public ScrimInsetsListView(Context context) {
        this(context, null);
    }

    public ScrimInsetsListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrimInsetsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, C0000R.styleable.ScrimInsetsFrameLayout, defStyleAttr, C0000R.style.Widget_Design_ScrimInsetsFrameLayout);
        this.mInsetForeground = a.getDrawable(0);
        a.recycle();
        setWillNotDraw(true);
        ViewCompat.setOnApplyWindowInsetsListener(this, this);
    }

    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        if (this.mInsets == null) {
            this.mInsets = new Rect();
        }
        this.mInsets.set(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
        boolean z = this.mInsets.isEmpty() || this.mInsetForeground == null;
        setWillNotDraw(z);
        ViewCompat.postInvalidateOnAnimation(this);
        return insets.consumeSystemWindowInsets();
    }

    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        if (this.mInsets != null && this.mInsetForeground != null) {
            int saveCount = canvas.save();
            canvas.translate((float) getScrollX(), (float) getScrollY());
            int width = getWidth();
            int height = getHeight();
            this.mInsetForeground.setBounds(0, 0, width, this.mInsets.top);
            this.mInsetForeground.draw(canvas);
            this.mInsetForeground.setBounds(0, height - this.mInsets.bottom, width, height);
            this.mInsetForeground.draw(canvas);
            this.mInsetForeground.setBounds(0, this.mInsets.top, this.mInsets.left, height - this.mInsets.bottom);
            this.mInsetForeground.draw(canvas);
            this.mInsetForeground.setBounds(width - this.mInsets.right, this.mInsets.top, width, height - this.mInsets.bottom);
            this.mInsetForeground.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mInsetForeground != null) {
            this.mInsetForeground.setCallback(this);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mInsetForeground != null) {
            this.mInsetForeground.setCallback(null);
        }
    }
}
