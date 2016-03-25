package ru.ok.android.ui.custom.mediacomposer;

import android.text.InputFilter.LengthFilter;
import android.text.Spanned;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class MaxLengthFilter extends LengthFilter {
    private final String limitType;
    private final MediaTopicType mediaTopicType;

    public MaxLengthFilter(int max, String limitType, MediaTopicType mediaTopicType) {
        super(max);
        this.limitType = limitType;
        this.mediaTopicType = mediaTopicType;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        CharSequence res = super.filter(source, start, end, dest, dstart, dend);
        if (res != null) {
            MediaComposerStats.hitLimit(this.mediaTopicType, this.limitType);
        }
        return res;
    }
}
