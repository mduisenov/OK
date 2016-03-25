package ru.ok.android.utils;

import java.util.List;
import ru.ok.model.stream.Feed;

public final class LogUtils {

    private interface FeedToString<T> {
        String toString(T t);
    }

    public static void logFeeds(List<Feed> feeds, String prefix) {
        if (Logger.isLoggingEnable()) {
            logFeeds(feeds, prefix, null);
        }
    }

    private static <T> void logFeeds(List<T> feeds, String prefix, FeedToString<T> feedToString) {
        if (feeds == null) {
            Logger.m173d("%s feeds=null", prefix);
            return;
        }
        Logger.m173d("%s feeds.size=%d {", prefix, Integer.valueOf(feeds.size()));
        for (int i = 0; i < feeds.size(); i++) {
            String feedStr;
            if (feedToString != null) {
                feedStr = feedToString.toString(feeds.get(i));
            } else {
                feedStr = String.valueOf(feeds.get(i));
            }
            Logger.m173d("%s     feed[%d]=%s", prefix, Integer.valueOf(i), feedStr);
        }
        Logger.m173d("%s }", prefix);
    }
}
