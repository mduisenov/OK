package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import ru.ok.android.C0206R;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;

public class StreamMusicTrackBigView extends StreamTrackView {
    private boolean changedCoverImage;
    private final boolean keepPlaylistImage;
    private Uri playlistImageUri;

    public StreamMusicTrackBigView(Context context) {
        this(context, null);
    }

    public StreamMusicTrackBigView(Context context, AttributeSet attrs) {
        this(context, attrs, 2131296613);
    }

    public StreamMusicTrackBigView(Context context, AttributeSet attrs, int defStyleId) {
        super(context, attrs, defStyleId);
        this.changedCoverImage = false;
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.StreamMusicTrackBigView, 0, defStyleId);
        this.keepPlaylistImage = a.getBoolean(0, true);
        a.recycle();
    }

    public void setPlaylistImage(String imageUrl) {
        this.playlistImageUri = TextUtils.isEmpty(imageUrl) ? null : Uri.parse(imageUrl);
        this.changedCoverImage = false;
        if (this.trackEntities == null || this.displayedTrackPosition < 0 || this.displayedTrackPosition >= this.trackEntities.size()) {
            this.coverImageView.setImageDrawable(null);
        } else {
            bindCoverImage((FeedMusicTrackEntity) this.trackEntities.get(this.displayedTrackPosition), this.playlistImageUri);
        }
    }

    void bindCoverImage(FeedMusicTrackEntity trackEntity, Uri defaultCoverImageUri) {
        if (this.playlistImageUri == null || (!this.keepPlaylistImage && this.changedCoverImage)) {
            super.bindCoverImage(trackEntity, defaultCoverImageUri);
        } else {
            this.coverImageView.setUri(this.playlistImageUri);
        }
    }
}
