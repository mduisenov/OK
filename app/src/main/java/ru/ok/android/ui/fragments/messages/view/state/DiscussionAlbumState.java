package ru.ok.android.ui.fragments.messages.view.state;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import ru.ok.android.ui.fragments.messages.view.DiscussionInfoView.DiscussionInfoViewListener;
import ru.ok.android.utils.Utils;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoSize;

public final class DiscussionAlbumState extends DiscussionState {
    private PhotoAlbumInfo albumInfo;
    private final DiscussionInfoViewListener listener;

    public DiscussionAlbumState(DiscussionInfoResponse infoResponse, DiscussionInfoViewListener listener) {
        this.listener = listener;
        this.albumInfo = infoResponse.albumInfo;
    }

    public void configureView(View contentView, DiscussionInfoResponse discussion) {
        String url;
        String title;
        int photosCount;
        int i = 0;
        PhotoSize size = null;
        if (!(this.albumInfo == null || this.albumInfo.getMainPhotoInfo() == null)) {
            size = this.albumInfo.getMainPhotoInfo().getLargestSize();
        }
        if (size != null) {
            url = size.getUrl();
        } else {
            url = null;
        }
        if (this.albumInfo != null) {
            title = this.albumInfo.getTitle();
        } else {
            title = null;
        }
        AlbumHolder holder = (AlbumHolder) contentView.getTag();
        holder.image.setAspectRatio(size != null ? size.getAspectRatio() : 0.0f);
        Utils.setImageViewUrlWithVisibility(holder.image, url, 0);
        Utils.setTextViewTextWithVisibility(holder.name, title);
        if (this.albumInfo != null) {
            photosCount = this.albumInfo.getPhotoCount();
        } else {
            photosCount = 0;
        }
        holder.photosCount.setText(String.valueOf(photosCount));
        TextView textView = holder.photosCount;
        if (photosCount <= 0) {
            i = 8;
        }
        textView.setVisibility(i);
    }

    public View createContentView(Context context) {
        return DiscussionInfoViewFactory.photoAlbumView(context);
    }

    public void onContentClicked() {
        this.listener.onAlbumClicked(this.albumInfo);
    }
}
