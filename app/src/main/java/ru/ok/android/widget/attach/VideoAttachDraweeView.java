package ru.ok.android.widget.attach;

import android.content.Context;
import android.util.AttributeSet;
import ru.ok.model.messages.Attachment;

public final class VideoAttachDraweeView extends BaseAttachDraweeView {
    public VideoAttachDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidthHeightRatio(1.7777778f);
        setEmptyImageResId(2130837771);
    }

    protected String getLoadUrl(Attachment attach) {
        if (attach == null) {
            return null;
        }
        return attach.thumbnailUrl;
    }
}
