package ru.ok.android.ui.stream.list;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.noundla.centerviewpagersample.comps.StreamCenterLockViewPager;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.photos.PhotosFeedAdapter;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.FeedUtils;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;
import ru.ok.model.stream.entities.BaseEntity;

public class StreamPhotoLayerItem extends StreamCenterLockPagerItem {
    private final float aspectRatio;
    private final List<? extends BaseEntity> photos;
    private final List<Uri> prefetchLowQualityUris;
    private final List<Uri> prefetchUris;
    private final boolean simpleMode;
    private final List<PhotoInfo> tagPhotos;

    protected StreamPhotoLayerItem(FeedWithState feed, List<? extends BaseEntity> photos, boolean simpleMode, boolean isLastItemInFeed) {
        this(feed, photos, -1.0f, simpleMode, isLastItemInFeed);
    }

    protected StreamPhotoLayerItem(FeedWithState feed, List<? extends BaseEntity> photos, float aspectRatio, boolean simpleMode, boolean isLastItemInFeed) {
        super(3, 2, 2, feed, isLastItemInFeed);
        this.prefetchUris = new ArrayList();
        this.prefetchLowQualityUris = new ArrayList();
        this.photos = photos;
        this.aspectRatio = aspectRatio;
        this.simpleMode = simpleMode;
        this.tagPhotos = FeedUtils.getPhotoInfos(photos);
        initUris();
    }

    private static void addSizeToList(PhotoSize photoSize, List<Uri> uris) {
        if (uris.size() < 2 && photoSize != null) {
            String url = photoSize.getUrl();
            if (!TextUtils.isEmpty(url)) {
                uris.add(Uri.parse(url));
            }
        }
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903488, parent, false);
    }

    private void initUris() {
        int highQualityPhotoWidth = DeviceUtils.getStreamHighQualityPhotoWidth();
        int lowQualityPhotoWidth = DeviceUtils.getStreamLowQualityPhotoWidth();
        for (BaseEntity entity : this.photos) {
            if (entity instanceof AbsFeedPhotoEntity) {
                PhotoInfo photoInfo = ((AbsFeedPhotoEntity) entity).getPhotoInfo();
                PhotoSize photoSizeHighQuality = photoInfo.getSizeFloor(highQualityPhotoWidth);
                PhotoSize photoSizeLowQuality = photoInfo.getSizeFloor(lowQualityPhotoWidth);
                addSizeToList(photoSizeHighQuality, this.prefetchUris);
                addSizeToList(photoSizeLowQuality, this.prefetchLowQualityUris);
            }
        }
    }

    public void prefetch() {
        for (Uri uri : this.prefetchLowQualityUris) {
            PrefetchUtils.prefetchUrl(uri, false);
        }
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof PagerViewHolder) {
            StreamCenterLockViewPager photoPager = ((PagerViewHolder) holder).pager;
            PhotosFeedAdapter photoAdapter = new PhotosFeedAdapter(streamItemViewController.getActivity(), photoPager, this.photos, this.feedWithState, streamItemViewController.getStreamAdapterListener(), this.simpleMode, streamItemViewController.getViewCache());
            photoPager.setAdapter(photoAdapter, streamItemViewController.getViewPagerStateHolder().watchViewPager(this.feedWithState, photoPager));
            if (this.aspectRatio < 0.0f) {
                photoPager.setAspectRatio(photoPager.getInitialAspectRatio());
            } else {
                photoPager.setAspectRatio(this.aspectRatio);
            }
            photoAdapter.notifyDataSetChanged();
        }
        holder.itemView.setTag(2131624334, this.tagPhotos);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void onUnbindView(@NonNull ViewHolder holder) {
        if (holder instanceof PagerViewHolder) {
            PagerAdapter adapter = ((PagerViewHolder) holder).pager.getAdapter();
            if (adapter instanceof PhotosFeedAdapter) {
                ((PhotosFeedAdapter) adapter).unbind();
            }
        }
    }

    public void updateForLayoutSize(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.updateForLayoutSize(holder, streamItemViewController, layoutConfig);
        if (layoutConfig.screenOrientation == 2 && !this.simpleMode && !this.isLastItemInFeed) {
            View view = holder.itemView;
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getResources().getDimensionPixelOffset(2131230986));
        }
    }
}
