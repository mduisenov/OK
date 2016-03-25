package ru.ok.android.ui.stream.list;

import android.view.View;
import com.noundla.centerviewpagersample.comps.StreamCenterLockViewPager;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public abstract class StreamCenterLockPagerItem extends StreamItem {
    protected boolean isLastItemInFeed;

    protected static class PagerViewHolder extends ViewHolder {
        final int originalLeftOffset;
        final int originalRightOffset;
        final StreamCenterLockViewPager pager;

        public PagerViewHolder(View view) {
            super(view);
            this.pager = (StreamCenterLockViewPager) view.findViewById(C0263R.id.pager);
            this.originalLeftOffset = this.pager.getLeftOffset();
            this.originalRightOffset = this.pager.getRightOffset();
        }
    }

    protected StreamCenterLockPagerItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feedWithState, boolean isLastItemInFeed) {
        super(viewType, topEdgeType, bottomEdgeType, feedWithState);
        this.isLastItemInFeed = isLastItemInFeed;
    }

    public static PagerViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new PagerViewHolder(view);
    }

    boolean isLaidOutInsideCard() {
        return false;
    }

    public void updateForLayoutSize(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.updateForLayoutSize(holder, streamItemViewController, layoutConfig);
        if (holder instanceof PagerViewHolder) {
            int leftOffset;
            int rightOffset;
            PagerViewHolder pagerViewHolder = (PagerViewHolder) holder;
            int bottomPadding = holder.originalBottomPadding;
            View view = holder.itemView;
            if (layoutConfig.screenOrientation == 2) {
                int extraMargin = layoutConfig.getExtraMarginForLandscapeAsInPortrait(true);
                int extraMarginForWidth = layoutConfig.getExtraMarginForLandscapeAsInPortrait(false);
                int innerPaddingSize = view.getResources().getDimensionPixelOffset(2131230965);
                leftOffset = (pagerViewHolder.originalLeftOffset + extraMargin) + innerPaddingSize;
                rightOffset = ((((pagerViewHolder.originalRightOffset + layoutConfig.listViewWidth) - layoutConfig.listViewPortraitWidth) - extraMarginForWidth) + (extraMarginForWidth - extraMargin)) + innerPaddingSize;
                if (this.isLastItemInFeed) {
                    bottomPadding = innerPaddingSize;
                }
            } else {
                leftOffset = pagerViewHolder.originalLeftOffset;
                rightOffset = pagerViewHolder.originalRightOffset;
            }
            pagerViewHolder.pager.setOffsets(leftOffset, rightOffset);
            holder.itemView.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), bottomPadding);
        }
    }
}
