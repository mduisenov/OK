package ru.ok.android.widget.attach;

import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import java.lang.ref.WeakReference;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.services.AttachmentUtils;
import ru.ok.android.ui.custom.imageview.GifMarkerDrawableHelper;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.PhotoUtil;
import ru.ok.model.messages.Attachment;
import ru.ok.model.photo.PhotoSize;

public final class PhotoAttachDraweeView extends BaseAttachDraweeView {
    private GifMarkerDrawableHelper mGifMarkerDrawableHelper;

    private static class AttachLoader extends AsyncTask<Void, Void, Attachment> {
        private final WeakReference<PhotoAttachDraweeView> attachImageView;
        private final boolean stub;

        private AttachLoader(PhotoAttachDraweeView attachImageView, boolean stub) {
            this.attachImageView = new WeakReference(attachImageView);
            this.stub = stub;
        }

        protected Attachment doInBackground(Void... voids) {
            try {
                BaseAttachDraweeView imageView = (BaseAttachDraweeView) this.attachImageView.get();
                if (imageView != null) {
                    Attachment attach = AttachmentUtils.getAttachments(imageView.attachment.id);
                    return attach != null ? attach : attach;
                }
            } catch (Throwable e) {
                Logger.m178e(e);
            }
            return null;
        }

        protected void onPostExecute(Attachment res) {
            PhotoAttachDraweeView imageView = (PhotoAttachDraweeView) this.attachImageView.get();
            if (imageView != null) {
                if (res == null || res.sizes == null || res.sizes.isEmpty()) {
                    imageView.onLoadingFailedSuper(this.stub);
                } else if (imageView.attachment.id.equals(res.id)) {
                    imageView.attachment.fillMedia(res);
                    imageView.setAttachPhoto(imageView.progressView, imageView.reloadButtonView, res, imageView.width, imageView.height, true);
                }
            }
        }
    }

    public PhotoAttachDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mGifMarkerDrawableHelper = new GifMarkerDrawableHelper();
    }

    protected String getLoadUrl(Attachment attach) {
        if (attach == null) {
            return null;
        }
        PhotoSize size = PhotoUtil.getClosestSize(this.width, this.height, attach.sizes);
        if (size != null) {
            return size.getUrl();
        }
        return null;
    }

    public void setAttachPhoto(View progressView, View reloadButtonView, Attachment attach, int width, int height, boolean isFromReload) {
        super.setAttachPhoto(progressView, reloadButtonView, attach, width, height, isFromReload);
        if (!(getUri() != null || this.attachment == null || attach.attachBeReload)) {
            new AttachLoader(false, null).execute(new Void[0]);
        }
        this.mGifMarkerDrawableHelper.setShouldDrawGifMarker(GifAsMp4PlayerHelper.shouldShowGifAsMp4(attach));
    }

    protected void onImageFailed(boolean stub) {
        if (this.attachment == null || this.attachment.attachBeReload) {
            onLoadingFailedSuper(stub);
        } else {
            new AttachLoader(stub, null).execute(new Void[0]);
        }
    }

    private void onLoadingFailedSuper(boolean stub) {
        super.onImageFailed(stub);
        if (this.reloadButtonView != null) {
            this.reloadButtonView.setVisibility(0);
        }
        if (this.progressView != null) {
            this.progressView.setVisibility(8);
        }
        this.attachment.attachLoadWithError = true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mGifMarkerDrawableHelper.drawGifMarkerIfNecessary(this, canvas);
    }
}
