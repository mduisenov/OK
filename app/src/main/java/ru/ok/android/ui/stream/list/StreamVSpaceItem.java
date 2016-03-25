package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class StreamVSpaceItem extends AbsStreamClickableItem {
    private final int height;

    protected StreamVSpaceItem(FeedWithState feed, ClickAction clickAction, int height) {
        super(21, 1, 1, feed, clickAction);
        this.height = height;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903515, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null) {
            lp.height = this.height;
            holder.itemView.setLayoutParams(lp);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }
}
