package ru.ok.android.ui.stream.list;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import bo.pic.android.media.content.MediaContent;
import bo.pic.android.media.content.presenter.MediaContentPresenter;
import bo.pic.android.media.util.ScaleMode;
import bo.pic.android.media.view.MediaContentView;
import ru.ok.android.app.GifAsMp4ImageLoaderHelper;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.imageview.AspectRatioGifAsMp4ImageView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedFooterView;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public abstract class AbsStreamSingleGifAsMp4PhotoItem extends AbsStreamSinglePhotoItem {
    protected final String mMp4Url;
    private final Drawable placeholderDrawable;

    /* renamed from: ru.ok.android.ui.stream.list.AbsStreamSingleGifAsMp4PhotoItem.1 */
    class C12321 implements MediaContentPresenter {
        final /* synthetic */ AspectRatioGifAsMp4ImageView val$animatedView;

        C12321(AspectRatioGifAsMp4ImageView aspectRatioGifAsMp4ImageView) {
            this.val$animatedView = aspectRatioGifAsMp4ImageView;
        }

        public void setMediaContent(@NonNull MediaContent content, @NonNull MediaContentView view) {
            view.setMediaContent(content, true);
            FeedFooterView feedFooterView = (FeedFooterView) this.val$animatedView.getTag(2131624319);
            if (feedFooterView != null) {
                feedFooterView.setVisibility(0);
            }
        }
    }

    static class GifAsMp4ImageViewHolder extends InnerViewHolder {
        final AspectRatioGifAsMp4ImageView animatedView;

        public GifAsMp4ImageViewHolder(View view) {
            super(view);
            this.animatedView = (AspectRatioGifAsMp4ImageView) view.findViewById(C0263R.id.image);
        }
    }

    protected AbsStreamSingleGifAsMp4PhotoItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feed, AbsFeedPhotoEntity photo, MediaItemPhoto mediaItem, float aspectRatio) {
        super(viewType, topEdgeType, bottomEdgeType, feed, photo, mediaItem, aspectRatio);
        this.placeholderDrawable = new ColorDrawable(OdnoklassnikiApplication.getContext().getResources().getColor(2131493182));
        this.mMp4Url = photo.getPhotoInfo().getMp4Url();
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof GifAsMp4ImageViewHolder) {
            AspectRatioGifAsMp4ImageView animatedView = ((GifAsMp4ImageViewHolder) holder).animatedView;
            animatedView.setWidthHeightRatio(getAspectRatio());
            animatedView.setMaximumWidth(calculateMaximumWidth());
            if (!TextUtils.equals(this.mMp4Url, animatedView.getEmbeddedAnimationUri())) {
                LayoutParams lp = animatedView.getLayoutParams();
                GifAsMp4ImageLoaderHelper.with(animatedView.getContext()).load(this.mMp4Url, GifAsMp4ImageLoaderHelper.GIF).setDimensions(lp.width, lp.height).setPlaceholder(this.placeholderDrawable).setPresenter(new C12321(animatedView)).setScaleMode(ScaleMode.CROP).into(animatedView);
            }
            animatedView.setTag(2131624320, this.photo);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void onUnbindView(@NonNull ViewHolder holder) {
        if (holder instanceof GifAsMp4ImageViewHolder) {
            GifAsMp4PlayerHelper.resetAndStopPlaying(((GifAsMp4ImageViewHolder) holder).animatedView);
        }
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new GifAsMp4ImageViewHolder(view);
    }
}
