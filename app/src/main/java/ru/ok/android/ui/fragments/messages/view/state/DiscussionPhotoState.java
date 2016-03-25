package ru.ok.android.ui.fragments.messages.view.state;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.ui.fragments.messages.view.DiscussionInfoView.DiscussionInfoViewListener;
import ru.ok.android.ui.fragments.messages.view.DiscussionPhotoView;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.PhotoUtil;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotoSize;

public class DiscussionPhotoState extends DiscussionState {
    private final PhotoAlbumInfo albumInfo;
    private final DiscussionInfoViewListener listener;
    private final PhotoInfo photoInfo;
    private final VideoGetResponse videoInfo;

    public DiscussionPhotoState(DiscussionInfoResponse infoResponse, DiscussionInfoViewListener listener) {
        this.listener = listener;
        this.photoInfo = infoResponse.photoInfo;
        this.albumInfo = infoResponse.albumInfo;
        this.videoInfo = infoResponse.videoInfo;
    }

    public void configureView(View contentView, DiscussionInfoResponse discussion) {
        boolean z = false;
        PhotoSize size = null;
        int highQualityPhotoWidth = DeviceUtils.getStreamHighQualityPhotoWidth();
        float aspect = 1.0f;
        if (this.photoInfo != null && this.photoInfo.getSizes() != null && !this.photoInfo.getSizes().isEmpty()) {
            size = PhotoUtil.getClosestSize(highQualityPhotoWidth, 0, this.photoInfo.getSizes());
            aspect = ((float) this.photoInfo.getStandartWidth()) / ((float) this.photoInfo.getStandartHeight());
        } else if (!(this.videoInfo == null || this.videoInfo.thumbnails.isEmpty())) {
            size = PhotoUtil.getClosestSize(highQualityPhotoWidth, 0, this.videoInfo.thumbnails);
            aspect = ((float) size.getWidth()) / ((float) size.getHeight());
        }
        if (size != null) {
            PhotoHolder holder = (PhotoHolder) contentView.getTag();
            holder.image.setWidthHeightRatio(aspect);
            DiscussionPhotoView discussionPhotoView = holder.image;
            if (this.photoInfo != null && GifAsMp4PlayerHelper.shouldShowGifAsMp4(this.photoInfo)) {
                z = true;
            }
            discussionPhotoView.setShouldDrawGifMarker(z);
            holder.image.setUri(Uri.parse(size.getUrl()));
        }
    }

    public View createContentView(Context context) {
        return DiscussionInfoViewFactory.photoView(context);
    }

    public void onContentClicked() {
        if (this.photoInfo != null && !TextUtils.isEmpty(this.photoInfo.getId())) {
            this.listener.onPhotoClicked(this.photoInfo, this.albumInfo);
        }
    }

    public PhotoInfo getPhotoInfo() {
        return this.photoInfo;
    }
}
