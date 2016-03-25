package ru.ok.android.ui.image;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.ui.custom.ImageUploadStatusView;
import ru.ok.android.ui.custom.photo.UploadStatusHeaderView;
import ru.ok.android.utils.MimeTypes;
import ru.ok.model.GroupInfo;
import ru.ok.model.photo.PhotoAlbumInfo;

public final class UploadsAdapter extends ArrayAdapter<ImageForUpload> implements StickyGridHeadersBaseAdapter {
    private List<HeaderData> headers;
    private ImageUploadStatusActivity imageUploadStatusActivity;
    private List<ImageForUpload> uploads;

    public static final class HeaderData {
        public PhotoAlbumInfo albumInfo;
        public int count;
        public GroupInfo groupInfo;
        public int type;

        public HeaderData(int type, PhotoAlbumInfo albumInfo, GroupInfo groupInfo, int count) {
            this.type = type;
            this.albumInfo = albumInfo;
            this.groupInfo = groupInfo;
            this.count = count;
        }
    }

    public UploadsAdapter(ImageUploadStatusActivity imageUploadStatusActivity, Context context, List<ImageForUpload> uploads) {
        super(context, 0, uploads);
        this.headers = new ArrayList();
        this.uploads = uploads;
        this.imageUploadStatusActivity = imageUploadStatusActivity;
        rebuildHeaders();
    }

    private final void rebuildHeaders() {
        this.headers.clear();
        int count = 0;
        int type = 0;
        PhotoAlbumInfo albumInfo = null;
        ListIterator<ImageForUpload> it = this.uploads.listIterator();
        while (it.hasNext()) {
            ImageForUpload upload = (ImageForUpload) it.next();
            if (!it.hasPrevious()) {
                count++;
                albumInfo = upload.getAlbumInfo();
            } else if (upload.getAlbumInfo().equals(albumInfo)) {
                type = getType(upload);
                count++;
            } else {
                this.headers.add(new HeaderData(type, albumInfo, null, count));
                albumInfo = upload.getAlbumInfo();
                count = 1;
            }
            if (!it.hasNext()) {
                type = getType(upload);
                this.headers.add(new HeaderData(type, upload.getAlbumInfo(), null, count));
            }
        }
    }

    private final int getType(ImageForUpload image) {
        if (TextUtils.isEmpty(image.getAlbumInfo().getGroupId())) {
            return 0;
        }
        if (TextUtils.equals(image.getAlbumInfo().getId(), "group_main")) {
            return 2;
        }
        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageUploadStatusView statusView = (ImageUploadStatusView) convertView;
        if (statusView == null) {
            statusView = new ImageUploadStatusView(parent.getContext());
        }
        statusView.setContentSize(this.imageUploadStatusActivity.mMeasuredTileSide, this.imageUploadStatusActivity.mMeasuredTileSide);
        ImageForUpload imageForUpload = (ImageForUpload) getItem(position);
        statusView.setStatus(imageForUpload.getCurrentStatus(), imageForUpload.getError());
        statusView.setImage(imageForUpload.getUri(), imageForUpload.getRotation());
        statusView.setShouldDrawGifMarker(MimeTypes.isGif(imageForUpload.getMimeType()));
        return statusView;
    }

    public boolean isEnabled(int position) {
        ImageForUpload imageForUpload = (ImageForUpload) getItem(position);
        boolean enabled = false;
        ImageUploadException error = imageForUpload.getError();
        int errorCode = error == null ? 0 : error.getErrorCode();
        if (imageForUpload.getCurrentStatus() == 8) {
            if (!(errorCode == 14 || errorCode == 11)) {
                enabled = true;
            }
        } else if (imageForUpload.getCurrentStatus() == 5) {
            enabled = true;
        }
        if (errorCode == 4 && error.getServerErrorCode() == 454) {
            return true;
        }
        return enabled;
    }

    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        UploadStatusHeaderView view = (UploadStatusHeaderView) convertView;
        if (view == null) {
            view = new UploadStatusHeaderView(getContext());
        }
        HeaderData headerData = (HeaderData) this.headers.get(position);
        view.setAlbumInfo(headerData.albumInfo);
        view.setGroupInfo(headerData.groupInfo);
        view.setType(headerData.type);
        return view;
    }

    public int getCountForHeader(int header) {
        return ((HeaderData) this.headers.get(header)).count;
    }

    public int getNumHeaders() {
        return this.headers.size();
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        rebuildHeaders();
    }

    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        rebuildHeaders();
    }

    public final HeaderData getHeaderData(int id) {
        return (HeaderData) this.headers.get(id);
    }
}
