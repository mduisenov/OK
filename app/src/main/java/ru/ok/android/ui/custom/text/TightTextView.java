package ru.ok.android.ui.custom.text;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.TextView;

public final class TightTextView extends TextView {
    private boolean hasMaxWidth;

    public TightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.hasMaxWidth && MeasureSpec.getMode(widthMeasureSpec) != 1073741824) {
            Layout layout = getLayout();
            int linesCount = layout.getLineCount();
            if (linesCount > 1) {
                float textRealMaxWidth = 0.0f;
                for (int n = 0; n < linesCount; n++) {
                    textRealMaxWidth = Math.max(textRealMaxWidth, layout.getLineWidth(n));
                }
                int w = (int) Math.ceil((double) textRealMaxWidth);
                if (w < getMeasuredWidth()) {
                    super.onMeasure(MeasureSpec.makeMeasureSpec(w, LinearLayoutManager.INVALID_OFFSET), heightMeasureSpec);
                }
            }
        }
    }

    public void setMaxWidth(int maxpixels) {
        super.setMaxWidth(maxpixels);
        this.hasMaxWidth = maxpixels > 0;
    }
}
