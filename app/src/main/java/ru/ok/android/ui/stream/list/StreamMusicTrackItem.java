package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
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

public class StreamMusicTrackItem extends AbsStreamMusicTrackItem {
    protected StreamMusicTrackItem(FeedWithState feed, ArrayList<Track> playlist, List<FeedMusicTrackEntity> trackEntities, int trackPosition, Uri defaultCoverImageUri) {
        super(7, 3, 3, feed, playlist, trackEntities, trackPosition, defaultCoverImageUri);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903486, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.bindView(holder, streamItemViewController, layoutConfig);
        if (holder instanceof TrackViewHolder) {
            ((TrackViewHolder) holder).trackView.setTag(2131624343, "track");
        }
    }

    int getVSpacingTop(Context context) {
        return (int) TypedValue.applyDimension(1, 8.0f, context.getResources().getDisplayMetrics());
    }

    int getVSpacingBottom(Context context) {
        return (int) TypedValue.applyDimension(1, 8.0f, context.getResources().getDisplayMetrics());
    }

    protected boolean hasCoverImage() {
        return false;
    }
}
