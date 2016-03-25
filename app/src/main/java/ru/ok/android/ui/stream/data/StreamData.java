package ru.ok.android.ui.stream.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import ru.ok.android.ui.stream.list.StreamItem;
import ru.ok.model.stream.Holidays;
import ru.ok.model.stream.StreamPage;
import ru.ok.model.stream.StreamPageKey;
import ru.ok.model.stream.banner.PromoLink;

public final class StreamData {
    @Nullable
    StreamPageKey bottomPageKey;
    int deliverResultId;
    @NonNull
    public final ArrayList<FeedWithState> feeds;
    boolean hasRefreshedFromWeb;
    public final ArrayList<PromoLink> headerBanners;
    @Nullable
    public Holidays holidays;
    @NonNull
    public final ArrayList<StreamItem> items;
    @NonNull
    public final LinkedList<StreamPage> pages;
    @Nullable
    StreamPageKey topPageKey;

    public ArrayList<StreamItem> getItems() {
        return this.items;
    }

    public boolean canLoadTop() {
        return this.topPageKey != null;
    }

    public boolean canLoadBottom() {
        return this.bottomPageKey != null;
    }

    public boolean canHaveData() {
        return (this.items.isEmpty() && this.topPageKey == null && this.bottomPageKey == null) ? false : true;
    }

    public String toString() {
        return "StreamData[pages.size=" + this.pages.size() + " feeds.size=" + this.feeds.size() + " items.size=" + this.items.size() + " topPageKey=" + this.topPageKey + " bottomPageKey=" + this.bottomPageKey + " hasRefreshedFromWeb=" + this.hasRefreshedFromWeb + " headerBanners.size=" + this.headerBanners.size() + "]";
    }

    StreamData() {
        this.headerBanners = new ArrayList();
        this.pages = new LinkedList();
        this.feeds = new ArrayList();
        this.items = new ArrayList();
    }

    StreamData(StreamData copy) {
        this.headerBanners = new ArrayList();
        this.pages = new LinkedList(copy.pages);
        this.feeds = new ArrayList(copy.feeds);
        this.items = new ArrayList(copy.items);
        this.topPageKey = copy.topPageKey;
        this.bottomPageKey = copy.bottomPageKey;
        this.hasRefreshedFromWeb = copy.hasRefreshedFromWeb;
        this.holidays = copy.holidays;
        this.headerBanners.addAll(copy.headerBanners);
    }
}
