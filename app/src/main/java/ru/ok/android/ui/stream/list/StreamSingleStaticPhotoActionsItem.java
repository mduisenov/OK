package ru.ok.android.ui.stream.list;

import android.graphics.drawable.Animatable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.imagepipeline.image.ImageInfo;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedFooterView;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public class StreamSingleStaticPhotoActionsItem extends AbsStreamSingleStaticPhotoItem {
    private final FeedFooterContextBinder mFeedFooterContextBinder;

    static class StaticImageActionsViewHolder extends StaticImageViewHolder implements FeedFooterViewHolder {
        public final FeedFooterViewHelper feedFooterViewHelper;
        private final StreamItemViewController streamItemViewController;

        /* renamed from: ru.ok.android.ui.stream.list.StreamSingleStaticPhotoActionsItem.StaticImageActionsViewHolder.1 */
        class C12401 extends BaseControllerListener<ImageInfo> {
            C12401() {
            }

            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                StaticImageActionsViewHolder.this.streamItemViewController.getPhotoActionsVisibilityListener().onFinishedSetImage(StaticImageActionsViewHolder.this.imageView, true);
            }
        }

        StaticImageActionsViewHolder(View view, StreamItemViewController streamItemViewController) {
            super(view, streamItemViewController);
            this.streamItemViewController = streamItemViewController;
            this.feedFooterViewHelper = new FeedFooterViewHelper();
        }

        public void setImageListener(PipelineDraweeControllerBuilder controller) {
            controller.setControllerListener(new C12401());
        }

        public FeedFooterView getFeedFooterView(@NonNull StreamItemViewController streamItemViewController) {
            return this.feedFooterViewHelper.getView(this.itemView, streamItemViewController);
        }

        public void hideFeedFooterView() {
            this.feedFooterViewHelper.hideView();
        }

        public void setTagFor(FeedFooterView view) {
            this.imageView.setTag(2131624319, view);
        }
    }

    protected StreamSingleStaticPhotoActionsItem(int bottomEdgeType, FeedWithState feed, AbsFeedPhotoEntity photo, MediaItemPhoto mediaItem, float aspectRatio) {
        super(36, 2, bottomEdgeType, feed, photo, mediaItem, aspectRatio);
        this.mFeedFooterContextBinder = new FeedFooterContextBinder(feed, photo);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new StaticImageActionsViewHolder(view, streamItemViewController);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903505, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof StaticImageActionsViewHolder) {
            StaticImageActionsViewHolder imageViewHolder = (StaticImageActionsViewHolder) holder;
            this.mFeedFooterContextBinder.bind(streamItemViewController, imageViewHolder, imageViewHolder, this.photo);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void updateForLayoutSize(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        int bottomPadding;
        super.updateForLayoutSize(holder, streamItemViewController, layoutConfig);
        if (layoutConfig.screenOrientation == 2) {
            bottomPadding = holder.itemView.getResources().getDimensionPixelOffset(2131230985);
        } else {
            bottomPadding = holder.originalBottomPadding;
        }
        holder.itemView.setPadding(holder.itemView.getPaddingLeft(), holder.itemView.getPaddingTop(), holder.itemView.getPaddingRight(), bottomPadding);
        if ((holder instanceof InnerViewHolder) && layoutConfig.screenOrientation == 1) {
            int radius = holder.itemView.getResources().getDimensionPixelSize(2131230962);
            ((GenericDraweeHierarchy) ((StaticImageViewHolder) holder).imageView.getHierarchy()).setRoundingParams(RoundingParams.fromCornersRadii(0.0f, 0.0f, (float) radius, (float) radius));
        }
    }
}
