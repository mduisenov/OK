package ru.ok.android.ui.stream.data;

import ru.ok.model.stream.Feed;

public final class FeedWithState {
    public final Feed feed;
    public int position;
    public boolean shownOnScrollSent;

    public FeedWithState(Feed feed) {
        this.position = -1;
        this.feed = feed;
    }

    public String toString() {
        return "Feed#" + this.feed.getId();
    }
}
