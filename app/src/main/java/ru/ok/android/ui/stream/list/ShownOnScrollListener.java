package ru.ok.android.ui.stream.list;

import android.graphics.Rect;
import ru.ok.model.stream.Feed;

public interface ShownOnScrollListener {
    boolean onShownOnScroll(Feed feed, Rect rect, int i, int i2);
}
