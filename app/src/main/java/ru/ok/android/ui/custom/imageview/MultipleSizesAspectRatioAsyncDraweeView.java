package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import java.util.TreeSet;
import ru.ok.android.utils.PhotoUtil;
import ru.ok.model.photo.PhotoSize;

public class MultipleSizesAspectRatioAsyncDraweeView extends AspectRatioAsyncDraweeView {
    private TreeSet<PhotoSize> sizes;

    public MultipleSizesAspectRatioAsyncDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSizes(TreeSet<PhotoSize> sizes) {
        this.sizes = sizes;
        updateUri();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateUri();
    }

    private void updateUri() {
        Uri lowUri = null;
        if (getWidth() != 0) {
            Uri uri;
            if (this.sizes == null) {
                setUri(null);
            }
            PhotoSize size = PhotoUtil.getClosestSize(getWidth(), getHeight(), this.sizes);
            PhotoSize lowSize = PhotoUtil.getClosestSize(getWidth() / 2, getHeight() / 2, this.sizes);
            if (size == lowSize) {
                lowSize = null;
            }
            if (size != null) {
                uri = Uri.parse(size.getUrl());
            } else {
                uri = null;
            }
            if (lowSize != null) {
                lowUri = Uri.parse(lowSize.getUrl());
            }
            setUri(uri, lowUri);
            if (size != null) {
                setWidthHeightRatio(((float) size.getWidth()) / ((float) size.getHeight()));
            }
        }
    }
}
