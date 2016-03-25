package ru.ok.android.ui.stream.photos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import bo.pic.android.media.content.MediaContent;
import bo.pic.android.media.content.presenter.MediaContentPresenter;
import bo.pic.android.media.util.ScaleMode;
import bo.pic.android.media.view.AnimatedMediaContentView;
import bo.pic.android.media.view.MediaContentView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.noundla.centerviewpagersample.comps.CenterLockPagerAdapter;
import com.noundla.centerviewpagersample.comps.CenterLockViewPager;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.GifAsMp4ImageLoaderHelper;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.GifAsMp4PlayerHelper.AutoplayContext;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fresco.FrescoGifMarkerView;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.fresco.FrescoOdkl.SideCrop;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.ui.image.view.PhotoLayerAnimationHelper;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.view.FeedFooterInfo;
import ru.ok.android.ui.stream.view.FeedFooterView;
import ru.ok.android.ui.stream.view.FeedFooterView.OnCommentsClickListener;
import ru.ok.android.ui.stream.view.FeedFooterView.OnLikeListener;
import ru.ok.android.ui.stream.viewcache.StreamViewCache;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.FeedUtils;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;
import ru.ok.model.stream.entities.BaseEntity;

public final class PhotosFeedAdapter extends CenterLockPagerAdapter implements OnClickListener, OnCommentsClickListener, OnLikeListener {
    protected final Context context;
    private final FeedWithState feed;
    private final PhotoViewStrategy gifAsMp4PhotoViewStrategy;
    protected final float horizontalPhotoAspectRatio;
    private final PhotosAdapterListener listener;
    private final List<AbsFeedPhotoEntity> photos;
    private final boolean simpleMode;
    private final PhotoViewStrategy staticPhotoViewStrategy;
    private final StreamViewCache streamViewCache;
    private final SparseArray<Unbindable> unbindables;
    protected final float verticalPhotoAspectRatio;
    protected final CenterLockViewPager viewPager;

    public interface PhotosAdapterListener {
        LikeInfoContext onLikePhotoClicked(int i, Feed feed, LikeInfoContext likeInfoContext);
    }

    public static abstract class ViewHolder {
        protected FeedFooterView countersView;

        protected abstract void destroy();
    }

    private interface Unbindable {
        void unbind();
    }

    public static class GifAsMp4PhotoViewHolder extends ViewHolder implements Unbindable {
        private AnimatedMediaContentView animatedView;

        protected void destroy() {
            this.animatedView.setOnClickListener(null);
            GifAsMp4PlayerHelper.resetAndStopPlaying(this.animatedView);
        }

        public void unbind() {
            GifAsMp4PlayerHelper.resetAndStopPlaying(this.animatedView);
        }
    }

    protected abstract class PhotoViewStrategy {
        @NonNull
        protected abstract ViewHolder createViewHolder(@NonNull View view, int i);

        protected abstract int getPhotoViewLayoutId();

        protected abstract void onBindView(@NonNull View view, @NonNull AbsFeedPhotoEntity absFeedPhotoEntity);

        protected PhotoViewStrategy() {
        }

        @NonNull
        public final View newView(@NonNull ViewGroup parent, int position) {
            View mainView = PhotosFeedAdapter.this.streamViewCache.getViewWithLayoutId(getPhotoViewLayoutId(), parent);
            mainView.setLayoutParams(new LayoutParams());
            ViewHolder holder = createViewHolder(mainView, position);
            holder.countersView = (FeedFooterView) mainView.findViewById(2131625189);
            holder.countersView.setOnLikeListener(PhotosFeedAdapter.this);
            holder.countersView.setOnCommentsClickListener(PhotosFeedAdapter.this);
            mainView.setTag(holder);
            return mainView;
        }

        public final void bindView(@NonNull View view, @NonNull AbsFeedPhotoEntity photoEntity) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (PhotosFeedAdapter.this.simpleMode) {
                holder.countersView.setVisibility(8);
            } else {
                holder.countersView.setVisibility(4);
            }
            onBindView(view, photoEntity);
            holder.countersView.setTag(2131624320, photoEntity);
            holder.countersView.setTag(2131624349, holder);
            holder.countersView.setInfo(new FeedFooterInfo(PhotosFeedAdapter.this.feed, photoEntity.getLikeInfo(), photoEntity.getPhotoInfo().getDiscussionSummary(), null));
        }
    }

    private class GifAsMp4PhotoViewStrategy extends PhotoViewStrategy {
        private final Drawable placeholderDrawable;

        /* renamed from: ru.ok.android.ui.stream.photos.PhotosFeedAdapter.GifAsMp4PhotoViewStrategy.1 */
        class C12551 implements MediaContentPresenter {
            final /* synthetic */ GifAsMp4PhotoViewHolder val$holder;

            C12551(GifAsMp4PhotoViewHolder gifAsMp4PhotoViewHolder) {
                this.val$holder = gifAsMp4PhotoViewHolder;
            }

            public void setMediaContent(@NonNull MediaContent content, @NonNull MediaContentView view) {
                view.setMediaContent(content, true);
                this.val$holder.countersView.setVisibility(!PhotosFeedAdapter.this.simpleMode ? 0 : 4);
            }
        }

        private GifAsMp4PhotoViewStrategy() {
            super();
            this.placeholderDrawable = new ColorDrawable(OdnoklassnikiApplication.getContext().getResources().getColor(2131493182));
        }

        @NonNull
        protected ViewHolder createViewHolder(@NonNull View mainView, int position) {
            GifAsMp4PhotoViewHolder holder = new GifAsMp4PhotoViewHolder();
            holder.animatedView = (AnimatedMediaContentView) mainView.findViewById(C0263R.id.image);
            holder.animatedView.setOnClickListener(PhotosFeedAdapter.this);
            holder.animatedView.setTag(holder);
            PhotosFeedAdapter.this.unbindables.append(position, holder);
            return holder;
        }

        protected int getPhotoViewLayoutId() {
            return 2130903463;
        }

        protected void onBindView(@NonNull View view, @NonNull AbsFeedPhotoEntity photoEntity) {
            PhotoInfo photoInfo = photoEntity.getPhotoInfo();
            GifAsMp4PhotoViewHolder holder = (GifAsMp4PhotoViewHolder) view.getTag();
            if (!TextUtils.equals(photoInfo.getMp4Url(), holder.animatedView.getEmbeddedAnimationUri())) {
                GifAsMp4ImageLoaderHelper.with(PhotosFeedAdapter.this.context).load(photoInfo.getMp4Url(), GifAsMp4ImageLoaderHelper.GIF).setPlaceholder(this.placeholderDrawable).setScaleMode(ScaleMode.CROP).setDimensions(holder.animatedView.getWidth(), holder.animatedView.getHeight()).setPresenter(new C12551(holder)).into(holder.animatedView);
            }
            holder.animatedView.setTag(2131624320, photoEntity);
        }
    }

    private static class StaticPhotoViewHolder extends ViewHolder {
        FrescoGifMarkerView imageView;

        private StaticPhotoViewHolder() {
        }

        protected void destroy() {
            this.imageView.setOnClickListener(null);
        }
    }

    private class StaticPhotoViewStrategy extends PhotoViewStrategy {

        /* renamed from: ru.ok.android.ui.stream.photos.PhotosFeedAdapter.StaticPhotoViewStrategy.1 */
        class C12561 extends BaseControllerListener<ImageInfo> {
            final /* synthetic */ StaticPhotoViewHolder val$holder;

            C12561(StaticPhotoViewHolder staticPhotoViewHolder) {
                this.val$holder = staticPhotoViewHolder;
            }

            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                this.val$holder.countersView.setVisibility(!PhotosFeedAdapter.this.simpleMode ? 0 : 4);
            }
        }

        private StaticPhotoViewStrategy() {
            super();
        }

        @NonNull
        protected ViewHolder createViewHolder(@NonNull View mainView, int position) {
            StaticPhotoViewHolder holder = new StaticPhotoViewHolder();
            holder.imageView = (FrescoGifMarkerView) mainView.findViewById(C0263R.id.image);
            holder.imageView.setOnClickListener(PhotosFeedAdapter.this);
            holder.imageView.setTag(holder);
            return holder;
        }

        protected int getPhotoViewLayoutId() {
            return 2130903518;
        }

        protected void onBindView(@NonNull View view, @NonNull AbsFeedPhotoEntity photoEntity) {
            PhotoInfo photoInfo = photoEntity.getPhotoInfo();
            StaticPhotoViewHolder holder = (StaticPhotoViewHolder) view.getTag();
            int highQualityPhotoWidth = DeviceUtils.getStreamHighQualityPhotoWidth();
            int lowQualityPhotoWidth = DeviceUtils.getStreamLowQualityPhotoWidth();
            String url = photoInfo.getClosestSizeUrl(highQualityPhotoWidth, 0);
            String urlLowQuality = photoInfo.getSizeFloorUrl(lowQualityPhotoWidth);
            Uri uriLowQuality = TextUtils.isEmpty(urlLowQuality) ? null : Uri.parse(urlLowQuality);
            Uri uri = TextUtils.isEmpty(url) ? null : Uri.parse(url);
            holder.imageView.setUri(uri);
            holder.imageView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setControllerListener(new C12561(holder))).setUri(uri).setLowResImageRequest(ImageRequest.fromUri(uriLowQuality))).setRetainImageOnFailure(true)).build());
            boolean isVertical = photoInfo.getStandartWidth() <= photoInfo.getStandartHeight();
            holder.imageView.setAspectRatio(isVertical ? PhotosFeedAdapter.this.verticalPhotoAspectRatio : PhotosFeedAdapter.this.horizontalPhotoAspectRatio);
            FrescoOdkl.cropToSide(holder.imageView, isVertical ? SideCrop.TOP_CENTER : SideCrop.CENTER, FrescoOdkl.ACTUAL_IMAGE);
            holder.imageView.setShouldDrawGifMarker(GifAsMp4PlayerHelper.shouldShowGifAsMp4(photoInfo));
            holder.imageView.setTag(2131624320, photoEntity);
        }
    }

    public PhotosFeedAdapter(Context context, CenterLockViewPager viewPager, List<? extends BaseEntity> photos, FeedWithState feed, PhotosAdapterListener listener, boolean simpleMode, StreamViewCache streamViewCache) {
        float f;
        float f2 = 1.0f;
        this.staticPhotoViewStrategy = new StaticPhotoViewStrategy();
        this.gifAsMp4PhotoViewStrategy = new GifAsMp4PhotoViewStrategy();
        this.unbindables = new SparseArray();
        this.context = context;
        this.photos = initPhotos(photos);
        this.feed = feed;
        this.simpleMode = simpleMode;
        this.viewPager = viewPager;
        this.listener = listener;
        this.streamViewCache = streamViewCache;
        Resources res = context.getResources();
        TypedValue floatVal = new TypedValue();
        res.getValue(2131230988, floatVal, true);
        if (floatVal.type == 4) {
            f = floatVal.getFloat();
        } else {
            f = 1.0f;
        }
        this.verticalPhotoAspectRatio = f;
        res.getValue(2131230987, floatVal, true);
        if (floatVal.type == 4) {
            f2 = floatVal.getFloat();
        }
        this.horizontalPhotoAspectRatio = f2;
    }

    @NonNull
    private List<AbsFeedPhotoEntity> initPhotos(@NonNull List<? extends BaseEntity> entities) {
        List<AbsFeedPhotoEntity> feedPhotoEntityList = new ArrayList();
        for (BaseEntity entity : entities) {
            if (entity instanceof AbsFeedPhotoEntity) {
                feedPhotoEntityList.add((AbsFeedPhotoEntity) entity);
            }
        }
        return feedPhotoEntityList;
    }

    public int getCount() {
        return this.photos.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        AbsFeedPhotoEntity photoEntity = getItem(position);
        PhotoViewStrategy photoViewStrategy = pickStrategy(photoEntity.getPhotoInfo());
        View view = photoViewStrategy.newView(container, position);
        photoViewStrategy.bindView(view, photoEntity);
        container.addView(view, 0);
        return view;
    }

    public float getPageWidth(int position) {
        int count = getCount();
        int viewportWidth = (this.viewPager.getMeasuredWidth() - this.viewPager.getPaddingLeft()) - this.viewPager.getPaddingRight();
        if ((count < 2 || position >= count) && viewportWidth > 0) {
            return ((float) ((viewportWidth - this.leftOffset) - this.rightOffset)) / ((float) viewportWidth);
        }
        if (this.isVerticalSquared && isVerticalPhoto((AbsFeedPhotoEntity) this.photos.get(position))) {
            int viewportHeight = (this.viewPager.getMeasuredHeight() - this.viewPager.getPaddingTop()) - this.viewPager.getPaddingBottom();
            if (viewportWidth > 0 && viewportWidth > viewportHeight) {
                return ((float) viewportHeight) / ((float) viewportWidth);
            }
        }
        if (viewportWidth > 0) {
            return (this.pageWidthFactor * ((float) ((viewportWidth - this.leftOffset) - this.rightOffset))) / ((float) viewportWidth);
        }
        return this.pageWidthFactor;
    }

    private boolean isVerticalPhoto(AbsFeedPhotoEntity entity) {
        PhotoInfo info = entity.getPhotoInfo();
        return info.getStandartHeight() >= info.getStandartWidth();
    }

    public void destroyItem(ViewGroup collection, int position, Object object) {
        ViewGroup view = (ViewGroup) object;
        if (view.getParent() != null) {
            this.streamViewCache.collectThisView(view);
            collection.removeView(view);
        }
        this.unbindables.remove(position);
        ((ViewHolder) view.getTag()).destroy();
    }

    public AbsFeedPhotoEntity getItem(int position) {
        return (AbsFeedPhotoEntity) this.photos.get(position);
    }

    public void onClick(View view) {
        AbsFeedPhotoEntity tag = view.getTag(2131624320);
        if (tag != null && (tag instanceof AbsFeedPhotoEntity)) {
            AbsFeedPhotoEntity feedPhotoEntity = tag;
            if (view.getId() == C0263R.id.image) {
                clickToPhoto((Activity) this.context, null, feedPhotoEntity, this.feed, null, view);
            }
        }
    }

    public void onCommentsClicked(FeedFooterView feedFooterView, FeedFooterInfo info) {
        clickToComments((AbsFeedPhotoEntity) feedFooterView.getTag(2131624320));
        StreamStats.clickCommentPhoto(info.feed.position, info.feed.feed, info.discussionSummary);
    }

    public void onLikeClicked(FeedFooterView feedFooterView, FeedFooterInfo info, LikeInfoContext likeInfo) {
        clickToLike((AbsFeedPhotoEntity) feedFooterView.getTag(2131624320), (ViewHolder) feedFooterView.getTag(2131624349), likeInfo);
    }

    public void onLikeCountClicked(FeedFooterView feedFooterView, FeedFooterInfo info) {
        clickToLikeCount((AbsFeedPhotoEntity) feedFooterView.getTag(2131624320));
        StreamStats.clickLikeCount(info.feed.position, info.feed.feed, info.klassInfo);
    }

    private void clickToComments(AbsFeedPhotoEntity feed) {
        NavigationHelper.showDiscussionCommentsFragment((Activity) this.context, feed.getDiscussionSummary().discussion, Page.MESSAGES, null);
    }

    public static void clickToPhoto(Activity activity, @Nullable ru.ok.android.model.pagination.Page<PhotoInfo> photoInfoPage, AbsFeedPhotoEntity feedPhotoEntity, FeedWithState enclosingFeed, @Nullable MediaItemPhoto photoItem, View photoView) {
        String photoId = feedPhotoEntity.getId();
        StreamStats.clickPhoto(enclosingFeed.position, enclosingFeed.feed, photoId);
        if (photoId == null) {
            Logger.m185w("Photo id in feed %s is null", enclosingFeed);
            return;
        }
        PhotoOwner owner;
        Intent intent;
        PhotoInfo photoInfo = feedPhotoEntity.getPhotoInfo();
        String authorId = photoInfo.getOwnerId();
        if (TextUtils.isEmpty(authorId)) {
            BaseEntity feedContentOwner = (photoItem == null || !photoItem.isReshare()) ? FeedUtils.findFirstOwner(enclosingFeed.feed) : FeedUtils.findReshareOwner(photoItem);
            owner = new PhotoOwner(feedContentOwner.getId(), feedContentOwner.getType() == 7 ? 0 : 1);
        } else {
            int i;
            if (photoInfo.getOwnerType() == OwnerType.USER) {
                i = 0;
            } else {
                i = 1;
            }
            owner = new PhotoOwner(authorId, i);
        }
        String albumId = null;
        BaseEntity photoAlbum = FeedUtils.findFirstPhotoAlbum(enclosingFeed.feed);
        if (photoAlbum != null) {
            albumId = photoAlbum.getId();
        }
        if (albumId == null) {
            albumId = photoInfo.getAlbumId();
        }
        if (photoInfoPage != null) {
            String[] photoInfos = new String[photoInfoPage.getElements().size()];
            int i2 = 0;
            for (PhotoInfo photoInfo1 : photoInfoPage.getElements()) {
                int i3 = i2 + 1;
                photoInfos[i2] = photoInfo1.getId();
                i2 = i3;
            }
            intent = IntentUtils.createIntentForPhotoView(activity, owner, albumId, photoInfos, photoInfo, photoInfoPage, 1);
        } else {
            intent = IntentUtils.createIntentForPhotoView((Context) activity, owner, albumId, photoInfo, null, 1);
        }
        NavigationHelper.showPhoto(activity, intent, GifAsMp4PlayerHelper.shouldShowGifAsMp4(photoInfo) ? null : PhotoLayerAnimationHelper.makeScaleUpAnimationBundle(photoView, photoInfo.getStandartWidth(), photoInfo.getStandartHeight(), 0));
    }

    private void clickToLike(AbsFeedPhotoEntity feedPhotoEntity, ViewHolder viewHolder, LikeInfoContext likeInfo) {
        if (this.listener != null && likeInfo != null) {
            Logger.m173d("photoId: %s", feedPhotoEntity.getId());
            viewHolder.countersView.setInfo(new FeedFooterInfo(this.feed, this.listener.onLikePhotoClicked(this.feed.position, this.feed.feed, likeInfo), feedPhotoEntity.getDiscussionSummary(), null));
        }
    }

    private void clickToLikeCount(AbsFeedPhotoEntity feedPhotoEntity) {
        if (this.listener != null) {
            DiscussionSummary discussionSummary = feedPhotoEntity.getPhotoInfo().getDiscussionSummary();
            if (discussionSummary != null && discussionSummary.discussion != null) {
                NavigationHelper.showDiscussionLikes((Activity) this.context, discussionSummary.discussion);
            }
        }
    }

    @NonNull
    private PhotoViewStrategy pickStrategy(@NonNull PhotoInfo photoInfo) {
        return GifAsMp4PlayerHelper.shouldPlayGifAsMp4InPlace(photoInfo, AutoplayContext.FEED) ? this.gifAsMp4PhotoViewStrategy : this.staticPhotoViewStrategy;
    }

    public void unbind() {
        int size = this.unbindables.size();
        for (int i = 0; i < size; i++) {
            ((Unbindable) this.unbindables.valueAt(i)).unbind();
        }
    }
}
