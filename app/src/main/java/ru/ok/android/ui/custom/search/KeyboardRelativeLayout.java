package ru.ok.android.ui.custom.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import ru.ok.android.utils.DimenUtils;

public class KeyboardRelativeLayout extends RelativeLayout {
    private KeyboardListener keyboardListener;

    public interface KeyboardListener {
        void onKeyboardVisibilityChange(boolean z);
    }

    public KeyboardRelativeLayout(Context context) {
        super(context);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int currentHeight = getMeasuredHeight();
        if (!(this.keyboardListener == null || currentHeight == 0)) {
            int newHeight = MeasureSpec.getSize(heightMeasureSpec);
            if (Math.abs(newHeight - currentHeight) >= DimenUtils.getRealDisplayPixels(200, getContext())) {
                if (newHeight > currentHeight) {
                    this.keyboardListener.onKeyboardVisibilityChange(false);
                } else if (newHeight < currentHeight) {
                    this.keyboardListener.onKeyboardVisibilityChange(true);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setKeyboardListener(KeyboardListener keyboardListener) {
        this.keyboardListener = keyboardListener;
    }
}
