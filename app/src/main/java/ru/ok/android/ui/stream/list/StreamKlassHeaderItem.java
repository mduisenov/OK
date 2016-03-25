package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.view.FeedHeaderInfo;

public class StreamKlassHeaderItem extends AbsStreamContentHeaderItem {
    protected StreamKlassHeaderItem(FeedWithState feed, FeedHeaderInfo info, boolean hasOptions, boolean isCardTop) {
        int i;
        if (isCardTop) {
            i = 4;
        } else {
            i = 1;
        }
        super(32, i, 1, feed, info, hasOptions);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903481, parent, false);
    }
}
