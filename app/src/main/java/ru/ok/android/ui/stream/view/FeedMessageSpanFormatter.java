package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import ru.ok.android.C0206R;
import ru.ok.model.stream.message.FeedActorSpan;
import ru.ok.model.stream.message.FeedMessageSpan;
import ru.ok.model.stream.message.FeedTargetAppSpan;
import ru.ok.model.stream.message.FeedTargetGroupSpan;
import ru.ok.model.stream.message.FeedTargetSpan;

public class FeedMessageSpanFormatter {
    private final int actorTextAppearance;
    private final Context context;
    private final int messageTextAppearance;
    private final int targetAppTextAppearance;
    private final int targetGroupTextAppearance;
    private final int targetTextAppearance;

    public FeedMessageSpanFormatter(Context context, AttributeSet attrs, int defStyleId) {
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.FeedMessageSpanFormatter, 0, defStyleId);
        this.messageTextAppearance = a.getResourceId(1, 0);
        this.actorTextAppearance = a.getResourceId(0, this.messageTextAppearance);
        this.targetTextAppearance = a.getResourceId(2, this.messageTextAppearance);
        this.targetGroupTextAppearance = a.getResourceId(3, this.targetTextAppearance);
        this.targetAppTextAppearance = a.getResourceId(4, this.targetTextAppearance);
        a.recycle();
    }

    public int getMessageTextAppearance() {
        return this.messageTextAppearance;
    }

    private TextAppearanceSpan createTextAppearanceSpan(FeedMessageSpan feedSpan) {
        if (feedSpan instanceof FeedActorSpan) {
            return new TextAppearanceSpan(this.context, this.actorTextAppearance);
        }
        if (feedSpan instanceof FeedTargetGroupSpan) {
            return new TextAppearanceSpan(this.context, this.targetGroupTextAppearance);
        }
        if (feedSpan instanceof FeedTargetAppSpan) {
            return new TextAppearanceSpan(this.context, this.targetAppTextAppearance);
        }
        if (feedSpan instanceof FeedTargetSpan) {
            return new TextAppearanceSpan(this.context, this.targetTextAppearance);
        }
        return null;
    }

    public void applyStyle(Spannable srcMessage, SpannableStringBuilder outMessage, int outMessageStartOffset) {
        FeedMessageSpan[] feedSpans = (FeedMessageSpan[]) srcMessage.getSpans(0, srcMessage.length(), FeedMessageSpan.class);
        if (feedSpans != null) {
            for (FeedMessageSpan feedSpan : feedSpans) {
                TextAppearanceSpan textAppearanceSpan = createTextAppearanceSpan(feedSpan);
                if (textAppearanceSpan != null) {
                    int spanStart = srcMessage.getSpanStart(feedSpan);
                    int spanEnd = srcMessage.getSpanEnd(feedSpan);
                    if (!(spanStart == -1 || spanEnd == -1)) {
                        outMessage.setSpan(textAppearanceSpan, outMessageStartOffset + spanStart, outMessageStartOffset + spanEnd, srcMessage.getSpanFlags(feedSpan));
                    }
                }
            }
        }
    }
}
