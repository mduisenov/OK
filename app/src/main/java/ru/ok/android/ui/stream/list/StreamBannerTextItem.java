package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;

public class StreamBannerTextItem extends AbsStreamTextItem<CharSequence> {
    protected StreamBannerTextItem(FeedWithState feed, CharSequence text, BannerClickAction clickAction) {
        super(28, 1, 3, feed, text, (ClickAction) clickAction);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903471, parent, false);
    }
}
