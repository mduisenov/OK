package ru.ok.android.ui.stream.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.fresco.FrescoGifMarkerView;
import ru.ok.android.model.pagination.impl.PhotoInfoPage;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.ViewUtil;
import ru.ok.model.photo.PhotoInfo;

public class TwoPhotoCollageItem extends StreamItem {
    private final PhotoCollagePart leftCollagePart;
    private final PhotoInfoPage page;
    private final PhotoCollagePart rightCollagePart;
    private final List<PhotoInfo> tagPhotos;

    public interface PhotoLocate {
        int locate(ViewHolder viewHolder, View view, StreamLayoutConfig streamLayoutConfig);
    }

    public static class OrientationDecoratorLocate implements PhotoLocate {
        private final PhotoLocate landscape;
        private final PhotoLocate portrait;

        public OrientationDecoratorLocate(PhotoLocate portrait, PhotoLocate landscape) {
            this.landscape = landscape;
            this.portrait = portrait;
        }

        public int locate(ViewHolder holder, View imageView, StreamLayoutConfig layoutConfig) {
            if (layoutConfig.screenOrientation == 2) {
                return this.landscape.locate(holder, imageView, layoutConfig);
            }
            return this.portrait.locate(holder, imageView, layoutConfig);
        }
    }

    public static class TwoPhotoCollageViewHolder extends ViewHolder {
        private final FrescoGifMarkerView smallLeftImageView;
        private final FrescoGifMarkerView smallRightImageView;

        public TwoPhotoCollageViewHolder(View view) {
            super(view);
            this.smallLeftImageView = (FrescoGifMarkerView) view.findViewById(2131625362);
            this.smallRightImageView = (FrescoGifMarkerView) view.findViewById(2131625363);
        }
    }

    public static class WidthAspectRatioPhotoLocate implements PhotoLocate {
        private final int border;
        private final float imageAspectRatio;
        private final float widthAspectRatio;

        public WidthAspectRatioPhotoLocate(float imageAspectRatio, float widthAspectRatio, int border) {
            this.imageAspectRatio = imageAspectRatio;
            this.widthAspectRatio = widthAspectRatio;
            this.border = border;
        }

        public int locate(ViewHolder holder, View imageView, StreamLayoutConfig layoutConfig) {
            int width = (int) (this.widthAspectRatio * ((float) ((((layoutConfig.listViewWidth - holder.originalLeftPadding) - holder.originalRightPadding) - (layoutConfig.getExtraMarginForLandscapeAsInPortrait(true) * 2)) - this.border)));
            int height = (int) (((float) width) / this.imageAspectRatio);
            if (imageView instanceof FrescoGifMarkerView) {
                ((FrescoGifMarkerView) imageView).setAspectRatio(this.imageAspectRatio);
            }
            ViewUtil.resetLayoutParams(imageView, width, height);
            return Math.max(width, height);
        }
    }

    public static class WidthAspectRatioWithMarginLocate implements PhotoLocate {
        private final float imageAspectRatio;
        private final int leftMargin;
        private final int rightMargin;
        private final float widthAspectRatio;

        public WidthAspectRatioWithMarginLocate(float imageAspectRatio, float widthAspectRatio, int leftMargin, int rightMargin) {
            this.imageAspectRatio = imageAspectRatio;
            this.widthAspectRatio = widthAspectRatio;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
        }

        public int locate(ViewHolder holder, View imageView, StreamLayoutConfig layoutConfig) {
            int width = (int) (this.widthAspectRatio * ((float) (((((layoutConfig.listViewWidth - holder.originalLeftPadding) - holder.originalRightPadding) - (layoutConfig.getExtraMarginForLandscapeAsInPortrait(true) * 2)) - this.leftMargin) - this.rightMargin)));
            int height = (int) (((float) width) / this.imageAspectRatio);
            ViewUtil.resetLayoutParams(imageView, width, height, this.leftMargin, this.rightMargin);
            return Math.max(width, height);
        }
    }

    public static class WidthHeightWithMarginLocate implements PhotoLocate {
        private final int height;
        private final int leftMargin;
        private final int rightMargin;
        private final int width;

        public WidthHeightWithMarginLocate(int width, int height, int leftMargin, int rightMargin) {
            this.width = width;
            this.height = height;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
        }

        public int locate(ViewHolder holder, View imageView, StreamLayoutConfig layoutConfig) {
            ViewUtil.resetLayoutParams(imageView, this.width, this.height, this.leftMargin, this.rightMargin);
            return Math.max(this.width, this.height);
        }
    }

    public TwoPhotoCollageItem(FeedWithState feedWithState, PhotoCollagePart leftCollagePart, PhotoCollagePart rightCollagePart, @Nullable PhotoInfoPage page) {
        super(39, 2, 3, feedWithState);
        this.leftCollagePart = leftCollagePart;
        this.rightCollagePart = rightCollagePart;
        this.page = page;
        this.tagPhotos = new ArrayList(2);
        this.tagPhotos.add(leftCollagePart.getPhotoInfo());
        this.tagPhotos.add(rightCollagePart.getPhotoInfo());
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903491, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof TwoPhotoCollageViewHolder) {
            TwoPhotoCollageViewHolder collageViewHolder = (TwoPhotoCollageViewHolder) holder;
            this.leftCollagePart.bindView(holder, collageViewHolder.smallLeftImageView, streamItemViewController, layoutConfig);
            this.rightCollagePart.bindView(holder, collageViewHolder.smallRightImageView, streamItemViewController, layoutConfig);
            collageViewHolder.smallLeftImageView.setTag(2131624332, this.page);
            collageViewHolder.smallRightImageView.setTag(2131624332, this.page);
        }
        holder.itemView.setTag(2131624334, this.tagPhotos);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void onUnbindView(@NonNull ViewHolder holder) {
        super.onUnbindView(holder);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new TwoPhotoCollageViewHolder(view);
    }

    public void prefetch() {
        this.leftCollagePart.prefetch();
        this.rightCollagePart.prefetch();
    }
}
