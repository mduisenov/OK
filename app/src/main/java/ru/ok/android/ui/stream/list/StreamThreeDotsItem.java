package ru.ok.android.ui.stream.list;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.model.pagination.Page;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class StreamThreeDotsItem extends AbsStreamClickableItem {
    private final Page page;
    private final boolean sharePressedState;

    protected StreamThreeDotsItem(FeedWithState feedWithState, @Nullable ClickAction action, @Nullable Page page) {
        super(41, 1, 1, feedWithState, action);
        this.page = page;
        this.sharePressedState = false;
    }

    protected StreamThreeDotsItem(FeedWithState feedWithState, @Nullable ClickAction action, @Nullable Page page, boolean sharePressedState) {
        super(41, 4, 4, feedWithState, action);
        this.page = page;
        this.sharePressedState = sharePressedState;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903490, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        holder.itemView.setTag(2131624324, Boolean.valueOf(false));
        holder.itemView.setTag(2131624332, this.page);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    boolean sharePressedState() {
        return this.sharePressedState;
    }
}
