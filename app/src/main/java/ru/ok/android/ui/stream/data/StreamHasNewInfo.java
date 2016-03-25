package ru.ok.android.ui.stream.data;

import ru.ok.model.stream.StreamPage;

public class StreamHasNewInfo {
    public final int newEventsCount;
    public final StreamPage page;

    public StreamHasNewInfo(int newEventsCount, StreamPage page) {
        this.newEventsCount = newEventsCount;
        this.page = page;
    }

    public String toString() {
        return "StreamHasNewInfo[newEventsCount=" + this.newEventsCount + " page=" + this.page + "]";
    }
}
