package ru.ok.android.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

public final class FriendlySpannableStringBuilder {
    private Object[] currentSpans;
    private final SpannableStringBuilder sb;
    private int spanStartIndex;

    public FriendlySpannableStringBuilder() {
        this.sb = new SpannableStringBuilder();
        this.spanStartIndex = -1;
    }

    public FriendlySpannableStringBuilder append(@Nullable CharSequence chunk) {
        if (chunk != null) {
            this.sb.append(chunk);
        }
        return this;
    }

    public FriendlySpannableStringBuilder append(@Nullable CharSequence chunk, @NonNull Object... spans) {
        if (chunk != null) {
            startSpan(spans);
            append(chunk);
            completeSpan();
        }
        return this;
    }

    public FriendlySpannableStringBuilder startSpan(@NonNull Object... spans) {
        this.spanStartIndex = this.sb.length();
        this.currentSpans = spans;
        return this;
    }

    public FriendlySpannableStringBuilder completeSpan() {
        if (this.spanStartIndex == -1) {
            throw new IllegalStateException("startSpan was not called!");
        }
        for (Object span : this.currentSpans) {
            this.sb.setSpan(span, this.spanStartIndex, this.sb.length(), 17);
        }
        this.spanStartIndex = -1;
        this.currentSpans = null;
        return this;
    }

    public SpannableStringBuilder build() {
        return this.sb;
    }

    public int length() {
        return this.sb.length();
    }
}
