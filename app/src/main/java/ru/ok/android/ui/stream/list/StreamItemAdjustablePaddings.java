package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public abstract class StreamItemAdjustablePaddings extends StreamItem {
    private int paddingBottom;
    private int paddingTop;

    protected StreamItemAdjustablePaddings(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feedWithState) {
        super(viewType, topEdgeType, bottomEdgeType, feedWithState);
        this.paddingTop = LinearLayoutManager.INVALID_OFFSET;
        this.paddingBottom = LinearLayoutManager.INVALID_OFFSET;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        holder.itemView.setPadding(holder.itemView.getPaddingLeft(), getPaddingTop(holder), holder.itemView.getPaddingRight(), getPaddintBottom(holder));
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    void applyExtraMarginsToPaddings(ViewHolder holder, int extraLeftPadding, int extraRightPadding, StreamLayoutConfig layoutConfig) {
        int paddingLeft = holder.originalLeftPadding + extraLeftPadding;
        int paddingRight = holder.originalRightPadding + extraRightPadding;
        holder.itemView.setPadding(paddingLeft, getPaddingTop(holder), paddingRight, getPaddintBottom(holder));
    }

    private int getPaddintBottom(ViewHolder holder) {
        return this.paddingBottom == LinearLayoutManager.INVALID_OFFSET ? holder.originalBottomPadding : this.paddingBottom;
    }

    private int getPaddingTop(ViewHolder holder) {
        return this.paddingTop == LinearLayoutManager.INVALID_OFFSET ? holder.originalTopPadding : this.paddingTop;
    }

    int getVSpacingTop(Context context) {
        if (this.paddingTop == LinearLayoutManager.INVALID_OFFSET) {
            return super.getVSpacingTop(context);
        }
        return this.paddingTop;
    }

    int getVSpacingBottom(Context context) {
        if (this.paddingBottom == LinearLayoutManager.INVALID_OFFSET) {
            return super.getVSpacingBottom(context);
        }
        return this.paddingBottom;
    }
}
