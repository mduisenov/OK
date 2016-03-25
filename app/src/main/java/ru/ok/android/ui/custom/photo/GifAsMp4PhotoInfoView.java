package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import bo.pic.android.media.Dimensions;
import bo.pic.android.media.content.MediaContent;
import bo.pic.android.media.content.presenter.MediaContentPresenter;
import bo.pic.android.media.util.ProcessingCallback;
import bo.pic.android.media.view.AnimatedMediaContentView;
import bo.pic.android.media.view.MediaContentView;
import ru.ok.android.app.GifAsMp4ImageLoaderHelper;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.Logger;
import ru.ok.model.photo.PhotoInfo;

public class GifAsMp4PhotoInfoView extends AbstractPhotoInfoView {
    private AnimatedMediaContentView mAnimatedView;

    /* renamed from: ru.ok.android.ui.custom.photo.GifAsMp4PhotoInfoView.1 */
    class C07051 implements MediaContentPresenter {
        C07051() {
        }

        public void setMediaContent(@NonNull MediaContent content, @NonNull MediaContentView view) {
            GifAsMp4PhotoInfoView.this.mProgressView.setVisibility(8);
            GifAsMp4PhotoInfoView.this.mAnimatedView.setMediaContent(content, true);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.photo.GifAsMp4PhotoInfoView.2 */
    class C07072 implements ProcessingCallback<MediaContent> {

        /* renamed from: ru.ok.android.ui.custom.photo.GifAsMp4PhotoInfoView.2.1 */
        class C07061 implements Runnable {
            C07061() {
            }

            public void run() {
                GifAsMp4PhotoInfoView.this.mProgressView.setVisibility(8);
                GifAsMp4PhotoInfoView.this.setStubViewVisible(true);
            }
        }

        C07072() {
        }

        public void onSuccess(@NonNull MediaContent data) {
        }

        public void onFail(@Nullable Throwable e) {
            Logger.m178e(e);
            GifAsMp4PhotoInfoView.this.post(new C07061());
        }
    }

    public GifAsMp4PhotoInfoView(Context context) {
        super(context);
    }

    protected void onCreate() {
        super.onCreate();
        this.mAnimatedView = (AnimatedMediaContentView) findViewById(C0263R.id.image);
    }

    protected int getPhotoViewId() {
        return 2130903224;
    }

    public void bindPhotoInfo(@NonNull PhotoInfo photoInfo, @NonNull int[] sizesHolder) {
        if (!TextUtils.equals(photoInfo.getMp4Url(), this.mAnimatedView.getEmbeddedAnimationUri())) {
            setStubViewVisible(false);
            Dimensions dimensions = new Dimensions(Math.min(photoInfo.getStandartWidth(), sizesHolder[0]), Math.min(photoInfo.getStandartHeight(), sizesHolder[1]));
            LayoutParams viewPagerLayoutParams = new LayoutParams();
            viewPagerLayoutParams.width = dimensions.getWidth();
            viewPagerLayoutParams.height = dimensions.getHeight();
            viewPagerLayoutParams.gravity = 17;
            setLayoutParams(viewPagerLayoutParams);
            GifAsMp4ImageLoaderHelper.with(getContext()).load(photoInfo.getMp4Url(), GifAsMp4ImageLoaderHelper.GIF).setDimensions(dimensions.getWidth(), dimensions.getHeight()).setProcessingCallback(new C07072()).setPresenter(new C07051()).into(this.mAnimatedView);
        }
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
}
