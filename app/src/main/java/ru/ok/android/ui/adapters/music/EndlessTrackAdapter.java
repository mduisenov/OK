package ru.ok.android.ui.adapters.music;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.PlayerImageView;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.LruCache;
import ru.ok.android.utils.music.MusicPlayerUtils;
import ru.ok.model.wmf.PlayTrackInfo;
import ru.ok.model.wmf.Track;

public class EndlessTrackAdapter extends PagerAdapter {
    private final List<View> activeViews;
    private Context context;
    private int currentPosition;
    private OnClickListener onClickListener;
    private final List<Pair<Integer, Track>> positionsToTracks;
    private final LruCache<Long, PlayTrackInfo> trackInfos;

    class Holder {
        public PlayerImageView albumImageView;
        public TextView legalInfoView;
        public int position;
        public long trackId;

        Holder() {
        }

        public void setStubImage() {
            if (this.albumImageView != null) {
                this.albumImageView.setImageResource(2130838395);
            }
        }
    }

    public EndlessTrackAdapter(Context context) {
        this.activeViews = new LinkedList();
        this.trackInfos = new LruCache(10);
        this.positionsToTracks = new ArrayList(10);
        this.currentPosition = 50000;
        this.context = context;
    }

    public int getCount() {
        return 100000;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        this.activeViews.remove(view);
        Logger.m173d("Page gets destroyed = %d (internal: %d)", Integer.valueOf(position), Integer.valueOf(((Holder) view.getTag()).position));
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Holder holder = new Holder();
        View view = View.inflate(this.context, 2130903543, null);
        holder.legalInfoView = (TextView) view.findViewById(2131625241);
        holder.albumImageView = (PlayerImageView) view.findViewById(2131625403);
        if (holder.albumImageView != null) {
            holder.albumImageView.setTag(view.findViewById(2131625407));
        }
        holder.position = position;
        Track track = getTrack(position);
        if (track != null) {
            fillHolder(holder, track);
        }
        view.setTag(holder);
        this.activeViews.add(view);
        container.addView(view);
        Logger.m173d("Page gets created = %d. Track is %s", Integer.valueOf(position), track);
        return view;
    }

    private void fillHolder(Holder holder, @NonNull Track track) {
        boolean z = false;
        if (holder.legalInfoView != null) {
            holder.legalInfoView.setText(MusicPlayerUtils.buildLegalInfo(this.context, (PlayTrackInfo) this.trackInfos.get(Long.valueOf(track.id))));
        }
        if (holder.albumImageView != null) {
            holder.albumImageView.setOnClickListener(this.onClickListener);
            PlayTrackInfo trackInfo = (PlayTrackInfo) this.trackInfos.get(Long.valueOf(track.id));
            String url = trackInfo != null ? trackInfo.imageUrl : null;
            if (url == null && track.imageUrl != null) {
                url = track.imageUrl;
            }
            if (TextUtils.isEmpty(url) || PlayerImageView.isStubImageUrl(url)) {
                holder.setStubImage();
            } else {
                url = pickAlbumImageSize(url);
                ImageViewManager.getInstance().displayImage(url, holder.albumImageView, 2130838395, null, 0);
            }
            String str = "Track: %s. From info: %b. Url: %s";
            Object[] objArr = new Object[3];
            objArr[0] = track.name;
            if (!(trackInfo == null || trackInfo.imageUrl == null)) {
                z = true;
            }
            objArr[1] = Boolean.valueOf(z);
            objArr[2] = url;
            Logger.m173d(str, objArr);
        }
        holder.trackId = track.id;
    }

    private String pickAlbumImageSize(String url) {
        return PlayTrackInfo.getBigImageUrl(url);
    }

    private Track getTrackById(long trackId) {
        for (Pair<Integer, Track> pair : this.positionsToTracks) {
            if (pair.second != null && ((Track) pair.second).id == trackId) {
                return (Track) pair.second;
            }
        }
        return null;
    }

    private Track getTrack(int trackPosition) {
        for (Pair<Integer, Track> positionsToTrack : this.positionsToTracks) {
            if (((Integer) positionsToTrack.first).intValue() == trackPosition) {
                return (Track) positionsToTrack.second;
            }
        }
        return null;
    }

    public void replaceTracks(Track track, Track prevTrack, Track nextTrack) {
        Logger.m173d("Replace tracks is called for position %d", Integer.valueOf(this.currentPosition));
        this.positionsToTracks.clear();
        addTrackInternal(track, this.currentPosition);
        addTrackInternal(nextTrack, this.currentPosition + 1);
        addTrackInternal(prevTrack, this.currentPosition - 1);
    }

    private void addTrackInternal(Track track, int position) {
        if (track != null) {
            this.positionsToTracks.add(new Pair(Integer.valueOf(position), track));
            for (View view : this.activeViews) {
                Holder holder = (Holder) view.getTag();
                if (holder != null && holder.position == position && holder.trackId != track.id) {
                    fillHolder(holder, track);
                    return;
                }
            }
        }
    }

    public Track getCurrentTrack() {
        for (Pair<Integer, Track> positionsToTrack : this.positionsToTracks) {
            if (((Integer) positionsToTrack.first).intValue() == this.currentPosition) {
                return (Track) positionsToTrack.second;
            }
        }
        return null;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void addTrackInfo(long trackId, PlayTrackInfo trackInfo) {
        if (trackInfo != null && ((PlayTrackInfo) this.trackInfos.put(Long.valueOf(trackId), trackInfo)) == null) {
            for (View view : this.activeViews) {
                Holder holder = (Holder) view.getTag();
                if (holder != null && holder.trackId == trackId) {
                    Track track = getTrackById(holder.trackId);
                    if (track != null) {
                        fillHolder(holder, track);
                        return;
                    }
                    return;
                }
            }
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
