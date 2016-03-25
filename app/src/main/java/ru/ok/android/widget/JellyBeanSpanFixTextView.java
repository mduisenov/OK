package ru.ok.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.graylog.GrayLog;

@SuppressLint({"WrongCall"})
public class JellyBeanSpanFixTextView extends TextView {
    private static final String TAG;

    private static class FixingResult {
        public final boolean fixed;
        public final List<Object> spansWithSpacesAfter;
        public final List<Object> spansWithSpacesBefore;

        public static FixingResult fixed(List<Object> spansWithSpacesBefore, List<Object> spansWithSpacesAfter) {
            return new FixingResult(true, spansWithSpacesBefore, spansWithSpacesAfter);
        }

        public static FixingResult notFixed() {
            return new FixingResult(false, null, null);
        }

        private FixingResult(boolean fixed, List<Object> spansWithSpacesBefore, List<Object> spansWithSpacesAfter) {
            this.fixed = fixed;
            this.spansWithSpacesBefore = spansWithSpacesBefore;
            this.spansWithSpacesAfter = spansWithSpacesAfter;
        }
    }

    static {
        TAG = JellyBeanSpanFixTextView.class.getSimpleName();
    }

    public JellyBeanSpanFixTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public JellyBeanSpanFixTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JellyBeanSpanFixTextView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (IndexOutOfBoundsException e) {
            fixOnMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void fixOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence text = getText();
        if (text instanceof Spanned) {
            fixSpannedWithSpaces(new SpannableStringBuilder(text), widthMeasureSpec, heightMeasureSpec);
        } else {
            fallbackToString(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void fixSpannedWithSpaces(SpannableStringBuilder builder, int widthMeasureSpec, int heightMeasureSpec) {
        long startFix = System.currentTimeMillis();
        FixingResult result = addSpacesAroundSpansUntilFixed(builder, widthMeasureSpec, heightMeasureSpec);
        if (result.fixed) {
            removeUnneededSpaces(widthMeasureSpec, heightMeasureSpec, builder, result);
        } else {
            fallbackToString(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private FixingResult addSpacesAroundSpansUntilFixed(SpannableStringBuilder builder, int widthMeasureSpec, int heightMeasureSpec) {
        Object[] spans = builder.getSpans(0, builder.length(), Object.class);
        List<Object> spansWithSpacesBefore = new ArrayList(spans.length);
        List<Object> spansWithSpacesAfter = new ArrayList(spans.length);
        Object[] arr$ = spans;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Object span = arr$[i$];
            int spanStart = builder.getSpanStart(span);
            if (isNotSpace(builder, spanStart - 1)) {
                builder.insert(spanStart, " ");
                spansWithSpacesBefore.add(span);
            }
            int spanEnd = builder.getSpanEnd(span);
            if (isNotSpace(builder, spanEnd)) {
                builder.insert(spanEnd, " ");
                spansWithSpacesAfter.add(span);
            }
            try {
                setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec);
                return FixingResult.fixed(spansWithSpacesBefore, spansWithSpacesAfter);
            } catch (IndexOutOfBoundsException e) {
                i$++;
            }
        }
        return FixingResult.notFixed();
    }

    private boolean isNotSpace(CharSequence text, int where) {
        if (text == null || where < 0 || where >= text.length() || text.charAt(where) == ' ') {
            return false;
        }
        return true;
    }

    private void setTextAndMeasure(CharSequence text, int widthMeasureSpec, int heightMeasureSpec) {
        setText(text);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void removeUnneededSpaces(int widthMeasureSpec, int heightMeasureSpec, SpannableStringBuilder builder, FixingResult result) {
        if (result.spansWithSpacesAfter != null) {
            for (Object span : result.spansWithSpacesAfter) {
                if (span != null) {
                    int spanEnd = builder.getSpanEnd(span);
                    builder.delete(spanEnd, spanEnd + 1);
                    try {
                        setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec);
                    } catch (IndexOutOfBoundsException e) {
                        builder.insert(spanEnd, " ");
                    }
                }
            }
            boolean needReset = true;
            for (Object span2 : result.spansWithSpacesBefore) {
                if (span2 != null) {
                    int spanStart = builder.getSpanStart(span2);
                    builder.delete(spanStart - 1, spanStart);
                    try {
                        setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec);
                        needReset = false;
                    } catch (IndexOutOfBoundsException e2) {
                        needReset = true;
                        builder.insert(spanStart - 1, " ");
                    }
                }
            }
            if (needReset) {
                setText(builder);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    private void fallbackToString(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            setTextAndMeasure(getText().toString(), widthMeasureSpec, heightMeasureSpec);
        } catch (IndexOutOfBoundsException e) {
            GrayLog.log(String.format("Fallback to string '%s' failed. Original text class is %s.", new Object[]{fallbackText, getText().getClass()}), e);
            setTextAndMeasure("", widthMeasureSpec, heightMeasureSpec);
        }
    }
}
