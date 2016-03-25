package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.TreeSet;
import ru.ok.android.C0206R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.imageview.MultipleSizesAspectRatioAsyncDraweeView;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Utils;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.entities.FeedVideoEntity;

public final class VideoThumbView extends FrameLayout {
    private final TextView duration;
    private final MultipleSizesAspectRatioAsyncDraweeView thumbImageView;

    public VideoThumbView(Context context) {
        this(context, null);
    }

    public VideoThumbView(Context context, AttributeSet attrs) {
        this(context, attrs, 2130772011, 2131296616);
    }

    public VideoThumbView(Context context, AttributeSet attrs, int defAttrId, int defStyleId) {
        super(context, attrs, defAttrId);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.VideoThumbView, defAttrId, defStyleId);
        int layoutId = a.getResourceId(1, 2130903571);
        float aspectRatio = a.getFloat(0, 1.7777778f);
        a.recycle();
        inflate(context, layoutId, this);
        this.thumbImageView = (MultipleSizesAspectRatioAsyncDraweeView) findViewById(C0263R.id.image);
        this.duration = (TextView) findViewById(2131625425);
        if (this.thumbImageView != null) {
            this.thumbImageView.setWidthHeightRatio(aspectRatio);
        }
    }

    public void setVideo(FeedVideoEntity video) {
        if (video != null) {
            setVideo(video.thumbnailUrls, video.title, (int) (video.duration / 1000));
        } else {
            clear();
        }
    }

    public void setVideo(TreeSet<PhotoSize> thumbs, String title, int durationSec) {
        if (this.thumbImageView != null) {
            this.thumbImageView.setSizes(thumbs);
        }
        if (this.duration != null) {
            Utils.setTextViewTextWithVisibility(this.duration, durationSec > 0 ? DateFormatter.getTimeStringFromSec(durationSec) : null);
        }
        setClickable(true);
    }

    public void clear() {
        if (this.thumbImageView != null) {
            this.thumbImageView.setUri(null);
        }
        if (this.duration != null) {
            this.duration.setVisibility(8);
        }
        setClickable(false);
    }
}
