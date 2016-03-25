package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.Feed2StreamItemBinder;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.wmf.Track;

public final class PresentTrackView extends BasePresentTrackView {
    private Track track;

    long getTrackId() {
        return this.track.id;
    }

    public void onStartPlayMusic() {
        MusicService.startPlayMusic(OdnoklassnikiApplication.getContext(), 0, new ArrayList(Arrays.asList(new Track[]{this.track})), MusicListType.NO_DIRECTION);
    }

    public PresentTrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    boolean isMusicPresent() {
        return this.track != null;
    }

    public void setTrack(FeedMusicTrackEntity trackEntity) {
        this.track = Feed2StreamItemBinder.trackFromEntity(trackEntity);
        updateIsPlayingState();
    }

    public void onClick(View v) {
        super.onClick(v);
        FeedWithState feed = (FeedWithState) v.getTag(2131624322);
        StreamStats.clickPlayPresent(feed.position, feed.feed);
    }
}
