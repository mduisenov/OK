package ru.ok.android.widget.attach;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.ui.custom.imageview.AspectRatioAsyncDraweeView;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView.OnImageSetListener;
import ru.ok.model.messages.Attachment;

public abstract class BaseAttachDraweeView extends AspectRatioAsyncDraweeView implements OnClickListener, OnImageSetListener {
    protected Attachment attachment;
    protected int height;
    protected View progressView;
    protected View reloadButtonView;
    protected int width;

    protected abstract String getLoadUrl(Attachment attachment);

    public BaseAttachDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attachment = null;
        this.width = 0;
        this.height = 0;
        setOnImageSetListener(this);
    }

    private void setAttachPhoto(View progressView, View reloadButtonView, Attachment attach) {
        setAttachPhoto(progressView, reloadButtonView, attach, this.width, this.height, false);
    }

    public void onClick(View view) {
        this.attachment.attachLoadWithError = false;
        this.attachment.attachBeReload = false;
        setAttachPhoto(this.progressView, this.reloadButtonView, this.attachment);
    }

    public void setAttachPhoto(View progressView, View reloadButtonView, Attachment attach, int width, int height, boolean isFromReload) {
        int i = 8;
        this.reloadButtonView = reloadButtonView;
        this.progressView = progressView;
        this.attachment = attach;
        if (reloadButtonView != null) {
            int i2;
            reloadButtonView.setOnClickListener(this);
            if (attach.attachLoadWithError) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            reloadButtonView.setVisibility(i2);
        }
        if (!attach.attachLoadWithError) {
            i = 0;
        }
        progressView.setVisibility(i);
        attach.attachBeReload = isFromReload;
        this.width = width;
        this.height = height;
        String toLoad = getLoadUrl(attach);
        setUri(!TextUtils.isEmpty(toLoad) ? Uri.parse(toLoad) : null);
    }

    public void onJustSetImage(AsyncDraweeView view) {
    }

    public void onFinishedSetImage(View view, boolean imageIsShown) {
        this.progressView.setVisibility(8);
    }
}
