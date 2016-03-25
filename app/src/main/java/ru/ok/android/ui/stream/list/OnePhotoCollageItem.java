package ru.ok.android.ui.stream.list;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Collections;
import java.util.List;
import ru.ok.android.fresco.FrescoGifMarkerView;
import ru.ok.android.model.pagination.impl.PhotoInfoPage;
import ru.ok.android.ui.custom.imageview.ViewWithSlideMode;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.photo.PhotoInfo;

public class OnePhotoCollageItem extends StreamItem {
    private final PhotoCollagePart collagePart;
    private final PhotoInfoPage photoInfoPage;
    private final List<PhotoInfo> tagPhotos;

    public static class OnePhotoCollageViewHolder extends ViewHolder {
        public final FrescoGifMarkerView imageView;

        public OnePhotoCollageViewHolder(View view, StreamItemViewController streamItemViewController) {
            super(view);
            this.imageView = (FrescoGifMarkerView) view.findViewById(2131625362);
        }
    }

    public OnePhotoCollageItem(FeedWithState feedWithState, int edgeBottomType, PhotoCollagePart collagePart) {
        this(feedWithState, edgeBottomType, collagePart, null);
    }

    public OnePhotoCollageItem(FeedWithState feedWithState, int edgeBottomType, PhotoCollagePart collagePart, @Nullable PhotoInfoPage photoInfoPage) {
        super(40, 2, edgeBottomType, feedWithState);
        this.collagePart = collagePart;
        this.photoInfoPage = photoInfoPage;
        this.tagPhotos = Collections.singletonList(collagePart.getPhotoInfo());
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903489, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof OnePhotoCollageViewHolder) {
            OnePhotoCollageViewHolder collageViewHolder = (OnePhotoCollageViewHolder) holder;
            if (collageViewHolder.imageView instanceof ViewWithSlideMode) {
                ((ViewWithSlideMode) collageViewHolder.imageView).setSlidingMode(this.collagePart.isPanorama());
            }
            this.collagePart.bindView(holder, collageViewHolder.imageView, streamItemViewController, layoutConfig);
            collageViewHolder.imageView.setTag(2131624332, this.photoInfoPage);
        }
        holder.itemView.setTag(2131624334, this.tagPhotos);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void prefetch() {
        this.collagePart.prefetch();
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new OnePhotoCollageViewHolder(view, streamItemViewController);
    }
}
