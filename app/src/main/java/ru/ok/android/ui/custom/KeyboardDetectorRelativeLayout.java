package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.Iterator;

public class KeyboardDetectorRelativeLayout extends RelativeLayout {
    private ArrayList<KeyboardChangedListener> keyboardListener;

    public interface KeyboardChangedListener {
        void onKeyboardHidden();

        void onKeyboardShown();
    }

    public KeyboardDetectorRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.keyboardListener = new ArrayList();
    }

    public KeyboardDetectorRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.keyboardListener = new ArrayList();
    }

    public KeyboardDetectorRelativeLayout(Context context) {
        super(context);
        this.keyboardListener = new ArrayList();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
        int actualHeight = getHeight();
        if (actualHeight > proposedheight) {
            notifyKeyboardShown();
        } else if (actualHeight < proposedheight) {
            notifyKeyboardHidden();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void notifyKeyboardHidden() {
        Iterator i$ = this.keyboardListener.iterator();
        while (i$.hasNext()) {
            ((KeyboardChangedListener) i$.next()).onKeyboardHidden();
        }
    }

    private void notifyKeyboardShown() {
        Iterator i$ = this.keyboardListener.iterator();
        while (i$.hasNext()) {
            ((KeyboardChangedListener) i$.next()).onKeyboardShown();
        }
    }
}
