package ru.ok.android.ui.adapters.photo;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.List;
import ru.ok.android.ui.custom.photo.PhotoAlbumTileView;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;

public class AlbumsInfoAdapter extends ArrayAdapter<PhotoAlbumInfo> {
    private int minTileSize;
    private OnNearListEndListener onNearListEndListener;

    public interface OnNearListEndListener {
        void onNearListEnd();
    }

    public AlbumsInfoAdapter(Context context, List<PhotoAlbumInfo> albumsInfoList) {
        super(context, 0, albumsInfoList);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        boolean z = true;
        if (convertView == null) {
            convertView = new PhotoAlbumTileView(parent.getContext());
        }
        PhotoAlbumTileView tileView = (PhotoAlbumTileView) convertView;
        PhotoAlbumInfo album = (PhotoAlbumInfo) getItem(position);
        tileView.setAlbumName(album.getTitle());
        tileView.setId(2131624286);
        tileView.setPhotosCount(album.getPhotoCount());
        boolean hasCover = false;
        if (album.getMainPhotoInfo() != null) {
            String albumCoverUrl = album.getMainPhotoInfo().getClosestSizeUrl(this.minTileSize, this.minTileSize);
            if (!TextUtils.isEmpty(albumCoverUrl)) {
                hasCover = true;
                tileView.setAlbumCoverUri(Uri.parse(albumCoverUrl), true);
            }
        }
        if (album.getOwnerType() != OwnerType.USER || album.getTypes() == null || album.getTypes().contains(AccessType.PUBLIC)) {
            z = false;
        }
        tileView.setLocked(z);
        if (!hasCover) {
            tileView.setAlbumCover(2130838542, false);
        }
        notifyPosition(position);
        return tileView;
    }

    private final void notifyPosition(int position) {
        if (this.onNearListEndListener != null && getCount() - position < 14) {
            this.onNearListEndListener.onNearListEnd();
        }
    }

    public void setOnNearListEndListener(OnNearListEndListener onNearListEndListener) {
        this.onNearListEndListener = onNearListEndListener;
    }

    public final void setMinTileSize(int minTileSize) {
        this.minTileSize = minTileSize;
    }
}
