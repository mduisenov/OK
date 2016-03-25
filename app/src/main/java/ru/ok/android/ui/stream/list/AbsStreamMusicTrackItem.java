package ru.ok.android.ui.stream.list;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.StreamTrackView;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.wmf.Track;

public abstract class AbsStreamMusicTrackItem extends StreamItem {
    private final Uri defaultCoverImageUri;
    private final ArrayList<Track> playlist;
    private final List<FeedMusicTrackEntity> trackEntities;
    private final int trackPosition;

    static class TrackViewHolder extends ViewHolder {
        final StreamTrackView trackView;

        TrackViewHolder(View view) {
            super(view);
            this.trackView = (StreamTrackView) view.findViewById(2131625361);
        }
    }

    protected abstract boolean hasCoverImage();

    protected AbsStreamMusicTrackItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feed, ArrayList<Track> playlist, List<FeedMusicTrackEntity> trackEntities, int trackPosition, Uri defaultCoverImageUri) {
        super(viewType, topEdgeType, bottomEdgeType, feed);
        this.playlist = playlist;
        this.trackEntities = trackEntities;
        this.trackPosition = trackPosition;
        this.defaultCoverImageUri = defaultCoverImageUri;
    }

    public void prefetch() {
        if (hasCoverImage() && this.trackPosition >= 0 && this.trackPosition < this.trackEntities.size()) {
            String url = ((FeedMusicTrackEntity) this.trackEntities.get(this.trackPosition)).getImageUrl();
            if (!TextUtils.isEmpty(url)) {
                PrefetchUtils.prefetchUrl(url);
            } else if (this.defaultCoverImageUri != null) {
                PrefetchUtils.prefetchUrl(this.defaultCoverImageUri);
            }
        }
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof TrackViewHolder) {
            TrackViewHolder trackViewHolder = (TrackViewHolder) holder;
            trackViewHolder.trackView.configureWith(this.trackPosition, this.playlist, this.trackEntities, this.defaultCoverImageUri);
            trackViewHolder.trackView.setTag(2131624322, this.feedWithState);
            trackViewHolder.trackView.setTag(2131624342, this.feedWithState.feed);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        TrackViewHolder viewHolder = new TrackViewHolder(view);
        viewHolder.trackView.setPlayerStateHolder(streamItemViewController.getPlayerStateHolder());
        viewHolder.trackView.setListener(streamItemViewController.getStreamTrackViewListener());
        return viewHolder;
    }

    public String toString() {
        return String.format("AbsStreamMusicTrackItem{playlist %s}", new Object[]{this.playlist});
    }
}
