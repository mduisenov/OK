package ru.ok.android.ui.custom.text;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

public final class EditDeleteSpanTextView extends EditText {

    private class SpanInputConnection extends InputConnectionWrapper {
        public SpanInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            Editable text = EditDeleteSpanTextView.this.getText();
            if (text instanceof SpannableStringBuilder) {
                int selectionStart = Math.min(EditDeleteSpanTextView.this.getSelectionStart(), EditDeleteSpanTextView.this.getSelectionEnd());
                int selectionEnd = Math.max(EditDeleteSpanTextView.this.getSelectionStart(), EditDeleteSpanTextView.this.getSelectionEnd());
                SpannableStringBuilder sb = (SpannableStringBuilder) text;
                ImageSpan[] spans = (ImageSpan[]) sb.getSpans(selectionStart - beforeLength, selectionEnd + afterLength, ImageSpan.class);
                if (spans != null) {
                    for (ImageSpan span : spans) {
                        int spanStart = sb.getSpanStart(span);
                        int spanEnd = sb.getSpanEnd(span);
                        if (spanStart >= 0 && spanEnd < sb.length()) {
                            if (spanStart < selectionStart) {
                                beforeLength -= selectionStart - spanStart;
                                sb.delete(spanStart, selectionStart);
                            }
                            if (spanEnd > selectionEnd) {
                                afterLength -= selectionEnd - spanEnd;
                                sb.delete(selectionEnd, spanEnd);
                            }
                            sb.removeSpan(span);
                        }
                    }
                }
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    public EditDeleteSpanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
        if (inputConnection == null) {
            return null;
        }
        return new SpanInputConnection(inputConnection, false);
    }
}
