package ru.ok.android.ui.stream.list;

import android.support.annotation.Nullable;
import android.view.View;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public abstract class AbsStreamClickableItem extends StreamItem {
    protected ClickAction clickAction;

    protected AbsStreamClickableItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feedWithState, @Nullable ClickAction clickAction) {
        super(viewType, topEdgeType, bottomEdgeType, feedWithState);
        this.clickAction = clickAction;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        setupClick(holder.itemView, streamItemViewController, this.clickAction);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static void setupClick(View view, StreamItemViewController streamItemViewController, ClickAction clickAction) {
        if (clickAction != null) {
            clickAction.setClickListener(view, streamItemViewController);
            clickAction.setTags(view);
            return;
        }
        view.setOnClickListener(null);
        view.setClickable(false);
    }
}
