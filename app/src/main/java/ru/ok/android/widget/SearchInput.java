package ru.ok.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;

public class SearchInput extends EditText {
    private int paddingLeft;

    public SearchInput(Context context) {
        super(context);
        this.paddingLeft = -1;
    }

    public SearchInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paddingLeft = -1;
    }

    public SearchInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.paddingLeft = -1;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        boolean selected = false;
        for (int i : getDrawableState()) {
            switch (i) {
                case 16842908:
                    selected = true;
                    break;
                case 16842919:
                    selected = true;
                    break;
                default:
                    break;
            }
        }
        ViewGroup layout = (ViewGroup) getParent();
        if (this.paddingLeft == -1) {
            this.paddingLeft = layout.getPaddingLeft();
        }
        if (layout != null) {
            layout.setSelected(selected);
            int paddingTop = layout.getPaddingTop();
            int paddingBottom = layout.getPaddingBottom();
            int paddingRight = layout.getPaddingRight();
            if (selected) {
                layout.setPadding(this.paddingLeft / 3, paddingTop, paddingRight, paddingBottom);
            } else {
                layout.setPadding(this.paddingLeft, paddingTop, paddingRight, paddingBottom);
            }
        }
    }
}
