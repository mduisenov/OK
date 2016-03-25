package ru.ok.android.widget.attach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.custom.imageview.ConversationAttachDraweeView;
import ru.ok.model.messages.Attachment;

public abstract class ConversationUploadBaseAttachView extends RelativeLayout {
    private View errorView;
    private ConversationAttachDraweeView imageView;
    private ProgressBar progressView;

    protected abstract float getStateAlpha(int i);

    public ConversationUploadBaseAttachView(Context context, int layoutResourceId, float aspectRatio) {
        super(context);
        LayoutInflater.from(getContext()).inflate(layoutResourceId, this, true);
        this.imageView = (ConversationAttachDraweeView) findViewById(C0263R.id.image);
        this.imageView.setWidthHeightRatio(aspectRatio);
        this.progressView = (ProgressBar) findViewById(2131624548);
        this.errorView = findViewById(2131624551);
    }

    public void setAttach(Attachment attachment) {
        if ((attachment == null ? null : attachment.getUri()) != null) {
            this.imageView.setPreviewAttach(attachment);
        } else {
            this.imageView.setAttach(attachment);
        }
    }

    public void setState(int state) {
        int i;
        int i2 = 0;
        View view = this.errorView;
        if (state == 3) {
            i = 0;
        } else {
            i = 8;
        }
        view.setVisibility(i);
        ProgressBar progressBar = this.progressView;
        if (state != 0) {
            i2 = 8;
        }
        progressBar.setVisibility(i2);
        this.imageView.setAlpha(getStateAlpha(state));
        invalidate();
    }

    public AsyncDraweeView getImageView() {
        return this.imageView;
    }

    public void setAspectRatio(float aspectRatio) {
        this.imageView.setWidthHeightRatio(aspectRatio);
    }
}
