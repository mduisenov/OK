package ru.ok.android.ui.adapters.music.playlist;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.view.ViewGroup;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.model.cache.music.async.AsyncFileCache.ContainsKeyCallBack;
import ru.ok.android.model.cache.music.async.MusicAsyncFileCache;
import ru.ok.android.ui.adapters.friends.ItemClickListenerControllerProvider;
import ru.ok.android.ui.adapters.music.DotsCursorRecyclerAdapter;
import ru.ok.android.ui.adapters.music.playlist.ViewHolder.AnimateType;
import ru.ok.android.ui.adapters.music.playlist.ViewHolder.OnSelectionChangeListener;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Track;

public class PlayListCursorAdapter extends DotsCursorRecyclerAdapter<Track, ViewHolder> implements ItemClickListenerControllerProvider, OnSelectionChangeListener {
    private final PlayListAdapterCallback callback;
    Context context;
    protected final RecyclerItemClickListenerController itemClickListenerController;
    private MusicFragmentMode mode;
    private OnCheckedChangeListener onCheckedChangeListener;
    private int playPosition;
    Handler uiCacheHandler;

    public interface OnCheckedChangeListener {
        void onCheckedChange(boolean z, int i);
    }

    public interface PlayListAdapterCallback {
        boolean isDataPositionChecked(int i);
    }

    /* renamed from: ru.ok.android.ui.adapters.music.playlist.PlayListCursorAdapter.1 */
    class C05881 implements ContainsKeyCallBack {
        final /* synthetic */ ViewHolder val$holder;

        /* renamed from: ru.ok.android.ui.adapters.music.playlist.PlayListCursorAdapter.1.1 */
        class C05871 implements Runnable {
            final /* synthetic */ String val$key;

            C05871(String str) {
                this.val$key = str;
            }

            public void run() {
                if (this.val$key.equals(String.valueOf(C05881.this.val$holder.trackId))) {
                    C05881.this.val$holder.setTrackInCache();
                }
            }
        }

        C05881(ViewHolder viewHolder) {
            this.val$holder = viewHolder;
        }

        public void onGetKeyInCacheValue(String key, boolean isCache) {
            if (isCache && key.equals(String.valueOf(this.val$holder.trackId))) {
                PlayListCursorAdapter.this.uiCacheHandler.post(new C05871(key));
            }
        }
    }

    public PlayListCursorAdapter(Context context, PlayListAdapterCallback callback, MusicFragmentMode mode) {
        super(context, null, true);
        this.uiCacheHandler = new Handler();
        this.playPosition = -1;
        this.itemClickListenerController = new RecyclerItemClickListenerController();
        this.mode = mode;
        this.context = context;
        this.callback = callback;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.createViewHolder(this.context, LocalizationManager.inflate(this.context, 2130903266, parent, false));
        onCreateDotsView(holder.dots);
        return holder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Track track = MusicStorageFacade.cursor2Track((Cursor) getItem(position));
        holder.setTrackValue(track);
        int dataPosition = this.mCursor.getPosition();
        holder.setDataPosition(dataPosition);
        holder.setPlayValue(isTrackPlaying(dataPosition), AnimateType.NONE);
        holder.setModePlayingState(this.mode);
        holder.setSelected(this.callback.isDataPositionChecked(dataPosition));
        holder.addSelectionChangeListener(this);
        bindDots(holder.dots, track);
        if (DeviceUtils.isTablet(this.context)) {
            holder.dots.setVisibility(4);
        }
        updateInCacheValue(holder);
        this.itemClickListenerController.onBindViewHolder(holder, position);
    }

    private boolean isTrackPlaying(int dataPosition) {
        return dataPosition == this.playPosition;
    }

    public void updateInCacheValue(ViewHolder holder) {
        MusicAsyncFileCache.getInstance().isKeyContains(String.valueOf(holder.trackId), new C05881(holder));
    }

    public void setPlayingTrackPosition(int playPosition) {
        this.playPosition = playPosition;
    }

    public void hidePlayingTrack() {
        this.playPosition = -1;
    }

    public void setSelectedMode(MusicFragmentMode mode) {
        this.mode = mode;
    }

    public void onSelectChange(boolean value, long trackId, int dataPosition) {
        if (this.onCheckedChangeListener != null) {
            this.onCheckedChangeListener.onCheckedChange(value, dataPosition);
        }
    }

    public RecyclerItemClickListenerController getItemClickListenerController() {
        return this.itemClickListenerController;
    }
}
