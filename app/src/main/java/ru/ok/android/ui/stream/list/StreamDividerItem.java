package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;

public class StreamDividerItem extends StreamItem {
    private final int eatBottomSpace;

    protected StreamDividerItem(FeedWithState feed) {
        this(feed, 0);
    }

    protected StreamDividerItem(FeedWithState feed, int eatBottomSpace) {
        super(2, 2, 2, feed);
        this.eatBottomSpace = eatBottomSpace;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903475, parent, false);
    }

    int getVSpacingBottom(Context context) {
        return this.eatBottomSpace;
    }
}
