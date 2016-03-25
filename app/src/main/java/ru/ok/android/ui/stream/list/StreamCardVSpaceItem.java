package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;

public class StreamCardVSpaceItem extends StreamItem {
    protected StreamCardVSpaceItem(FeedWithState feed) {
        super(23, 1, 4, feed);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903474, parent, false);
    }
}
