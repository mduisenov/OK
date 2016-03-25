package ru.ok.android.ui.stream.list.controller;

import android.app.Activity;
import android.support.annotation.Nullable;
import ru.ok.android.statistics.stream.StreamBannerStatisticsHandler;
import ru.ok.android.ui.stream.StreamListStatistics;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;

public abstract class AbsStreamStatisticsViewController extends AbsStreamLinksProcessorsViewController {
    @Nullable
    private StreamBannerStatisticsHandler bannerStatHandler;
    private StreamListStatistics stats;

    public AbsStreamStatisticsViewController(Activity activity, StreamAdapterListener listener, String logContext) {
        super(activity, listener, logContext);
    }

    public void setStreamBannerStatisticsHandler(StreamBannerStatisticsHandler bannerStatHandler) {
        this.bannerStatHandler = bannerStatHandler;
    }

    public void setStreamListStatistics(StreamListStatistics stats) {
        this.stats = stats;
    }

    @Nullable
    public StreamBannerStatisticsHandler getStreamBannerStatisticsHandler() {
        return this.bannerStatHandler;
    }

    public StreamListStatistics getStreamListStatistics() {
        return this.stats;
    }
}
