package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import ru.ok.model.messages.Attachment;

public final class ConversationAttachDraweeView extends AspectRatioAsyncDraweeView {
    private final GifMarkerDrawableHelper gifMarkerDrawableHelper;
    private int rotate;

    public ConversationAttachDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
        this.rotate = 0;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
        requestLayout();
    }

    public void setAttach(Attachment attachment) {
        setUri(attachment == null ? null : attachment.getUri());
        GifMarkerDrawableHelper gifMarkerDrawableHelper = this.gifMarkerDrawableHelper;
        boolean z = attachment != null && attachment.hasGif();
        gifMarkerDrawableHelper.setShouldDrawGifMarker(z);
    }

    public void setPreviewAttach(Attachment attachment) {
        if (attachment != null) {
            Uri uri;
            if (TextUtils.isEmpty(attachment.thumbnailUrl)) {
                uri = attachment.getUri();
            } else {
                uri = Uri.parse(attachment.thumbnailUrl);
            }
            setLocalUri(uri, attachment.getRotation());
        } else {
            setUri(null);
        }
        GifMarkerDrawableHelper gifMarkerDrawableHelper = this.gifMarkerDrawableHelper;
        boolean z = attachment != null && attachment.hasGif();
        gifMarkerDrawableHelper.setShouldDrawGifMarker(z);
    }

    public void draw(Canvas canvas) {
        int i = canvas.save();
        canvas.rotate((float) this.rotate, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
        super.draw(canvas);
        canvas.restoreToCount(i);
        this.gifMarkerDrawableHelper.drawGifMarkerIfNecessary(this, canvas);
    }
}
