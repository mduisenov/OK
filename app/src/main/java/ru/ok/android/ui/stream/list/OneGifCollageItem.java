package ru.ok.android.ui.stream.list;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bo.pic.android.media.content.MediaContent;
import bo.pic.android.media.content.presenter.MediaContentPresenter;
import bo.pic.android.media.util.ScaleMode;
import bo.pic.android.media.view.MediaContentView;
import java.util.Collections;
import java.util.List;
import ru.ok.android.app.GifAsMp4ImageLoaderHelper;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.imageview.AspectRatioGifAsMp4ImageView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.TwoPhotoCollageItem.PhotoLocate;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedFooterView;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public class OneGifCollageItem extends AbsStreamClickableItem {
    private final String mp4Url;
    private final AbsFeedPhotoEntity photo;
    private final PhotoLocate photoLocate;
    private final Drawable placeholderDrawable;
    private final List<PhotoInfo> tagPhotos;

    /* renamed from: ru.ok.android.ui.stream.list.OneGifCollageItem.1 */
    class C12341 implements MediaContentPresenter {
        final /* synthetic */ AspectRatioGifAsMp4ImageView val$animatedView;

        C12341(AspectRatioGifAsMp4ImageView aspectRatioGifAsMp4ImageView) {
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

    static class GifAsMp4ImageViewHolder extends ViewHolder {
        final AspectRatioGifAsMp4ImageView animatedView;

        public GifAsMp4ImageViewHolder(View view) {
            super(view);
            this.animatedView = (AspectRatioGifAsMp4ImageView) view.findViewById(C0263R.id.image);
        }
    }

    protected OneGifCollageItem(FeedWithState feed, AbsFeedPhotoEntity photo, MediaItemPhoto mediaItem, PhotoLocate photoLocate) {
        super(42, 2, 2, feed, new PhotoClickAction(feed, photo, mediaItem));
        this.placeholderDrawable = new ColorDrawable(OdnoklassnikiApplication.getContext().getResources().getColor(2131493182));
        this.photo = photo;
        this.mp4Url = photo.getPhotoInfo().getMp4Url();
        this.photoLocate = photoLocate;
        this.tagPhotos = Collections.singletonList(photo.getPhotoInfo());
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903502, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof GifAsMp4ImageViewHolder) {
            AspectRatioGifAsMp4ImageView animatedView = ((GifAsMp4ImageViewHolder) holder).animatedView;
            this.photoLocate.locate(holder, animatedView, layoutConfig);
            if (!TextUtils.equals(this.mp4Url, animatedView.getEmbeddedAnimationUri())) {
                GifAsMp4ImageLoaderHelper.with(animatedView.getContext()).load(this.mp4Url, GifAsMp4ImageLoaderHelper.GIF).setPlaceholder(this.placeholderDrawable).setDimensions(animatedView.getWidth(), animatedView.getHeight()).setPresenter(new C12341(animatedView)).setScaleMode(ScaleMode.CROP).into(animatedView);
            }
            animatedView.setTag(2131624320, this.photo);
        }
        holder.itemView.setTag(2131624334, this.tagPhotos);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void onUnbindView(@NonNull ViewHolder holder) {
        if (holder instanceof GifAsMp4ImageViewHolder) {
            GifAsMp4PlayerHelper.resetAndStopPlaying(((GifAsMp4ImageViewHolder) holder).animatedView);
        }
    }

    public static ViewHolder newViewHolder(View view) {
        return new GifAsMp4ImageViewHolder(view);
    }
}
