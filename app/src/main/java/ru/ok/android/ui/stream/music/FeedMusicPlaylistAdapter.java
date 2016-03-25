package ru.ok.android.ui.stream.music;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import com.noundla.centerviewpagersample.comps.CenterLockPagerAdapter;
import com.noundla.centerviewpagersample.comps.StreamCenterLockViewPager;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.music.PlayerStateHolder.PlayerStateHolderListener;
import ru.ok.android.ui.stream.view.StreamMusicTrackBigView;
import ru.ok.android.ui.stream.viewcache.StreamViewCache;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.wmf.Track;

public class FeedMusicPlaylistAdapter extends CenterLockPagerAdapter implements PlayerStateHolderListener {
    private final Uri defaultCoverImageUrl;
    private final List<FeedMusicTrackEntity> entities;
    private final FeedWithState feedWithState;
    private final StreamCenterLockViewPager pager;
    private final PlayerStateHolder playerStateHolder;
    private long prevScrollSongId;
    private final ArrayList<Track> tracks;
    private final StreamViewCache viewCache;

    public FeedMusicPlaylistAdapter(Context context, ArrayList<Track> tracks, List<FeedMusicTrackEntity> entities, StreamCenterLockViewPager pager, PlayerStateHolder playerStateHolder, StreamViewCache viewCache, Uri defaultCoverImageUrl, FeedWithState feedWithState) {
        this.tracks = tracks;
        this.entities = entities;
        this.pager = pager;
        this.playerStateHolder = playerStateHolder;
        this.viewCache = viewCache;
        this.defaultCoverImageUrl = defaultCoverImageUrl;
        this.feedWithState = feedWithState;
        playerStateHolder.addStateChangeListener(this);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        View view = newView(container);
        bindView(position, view);
        container.addView(view);
        return view;
    }

    private void bindView(int position, View view) {
        ((StreamMusicTrackBigView) view).configureWith(position, this.tracks, this.entities, this.defaultCoverImageUrl);
    }

    private View newView(ViewGroup parent) {
        StreamMusicTrackBigView mainView = (StreamMusicTrackBigView) this.viewCache.getViewWithLayoutId(2130903522, parent);
        mainView.setPlayerStateHolder(this.playerStateHolder);
        mainView.setTag(2131624343, "music_cover");
        mainView.setTag(2131624322, this.feedWithState);
        return mainView;
    }

    public void destroyItem(ViewGroup collection, int position, Object object) {
        ViewGroup view = (ViewGroup) object;
        if (view.getParent() != null) {
            this.viewCache.collectThisView(view);
            collection.removeView(view);
        }
    }

    public float getPageWidth(int position) {
        int count = getCount();
        if (count < 2 || position >= count) {
            return 1.0f;
        }
        int viewportWidth = (this.pager.getMeasuredWidth() - this.pager.getPaddingLeft()) - this.pager.getPaddingRight();
        int viewportHeight = (this.pager.getMeasuredHeight() - this.pager.getPaddingTop()) - this.pager.getPaddingBottom();
        if (viewportWidth <= 0 || viewportWidth <= viewportHeight) {
            return 1.0f;
        }
        return (float) (((double) viewportHeight) / ((double) viewportWidth));
    }

    public int getCount() {
        return this.entities.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void onMusicStateChanged() {
        long id = this.playerStateHolder.getCurrentPlayingSong();
        if (id != 0 && id != this.prevScrollSongId) {
            for (int i = 0; i < this.tracks.size(); i++) {
                if (id == ((Track) this.tracks.get(i)).id) {
                    this.pager.setCurrentItem(i, true);
                    this.prevScrollSongId = id;
                    return;
                }
            }
        }
    }
}
