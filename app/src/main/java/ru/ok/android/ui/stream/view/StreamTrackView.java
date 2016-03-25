package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import java.util.ArrayList;
import java.util.List;
import ru.mail.libverify.C0176R;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.stream.music.PlayerStateHolder;
import ru.ok.android.ui.stream.music.PlayerStateHolder.PlayerStateHolderListener;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.wmf.Track;

public class StreamTrackView extends RelativeLayout implements OnClickListener, PlayerStateHolderListener {
    private final TextView albumName;
    final AsyncDraweeView coverImageView;
    protected int displayedTrackPosition;
    private final View error;
    @Nullable
    private StreamTrackViewListener listener;
    final PlayingStateButton playPause;
    PlayerStateHolder playerStateHolder;
    long playingTrackId;
    protected ArrayList<Track> playlist;
    private Boolean prevCurrent;
    private final TextView time;
    protected List<FeedMusicTrackEntity> trackEntities;
    private final TextView trackName;

    public interface StreamTrackViewListener {
        void onPauseStreamTrack(StreamTrackView streamTrackView, long j);

        void onPlayStreamTrack(StreamTrackView streamTrackView, long j);
    }

    public StreamTrackView(Context context) {
        this(context, null);
    }

    public StreamTrackView(Context context, AttributeSet attrs) {
        this(context, attrs, 2131296614);
    }

    public StreamTrackView(Context context, AttributeSet attrs, int defStyleId) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.StreamTrackView, 0, defStyleId);
        int layoutResId = a.getResourceId(0, 2130903524);
        a.recycle();
        View.inflate(context, layoutResId, this);
        this.playPause = (PlayingStateButton) findViewById(C0158R.id.play_pause);
        this.trackName = (TextView) findViewById(2131625235);
        this.albumName = (TextView) findViewById(2131624948);
        this.time = (TextView) findViewById(C0176R.id.time);
        this.coverImageView = (AsyncDraweeView) findViewById(2131625387);
        if (this.coverImageView != null) {
            this.coverImageView.setEmptyImageResId(2130838051);
        }
        this.error = findViewById(2131624551);
        if (this.playPause != null) {
            this.playPause.setOnClickListener(this);
        }
    }

    public void setPlayerStateHolder(PlayerStateHolder playerStateHolder) {
        this.playerStateHolder = playerStateHolder;
        this.playerStateHolder.addStateChangeListener(this);
    }

    public void setListener(@Nullable StreamTrackViewListener listener) {
        this.listener = listener;
    }

    public void onClick(View v) {
        if (v.getId() == C0158R.id.play_pause) {
            Logger.m183v("Index: %d", Integer.valueOf(this.displayedTrackPosition));
            boolean isPlaying = this.playerStateHolder.isSongPlaying(this.playingTrackId);
            if (isPlaying) {
                v.getContext().startService(MusicService.getTogglePlayIntent(v.getContext()));
            } else {
                MusicService.startPlayMusic(OdnoklassnikiApplication.getContext(), this.displayedTrackPosition, this.playlist, MusicListType.STATUS_MUSIC);
            }
            if (this.listener == null) {
                return;
            }
            if (isPlaying) {
                this.listener.onPauseStreamTrack(this, this.playingTrackId);
            } else {
                this.listener.onPlayStreamTrack(this, this.playingTrackId);
            }
        }
    }

    public void configureWith(int index, ArrayList<Track> playlist, List<FeedMusicTrackEntity> trackEntities, Uri defaultCoverImageUri) {
        int i = -1;
        if (playlist == null || trackEntities == null || playlist.size() > trackEntities.size() || index < 0 || index >= playlist.size()) {
            String str = "Illegal parameters: playlist size=%d, trackEntities size=%d, index=%d";
            Object[] objArr = new Object[3];
            objArr[0] = Integer.valueOf(playlist == null ? -1 : playlist.size());
            if (trackEntities != null) {
                i = trackEntities.size();
            }
            objArr[1] = Integer.valueOf(i);
            objArr[2] = Integer.valueOf(index);
            Logger.m185w(str, objArr);
            if (this.playPause != null) {
                this.playPause.setEnabled(false);
                return;
            }
            return;
        }
        this.displayedTrackPosition = index;
        this.playlist = playlist;
        this.trackEntities = trackEntities;
        if (this.playPause != null) {
            this.playPause.setEnabled(true);
            this.playPause.setProgress(this.playerStateHolder.getProgress(this.playingTrackId));
        }
        Track track = (Track) playlist.get(index);
        FeedMusicTrackEntity trackEntity = (FeedMusicTrackEntity) trackEntities.get(index);
        if (this.trackName != null) {
            bindTrackName(track);
        }
        if (this.time != null) {
            updateTime();
        }
        if (this.albumName != null) {
            Utils.setTextViewTextWithVisibility(this.albumName, track.album.name);
        }
        if (this.coverImageView != null) {
            bindCoverImage(trackEntity, defaultCoverImageUri);
        }
        this.playingTrackId = track.id;
        updateIsPlayingState();
    }

    private void bindTrackName(Track track) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(track.artist.name)) {
            sb.append(track.artist.name);
        }
        if (!TextUtils.isEmpty(track.name)) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(track.name);
        }
        if (sb.length() == 0 && !TextUtils.isEmpty(track.fullName)) {
            sb.append(track.fullName);
        }
        Utils.setTextViewTextWithVisibility(this.trackName, sb);
    }

    void bindCoverImage(FeedMusicTrackEntity trackEntity, Uri defaultCoverImageUri) {
        String imageUrl = trackEntity.getImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            this.coverImageView.setUri(defaultCoverImageUri);
        } else {
            this.coverImageView.setUri(Uri.parse(imageUrl));
        }
    }

    private void updateIsPlayingState() {
        boolean isPlaying = this.playerStateHolder.isSongPlaying(this.playingTrackId);
        boolean isBuffering = this.playerStateHolder.isSongBuffering(this.playingTrackId);
        boolean isError = this.playerStateHolder.isSongError(this.playingTrackId);
        if (this.playPause != null) {
            this.playPause.setBuffering(isBuffering);
            if (this.playPause.isPlaying() != isPlaying) {
                this.playPause.setPlaying(isPlaying);
                if (this.trackName != null) {
                    this.trackName.setSingleLine();
                    this.trackName.setSelected(isPlaying);
                }
                if (this.albumName != null) {
                    this.albumName.setSelected(isPlaying);
                }
                if (this.time != null) {
                    this.time.setSelected(isPlaying);
                }
            }
        }
        if (this.error != null) {
            this.error.setVisibility(isError ? 0 : 8);
        }
    }

    private void updateTime() {
        int secondsLeft = this.playerStateHolder.getSecondsLeft(this.playingTrackId);
        Utils.setTextViewTextWithVisibility(this.time, secondsLeft < 0 ? null : DateFormatter.getTimeStringFromSec(secondsLeft));
    }

    public void onMusicStateChanged() {
        boolean isSongCurrent = this.playerStateHolder.isSongCurrent(this.playingTrackId);
        if (isSongCurrent || this.prevCurrent == null || this.prevCurrent.booleanValue() != isSongCurrent) {
            updateIsPlayingState();
            if (this.playPause != null) {
                this.playPause.setProgress(this.playerStateHolder.getProgress(this.playingTrackId));
                this.playPause.invalidate();
            }
            if (this.time != null) {
                updateTime();
            }
        }
        this.prevCurrent = Boolean.valueOf(isSongCurrent);
    }
}
