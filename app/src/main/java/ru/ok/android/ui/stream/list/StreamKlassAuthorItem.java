package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.view.FeedHeaderInfo;

public class StreamKlassAuthorItem extends AbsStreamContentHeaderItem {
    protected StreamKlassAuthorItem(FeedWithState feedWithState, FeedHeaderInfo info) {
        super(33, 1, 1, feedWithState, info, false);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903480, parent, false);
    }
}
