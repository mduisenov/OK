package ru.ok.android.ui.stream;

import android.graphics.Paint.FontMetricsInt;
import android.text.style.LineHeightSpan;

public class LineSpacingSpan implements LineHeightSpan {
    private final int height;

    public LineSpacingSpan(int height) {
        this.height = height;
    }

    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, FontMetricsInt fm) {
        fm.top -= this.height;
        fm.ascent -= this.height;
    }
}
