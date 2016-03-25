package ru.ok.android.ui.stream.list;

import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.noundla.centerviewpagersample.comps.StreamCenterLockViewPager;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.music.FeedMusicPlaylistAdapter;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.wmf.Track;

public class StreamMusicPagerItem extends StreamCenterLockPagerItem {
    private final Uri defaultCoverImageUrl;
    private final List<String> prefetchUrls;
    private final List<FeedMusicTrackEntity> trackEntities;
    private final ArrayList<Track> tracks;

    protected StreamMusicPagerItem(FeedWithState feed, ArrayList<Track> tracks, List<FeedMusicTrackEntity> trackEntities, Uri defaultCoverImageUrl, boolean isLastItemInFeed) {
        super(9, 2, 2, feed, isLastItemInFeed);
        this.tracks = tracks;
        this.trackEntities = trackEntities;
        this.defaultCoverImageUrl = defaultCoverImageUrl;
        this.prefetchUrls = new ArrayList(2);
        initPrefetchUrls();
    }

    private void initPrefetchUrls() {
        boolean addedDefaultImage = false;
        for (FeedMusicTrackEntity entity : this.trackEntities) {
            String coverImageUrl = entity.getImageUrl();
            if (TextUtils.isEmpty(coverImageUrl)) {
                if (!(addedDefaultImage || this.defaultCoverImageUrl == null)) {
                    this.prefetchUrls.add(this.defaultCoverImageUrl.toString());
                }
                addedDefaultImage = true;
            } else {
                this.prefetchUrls.add(coverImageUrl);
            }
            if (this.prefetchUrls.size() >= 2) {
                return;
            }
        }
    }

    public void prefetch() {
        for (String url : this.prefetchUrls) {
            PrefetchUtils.prefetchUrl(url);
        }
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        StreamCenterLockViewPager pager = (StreamCenterLockViewPager) inflater.inflate(2130903485, parent, false);
        pager.setPageMargin((int) TypedValue.applyDimension(1, 8.0f, inflater.getContext().getResources().getDisplayMetrics()));
        return pager;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder.itemView instanceof StreamCenterLockViewPager) {
            StreamCenterLockViewPager pager = holder.itemView;
            pager.setAdapter(new FeedMusicPlaylistAdapter(streamItemViewController.getActivity(), this.tracks, this.trackEntities, pager, streamItemViewController.getPlayerStateHolder(), streamItemViewController.getViewCache(), this.defaultCoverImageUrl, this.feedWithState), streamItemViewController.getViewPagerStateHolder().watchViewPager(this.feedWithState, pager));
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }
}
