package ru.ok.android.ui.stream.list;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.wmf.Track;

public class StreamMusicCoverItem extends AbsStreamMusicTrackItem {
    protected StreamMusicCoverItem(FeedWithState feed, ArrayList<Track> playlist, List<FeedMusicTrackEntity> trackEntities, int trackPosition, Uri defaultCoverImageUri) {
        super(8, 3, 3, feed, playlist, trackEntities, trackPosition, defaultCoverImageUri);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903484, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.bindView(holder, streamItemViewController, layoutConfig);
        if (holder instanceof TrackViewHolder) {
            ((TrackViewHolder) holder).trackView.setTag(2131624343, "music_cover");
        }
    }

    protected boolean hasCoverImage() {
        return true;
    }
}
