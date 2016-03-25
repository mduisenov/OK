package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class FeedMessageTextView extends TextView {
    final FeedMessageSpanFormatter spanFormatter;

    public FeedMessageTextView(Context context) {
        this(context, null);
    }

    public FeedMessageTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 2131296529);
    }

    public FeedMessageTextView(Context context, AttributeSet attrs, int defAttrId, int defStyleId) {
        super(context, attrs, defAttrId);
        this.spanFormatter = new FeedMessageSpanFormatter(context, attrs, defStyleId);
        setTextAppearance(context, this.spanFormatter.getMessageTextAppearance());
    }

    public void setText(CharSequence text, BufferType type) {
        if (text instanceof Spannable) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append(text);
            this.spanFormatter.applyStyle((Spannable) text, sb, 0);
            text = sb;
        }
        super.setText(text, type);
    }
}
