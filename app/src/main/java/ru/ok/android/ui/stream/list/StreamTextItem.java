package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;

public final class StreamTextItem extends AbsStreamTextItem<CharSequence> {
    protected StreamTextItem(FeedWithState feed, CharSequence text, ClickAction clickAction) {
        super(4, 3, 3, feed, text, clickAction);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903509, parent, false);
    }
}
