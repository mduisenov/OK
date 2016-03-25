package ru.ok.android.ui.custom.mediacomposer;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.ui.custom.imageview.GifMarkerDrawableHelper;
import ru.ok.android.utils.MimeTypes;

public class EditablePhotoItemView extends ImageView {
    private final GifMarkerDrawableHelper gifMarkerDrawableHelper;

    public EditablePhotoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
    }

    public EditablePhotoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
    }

    public void bindItem(@NonNull EditablePhotoItem item) {
        this.gifMarkerDrawableHelper.setShouldDrawGifMarker(isEditableItemGif(item.getImageEditInfo()));
    }

    private boolean isEditableItemGif(@NonNull ImageEditInfo imageEditInfo) {
        return MimeTypes.isGif(imageEditInfo.getMimeType());
    }

    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        this.gifMarkerDrawableHelper.drawGifMarkerIfNecessary(this, canvas);
    }
}
