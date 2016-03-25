package ru.ok.android.ui.adapters.music.playlist;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.TrackSelectionControl;
import ru.ok.android.model.cache.music.async.AsyncFileCache.ContainsKeyCallBack;
import ru.ok.android.model.cache.music.async.MusicAsyncFileCache;
import ru.ok.android.ui.adapters.CheckChangeAdapter;
import ru.ok.android.ui.adapters.music.DotsCursorAdapter.OnDotsClickListener;
import ru.ok.android.ui.adapters.music.playlist.ViewHolder.AnimateType;
import ru.ok.android.ui.adapters.music.playlist.ViewHolder.OnSelectionChangeListener;
import ru.ok.android.utils.ViewUtil;
import ru.ok.model.wmf.Track;

public final class PlayListAdapter extends CheckChangeAdapter implements OnClickListener {
    private final Context context;
    private final List<PlayListNode> data;
    protected final LayoutInflater inflater;
    private MusicFragmentMode mode;
    private OnDotsClickListener<Track> onDotsClickListener;
    private Track playingTrack;
    private TrackSelectionControl trackSelectionControl;
    Handler uiCacheHandler;

    /* renamed from: ru.ok.android.ui.adapters.music.playlist.PlayListAdapter.1 */
    class C05861 implements ContainsKeyCallBack {
        final /* synthetic */ ViewHolder val$holder;

        /* renamed from: ru.ok.android.ui.adapters.music.playlist.PlayListAdapter.1.1 */
        class C05851 implements Runnable {
            final /* synthetic */ String val$key;

            C05851(String str) {
                this.val$key = str;
            }

            public void run() {
                if (this.val$key.equals(String.valueOf(C05861.this.val$holder.trackId))) {
                    C05861.this.val$holder.setTrackInCache();
                }
            }
        }

        C05861(ViewHolder viewHolder) {
            this.val$holder = viewHolder;
        }

        public void onGetKeyInCacheValue(String key, boolean isCache) {
            if (isCache && key.equals(String.valueOf(this.val$holder.trackId))) {
                PlayListAdapter.this.uiCacheHandler.post(new C05851(key));
            }
        }
    }

    public class PlayListNode implements OnSelectionChangeListener {
        private Track track;

        PlayListNode(Track track) {
            this.track = track;
        }

        public Track getTrack() {
            return this.track;
        }

        public void onSelectChange(boolean value, long trackId, int dataPosition) {
            if (PlayListAdapter.this.mode == MusicFragmentMode.MULTI_SELECTION && PlayListAdapter.this.trackSelectionControl != null) {
                PlayListAdapter.this.trackSelectionControl.setTrackSelection(this.track, value);
            }
        }
    }

    public PlayListAdapter(Context context) {
        this(context, null);
    }

    public PlayListAdapter(Context context, Collection<? extends Track> tracks) {
        this.uiCacheHandler = new Handler();
        this.mode = MusicFragmentMode.STANDARD;
        this.trackSelectionControl = null;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.data = new CopyOnWriteArrayList();
        if (tracks != null) {
            addTracks(tracks);
        }
    }

    public synchronized void addTracks(Collection<? extends Track> tracks) {
        for (Track track : tracks) {
            this.data.add(new PlayListNode(track));
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.data.size();
    }

    public Track getItem(int i) {
        return this.data.size() > i ? ((PlayListNode) this.data.get(i)).getTrack() : null;
    }

    public int getPlayingPosition() {
        int position = 0;
        if (this.playingTrack != null) {
            for (PlayListNode node : this.data) {
                if (node.getTrack().id == this.playingTrack.id) {
                    return position;
                }
                position++;
            }
        }
        return -1;
    }

    public long getItemId(int i) {
        if (this.data.size() > i) {
            return ((PlayListNode) this.data.get(i)).getTrack().id;
        }
        return 0;
    }

    public synchronized boolean removeItem(Track track) {
        boolean value;
        PlayListNode deleteNode = null;
        for (PlayListNode node : this.data) {
            if (node.getTrack().id == track.id) {
                deleteNode = node;
            }
        }
        value = false;
        if (deleteNode != null) {
            value = this.data.remove(deleteNode);
        }
        notifyDataSetChanged();
        return value;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        boolean isPlayTrack;
        if (convertView == null || convertView.getTag() == null) {
            convertView = this.inflater.inflate(2130903266, parent, false);
            holder = ViewHolder.createViewHolder(this.context, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PlayListNode node = (PlayListNode) this.data.get(position);
        Track track = node.getTrack();
        if (this.playingTrack == null || track.id != this.playingTrack.id) {
            isPlayTrack = false;
        } else {
            isPlayTrack = true;
        }
        if (track != null) {
            holder.setTrackValue(track);
            holder.setDataPosition(position);
            updateCacheValue(holder);
            holder.setPlayValue(isPlayTrack, AnimateType.TRANSLATE);
            holder.setModePlayingState(this.mode);
            bindDots(holder.dots, track);
        }
        if (this.mode == MusicFragmentMode.MULTI_SELECTION) {
            holder.showCheckBox();
            holder.clearListeners();
            holder.addSelectionChangeListener(node);
            holder.addSelectionChangeListener(this.checkedChangeHolder);
            if (parent instanceof ListView) {
                ListView listView = (ListView) parent;
                holder.setSelected(listView.getCheckedItemPositions().get(listView.getHeaderViewsCount() + position));
            }
        } else {
            holder.hideCheckBox();
            convertView.setOnClickListener(null);
            convertView.setClickable(false);
        }
        return convertView;
    }

    protected <T> void bindDots(View dots, T o) {
        ViewUtil.setTouchDelegate(dots, dots.getContext().getResources().getDimensionPixelSize(2131230952));
        dots.setTag(o);
        dots.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == 2131624874 && v.getVisibility() == 0 && this.onDotsClickListener != null) {
            this.onDotsClickListener.onDotsClick((Track) v.getTag(), v);
        }
    }

    public void setOnDotsClickListener(OnDotsClickListener<Track> onDotsClickListener) {
        this.onDotsClickListener = onDotsClickListener;
    }

    private void updateCacheValue(ViewHolder holder) {
        MusicAsyncFileCache.getInstance().isKeyContains(String.valueOf(holder.trackId), new C05861(holder));
    }

    public Track getPlayingTrack() {
        return this.playingTrack;
    }

    public List<? extends Track> getData() {
        List<Track> tracks = new ArrayList(this.data.size());
        for (int i = 0; i < this.data.size(); i++) {
            tracks.add(((PlayListNode) this.data.get(i)).getTrack());
        }
        return tracks;
    }

    public void setData(Collection<? extends Track> tracks) {
        clearData();
        for (Track track : tracks) {
            this.data.add(new PlayListNode(track));
        }
        notifyDataSetChanged();
    }

    public synchronized void clearData() {
        this.data.clear();
    }

    public void showPlayingTrack(Track track) {
        this.playingTrack = track;
        notifyDataSetChanged();
    }

    public void hidePlayPosition() {
        this.playingTrack = null;
        notifyDataSetChanged();
    }

    public void setMode(MusicFragmentMode mode, TrackSelectionControl trackSelectionControl) {
        this.mode = mode;
        this.trackSelectionControl = trackSelectionControl;
        notifyDataSetChanged();
    }
}
