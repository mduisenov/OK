package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.FrameLayout.LayoutParams;
import bo.pic.android.media.Dimensions;
import bo.pic.android.media.content.MediaContent;
import bo.pic.android.media.content.presenter.MediaContentPresenter;
import bo.pic.android.media.view.AnimatedMediaContentView;
import bo.pic.android.media.view.MediaContentView;
import ru.ok.android.app.GifAsMp4ImageLoaderHelper;
import ru.ok.android.emoji.C0263R;
import ru.ok.model.messages.Attachment;

public class GifAsMp4AttachPhotoView extends AbstractAttachPhotoView {
    @NonNull
    private final AnimatedMediaContentView mAnimatedView;

    /* renamed from: ru.ok.android.ui.custom.photo.GifAsMp4AttachPhotoView.1 */
    class C07041 implements MediaContentPresenter {
        C07041() {
        }

        public void setMediaContent(@NonNull MediaContent content, @NonNull MediaContentView view) {
            GifAsMp4AttachPhotoView.this.mProgressView.setVisibility(8);
            GifAsMp4AttachPhotoView.this.mAnimatedView.setMediaContent(content, true);
        }
    }

    public GifAsMp4AttachPhotoView(Context context) {
        super(context);
        this.mAnimatedView = (AnimatedMediaContentView) findViewById(C0263R.id.image);
    }

    protected int getDraggableContentViewId() {
        return 2130903105;
    }

    public int getImageDisplayedX() {
        return 0;
    }

    public int getImageDisplayedY() {
        return 0;
    }

    public int getImageDisplayedWidth() {
        return 0;
    }

    public int getImageDisplayedHeight() {
        return 0;
    }

    public float getImageScale() {
        return 0.0f;
    }

    public RectF getImageDisplayRect() {
        return null;
    }

    public void bindAttachment(@NonNull Attachment attachment, @NonNull int[] sizesHolder) {
        if (!TextUtils.equals(attachment.mp4Url, this.mAnimatedView.getEmbeddedAnimationUri())) {
            Dimensions dimensions = new Dimensions(Math.min(attachment.standard_width, sizesHolder[0]), Math.min(attachment.standard_height, sizesHolder[1]));
            setLayoutParams(new LayoutParams(dimensions.getWidth(), dimensions.getHeight(), 17));
            GifAsMp4ImageLoaderHelper.with(getContext()).load(attachment.mp4Url, GifAsMp4ImageLoaderHelper.GIF).setDimensions(dimensions.getWidth(), dimensions.getHeight()).setPresenter(new C07041()).into(this.mAnimatedView);
        }
    }
}
