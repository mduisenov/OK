package ru.ok.android.ui.stream.list;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.drawee.view.SimpleDraweeView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.android.utils.StreamUtils;

public class StreamBannerImageItem extends AbsStreamClickableItem {
    private final float aspectRatio;
    private final Uri imageUri;

    static class BannerImageHolder extends ViewHolder {
        final SimpleDraweeView imageView;

        public BannerImageHolder(View view) {
            super(view);
            this.imageView = (SimpleDraweeView) view.findViewById(2131625345);
        }
    }

    protected StreamBannerImageItem(FeedWithState feed, Uri imageUri, float aspectRatio, BannerClickAction clickAction) {
        super(29, 2, 2, feed, clickAction);
        this.imageUri = imageUri;
        this.aspectRatio = aspectRatio;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903470, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof BannerImageHolder) {
            SimpleDraweeView imageView = ((BannerImageHolder) holder).imageView;
            imageView.setAspectRatio(this.aspectRatio);
            imageView.setImageURI(this.imageUri);
            StreamUtils.applyExtraMarginsToLandscapeImagePaddings(holder, layoutConfig);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void updateForLayoutSize(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.updateForLayoutSize(holder, streamItemViewController, layoutConfig);
        if (holder instanceof BannerImageHolder) {
            StreamUtils.applyExtraMarginsToLandscapeImagePaddings(holder, layoutConfig);
        }
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new BannerImageHolder(view);
    }

    public void prefetch() {
        PrefetchUtils.prefetchUrl(this.imageUri);
    }
}
